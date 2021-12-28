package net.driftverse.dispatch;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import com.google.common.collect.Iterables;

import net.driftverse.dispatch.api.Synthesizer;

public class Util {

	static {
		BasicConfigurator.configure();
	}

	@SuppressWarnings("unchecked")
	public static Queue<List<SynthesizerImpl<?, ?>>> copy(List<List<SynthesizerImpl<?, ?>>> synthesizers) {
		@SuppressWarnings("rawtypes")

		Queue queue = new LinkedList<>();

		synthesizers.forEach(
				(g) -> queue.add(g.stream().map((s) -> SerializationUtils.clone(s)).collect(Collectors.toList())));

		return queue;

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
	public static Integer delayFrame(Logger logger, Synthesizer<?, ?> synthesizer, boolean intervalSupport, int frames,
			int ticks) {

		if (synthesizer.delay() == 0) {
			logger.debug("result = -1, delay = 0");
			return -1;
		}

		Integer frame = delayFrame(logger, synthesizer, intervalSupport, frames, ticks, synthesizer.delay());

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

	public static Integer cummulativeFrame(Synthesizer<?, ?> synthesizer, boolean intervalSupport,
			int cummulativeFrames, int ticks) {

		int end = synthesizer.interval() * cummulativeFrames;

		if (cummulativeFrames <= 0 || synthesizer.interval() <= 0) {
			return -1;
		}

		return frame(synthesizer, intervalSupport, cummulativeFrames, ticks, end);
	}

	public static Integer cycleFrame(Synthesizer<?, ?> synthesizer, boolean intervalSupport, int frames, int ticks) {

		int end = synthesizer.interval() * frames;

		if (synthesizer.interval() <= 0) {
			return -1;
		}

		return frame(synthesizer, intervalSupport, frames, ticks, end);
	}

	public static Integer cycleDelayFrame(Logger logger, Synthesizer<?, ?> synthesizer, boolean intervalSupport,
			int ticks, int frames) {

		if (synthesizer.cycleDelay() <= 0) {
			return -1;
		}

		return delayFrame(logger, synthesizer, intervalSupport, ticks, frames, synthesizer.cycleDelay());
	}

	public static Integer finalDelayFrame(Logger logger, Synthesizer<?, ?> synthesizer, boolean intervalSupport,
			int ticks, int frames) {

		if (synthesizer.finalDelay() <= 0) {
			return -1;
		}

		return delayFrame(logger, synthesizer, intervalSupport, ticks, frames, synthesizer.finalDelay());
	}

	public static Integer frame(Synthesizer<?, ?> synthesizer, boolean intervalSupport, int frames, int ticks,
			int rangeEnd) {

		int interval = synthesizer.interval();

		Range<Integer> range = Range.between(0, rangeEnd - 1);

		if (interval <= 0 || !range.contains(ticks)) {
			return -1;
		}

		// On a frame that should not be sent
		if (intervalSupport && ticks % interval != 0) {
			return null;
		}

		int frame = (int) ticks / interval;

		return synthesizer.reversed() ? frames - frame : frame;
	}

	public static Integer delayFrame(Logger logger, Synthesizer<?, ?> synthesizer, boolean intervalSupport, int frames,
			int ticks, int rangeEnd) {

		Range<Integer> range = Range.between(0, rangeEnd - 1);

		if (!range.contains(ticks)) {
			logger.debug("delayFrame = -1, outOfRange = " + range);
			return -1;
		}

		if (intervalSupport) {
			return null;
		}

		int frame = synthesizer.reversed() ? 0 : (frames - 1);

		logger.debug("delayFrame = " + frame + " reversed = " + synthesizer.reversed());

		return frame;

	}

}
