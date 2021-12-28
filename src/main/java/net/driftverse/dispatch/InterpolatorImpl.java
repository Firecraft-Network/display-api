package net.driftverse.dispatch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import net.driftverse.dispatch.api.Dispatcher;
import net.driftverse.dispatch.api.Interpolator;
import net.driftverse.dispatch.api.enums.Cycle;
import net.driftverse.dispatch.api.enums.Timing;

public final class InterpolatorImpl<Reciver, Slot, Dispatch extends Comparable<Dispatch>> implements Interpolator<Dispatch> {

	/* 60 Gives us a 3 second buffer room */
	private final LinkedBlockingQueue<Dispatch> dispatches = new LinkedBlockingQueue<>(60);
	private final float threshold = 0.25f;

	private final Dispatcher<Reciver, Slot, Dispatch> dispatcher;
	private final Slot slot;
	private final Queue<List<SynthesizerImpl<?, ?>>> synthesizers;
	private final int cycles;

	private Queue<List<SynthesizerImpl<?, ?>>> workingCopy;
	private final Map<Timing, Integer> timings = new HashMap<>();
	private Cycle cycle;
	private int remainingCycles;

	@SuppressWarnings("unchecked")
	@SafeVarargs
	public InterpolatorImpl(Dispatcher<Reciver, Slot, Dispatch> dispatcher, Slot slot, int cycles,
			List<SynthesizerImpl<?, ?>>... synthesizers) {
		this.dispatcher = dispatcher;
		this.slot = slot;
		this.cycles = cycles;

		this.synthesizers = Util.copy(List.of(synthesizers));
		this.workingCopy = Util.copy((List<List<SynthesizerImpl<?, ?>>>) this.synthesizers);
	}

	public Dispatcher<Reciver, Slot, Dispatch> dispatcher() {
		return dispatcher;
	}

	public Slot slot() {
		return slot;
	}

	@Override
	public Dispatch peek() {
		return dispatches.peek();
	}

	@Override
	public Dispatch poll() {
		Dispatch dispatch = dispatches.poll();

		updateTimings();

		return null;
	}

	void updateTimings() {

//		List<SynthesizerImpl<?, ?>> synthesizers = workingCopy.peek();
//
//		if (synthesizers != null) {
//
//			BiFunction<Integer, Integer, Integer> function = (i1, i2) -> Math.max(i1, i2);
//
//			for (SynthesizerImpl<?, ?> synthesizer : synthesizers) {
//
//				Map<Timing, Integer> timings = synthesizer.timings();
//
//				timings.entrySet().forEach(e -> this.timings.merge(e.getKey(), e.getKey().getDefaultValue(), function));
//			}
//		}
	}

	@Override
	public boolean infinite() {
		return false;
	}

	@Override
	public boolean firstInterpolation() {
		return false;
	}

	@Override
	public boolean lastInterpolation() {
		// TODO Auto-generated method stub
		return false;
	}

	public void buffer() {

	}

	public Cycle cycle() {
		return cycle;
	}

	public float threshold() {
		return threshold;
	}

	public int capacity() {
		return dispatches.size() + dispatches.remainingCapacity();
	}

	public boolean shouldBuffer() {
		return dispatches.isEmpty() || dispatches.size() / capacity() <= threshold();
	}

	public void destroy() {

	}

	@Override
	public int delay() {
		return timings.getOrDefault(Timing.DELAY, Timing.DELAY.getDefaultValue());
	}

	@Override
	public int interval() {
		return timings.getOrDefault(Timing.INTERVAL, Timing.INTERVAL.getDefaultValue());
	}

	@Override
	public int cycles() {
		return timings.getOrDefault(Timing.CYCLES, Timing.CYCLES.getDefaultValue());
	}

	@Override
	public int cycleDelay() {

		return timings.getOrDefault(Timing.FINAL_DELAY, Timing.FINAL_DELAY.getDefaultValue());
	}

	@Override
	public int finalDelay() {
		return timings.getOrDefault(Timing.FINAL_DELAY, Timing.FINAL_DELAY.getDefaultValue());
	}
}
