package net.driftverse.dispatch.api;

import java.util.Map;
import java.util.function.BiConsumer;

import net.driftverse.dispatch.api.enums.Mode;

public abstract class AbstractDispatcher<Reciver, Slot, Dispatch extends Comparable<Dispatch>>
		implements Dispatcher<Reciver, Slot, Dispatch>, Runnable {

	@Override
	public final void run() {
		// TODO Auto-generated method stub

	}

	@Override
	public final <S extends Synthesizer<S, F>, F> int schedule(Reciver player, Mode mode, Slot slot, S synthesizer,
			BiConsumer<F, Dispatch> adapter) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public final void unschedule(int id) {
		// TODO Auto-generated method stub

	}

}
