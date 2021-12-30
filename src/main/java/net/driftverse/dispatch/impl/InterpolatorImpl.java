package net.driftverse.dispatch.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import net.driftverse.dispatch.api.Dispatcher;
import net.driftverse.dispatch.api.Interpolator;
import net.driftverse.dispatch.api.enums.Cycle;
import net.driftverse.dispatch.api.enums.Mode;
import net.driftverse.dispatch.api.enums.Timing;

public final class InterpolatorImpl<Reciver, Slot, Dispatch extends Comparable<Dispatch>>
		implements Interpolator<Dispatch> {

	private final Queue<List<SynthesizerImpl<?, ?>>> synthesizers = new LinkedList<>();
	private final Map<Timing, Integer> timings = new HashMap<>();
	private final Map<SynthesizerImpl<?, ?>, Integer> remainingCycles = new HashMap<>();

	private final LinkedBlockingQueue<Dispatch> dispatches;
	private final DispatcherImpl<Slot, Dispatch> dispatcher;
	private final Slot slot;
	private Cycle cycle;

	@SuppressWarnings("unchecked")
	@SafeVarargs
	public InterpolatorImpl(DispatcherImpl<Slot, Dispatch> dispatcher, Slot slot,
			List<SynthesizerImpl<?, ?>>... synthesizers) {
		this.dispatcher = dispatcher;
		this.dispatches = new LinkedBlockingQueue<>(dispatcher.bufferLength());
		this.slot = slot;

	}

	public Dispatcher<Slot, Dispatch> dispatcher() {
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

		Map<Timing, Integer> timings = new HashMap<>();

		List.of(Timing.values()).forEach(t -> timings.put(t, t.getDefaultValue()));

		List<SynthesizerImpl<?, ?>> peek = synthesizers.peek();

		if (peek != null) {

			peek.forEach(s -> timings.compute(Timing.CYCLE_DELAY, (t, i) -> Math.max(s.interval(), i)));
			peek.forEach(s -> timings.compute(Timing.CYCLES, (t, i) -> Math.max(s.cycles(), i)));
			peek.forEach(s -> timings.compute(Timing.DELAY, (t, i) -> Math.max(s.delay(), i)));
			peek.forEach(s -> timings.compute(Timing.FINAL_DELAY, (t, i) -> Math.max(s.finalDelay(), i)));
			peek.forEach(s -> timings.compute(Timing.INTERVAL, (t, i) -> Math.max(s.interval(), i)));

		}

		this.timings.clear();
		this.timings.putAll(timings);
	}

	public void addSynthesizers(Mode mode, int cycles, SynthesizerImpl<?, ?> synthesizer) {
//
//		List.of(synthesizers).forEach(g -> this.synthesizers.offer(g));
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
		return dispatcher.bufferThreshold();
	}

	int capacity() {
		return dispatches.size() + dispatches.remainingCapacity();
	}

	boolean shouldBuffer() {
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
