package net.driftverse.dispatch;

import java.util.stream.IntStream;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import net.driftverse.dispatch.SynthesizerImpl.Stage;

public class SynthesizerIntegrationTest {

	int frames = 10;
	int interval = 2;
	int delay = 10;
	int cycleDelay = 15;
	int finalDelay = 5;
	int cycles = 2;

	NumSyn syn = new NumSyn(frames).interval(interval).delay(delay).cycleDelay(cycleDelay).finalDelay(finalDelay)
			.cycles(cycles);

	Logger unsupportedLogger = Logger.getLogger("Synthesizer");
	Logger supportedLogger = Logger.getLogger("Interval Synthesizer");

	{
		unsupportedLogger.setLevel(Level.OFF);
		supportedLogger.setLevel(Level.OFF);
	}

	SynthesizerImpl<NumSyn, Integer> unsupported = new SynthesizerImpl<>(unsupportedLogger, false, syn);
	SynthesizerImpl<NumSyn, Integer> supported = new SynthesizerImpl<>(supportedLogger, true, syn);

	@Test
	public void integrateDelay() {
		unsupported.stage(Stage.DELAY);

		Assert.assertEquals("UnSupported not in correct stage", Stage.DELAY, unsupported.stage());

		IntStream.range(0, delay).forEach(i -> Assert.assertEquals("Excpected  frame for unsupported intervals", 0,
				unsupported.synthesize().intValue()));

		Assert.assertEquals("UnSupported not in correct stage", Stage.CUMMULATIVE, unsupported.stage());

		supported.stage(Stage.DELAY);

		Assert.assertEquals("Supported not in correct stage", Stage.DELAY, supported.stage());

		IntStream.range(0, delay).forEach(
				i -> Assert.assertEquals("Excpected null frame supported intervals", null, supported.synthesize()));

		Assert.assertEquals("Supported not in correct stage", Stage.CUMMULATIVE, supported.stage());

	}

	@Test
	public void integrateCummulative() {
		unsupported.stage(Stage.CUMMULATIVE);

		Assert.assertEquals("UnSupported not in correct stage", Stage.CUMMULATIVE, unsupported.stage());

		IntStream.range(0, frames * interval)
				.forEach(i -> Assert.assertEquals("Excpected  frame for unsupported intervals", i / interval,
						unsupported.synthesize().intValue()));

		Assert.assertEquals("UnSupported not in correct stage", Stage.CYCLE, unsupported.stage());

		supported.stage(Stage.CUMMULATIVE);

		Assert.assertEquals("Supported not in correct stage", Stage.CUMMULATIVE, supported.stage());

		IntStream.range(0, frames * interval)
				.forEach(i -> Assert.assertEquals("Excpected frame with supported intervals",
						i % interval == 0 ? i / interval : null, supported.synthesize()));

		Assert.assertEquals("Supported not in correct stage", Stage.CYCLE, supported.stage());
	}

	@Test
	public void integrateCycles() {

		// FIRST CYCLE

		unsupported.stage(Stage.CYCLE);
		Assert.assertEquals("UnSupported not in correct stage", Stage.CYCLE, unsupported.stage());

		IntStream.range(0, frames * interval)
				.forEach(i -> Assert.assertEquals("Excpected frame for unsupported intervals", i / interval,
						unsupported.synthesize().intValue()));

		Assert.assertEquals("UnSupported not in correct stage", Stage.CYCLE_DELAY, unsupported.stage());

		supported.stage(Stage.CYCLE);

		Assert.assertEquals("Supported not in correct stage", Stage.CYCLE, supported.stage());

		IntStream.range(0, frames * interval)
				.forEach(i -> Assert.assertEquals("Excpected frame with supported intervals",
						i % interval == 0 ? i / interval : null, supported.synthesize()));

		Assert.assertEquals("Supported not in correct stage", Stage.CYCLE_DELAY, supported.stage());

		// SECOND CYCLE

		unsupported.stage(Stage.CYCLE);

		Assert.assertEquals("UnSupported not in correct stage", Stage.CYCLE, unsupported.stage());

		IntStream.range(0, frames * interval)
				.forEach(i -> Assert.assertEquals("Excpected frame for unsupported intervals", i / interval,
						unsupported.synthesize().intValue()));

		Assert.assertEquals("UnSupported not in correct stage", Stage.FINAL_DELAY, unsupported.stage());

		supported.stage(Stage.CYCLE);

		Assert.assertEquals("Supported not in correct stage", Stage.CYCLE, supported.stage());

		IntStream.range(0, frames * interval)
				.forEach(i -> Assert.assertEquals("Excpected frame with supported intervals",
						i % interval == 0 ? i / interval : null, supported.synthesize()));

		Assert.assertEquals("Supported not in correct stage", Stage.FINAL_DELAY, supported.stage());
	}

	@Test
	public void integrateCycleDelay() {
		unsupported.stage(Stage.CYCLE_DELAY);

		Assert.assertEquals("UnSupported not in correct stage", Stage.CYCLE_DELAY, unsupported.stage());

		IntStream.range(0, cycleDelay).forEach(i -> Assert.assertEquals("Excpected frame for unsupported intervals",
				frames - 1, unsupported.synthesize().intValue()));

		Assert.assertEquals("UnSupported not in correct stage", Stage.CYCLE, unsupported.stage());

		supported.stage(Stage.CYCLE_DELAY);

		Assert.assertEquals("Supported not in correct stage", Stage.CYCLE_DELAY, supported.stage());

		IntStream.range(0, cycleDelay).forEach(
				i -> Assert.assertEquals("Excpected frame with supported intervals", null, supported.synthesize()));

		Assert.assertEquals("Supported not in correct stage", Stage.CYCLE, supported.stage());
	}

	@Test
	public void integrateFinalDelay() {
		unsupported.stage(Stage.FINAL_DELAY);

		Assert.assertEquals("UnSupported not in correct stage", Stage.FINAL_DELAY, unsupported.stage());

		IntStream.range(0, finalDelay).forEach(i -> Assert.assertEquals("Excpected frame for unsupported intervals",
				frames - 1, unsupported.synthesize().intValue()));

		Assert.assertEquals("UnSupported not in correct stage", Stage.COMPLETE, unsupported.stage());

		supported.stage(Stage.FINAL_DELAY);

		Assert.assertEquals("Supported not in correct stage", Stage.FINAL_DELAY, supported.stage());

		IntStream.range(0, finalDelay).forEach(
				i -> Assert.assertEquals("Excpected frame with supported intervals", null, supported.synthesize()));

		Assert.assertEquals("Supported not in correct stage", Stage.COMPLETE, supported.stage());
	}

	@Test
	public void integrateCompletion() {
		unsupported.stage(Stage.COMPLETE);

		Assert.assertEquals("UnSupported not in correct stage", Stage.COMPLETE, unsupported.stage());

		supported.stage(Stage.COMPLETE);

		Assert.assertEquals("Supported not in correct stage", Stage.COMPLETE, supported.stage());
	}
}
