package display.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import org.bukkit.entity.Player;

import display.api.Buffer.BufferBuilder;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Accessors;

@Builder
@Accessors(fluent = true)
@Data
@NonNull
public final class Display<Slot, Animation, Frame> {

	private final Map<Player, Map<Slot, Buffer<Slot, Animation, Frame>>> schedules = new HashMap<>();
	private final Map<Player, Boolean> cleanedUp = new HashMap<>();

	private final Animator<Animation, Frame> animator;

	@Default
	private final boolean intervalSupport = false;

	@Default
	@NonNull
	private final Consumer<Player> setup = (p) -> {
	}, cleanup = (p) -> {
	};

	@Default
	private final Function<Player, Boolean> condition = (p) -> true;

	@Default
	@NonNull
	private final BiConsumer<Player, List<Buffer<Slot, Animation, Frame>>> create = (p, bs) -> bs
			.forEach(b -> b.poll());
	@Default
	@NonNull
	private final BiConsumer<Player, List<Buffer<Slot, Animation, Frame>>> update = (p, bs) -> bs
			.forEach(b -> b.poll());
	@Default
	@NonNull
	private final BiConsumer<Player, List<Buffer<Slot, Animation, Frame>>> destroy = (p, bs) -> bs
			.forEach(b -> b.poll());

	public void setup(Player player) {

		if (cleanedUp.containsKey(player)) {
			return;
		}

		schedules.put(player, new HashMap<>());
		cleanedUp.put(player, false);
		setup.accept(player);

	}

	public void cleanup(Player player) {

		schedules.remove(player);

		if (!cleanedUp.containsKey(player)) {
			return;
		}
		cleanedUp.put(player, true);
		cleanup.accept(player);
	}

	public Map<Player, Map<Slot, Buffer<Slot, Animation, Frame>>> schedules() {
		return schedules;
	}

	@SafeVarargs
	public final void schedule(Player player, Slot slot, Timings timings, List<Animation>... animations) {

	}

	@SafeVarargs
	@NonNull
	public final void schedule(Player player, Slot slot, Timings timings, Animation... animations) {

		setup(player);
		Map<Slot, Buffer<Slot, Animation, Frame>> schedule = schedules.get(player);

		if (schedule == null) {
			// TODO: Throw exception
		}

		BufferBuilder<Slot, Animation, Frame> builder = Buffer.builder();

		builder.slot(slot);
		builder.animator(animator);
		builder.timings(timings);
		builder.intervalSupport(intervalSupport());
		builder.animations(List.of(animations));

		schedule.put(slot, builder.build());

	}

	@SafeVarargs
	public final void unschedule(Player player, Slot... slots) {
		unschedule(player, List.of(slots));
	}

	public void unschedule(Player player, Collection<Slot> slots) {
		Map<Slot, Buffer<Slot, Animation, Frame>> schedule = schedules.get(player);

		if (schedule == null) {
			// TODO: Throw exception
		}

		List<Buffer<Slot, Animation, Frame>> toDestroy = new ArrayList<>();

		slots.forEach(s -> toDestroy.add(schedule.remove(schedule)));

		toDestroy.removeIf(b -> b == null);

		toDestroy.forEach(b -> b.stage(Stage.DESTROY));
	}

	public final void empty(Player player) {
		Map<Slot, Buffer<Slot, Animation, Frame>> schedule = schedules.get(player);

		if (player.isOnline()) {

			unschedule(player, new ArrayList<>(schedule.keySet()));
		} else {
			schedules.remove(player);
		}
	}
}
