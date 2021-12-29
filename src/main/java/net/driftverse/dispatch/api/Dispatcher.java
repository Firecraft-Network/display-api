package net.driftverse.dispatch.api;

import java.util.List;
import java.util.function.BiConsumer;

import org.bukkit.entity.Player;

import net.driftverse.dispatch.api.enums.Mode;
import net.driftverse.dispatch.api.schedule.ScheduleResult;

public interface Dispatcher<Slot, Dispatch extends Comparable<Dispatch>> {

	boolean supportsIntervals();

	int bufferLength();

	float bufferThreshold();

	Dispatch makeDispatch(Slot slot);

	boolean shouldDispatch(List<Slot> otherSlots, Slot slot);

	void create(Player reciver, List<Interpolator<Dispatch>> interpolators);

	void update(Player reciver, List<Interpolator<Dispatch>> interpolators);

	void destroy(Player reciver, List<Interpolator<Dispatch>> interpolators);

	<S extends Synthesizer<S, F>, F> ScheduleResult schedule(Player reciever, Mode mode, Slot slot, S synthesizer,
			BiConsumer<F, Dispatch> adapter);

	default <S extends Synthesizer<S, F>, F> ScheduleResult schedule(Player reciever, Slot slot, S synthesizer,
			BiConsumer<F, Dispatch> adapter) {
		return schedule(reciever, Mode.REPLACE, slot, synthesizer, adapter);
	}

	void unschedule(ScheduleResult result);

}
