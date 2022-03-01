package net.driftverse.dispatch;

import java.util.List;
import java.util.stream.IntStream;

import org.junit.Assert;
import org.junit.Test;

import display.api.Buffer;
import display.api.Stage;
import display.api.Timings;

public class BufferIntegrationTest {

	int frames = 10;
	int interval = 2;
	int delay = 10;
	int cycleDelay = 15;
	int finalDelay = 5;
	int cycles = 2;

	Timings timings = Timings.of().interval(interval).cycles(cycles).delay(delay).cycleDelay(cycleDelay)
			.finalDelay(finalDelay).build();

	@SuppressWarnings({ "unchecked", "rawtypes" })
	Buffer<Integer, Integer, Integer> buffer(boolean intervalSupport) {
		return (Buffer) Buffer.builder().slot(0).animator(new NumAnimator()).animations(List.of(frames))
				.timings(timings).intervalSupport(intervalSupport).build();
	}

	@Test
	public void integrateDelay() {

		Buffer<Integer, Integer, Integer> unsupported = buffer(false);
		Buffer<Integer, Integer, Integer> supported = buffer(true);

		unsupported.stage(Stage.DELAY);

		Assert.assertEquals("UnSupported not in correct stage", Stage.DELAY, unsupported.stage());

		IntStream.range(0, delay).forEach(i -> Assert.assertEquals("Excpected  frame for unsupported intervals", 0,
				unsupported.poll().intValue()));

		Assert.assertEquals("UnSupported not in correct stage", Stage.CYCLE, unsupported.stage());

		supported.stage(Stage.DELAY);

		Assert.assertEquals("Supported not in correct stage", Stage.DELAY, supported.stage());

		IntStream.range(0, delay)
				.forEach(i -> Assert.assertEquals("Excpected null frame supported intervals", null, supported.poll()));

		Assert.assertEquals("Supported not in correct stage", Stage.CYCLE, supported.stage());

	}

//	@Test
//	public void integrateCummulative() {
//		unsupported.stage(Stage.CUMMULATIVE);
//
//		Assert.assertEquals("UnSupported not in correct stage", Stage.CUMMULATIVE, unsupported.stage());
//
//		IntStream.range(0, frames * interval)
//				.forEach(i -> Assert.assertEquals("Excpected  frame for unsupported intervals", i / interval,
//						unsupported.buffer().intValue()));
//
//		Assert.assertEquals("UnSupported not in correct stage", Stage.CYCLE, unsupported.stage());
//
//		supported.stage(Stage.CUMMULATIVE);
//
//		Assert.assertEquals("Supported not in correct stage", Stage.CUMMULATIVE, supported.stage());
//
//		IntStream.range(0, frames * interval)
//				.forEach(i -> Assert.assertEquals("Excpected frame with supported intervals",
//						i % interval == 0 ? i / interval : null, supported.buffer()));
//
//		Assert.assertEquals("Supported not in correct stage", Stage.CYCLE, supported.stage());
//	}

	@Test
	public void integrateCycles() {
		Buffer<Integer, Integer, Integer> unsupported = buffer(false);
		Buffer<Integer, Integer, Integer> supported = buffer(true);
		// FIRST CYCLE

		unsupported.stage(Stage.CYCLE);
		Assert.assertEquals("UnSupported not in correct stage", Stage.CYCLE, unsupported.stage());

		IntStream.range(0, frames * interval)
				.forEach(i -> Assert.assertEquals("Excpected frame for unsupported intervals", i / interval,
						unsupported.poll().intValue()));

		Assert.assertEquals("UnSupported not in correct stage", Stage.CYCLE_DELAY, unsupported.stage());

		supported.stage(Stage.CYCLE);

		Assert.assertEquals("Supported not in correct stage", Stage.CYCLE, supported.stage());

		IntStream.range(0, frames * interval)
				.forEach(i -> Assert.assertEquals("Excpected frame with supported intervals",
						i % interval == 0 ? i / interval : null, supported.poll()));

		Assert.assertEquals("Supported not in correct stage", Stage.CYCLE_DELAY, supported.stage());

		// SECOND CYCLE

		unsupported.stage(Stage.CYCLE);

		Assert.assertEquals("UnSupported not in correct stage", Stage.CYCLE, unsupported.stage());

		IntStream.range(0, frames * interval)
				.forEach(i -> Assert.assertEquals("Excpected frame for unsupported intervals", i / interval,
						unsupported.poll().intValue()));

		Assert.assertEquals("UnSupported not in correct stage", Stage.FINAL_DELAY, unsupported.stage());

		supported.stage(Stage.CYCLE);

		Assert.assertEquals("Supported not in correct stage", Stage.CYCLE, supported.stage());

		IntStream.range(0, frames * interval)
				.forEach(i -> Assert.assertEquals("Excpected frame with supported intervals",
						i % interval == 0 ? i / interval : null, supported.poll()));

		Assert.assertEquals("Supported not in correct stage", Stage.FINAL_DELAY, supported.stage());
	}

	@Test
	public void integrateCycleDelay() {

		Buffer<Integer, Integer, Integer> unsupported = buffer(false);
		Buffer<Integer, Integer, Integer> supported = buffer(true);

		unsupported.stage(Stage.CYCLE_DELAY);

		Assert.assertEquals("UnSupported not in correct stage", Stage.CYCLE_DELAY, unsupported.stage());

		IntStream.range(0, cycleDelay).forEach(i -> Assert.assertEquals("Excpected frame for unsupported intervals",
				frames - 1, unsupported.poll().intValue()));

		Assert.assertEquals("UnSupported not in correct stage", Stage.CYCLE, unsupported.stage());

		supported.stage(Stage.CYCLE_DELAY);

		Assert.assertEquals("Supported not in correct stage", Stage.CYCLE_DELAY, supported.stage());

		IntStream.range(0, cycleDelay)
				.forEach(i -> Assert.assertEquals("Excpected frame with supported intervals", null, supported.poll()));

		Assert.assertEquals("Supported not in correct stage", Stage.CYCLE, supported.stage());
	}

	@Test
	public void integrateFinalDelay() {

		Buffer<Integer, Integer, Integer> unsupported = buffer(false);
		Buffer<Integer, Integer, Integer> supported = buffer(true);

		unsupported.stage(Stage.FINAL_DELAY);

		Assert.assertEquals("UnSupported not in correct stage", Stage.FINAL_DELAY, unsupported.stage());

		IntStream.range(0, finalDelay).forEach(i -> Assert.assertEquals("Excpected frame for unsupported intervals",
				frames - 1, unsupported.poll().intValue()));

		Assert.assertEquals("UnSupported not in correct stage", Stage.DESTROY, unsupported.stage());

		supported.stage(Stage.FINAL_DELAY);

		Assert.assertEquals("Supported not in correct stage", Stage.FINAL_DELAY, supported.stage());

		IntStream.range(0, finalDelay)
				.forEach(i -> Assert.assertEquals("Excpected frame with supported intervals", null, supported.poll()));

		Assert.assertEquals("Supported not in correct stage", Stage.DESTROY, supported.stage());
	}

	@Test
	public void integrateCompletion() {

		Buffer<Integer, Integer, Integer> unsupported = buffer(false);
		Buffer<Integer, Integer, Integer> supported = buffer(true);

		unsupported.stage(Stage.DESTROY);

		Assert.assertEquals("UnSupported not in correct stage", Stage.DESTROY, unsupported.stage());

		supported.stage(Stage.DESTROY);

		Assert.assertEquals("Supported not in correct stage", Stage.DESTROY, supported.stage());
	}
}
