package net.driftverse.dispatch;

import java.util.List;
import java.util.stream.IntStream;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;

import hyleo.animations.Util;
import hyleo.animations.api.AnimationInfo;
import hyleo.animations.api.Buffer;
import junit.framework.Assert;

public class BufferMathTest {

	int frames = 10;

	int interval = 2;
	int cycles = 5;

	int delay = 10;
	int cycleDelay = 5;
	int finalDelay = 15;

	int totalTicks = delay + finalDelay + (cycleDelay * (cycles - 1)) + (cycles * interval * frames);

	Logger logger = Logger.getRootLogger();

	{
		logger.setLevel(Level.OFF);
	}

	AnimationInfo info = AnimationInfo.builder().interval(interval).cycles(cycles).delay(delay).cycleDelay(cycleDelay)
			.finalDelay(finalDelay).build();

	AnimationInfo infoReverse = AnimationInfo.builder().interval(interval).cycles(cycles).delay(delay)
			.cycleDelay(cycleDelay).finalDelay(finalDelay).reversed(true).build();

	@SuppressWarnings({ "unchecked", "rawtypes" })
	Buffer<Integer, Integer, Integer> buffer(AnimationInfo info, Integer frames, boolean intervalSupport) {
		return (Buffer) Buffer.builder().slot(0).animator(new NumAnimator()).animations(List.of(frames)).info(info)
				.coroperator((f) -> f.get(0)).intervalSupport(intervalSupport).build();
	}

	@Test
	public void delay() {

		IntStream.range(0, delay)
				.forEach(i -> Assert.assertEquals("Delay test Without Interval support was wrong frame", 0,
						Util.delayFrame(logger, info, false, frames, i).intValue()));

		IntStream.range(0, delay)
				.forEach(i -> Assert.assertEquals("Delay test Interval support frame should have been null", null,
						Util.delayFrame(logger, info, true, frames, i)));

		IntStream.range(0, delay).forEach(i -> Assert.assertEquals("Delay test in reverse was wrong frame", frames - 1,
				Util.delayFrame(logger, infoReverse, false, frames, i).intValue()));
	}

//	@Test
//	public void cummulative() {
//
//		int length = frames * interval;
//
//		IntStream.range(0, length)
//				.forEach(i -> Assert.assertEquals("Cummulative test Without Interval support was wrong frame",
//						i / interval, Util.cummulativeFrame(animator(), false, frames, i).intValue()));
//
//		for (int i = 0; i < frames; ++i) {
//
//			Assert.assertEquals("Cummulative test with interval support was wrong frame", i,
//					Util.cummulativeFrame(animator(), true, frames, i * interval).intValue());
//
//			IntStream.range(i * interval + 1, i * interval + interval)
//					.forEach(x -> Assert.assertEquals("Cummulative test  Interval frame support should have been null",
//							null, Util.cummulativeFrame(animator(), true, frames, x)));
//		}
//
//		IntStream.range(0, length).forEach(i -> Assert.assertEquals("Cummulative test in reverse was wrong frame",
//				frames - i / interval, Util.cummulativeFrame(animator().reverse(true), false, frames, i).intValue()));
//	}

	@Test
	public void cycle() {

		int length = frames * interval;

		IntStream.range(0, length)
				.forEach(i -> Assert.assertEquals("Cycle test Without Interval support was wrong frame", i / interval,
						Util.cycleFrame(info, false, frames, i).intValue()));

		for (int i = 0; i < frames; ++i) {

			Assert.assertEquals("Cycle test with interval support was wrong frame", i,
					Util.cycleFrame(info, true, frames, i * interval).intValue());

			IntStream.range(i * interval + 1, i * interval + interval)
					.forEach(x -> Assert.assertEquals("Cycle test  Interval frame support should have been null", null,
							Util.cycleFrame(info, true, frames, x)));
		}

		IntStream.range(0, length).forEach(i -> Assert.assertEquals("Cummulative test in reverse was wrong frame",
				frames - i / interval, Util.cycleFrame(infoReverse, false, frames, i).intValue()));

	}

	@Test
	public void cycleDelay() {

		IntStream.range(0, cycleDelay)
				.forEach(i -> Assert.assertEquals("Cycle Delay test Without Interval support was wrong frame",
						frames - 1, Util.cycleDelayFrame(logger, info, false, frames, i).intValue()));

		IntStream.range(0, cycleDelay)
				.forEach(i -> Assert.assertEquals("Cycle Delay test Interval support frame should have been null", null,
						Util.cycleDelayFrame(logger, info, true, frames, i)));

		IntStream.range(0, cycleDelay).forEach(i -> Assert.assertEquals("Cycle Delay test in reverse was wrong frame",
				0, Util.cycleDelayFrame(logger, infoReverse, false, frames, i).intValue()));
	}

	@Test
	public void finalDelay() {

		IntStream.range(0, finalDelay)
				.forEach(i -> Assert.assertEquals("Delay test Without Interval support was wrong frame", frames - 1,
						Util.finalDelayFrame(logger, info, false, frames, i).intValue()));

		IntStream.range(0, finalDelay)
				.forEach(i -> Assert.assertEquals("Delay test Interval support frame should have been null", null,
						Util.finalDelayFrame(logger, info, true, frames, i)));

		IntStream.range(0, finalDelay).forEach(i -> Assert.assertEquals("Delay test in reverse was wrong frame", 0,
				Util.finalDelayFrame(logger, infoReverse, false, frames, i).intValue()));
	}

}
