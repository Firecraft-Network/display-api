package hyleo.animations;

import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

import org.apache.commons.lang3.Range;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import com.google.common.collect.Iterables;

import hyleo.animations.api.AnimationInfo;

public class Util {

	static {
		BasicConfigurator.configure();
	}

	@SafeVarargs
	public static <T> Supplier<T> compilation(T... values) {
		Iterator<T> iterator = Iterables.cycle(List.of(values)).iterator();
		return () -> iterator.next();
	}

	/**
	 * -1 means not in a delay, null means interval support is enabled and no frame
	 * should be sent based on calculations
	 * 
	 * @return The frame
	 */
	public static Integer delayFrame(Logger logger, AnimationInfo info, boolean intervalSupport, int frames,
			int ticks) {

		if (info.delay() <= 0) {
			logger.debug("result = -1, delay = 0");
			return -1;
		}

		Integer frame = delayFrame(logger, info, intervalSupport, frames, ticks, info.delay());

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

	public static Integer cummulativeFrame(AnimationInfo info, boolean intervalSupport, int cummulativeFrames,
			int ticks) {

		int end = info.interval() * cummulativeFrames;

		if (cummulativeFrames <= 0 || info.interval() <= 0) {
			return -1;
		}

		return frame(info, intervalSupport, cummulativeFrames, ticks, end);
	}

	public static Integer cycleFrame(AnimationInfo info, boolean intervalSupport, int frames, int ticks) {

		int end = info.interval() * frames;

		if (info.interval() <= 0) {
			return -1;
		}

		return frame(info, intervalSupport, frames, ticks, end);
	}

	public static Integer cycleDelayFrame(Logger logger, AnimationInfo info, boolean intervalSupport, int frames,
			int ticks) {

		if (info.cycleDelay() <= 0) {
			return -1;
		}

		return delayFrame(logger, info, intervalSupport, frames, ticks, info.cycleDelay());
	}

	public static Integer finalDelayFrame(Logger logger, AnimationInfo info, boolean intervalSupport, int frames,
			int ticks) {

		if (info.finalDelay() <= 0) {
			return -1;
		}

		return delayFrame(logger, info, intervalSupport, frames, ticks, info.finalDelay());
	}

	public static Integer frame(AnimationInfo info, boolean intervalSupport, int frames, int ticks, int rangeEnd) {

		int interval = info.interval();

		Range<Integer> range = Range.between(0, rangeEnd - 1);

		if (interval <= 0 || !range.contains(ticks)) {
			return -1;
		}

		// On a frame that should not be sent
		if (intervalSupport && ticks % interval != 0) {
			return null;
		}

		int frame = (int) ticks / interval;

		return info.reversed() ? frames - frame : frame;
	}

	public static Integer delayFrame(Logger logger, AnimationInfo info, boolean intervalSupport, int frames, int ticks,
			int rangeEnd) {

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

}
