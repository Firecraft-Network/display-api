package hyleo.animations.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import hyleo.animations.api.Buffer.BufferBuilder;
import hyleo.animations.api.Buffer.Stage;
import lombok.NonNull;

public abstract class Display<Slot, Animation, Frame> extends BukkitRunnable {

	private final Map<Player, Map<Slot, Buffer<Slot, Animation, Frame>>> schedules = new HashMap<>();

	public abstract boolean intervalSupport();

	public abstract Animator<Animation, Frame> animator();

	public abstract boolean shouldDisplay(Player player);

	public abstract void create(Player player, List<Buffer<Slot, Animation, Frame>> buffers);

	public abstract void update(Player player, List<Buffer<Slot, Animation, Frame>> buffers);

	public abstract void destroy(Player player, List<Buffer<Slot, Animation, Frame>> buffers);

	private Animator<Animation, Frame> animator;

	private Map<Slot, Buffer<Slot, Animation, Frame>> schedule(Player player) {
		return schedules.computeIfAbsent(player, (p) -> new HashMap<>());
	}

	@SafeVarargs
	@NonNull
	public final void schedule(Player player, Slot slot, AnimationInfo info, Animation... animations) {
		Map<Slot, Buffer<Slot, Animation, Frame>> schedule = schedule(player);

		BufferBuilder<Slot, Animation, Frame> builder = Buffer.builder();

		animator = animator == null ? animator() : animator;

		builder.slot(slot);
		builder.animator(animator);
		builder.info(info);
		builder.intervalSupport(intervalSupport());
		builder.animations(List.of(animations));
		builder.coroperator(animator.coroperator());

		schedule.put(slot, builder.build());

	}

	public final void run() {

		for (Entry<Player, Map<Slot, Buffer<Slot, Animation, Frame>>> entry : schedules.entrySet()) {

			Player player = entry.getKey();

			if (!shouldDisplay(player)) {
				continue;
			}

			Map<Slot, Buffer<Slot, Animation, Frame>> schedule = entry.getValue();

			schedule.values().removeIf(b -> b.stage() == Stage.COMPLETE);

			List<Buffer<Slot, Animation, Frame>> create = new ArrayList<>();
			List<Buffer<Slot, Animation, Frame>> update = new ArrayList<>();
			List<Buffer<Slot, Animation, Frame>> destroy = new ArrayList<>();

			for (Buffer<Slot, Animation, Frame> buffer : schedule.values()) {

				if (buffer.stage() == Stage.CREATE) {
					create.add(buffer);
				} else if (buffer.stage() == Stage.DESTROY) {
					destroy.add(buffer);
				} else {
					update.add(buffer);
				}
			}

			if (!create.isEmpty()) {
				create(player, new ArrayList<>(create));// Prevent Accidental Tampering
			}
			// Create and Updates should happen on the same tick but create first then
			// update
			for (Buffer<Slot, Animation, Frame> buffer : create) {
				if (buffer.stage() != Stage.CREATE || buffer.stage() != Stage.DESTROY
						|| buffer.stage() != Stage.COMPLETE) {
					update.add(buffer);
				}
			}

			if (!destroy.isEmpty()) {
				destroy(player, destroy); // Who cares if they tamper with it, we dont need it any more
			}

			if (!update.isEmpty()) {
				update(player, update); // Who cares if they tamper with it, we dont need it any more
			}

			schedule.values().removeIf(b -> b.stage() == Stage.COMPLETE);
		}

	}

	public final void unschedule(Player player, List<Slot> slots) {
		Map<Slot, Buffer<Slot, Animation, Frame>> schedule = schedule(player);

		List<Buffer<Slot, Animation, Frame>> toDestroy = new ArrayList<>();

		slots.forEach(s -> toDestroy.add(schedule.remove(schedule)));

		toDestroy.removeIf(b -> b == null);

		toDestroy.forEach(b -> b.stage(Stage.DESTROY));
	}

	public final void empty(Player player) {
		Map<Slot, Buffer<Slot, Animation, Frame>> schedule = schedule(player);

		if (player.isOnline()) {
			unschedule(player, new ArrayList<>(schedule.keySet()));
		} else {
			schedules.remove(player);
		}
	}
}
