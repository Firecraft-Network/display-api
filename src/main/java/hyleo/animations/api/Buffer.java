package hyleo.animations.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import hyleo.animations.Util;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
@NonNull
public final class Buffer<Slot, Animation, Frame> {

	public enum Stage {
		CREATE, DELAY, CYCLE, CYCLE_DELAY, FINAL_DELAY, DESTROY, COMPLETE;
	}

	private final boolean intervalSupport;
	private final Slot slot;

	private final AnimationInfo info;
	private final Animator<Animation, Frame> animator;

	private final List<Animation> animations;
	private final Function<List<Frame>, Frame> coroperator;

	private Stage stage = Stage.CREATE;
	private int completedCycles, ticks = -1;

	private final Logger logger = Logger.getLogger("Buffer");

	{
		logger.setLevel(Level.OFF);
	}

	private final Map<Animation, Integer> frames = new HashMap<>();

	@SuppressWarnings("unchecked")
	@Builder
	public Buffer(boolean intervalSupport, Slot slot, Logger logger, AnimationInfo info,
			@SuppressWarnings("rawtypes") Animator animator, List<Animation> animations,
			Function<List<Frame>, Frame> coroperator) {
		this.intervalSupport = intervalSupport;
		this.slot = slot;
		this.info = info;
		this.animator = animator;
		this.animations = animations;
		this.coroperator = coroperator;
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
			if (increment) {
				stage(Stage.DELAY);
			}
			return null;
		case DELAY:
			frames = delaySynthesize(Stage.CYCLE, false,
					(a, t) -> Util.delayFrame(logger, info, intervalSupport, frames(a), t));
			break;
		case CYCLE:
			frames = cycleSynthesize();
			break;
		case CYCLE_DELAY:
			frames = delaySynthesize(Stage.CYCLE, true,
					(a, t) -> Util.cycleDelayFrame(logger, info, intervalSupport, frames(a), t));
			break;

		case FINAL_DELAY:
			frames = delaySynthesize(Stage.DESTROY, false,
					(a, t) -> Util.finalDelayFrame(logger, info, intervalSupport, frames(a), t));
			break;
		case DESTROY:
			if (increment) {
				stage(Stage.COMPLETE);
			}
			return null;
		default:
			throw new RuntimeException("Buffer has broken due to an unknown error, stage = " + stage);
		}

		if (frames == null) {
			logger.debug("BUFFERING AGAIN BECAUSE THE FRAMES WAS NULL");
			return buffer(increment);
		}
		if (!frames.isEmpty()) {
			return coroperator.apply(frames);
		}

		return null;
	}

	List<Frame> delaySynthesize(Stage nextStage, boolean updateFrames,
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
				stage(nextStage);

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

	List<Frame> cycleSynthesize() {
		List<Frame> frames = new ArrayList<>();

		Function<Animation, Integer> function = (a) -> Util.cycleFrame(info, intervalSupport, frames(a), ticks);
		Function<Animation, Integer> functionNext = (a) -> Util.cycleFrame(info, intervalSupport, frames(a), ticks + 1);

		Map<Animation, Integer> animationFrames = frames(function);
		Map<Animation, Integer> animationNextFrames = frames(functionNext);

		boolean complete = isComplete(animationFrames);

		if (complete) {
			logger.debug("Cycle length was 0");
			stage(completedCycles == info.cycles() ? Stage.FINAL_DELAY : Stage.CYCLE_DELAY);
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
				frame = info.reversed() ? 0 : frames(a) - 1; // Extends the cycle delay for animations with shorter
																// cycle lengths
			}
			logger.debug("animating " + frame);
			frames.add(animator.animate(a, frame));

		});

		if (isComplete(animationNextFrames)) {
			++completedCycles;
			logger.debug("All animations had a completed cycle");
			logger.debug("completed cycles " + completedCycles);
			stage(completedCycles == info.cycles() ? Stage.FINAL_DELAY : Stage.CYCLE_DELAY);
		}

		return frames;

	}

	public void stage(Stage stage) {
		logger.debug("Changing stage from " + this.stage + " -> " + stage);
		this.stage = stage;
		this.ticks = -1;
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
