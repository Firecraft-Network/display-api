package net.driftverse.dispatch.api;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import net.driftverse.dispatch.api.enums.Mode;

public interface Dispatcher<Reciver, Slot, Dispatch extends Comparable<Dispatch>> {

	boolean supportsIntervals();

	Dispatch dispatch(Slot slot);

	void create(Reciver reciver, List<Interpolator<Dispatch>> interpolators);

	void update(Reciver reciver, List<Interpolator<Dispatch>> interpolators);

	void destroy(Reciver reciver, List<Interpolator<Dispatch>> interpolators);

	<S extends Synthesizer<S, F>, F> int schedule(Reciver reciver, Mode mode, Slot slot, S synthesizer,
			BiConsumer<F, Dispatch> adapter);

	<S extends Synthesizer<S, F>, F> int schedule(Reciver reciver, Slot slot, S synthesizer,
			BiConsumer<F, Dispatch> adapter);

	<S extends Synthesizer<S, F>, F> Map<Reciver, Integer> schedule(Mode mode, Slot slot, S synthesizer,
			BiConsumer<F, Dispatch> adapter);

	<S extends Synthesizer<S, F>, F> Map<Reciver, Integer> schedule(Slot slot, S synthesizer,
			BiConsumer<F, Dispatch> adapter);

	void unschedule(int id);

}
