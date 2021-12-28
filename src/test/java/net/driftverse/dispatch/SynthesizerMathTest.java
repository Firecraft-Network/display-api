package net.driftverse.dispatch;

import java.util.stream.IntStream;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;

import junit.framework.Assert;

public class SynthesizerMathTest {

	int frames = 10;

	int interval = 2;
	int cycles = 5;

	int delay = 10;
	int cycleDelay = 5;
	int finalDelay = 15;

	int totalTicks = (frames * interval) + delay + finalDelay + (cycleDelay * (cycles - 1))
			+ (cycles * interval * frames);

	Logger logger = Logger.getRootLogger();

	{
		logger.setLevel(Level.OFF);
	}

	NumSyn syn() {
		return new NumSyn(frames).interval(interval).cycles(cycles).delay(delay).cycleDelay(cycleDelay)
				.finalDelay(finalDelay);
	}

	@Test
	public void delay() {

		IntStream.range(0, delay)
				.forEach(i -> Assert.assertEquals("Delay test Without Interval support was wrong frame", 0,
						Util.delayFrame(logger, syn(), false, frames, i).intValue()));

		IntStream.range(0, delay)
				.forEach(i -> Assert.assertEquals("Delay test Interval support frame should have been null", null,
						Util.delayFrame(logger, syn(), true, frames, i)));

		IntStream.range(0, delay).forEach(i -> Assert.assertEquals("Delay test in reverse was wrong frame", frames - 1,
				Util.delayFrame(logger, syn().reverse(true), false, frames, i).intValue()));
	}

	@Test
	public void cummulative() {

		int length = frames * interval;

		IntStream.range(0, length)
				.forEach(i -> Assert.assertEquals("Cummulative test Without Interval support was wrong frame",
						i / interval, Util.cummulativeFrame(syn(), false, frames, i).intValue()));

		for (int i = 0; i < frames; ++i) {

			Assert.assertEquals("Cummulative test with interval support was wrong frame", i,
					Util.cummulativeFrame(syn(), true, frames, i * interval).intValue());

			IntStream.range(i * interval + 1, i * interval + interval)
					.forEach(x -> Assert.assertEquals("Cummulative test  Interval frame support should have been null",
							null, Util.cummulativeFrame(syn(), true, frames, x)));
		}

		IntStream.range(0, length).forEach(i -> Assert.assertEquals("Cummulative test in reverse was wrong frame",
				frames - i / interval, Util.cummulativeFrame(syn().reverse(true), false, frames, i).intValue()));
	}

	@Test
	public void cycle() {

		int length = frames * interval;

		IntStream.range(0, length)
				.forEach(i -> Assert.assertEquals("Cycle test Without Interval support was wrong frame", i / interval,
						Util.cycleFrame(syn(), false, frames, i).intValue()));

		for (int i = 0; i < frames; ++i) {

			Assert.assertEquals("Cycle test with interval support was wrong frame", i,
					Util.cycleFrame(syn(), true, frames, i * interval).intValue());

			IntStream.range(i * interval + 1, i * interval + interval)
					.forEach(x -> Assert.assertEquals("Cycle test  Interval frame support should have been null", null,
							Util.cycleFrame(syn(), true, frames, x)));
		}

		IntStream.range(0, length).forEach(i -> Assert.assertEquals("Cummulative test in reverse was wrong frame",
				frames - i / interval, Util.cycleFrame(syn().reverse(true), false, frames, i).intValue()));

	}

	@Test
	public void cycleDelay() {

		IntStream.range(0, cycleDelay)
				.forEach(i -> Assert.assertEquals("Cycle Delay test Without Interval support was wrong frame",
						frames - 1, Util.cycleDelayFrame(logger, syn(), false, frames, i).intValue()));

		IntStream.range(0, cycleDelay)
				.forEach(i -> Assert.assertEquals("Cycle Delay test Interval support frame should have been null", null,
						Util.cycleDelayFrame(logger, syn(), true, frames, i)));

		IntStream.range(0, cycleDelay).forEach(i -> Assert.assertEquals("Cycle Delay test in reverse was wrong frame",
				0, Util.cycleDelayFrame(logger, syn().reverse(true), false, frames, i).intValue()));
	}

	@Test
	public void finalDelay() {

		IntStream.range(0, finalDelay)
				.forEach(i -> Assert.assertEquals("Delay test Without Interval support was wrong frame", frames - 1,
						Util.finalDelayFrame(logger, syn(), false, frames, i).intValue()));

		IntStream.range(0, finalDelay)
				.forEach(i -> Assert.assertEquals("Delay test Interval support frame should have been null", null,
						Util.finalDelayFrame(logger, syn(), true, frames, i)));

		IntStream.range(0, finalDelay).forEach(i -> Assert.assertEquals("Delay test in reverse was wrong frame", 0,
				Util.finalDelayFrame(logger, syn().reverse(true), false, frames, i).intValue()));
	}

}
