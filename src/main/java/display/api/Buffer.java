package display.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.apache.commons.lang3.Range;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@NonNull
@AllArgsConstructor
public final class Buffer<Slot, Animation, Frame> {
	/**
	 * TODO: Clean this the fuck up
	 */
	private final boolean intervalSupport;
	private final Slot slot;

	private final Animator<Animation, Frame> animator;
	private final Timings timings;

	private final List<Animation> animations;

	private Stage stage = Stage.CREATE;
	private int completedCycles, ticks = -1;

	private final Logger logger = Logger.getLogger("Buffer");

	{
		logger.setLevel(Level.OFF);
	}

	private final Map<Animation, Integer> frames = new HashMap<>();

	@SuppressWarnings("unchecked")
	public Buffer(boolean intervalSupport, Animator<Animation, Frame> animator,  Slot slot,  Timings timings,
			List<Animation> animations) {
		this.intervalSupport = intervalSupport;
		this.slot = slot;
		this.timings = timings;
		this.animator = animator;
		this.animations = animations;
	}

	public boolean onIntervalGap() {
		return peek() == null && ticks != -1 && !List.of(Stage.CREATE, Stage.DESTROY).contains(stage);
	}

	public Frame peek() {
		return buffer(false);
	}

	public Frame poll() {
		return buffer(true);
	}

	Frame buffer(boolean increment) {
		if (increment) {
			++ticks;
		}
		List<Frame> frames = new ArrayList<>();

		switch (stage) {
		case CREATE:
			stage(increment, Stage.DELAY);
			return null;
		case DELAY:
			frames = delaySynthesize(increment, Stage.CYCLE, false, (a, t) -> delayFrame(frames(a), t));
			break;
		case CYCLE:
			frames = cycleSynthesize(increment);
			break;
		case CYCLE_DELAY:
			frames = delaySynthesize(increment, Stage.CYCLE, true, (a, t) -> cycleDelayFrame(frames(a), t));
			break;

		case FINAL_DELAY:
			frames = delaySynthesize(increment, Stage.DESTROY, false, (a, t) -> finalDelayFrame(frames(a), t));
			break;
		case DESTROY:
			stage(increment, Stage.COMPLETE);
			return null;
		default:
			throw new RuntimeException("Buffer has broken due to an unknown error, stage = " + stage);
		}

		if (frames == null && increment) {
			logger.debug("BUFFERING AGAIN BECAUSE THE FRAMES WAS NULL");
			return buffer(increment);
		}
		if (frames != null && !frames.isEmpty()) {
			return animator.combine(frames);
		}

		return null;
	}

	List<Frame> delaySynthesize(boolean increment, Stage nextStage, boolean updateFrames,
			BiFunction<Animation, Integer, Integer> function) {

		List<Frame> frames = new ArrayList<>();

		for (Animation a : animations) {
			Integer frame = function.apply(a, ticks);
			Integer nextFrame = function.apply(a, ticks + 1);
			logger.debug("next " + nextFrame);

			if (frame == null && nextFrame == null) {
				break;
			}

			if (nextFrame.equals(-1) || frame.equals(-1)) {
				stage(increment, nextStage);

				if (updateFrames) {
					updateFrames();
				}

				if (frame != null && frame == -1) {
					return null;
				}

			}

			logger.debug(stage + " animating " + frame + " nextframe " + nextFrame + " ticks " + ticks);

			if (frame != null) {
				frames.add(animator.animate(a, frame));
			}
		}

		return frames;

	}

	List<Frame> cycleSynthesize(boolean increment) {
		List<Frame> frames = new ArrayList<>();

		Function<Animation, Integer> function = (a) -> cycleFrame(frames(a), ticks);
		Function<Animation, Integer> functionNext = (a) -> cycleFrame(frames(a), ticks + 1);

		Map<Animation, Integer> animationFrames = frames(function);
		Map<Animation, Integer> animationNextFrames = frames(functionNext);

		boolean complete = isComplete(animationFrames);

		if (complete) {
			logger.debug("Cycle length was 0");
			stage(increment, completedCycles == timings.repeats() ? Stage.FINAL_DELAY : Stage.CYCLE_DELAY);
			++completedCycles;
			return null; // Signals to recalculate since there were no frames on the first iteration
		}

		animationFrames.forEach((a, f) -> {
			if (f == null) {
				logger.debug("skipping " + a);
				return;
			}

			int frame = f;

			if (f == -1) {
				logger.debug("rest not done using final frame " + a);
				frame = timings.reversed() ? 0 : frames(a) - 1; // Extends the cycle delay for animations with shorter
																// cycle lengths
			}
			logger.debug("animating " + frame);
			frames.add(animator.animate(a, frame));

		});

		if (isComplete(animationNextFrames)) {
			++completedCycles;
			logger.debug("All animations had a completed cycle");
			logger.debug("completed cycles " + completedCycles);
			stage(increment, completedCycles == timings.repeats() ? Stage.FINAL_DELAY : Stage.CYCLE_DELAY);
		}

		return frames;

	}

	/**
	 * -1 means not in a delay, null means interval support is enabled and no frame
	 * should be sent based on calculations
	 * 
	 * @return The frame
	 */
	Integer delayFrame(int frames, int ticks) {

		if (timings.delay() <= 0) {
			logger.debug("result = -1, delay = 0");
			return -1;
		}

		Integer frame = delayFrame(logger, timings, intervalSupport, frames, ticks, timings.delay());

		if (frame != null && frame == -1) {
			return -1;
		}

		if (intervalSupport) {
			logger.debug("result = null, intervalSupport = true");
			return null;
		}

		frame = frames - 1 - frame;

		logger.debug("{result = " + frame + "}");

		return frame;
	}

	Integer cummulativeFrame(int cummulativeFrames, int ticks) {

		int end = timings.interval() * cummulativeFrames;

		if (cummulativeFrames <= 0 || timings.interval() <= 0) {
			return -1;
		}

		return frame(cummulativeFrames, ticks, end);
	}

	Integer cycleFrame(int frames, int ticks) {

		int end = timings.interval() * frames;

		if (timings.interval() <= 0) {
			return -1;
		}

		return frame(frames, ticks, end);
	}

	Integer cycleDelayFrame(int frames, int ticks) {

		if (timings.repeatDelay() <= 0) {
			return -1;
		}

		return delayFrame(logger, timings, intervalSupport, frames, ticks, timings.repeatDelay());
	}

	Integer finalDelayFrame(int frames, int ticks) {

		if (timings.finalDelay() <= 0) {
			return -1;
		}

		return delayFrame(logger, timings, intervalSupport, frames, ticks, timings.finalDelay());
	}

	Integer frame(int frames, int ticks, int rangeEnd) {

		int interval = timings.interval();

		Range<Integer> range = Range.between(0, rangeEnd - 1);

		if (interval <= 0 || !range.contains(ticks)) {
			return -1;
		}

		// On a frame that should not be sent
		if (intervalSupport && ticks % interval != 0) {
			return null;
		}

		int frame = (int) ticks / interval;

		return timings.reversed() ? frames - frame : frame;
	}

	Integer delayFrame(Logger logger, Timings info, boolean intervalSupport, int frames, int ticks, int rangeEnd) {

		Range<Integer> range = Range.between(0, rangeEnd - 1);

		if (!range.contains(ticks)) {
			logger.debug("delayFrame = -1, outOfRange = " + range);
			return -1;
		}

		if (intervalSupport) {
			return null;
		}

		int frame = info.reversed() ? 0 : (frames - 1);

		logger.debug("delayFrame = " + frame + " reversed = " + info.reversed());

		return frame;

	}

	public void stage(boolean increment, Stage stage) {
		if (increment) {
			logger.debug("Changing stage from " + this.stage + " -> " + stage);
			this.stage = stage;
			this.ticks = -1;
		}
	}

	public int frames(Animation animation) {
		return frames.computeIfAbsent(animation, (a) -> animator.frames(a));
	}

	void updateFrames() {
		animations.forEach(a -> frames.put(a, animator.frames(a)));
	}

	Map<Animation, Integer> frames(Function<Animation, Integer> function) {
		Map<Animation, Integer> frames = new HashMap<>();
		animations.forEach(a -> frames.put(a, function.apply(a)));

		return frames;
	}

	boolean isComplete(Map<Animation, Integer> frames) {

		List<Integer> remaining = new ArrayList<>(frames.values());

		remaining.removeIf((i) -> i != null && i == -1);

		return remaining.isEmpty();
	}
}
