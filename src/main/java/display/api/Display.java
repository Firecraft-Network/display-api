package display.api;

import display.api.Buffer.BufferBuilder;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Accessors;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

@Builder
@Accessors(fluent = true)
@Data
@NonNull
public final class Display<Slot, Animation, Frame> {

	private final Map<Player, Map<Slot, Buffer<Slot, Animation, Frame>>> schedules = new HashMap<>();
	private final List<Player> wasSetup = new ArrayList<>();

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

		if (wasSetup.contains(player)) {
			return;
		}

		schedules.put(player, new HashMap<>());
		wasSetup.add(player);
		setup.accept(player);

	}

	public void cleanup(Player player) {

		schedules.remove(player);

		if (!wasSetup.contains(player)) {
			return;
		}

		wasSetup.remove(player);
		cleanup.accept(player);
	}

	public Map<Player, Map<Slot, Buffer<Slot, Animation, Frame>>> schedules() {
		return schedules;
	}

	@SafeVarargs
	@NonNull
	public final void schedule(Player player, Slot slot, Timings timings, Animation... animations) {
		Validate.notNull(player, "Null player can not be used in displays");
		Validate.notNull(slot, "Null slots can not be used in displays");
		Validate.notNull(timings, "Null timings can not be used in displays");
		Validate.noNullElements(List.of(animations), "Null animations can not be used in displays");

		setup(player);
		Map<Slot, Buffer<Slot, Animation, Frame>> schedule = schedules.get(player);

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

		Validate.notNull(player, "Null player can not be used in displays");
		Validate.noNullElements(slots, "Null slots can not be used in displays");

		Map<Slot, Buffer<Slot, Animation, Frame>> schedule = schedules.get(player);

		List<Buffer<Slot, Animation, Frame>> toDestroy = new ArrayList<>();

		slots.forEach(s -> toDestroy.add(schedule.remove(schedule)));

		toDestroy.removeIf(b -> b == null);

		toDestroy.forEach(b -> b.stage(true, Stage.DESTROY));
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
