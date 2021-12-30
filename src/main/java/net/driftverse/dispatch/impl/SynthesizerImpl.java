package net.driftverse.dispatch.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.apache.log4j.Logger;

import net.driftverse.dispatch.Util;
import net.driftverse.dispatch.api.Synthesizer;

public final class SynthesizerImpl<S extends Synthesizer<S, Frame>, Frame> implements Synthesizer<S, Frame> {

	/**
	 * I sincerely hope you don't fuck this up - Tyler Frydenlund
	 * 
	 * Edit by Layton Romine: Spelling
	 * 
	 */

	public enum Stage {
		DELAY, CUMMULATIVE, CYCLE, CYCLE_DELAY, FINAL_DELAY, COMPLETE;
	}

	private static final long serialVersionUID = -6929614682548836365L;

	private final int seed = new Random().nextInt();
	private final long sync = System.currentTimeMillis();
	private final Map<UUID, Integer> frames = new HashMap<>();

	private final Logger logger;
	private final Synthesizer<S, Frame> implemented;
	private final boolean intervalSupport;

	private final Class<Frame> frame;
	private final int delay, interval, cycles, cycleDelay, finalDelay;
	private final boolean reversed, shuffled, synced, infinite;

	private final List<Frame> cummulativeFrames;

	private Stage stage = Stage.DELAY;
	private int completedCycles, ticks = -1;

	public SynthesizerImpl(Logger logger, boolean intervalSupport, Synthesizer<S, Frame> synthesizer) {

		this.logger = logger;

		this.intervalSupport = intervalSupport;
		logger.trace("Inverval support = " + intervalSupport);

		this.implemented = synthesizer;

		this.frame = synthesizer.frame();
		logger.trace("Frame type = " + frame.getTypeName());

		this.reversed = synthesizer.reversed();
		logger.trace("Reversed = " + reversed);

		this.shuffled = synthesizer.shuffled();
		logger.trace("Shuffled = " + shuffled);

		this.synced = synthesizer.synced();
		logger.trace("Synced = " + synced);

		this.delay = !reversed ? synthesizer.delay() : synthesizer.finalDelay();
		logger.trace("Delay = " + delay);

		this.interval = synthesizer.interval();
		logger.trace("Interval = " + interval);

		this.cycles = synthesizer.cycles();
		logger.trace("Cycles = " + cycles);

		this.cycleDelay = synthesizer.cycleDelay();
		logger.trace("Cycle Delay = " + cycleDelay);

		this.finalDelay = !reversed ? synthesizer.finalDelay() : synthesizer.delay();
		logger.trace("Final Delay = " + finalDelay);

		this.infinite = delay < 0 || cycles <= 0 || finalDelay < 0;
		logger.trace("Infinite = " + infinite);

		List<Frame> cummulativeFrames = synthesizer.cumulativeSynthesis();

		this.cummulativeFrames = cummulativeFrames == null ? List.of() : cummulativeFrames;
		logger.trace("Cummulative Frames = " + cummulativeFrames.size());

		logger.trace("Current Frames = " + frames);

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

	public Frame synthesize(UUID interpolator) {
		++ticks;

		switch (stage) {
		case COMPLETE:
			return null;
		case CUMMULATIVE:
			return cummulativeSynthesize(interpolator);
		case CYCLE:
			return cycleSynthesize(interpolator);
		case CYCLE_DELAY:
			return cycleDelaySynthesize(interpolator);
		case DELAY:
			return delaySynthesize(interpolator);
		case FINAL_DELAY:
			return finalDelaySynthesize(interpolator);
		default:
			String msg = "Synthesizer Impl has broken due to an unknown error, stage = " + stage;
			logger.error(msg);
			throw new RuntimeException(msg);
		}

	}

	int getFrames(UUID interpolator) {
		return frames.computeIfAbsent(interpolator, i -> frames(interpolator));
	}

	Frame delaySynthesize(UUID interpolator) {
		Integer frame = Util.delayFrame(logger, this, intervalSupport, getFrames(interpolator), ticks);

		Integer nextFrame = Util.delayFrame(logger, this, intervalSupport, getFrames(interpolator), ticks + 1);

		logger.trace("Delay next frame = " + nextFrame);

		if (nextFrame != null && nextFrame == -1) {
			stage(Stage.CUMMULATIVE);
		}

		Frame f = synthesize(interpolator, frame);

		logger.trace("Delay Frame " + f);
		return f;
	}

	Frame cummulativeSynthesize(UUID interpolator) {
		Integer frame = Util.cummulativeFrame(this, intervalSupport, cummulativeFrames.size(), ticks);

		Integer nextFrame = Util.cummulativeFrame(this, intervalSupport, cummulativeFrames.size(), ticks + 1);

		if (nextFrame != null && nextFrame == -1) {
			stage(Stage.CYCLE);
			this.frames.put(interpolator, frames(interpolator));
		}

		return frame != null ? cummulativeFrames.get(shuffled() ? random(frame, cummulativeFrames.size()) : frame)
				: null;
	}

	Frame cycleSynthesize(UUID interpolator) {

		Integer frame = Util.cycleFrame(this, intervalSupport, getFrames(interpolator), ticks);

		Integer nextFrame = Util.cycleFrame(this, intervalSupport, getFrames(interpolator), ticks + 1);

		if (nextFrame != null && nextFrame == -1) {

			++completedCycles;

			if (completedCycles == cycles) {
				stage(Stage.FINAL_DELAY);
			} else {
				stage(Stage.CYCLE_DELAY);
			}

		}
		return synthesize(interpolator, frame);
	}

	Frame cycleDelaySynthesize(UUID interpolator) {

		Integer frame = Util.cycleDelayFrame(logger, this, intervalSupport, getFrames(interpolator), ticks);

		Integer nextFrame = Util.cycleDelayFrame(logger, this, intervalSupport, getFrames(interpolator), ticks + 1);

		if (nextFrame != null && nextFrame == -1) {
			stage(Stage.CYCLE);
			this.frames.put(interpolator, frames(interpolator));
		}

		return synthesize(interpolator, frame);
	}

	Frame finalDelaySynthesize(UUID interpolator) {

		Integer frame = Util.finalDelayFrame(logger, this, intervalSupport, getFrames(interpolator), ticks);

		Integer nextFrame = Util.finalDelayFrame(logger, this, intervalSupport, getFrames(interpolator), ticks + 1);

		if (nextFrame != null && nextFrame == -1) {
			stage(Stage.COMPLETE);
		}

		return synthesize(interpolator, frame);

	}

	Frame synthesize(UUID interpolator, Integer frame) {

		if (frame == null) {
			return null;
		}

		return shuffled() ? randomSynthesis(interpolator, random(frame, getFrames(interpolator)))
				: randomSynthesis(interpolator, frame);

	}

	int random(int frame, int modulus) {

		/**
		 * Some random math I found online, this worked during the tests. Idk why this
		 * works, but it does.
		 */
		double random = 2920.f * Math.sin(seed * 21942.f + ticks * 171324.f + 8912.f)
				* Math.cos(seed * 23157.f * ticks * 217832.f + 9758.f);

		return (int) Math.abs(random % modulus);

	}

	public int ticks() {
		return (int) (synced ? ((System.currentTimeMillis() - sync) / 50) : ticks);
	}

	public boolean infinite() {
		return infinite;
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
	public int frames(UUID generator) {
		return implemented.frames(generator);
	}

	@Override
	public List<Frame> cumulativeSynthesis() {
		return implemented.cumulativeSynthesis();
	}

	@Override
	public Frame randomSynthesis(UUID generator, int frame) {
		return implemented.randomSynthesis(generator, frame);
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

	public void stage(Stage stage) {
		logger.trace("Changing stage from " + this.stage + " -> " + stage);
		this.stage = stage;
		this.ticks = -1;
	}

	public Stage stage() {
		return stage;
	}

	public int completedCycles() {
		return this.completedCycles;
	}
}
