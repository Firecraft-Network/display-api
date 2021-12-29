package net.driftverse.dispatch.api;

import java.util.function.BiConsumer;

import org.bukkit.entity.Player;

import net.driftverse.dispatch.api.enums.Mode;
import net.driftverse.dispatch.api.schedule.ScheduleResult;

public abstract class AbstractDispatcher<Slot, Dispatch extends Comparable<Dispatch>>
		implements Dispatcher<Slot, Dispatch> {

	@Override
	public final <S extends Synthesizer<S, F>, F> ScheduleResult schedule(Player player, Mode mode, Slot slot,
			S synthesizer, BiConsumer<F, Dispatch> adapter) {
		return ScheduleResult.failed();
	}

	@Override
	public final void unschedule(ScheduleResult result) {
		// TODO Auto-generated method stub

	}

}
