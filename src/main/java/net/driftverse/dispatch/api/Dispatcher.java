package net.driftverse.dispatch.api;

import java.util.List;
import java.util.function.BiConsumer;

import net.driftverse.dispatch.api.enums.Mode;

public interface Dispatcher<Reciever, Slot, Dispatch extends Comparable<Dispatch>> {

	boolean supportsIntervals();

	Dispatch dispatch(Slot slot);

	void create(Reciever reciver, List<Interpolator<Dispatch>> interpolators);

	void update(Reciever reciver, List<Interpolator<Dispatch>> interpolators);

	void destroy(Reciever reciver, List<Interpolator<Dispatch>> interpolators);

	<S extends Synthesizer<S, F>, F> int schedule(Reciever reciever, Mode mode, Slot slot, S synthesizer,
			BiConsumer<F, Dispatch> adapter);

	default <S extends Synthesizer<S, F>, F> int schedule(Reciever reciever, Slot slot, S synthesizer,
			BiConsumer<F, Dispatch> adapter) {
		return schedule(reciever, Mode.REPLACE, slot, synthesizer, adapter);
	}

	void unschedule(int id);

}
