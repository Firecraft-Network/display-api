package net.driftverse.dispatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import net.driftverse.dispatch.api.Synthesizer;

final class SynthesizerImpl<S extends Synthesizer<S, Frame>, Frame> implements Synthesizer<S, Frame> {

	/**
	 * I sincerely hope you don't fuck this up - Tyler Frydenlund
	 * 
	 * Edit by Layton Romine: Spelling
	 * 
	 */

	private static final long serialVersionUID = -6929614682548836365L;

	private final int seed = new Random().nextInt();
	private final long sync = System.currentTimeMillis();

	private final Synthesizer<S, Frame> implemented;
	private final boolean intervalSupport;

	private final Class<Frame> frame;
	private final int delay, interval, cycles, cycleDelay, finalDelay, cummulativeTicks;
	private final boolean reversed, shuffled, synced, infinite;

	// Integer represents master tick a cycle completed on, this list changes when
	// synthesize or skip is
	// called
	private final List<Integer> completedCycles = new ArrayList<>();

	private final List<Frame> cummulativeFrames;

	private int ticks = -1;
	private int cycleLength, frames;

	public SynthesizerImpl(boolean intervalSupport, Synthesizer<S, Frame> synthesizer) {
		this.intervalSupport = intervalSupport;
		this.implemented = synthesizer;
		this.frame = synthesizer.frame();

		this.reversed = synthesizer.reversed();
		this.shuffled = synthesizer.shuffled();
		this.synced = synthesizer.synced();

		this.delay = !reversed ? synthesizer.delay() : synthesizer.finalDelay();
		this.interval = synthesizer.interval();
		this.cycles = synthesizer.cycles();
		this.cycleDelay = synthesizer.cycleDelay();
		this.finalDelay = !reversed ? synthesizer.finalDelay() : synthesizer.delay();

		this.infinite = delay < 0 || cycles <= 0 || finalDelay < 0;

		List<Frame> cummulativeFrames = synthesizer.cumulativeSynthesis();

		this.cummulativeFrames = cummulativeFrames == null ? List.of() : cummulativeFrames;

		this.cummulativeTicks = cummulativeFrames.size() * interval;

		updateCycle();

	}

	public SynthesizerImpl<S, Frame> self() {
		return this;
	}

	public boolean intervalSupport() {
		return intervalSupport;
	}

	public Synthesizer<S, Frame> implemented() {
		return implemented;
	}

	public int seed() {
		return seed;
	}

	void updateCycle() {
		frames = frames();
		cycleLength = frames * interval + cycleDelay;
	}

	public Frame synthesize() {
		ticks = synced ? (int) ((System.currentTimeMillis() - sync) / 50) : ++ticks;

		int remainingDelay = Math.max(0, delay - ticks);

		/**
		 * The put method is used here so Styx DispatcherImpl knows the difference
		 * between a cycle delay and initial delay. This is useful for Dispatchers with
		 * interval support.
		 **/
		if (remainingDelay > 0) {
			System.out.println("Delay");
			return intervalSupport() ? null : randomSynthesis(0);
		}

		if (!cummulativeFrames.isEmpty() && ticks < cummulativeTicks + delay) {

			System.out.println("Cummulative");
			return intervalSupport() && (ticks - delay) % interval != 0 ? null
					: cummulativeFrames.get((ticks - delay) / interval);
		}

		if (getCycleFrame() == frames) {
			System.out.println("Completed cycle");
			completedCycles.add(ticks);
			updateCycle();
		}

		if (isInCycleDelay()) {

			System.out.println("Cycle Delay");
			/**
			 * If the slot supports intervals, a blank interpolation is returned as a
			 * DispatcherImpl signifier that we are on a delay of some sort.
			 * 
			 * If the slot does NOT support intervals, the final frame/interpolation of the
			 * cycle is returned.
			 **/
			return intervalSupport() ? null : reversed ? randomSynthesis(0) : randomSynthesis(frames - 1);
		}

		/**
		 * Only if the slot supports interval and has a frame that is being repeated.
		 */
		if (intervalSupport && getCycleFrame() % interval != 0) {
			/**
			 * If the slot supports intervals, a blank interpolation is returned as a Styx
			 * DispatcherImpl signifier that we are on a delay of some sort.
			 * 
			 **/

			System.out.println("Null frame (interval support)");
			return null;
		}

		if (isInFinalDelay()) {

			System.out.println("Final Delay");
			int interpolation = getFinalDelayInterpolation();

			/**
			 * If the the interpolation is -1 we have nothing more to dispatch so return
			 * null to tell the Styx DispatcherImpl we are complete;
			 */
			return interpolation == -1 ? null : randomSynthesis(interpolation);
		}

		/**
		 * If this part of the code is reached, we are not on a delay of any sort and
		 * the interpolation should be calculated out.
		 */

		int interpolation = getInterpolation();

		System.out.println("Normal Frame");
		return randomSynthesis(interpolation);

	}

	public int getCycleFrame() {

		/**
		 * Remove the initial delay, then take the modulus to find what tick // you are
		 * on in the current cycle. If the cycle length is 10, // you can be between 0 &
		 * 9 ticks
		 **/

		return ((ticks - delay - cummulativeTicks) % cycleLength);
	}

	public int cycleTick() {
		if (completedCycles.isEmpty()) {
			return ticks;
		}

		return (int) ((ticks - completedCycles.get(completedCycles.size() - 1)));
	}

	public int getInterpolation() {

		/**
		 * If the we are still in a delay, return -1 to signify there is no
		 * interpolation to send
		 **/
		if (ticks - delay - cummulativeTicks < 0) {
			return -1;
		}

		if (shuffled) {
			return getRandomInterpolation(ticks);
		}

		/**
		 * Remove the delay from the ticks, make ticks relative to 1 cycle. Divide by
		 * the interval to find the interpolation.
		 */

		int interpolation = (((ticks - delay - cummulativeTicks) % cycleLength) / interval) % frames;

		return reversed ? frames - interpolation : interpolation; // When reversed, we
																	// must increment
		// backwards
	}

	public int getRandomInterpolation(int ticks) {

		/**
		 * Some random math I found online, this worked during the tests. Idk why this
		 * works, but it does.
		 */
		double random = 2920.f * Math.sin(seed * 21942.f + ticks * 171324.f + 8912.f)
				* Math.cos(seed * 23157.f * ticks * 217832.f + 9758.f);

		return (int) Math.abs(random % frames);

	}

	public boolean isInCycleDelay() {
		/**
		 * Remove the delay, make the ticks relative to a single cycle. Then divide by
		 * the length of 1 cycle length and remove the cycle delay
		 **/

		return (((ticks - delay - cummulativeTicks) % cycleLength) / (cycleLength - cycleDelay)) >= 1;
	}

	public boolean isInFinalDelay() {
		/**
		 * If infinite we will never reach the final delay.
		 * 
		 * If NOT infinite, any tick greater than the total ticks - final delay is a
		 * final frame
		 */

		return !infinite && completedCycles.size() == cycles;
	}

	public int getFinalDelayInterpolation() {

		/**
		 * If the slot has interval support, do not send the final frame again and
		 * again.
		 */

		if (intervalSupport) {

			long endTime = completedCycles.isEmpty() ? 0 : finalDelay + completedCycles.get(completedCycles.size() - 1);

			return ticks == endTime ? reversed ? 0 : frames - 1 : -1;
		}

		if (shuffled) {
			/* Some "arbitrary" number */
			return getRandomInterpolation(69 + 420 + 666) % frames;
		}

		return reversed ? 0 : frames - 1; // When reversed the first frame
		// should be used as the last frame.
	}

	public boolean isComplete() {

		int lastCompleted = completedCycles.isEmpty() ? 0 : completedCycles.get(completedCycles.size() - 1);

		/** If infinite this can never be complete **/
		return !infinite && completedCycles.size() == cycles && ticks - lastCompleted > finalDelay - 1;
	}

	public void skip(int ticks) {
		if (!synced && !isComplete()) {

			this.ticks += ticks;

			ticks = completedCycles.isEmpty() ? this.ticks
					: this.ticks - completedCycles.get(completedCycles.size() - 1);

			int skippedCycles = ticks / cycleLength;

			System.out.println("Skipped Cycles: " + skippedCycles);
			for (int i = 0; i < skippedCycles; ++i) {
				completedCycles.add(this.ticks + i * cycleLength);
			}

			completedCycles.removeIf(c -> c > this.ticks); // Remove completed cycles when skipping backwards

		}
	}

	public int ticks() {
		return (int) (synced ? ((System.currentTimeMillis() - sync) / 50) : ticks);
	}

	public int cummulativeTicks() {
		return cummulativeTicks;
	}

	public boolean infinite() {
		return infinite;
	}

	@Override
	public Logger logger() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public S reverse(boolean reverse) {
		throw new UnsupportedOperationException("Synthesizer already implemented");
	}

	@Override
	public boolean reversed() {
		return reversed;
	}

	@Override
	public S shuffle(boolean shuffle) {
		throw new UnsupportedOperationException("Synthesizer already implemented");
	}

	@Override
	public boolean shuffled() {
		return shuffled;
	}

	@Override
	public S delay(int delay) {
		throw new UnsupportedOperationException("Synthesizer already implemented");
	}

	@Override
	public int delay() {
		return delay;
	}

	@Override
	public S interval(int interval) {
		throw new UnsupportedOperationException("Synthesizer already implemented");
	}

	@Override
	public int interval() {
		return interval;
	}

	@Override
	public S cycles(int cycles) {
		throw new UnsupportedOperationException("Synthesizer already implemented");
	}

	@Override
	public int cycles() {
		return cycles;
	}

	@Override
	public S cycleDelay(int cycleDelay) {
		throw new UnsupportedOperationException("Synthesizer already implemented");
	}

	@Override
	public int cycleDelay() {
		return cycleDelay;
	}

	@Override
	public S finalDelay(int finalDelay) {
		throw new UnsupportedOperationException("Synthesizer already implemented");
	}

	@Override
	public int finalDelay() {
		return finalDelay;
	}

	@Override
	public Class<Frame> frame() {
		return frame;
	}

	@Override
	public int frames() {
		return implemented.frames();
	}

	@Override
	public List<Frame> cumulativeSynthesis() {
		return implemented.cumulativeSynthesis();
	}

	@Override
	public Frame randomSynthesis(int frame) {
		return implemented.randomSynthesis(frame);
	}

	@Override
	public S sync(boolean sync) {
		throw new UnsupportedOperationException("Synthesizer already implemented");
	}

	long syncTime() {
		return sync;
	}

	@Override
	public boolean synced() {
		return synced;
	}

}
