package net.driftverse.dispatch.impl;

import java.util.List;
import java.util.function.BiConsumer;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import net.driftverse.dispatch.api.Dispatcher;
import net.driftverse.dispatch.api.Interpolator;
import net.driftverse.dispatch.api.Synthesizer;
import net.driftverse.dispatch.api.enums.Mode;
import net.driftverse.dispatch.api.schedule.ScheduleResult;

public final class DispatcherImpl<Slot, Dispatch extends Comparable<Dispatch>> extends BukkitRunnable
		implements Dispatcher<Slot, Dispatch> {

	private final Plugin plugin;
	private final Dispatcher<?, ?> dispatcher;

	public DispatcherImpl(Plugin plugin, Dispatcher<?, ?> dispatcher) {
		super();
		this.plugin = plugin;
		this.dispatcher = dispatcher;
	}

	public Plugin plugin() {
		return plugin;
	}

	public Dispatcher<?, ?> implemented() {
		return dispatcher;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean supportsIntervals() {
		return false;
	}

	@Override
	public Dispatch makeDispatch(Slot slot) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean shouldDispatch(List<Slot> otherSlots, Slot slot) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void create(Player reciver, List<Interpolator<Dispatch>> interpolators) {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(Player reciver, List<Interpolator<Dispatch>> interpolators) {
		// TODO Auto-generated method stub

	}

	@Override
	public void destroy(Player reciver, List<Interpolator<Dispatch>> interpolators) {
		// TODO Auto-generated method stub

	}

	@Override
	public <S extends Synthesizer<S, F>, F> ScheduleResult schedule(Player reciever, Mode mode, Slot slot,
			S synthesizer, BiConsumer<F, Dispatch> adapter) {
		// TODO Auto-generated method stub
		return ScheduleResult.failed();
	}

	@Override
	public void unschedule(ScheduleResult result) {
		// TODO Auto-generated method stub

	}

	@Override
	public int bufferLength() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float bufferThreshold() {
		// TODO Auto-generated method stub
		return 0;
	}

}
