package net.driftverse.dispatch.api;

import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.Validate;

import net.driftverse.dispatch.api.enums.Timing;

@SuppressWarnings("unchecked")
public abstract class AbstractSynthesizer<S extends Synthesizer<S, Frame>, Frame> implements Synthesizer<S, Frame> {

	private static final long serialVersionUID = -5946120906336712286L;

	private int delay, interval = 1, cycles, cycleDelay, finalDelay;
	private boolean reversed, shuffled, synced;

	@Override
	public final S reverse(boolean reverse) {
		this.reversed = reverse;
		return (S) this;
	}

	@Override
	public final boolean reversed() {
		return reversed;
	}

	@Override
	public final S shuffle(boolean shuffle) {
		this.shuffled = shuffle;
		return (S) this;
	}

	@Override
	public final boolean shuffled() {
		return shuffled;
	}

	int validateTiming(Timing timing, int time) {

		Validate.notNull(timing, "A timing value can not be null");

		Range<Integer> range = timing.getRange();

		if (!range.contains(time)) {
			time = timing.getDefaultValue();
		}

		return time;

	}

	@Override
	public final S delay(int delay) {
		this.delay = validateTiming(Timing.DELAY, delay);
		return (S) this;
	}

	@Override
	public final int delay() {
		return delay;
	}

	@Override
	public final S interval(int interval) {
		this.interval = validateTiming(Timing.INTERVAL, interval);
		return (S) this;
	}

	@Override
	public final int interval() {
		return interval;
	}

	@Override
	public final S cycles(int cycles) {
		this.cycles = validateTiming(Timing.CYCLES, cycles);
		return (S) this;
	}

	@Override
	public final int cycles() {
		return cycles;
	}

	@Override
	public final S cycleDelay(int cycleDelay) {
		this.cycleDelay = validateTiming(Timing.CYCLE_DELAY, cycleDelay);
		return (S) this;
	}

	@Override
	public final int cycleDelay() {
		return cycleDelay;
	}

	@Override
	public final S finalDelay(int finalDelay) {
		this.finalDelay = validateTiming(Timing.FINAL_DELAY, finalDelay);
		return (S) this;
	}

	@Override
	public final int finalDelay() {
		return finalDelay;
	}

	@Override
	public final S sync(boolean sync) {
		this.synced = sync;
		return (S) this;
	}

	@Override
	public final boolean synced() {
		return synced;
	}

}
