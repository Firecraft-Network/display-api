package net.driftverse.dispatch;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.junit.Assert;
import org.junit.Test;

public class SynthesizerHybridTest {

	/*
	 * Write Cumulative tests
	 * 
	 * Write Random Synthesis tests
	 * 
	 * Update cycle length at the begining of every cycle
	 * 
	 */

	int frames = 10;
	int delay = 10;
	int cycles = 2;
	int interval = 3;
	int finalDelay = 10;
	int cycleDelay = 5;

	public NumSyn numSyn() {

		/* Null Puts NumSyn into Hybrid mode */
		return new NumSyn(frames, null).delay(delay).cycles(cycles).interval(interval).finalDelay(finalDelay)
				.cycleDelay(cycleDelay);

	}

	// @Test
	public void implement() {
		new SynthesizerImpl<>(false, numSyn());
	}

	// @Test(timeout = 500)
	public void sync() throws InterruptedException {

		SynthesizerImpl<NumSyn, Integer> synthesizer = new SynthesizerImpl<>(false, numSyn().reverse(true).sync(true));

		Thread.sleep(200); // Wait to get the frame

		Assert.assertTrue("After waiting, a synced synthesizer should not be at 0 ticks", synthesizer.ticks() != 0);

	}

	// @Test
	public void reversed() {
		SynthesizerImpl<NumSyn, Integer> synthesizer = new SynthesizerImpl<>(false, numSyn().reverse(true));

		/* Delay and Final delay should be swapped when reversed */
		Assert.assertEquals("Wrong delay", finalDelay, synthesizer.delay());
		Assert.assertEquals("Wrong final delay", delay, synthesizer.finalDelay());

	}

	// @Test
	public void lengthMath() {
		SynthesizerImpl<NumSyn, Integer> synthesizer = new SynthesizerImpl<>(false, numSyn());

		Validate.isTrue(!synthesizer.infinite(), "Was infinite when it shouldnt have been");

		SynthesizerImpl<NumSyn, Integer> synthesizer2 = new SynthesizerImpl<>(false, numSyn().cycles(0));
		Validate.isTrue(synthesizer2.infinite(), "Was infinite when it should have been");
	}

//	@Test(timeout = 200)
	public void delay() {
		SynthesizerImpl<NumSyn, Integer> synthesizer = new SynthesizerImpl<>(false, numSyn());

		/* Skip the cumulative part */
		synthesizer.skip(frames * interval);

		for (int i = 0; i < delay; ++i) {
			Assert.assertSame("Was not first frame", 0, synthesizer.synthesize());
		}

		SynthesizerImpl<NumSyn, Integer> synthesizer2 = new SynthesizerImpl<>(true, numSyn());

		/* Skip the cumulative part */
		synthesizer2.skip(frames * interval);

		for (int i = 0; i < delay; ++i) {
			Validate.isTrue(synthesizer2.synthesize() == null, "Excpected delay frame to be null");
		}

	}

//	@Test(timeout = 200)
	public void cummulative() {
		SynthesizerImpl<NumSyn, Integer> synthesizer = new SynthesizerImpl<>(false, numSyn());

		Assert.assertEquals("Wrong cumulative ticks", synthesizer.cummulativeTicks(), frames * interval);

		synthesizer.skip(delay);

		for (int i = 0; i < frames * interval; ++i) {
			int frame = synthesizer.synthesize();

			// System.out.println("Cummulative Test frame - interval support: " + frame);
			Assert.assertEquals("Failed", i / interval, frame);

		}

		SynthesizerImpl<NumSyn, Integer> synthesizer2 = new SynthesizerImpl<>(true, numSyn());

		synthesizer2.skip(delay);

		for (int i = 0; i < frames; ++i) {

			int frame = synthesizer2.synthesize();

			// System.out.println("Cummulative Test frame - interval support: " + frame);
			Assert.assertEquals("Failed", i, frame);

			for (int x = 0; x < interval - 1; ++x) {
				Integer intervalFrame = synthesizer2.synthesize();
				Validate.isTrue(intervalFrame == null,
						"Excpected null frame on interval gap while using interval support");
			}

		}
	}

	@Test(timeout = 200)
	public void cycle() {
		SynthesizerImpl<NumSyn, Integer> synthesizer = new SynthesizerImpl<>(false, numSyn());

		/* Skip the delay */
		synthesizer.skip(delay);

		/* Skip the cumulative part */
		synthesizer.skip(frames * interval);

		for (int i = 0; i < frames * interval; ++i) {
			int frame = synthesizer.synthesize();

			Assert.assertEquals("Failed", i / interval, frame);

		}

		SynthesizerImpl<NumSyn, Integer> synthesizer2 = new SynthesizerImpl<>(true, numSyn());

		/* Skip the cumulative part */
		synthesizer2.skip(frames * interval);

		/* Skip the delay */
		synthesizer2.skip(delay);

		for (int i = 0; i < frames; ++i) {
			int frame = synthesizer2.synthesize();

			// System.out.println("Cummulative Test frame - interval support: " + frame);
			Assert.assertEquals("Failed on interval support", i, frame);

			for (int x = 0; x < interval - 1; ++x) {
				Integer intervalFrame = synthesizer2.synthesize();
				Validate.isTrue(intervalFrame == null,
						"Excpected null frame on interval gap while using interval support");
			}
		}
	}

	@Test(timeout = 200)
	public void cycleDelay() {
		SynthesizerImpl<NumSyn, Integer> synthesizer = new SynthesizerImpl<>(false, numSyn());

		/* Skip the cumulative part */
		synthesizer.skip(frames * interval);

		/* Skip the delay part */
		synthesizer.skip(delay);

		/* Skip the first cycle part */
		synthesizer.skip(frames * interval);

		for (int i = 0; i < cycleDelay; ++i) {
			Assert.assertSame("Failed excpeted final frame during cycle delay", frames - 1, synthesizer.synthesize());
		}

		SynthesizerImpl<NumSyn, Integer> synthesizer2 = new SynthesizerImpl<>(true, numSyn());

		/* Skip the cumulative part */
		synthesizer2.skip(frames * interval);

		/* Skip the delay part */
		synthesizer2.skip(delay);

		/* Skip the first cycle part */
		synthesizer2.skip(frames * interval);

		for (int i = 0; i < cycleDelay; ++i) {
			Validate.isTrue(synthesizer2.synthesize() == null, "Excpected cycled delay frame to be null");
		}
	}

	@Test(timeout = 200)
	public void finalDelay() {
		SynthesizerImpl<NumSyn, Integer> synthesizer = new SynthesizerImpl<>(false, numSyn());

		/* Skip the delay part */
		synthesizer.skip(delay);

		/* Skip the cumulative part */
		synthesizer.skip(frames * interval);

		/* Skip all cycles */
		synthesizer.skip(frames * interval * cycles);

		/*
		 * Skip all the cycle delays (-1 because final cycle does not have a cycle
		 * delay)
		 */
		synthesizer.skip(((cycles - 1) * cycleDelay));

		for (int i = 0; i < finalDelay; ++i) {
			Assert.assertSame("Failed excpeted final frame during final delay", frames - 1, synthesizer.synthesize());
		}

		System.out.println("---------------");

		SynthesizerImpl<NumSyn, Integer> synthesizer2 = new SynthesizerImpl<>(true, numSyn());

		/* Skip the cumulative part */
		synthesizer2.skip(frames * interval);

		/* Skip the delay part */
		synthesizer2.skip(delay);

		/* Skip all cycles */
		synthesizer2.skip(frames * interval * cycles);

		/*
		 * Skip all the cycle delays (-1 because final cycle does not have a cycle
		 * delay)
		 */
		synthesizer2.skip(((cycles - 1) * cycleDelay));

		for (int i = 0; i < finalDelay; ++i) {
			Validate.isTrue(synthesizer2.synthesize() == null, "Excpected final delay frame to be null");
		}
	}

	@Test(timeout = 200)
	public void complete() {
		SynthesizerImpl<NumSyn, Integer> synthesizer = new SynthesizerImpl<>(false, numSyn());

		/* Skip the cumulative part */
		synthesizer.skip(frames * interval);

		/* Skip the delay part */
		synthesizer.skip(delay);

		/* Skip all cycles */
		synthesizer.skip(frames * interval * cycles);

		/*
		 * Skip all the cycle delays (-1 because final cycle does not have a cycle
		 * delay)
		 */
		synthesizer.skip(((cycles - 1) * cycleDelay));

		/* Skip the final delay */
		synthesizer.skip(finalDelay);

		Validate.isTrue(synthesizer.isComplete(), "The synthesizer should be complete");

	}

	// @Test(timeout = 200)
	public void shuffle() {
		SynthesizerImpl<NumSyn, Integer> synthesizer = new SynthesizerImpl<>(false, numSyn().shuffle(true));

		List<Integer> frames = new ArrayList<>();

		int populationSize = 20;

		for (int i = 0; i < populationSize; ++i) {
			frames.add(synthesizer.synthesize());
		}

		int orderCount = 0;
		int previous = -2; // Start here to ensure if 1 is added, it does not equal 0 which is possible.
							// Only positive numbers are generated by num syn

		for (int i = 0; i < populationSize; ++i) {

			/* Add 1 to check if it equals the following frame */
			orderCount += previous + 1 == i ? 1 : -orderCount; // increment if true, reset if false

			Assert.assertTrue("Shuffle mode error. Has been in order for more than 5 frames!", orderCount < 5);

		}

	}
}
