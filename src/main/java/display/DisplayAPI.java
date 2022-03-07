package display;

import display.api.Buffer;
import display.api.Display;
import display.api.Stage;
import display.hyleo.Destination;
import display.hyleo.Displays;
import display.text.TextAnimation;
import display.text.TextAnimator;
import examples.display.Sidebar;
import examples.display.Title;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

@Accessors(fluent = true)
public class DisplayAPI extends JavaPlugin {
	/**
	 * Whats Missing:
	 * 
	 * - Timings for individual Animations (its the same for a slot)
	 * 
	 * - Sucessive Animations
	 * 
	 * - Cummulative Animations
	 * 
	 * - Dispatch Loader Plugin (for non dev use)
	 * 
	 * - Restful API
	 */
	/**
	 * TODO:
	 *
	 * - Split Displays into seperate plugin
	 *
	 * - Write Tests Using Mockito
	 * 
	 * - Test Destroy Method
	 * 
	 * - Fix Interval Support
	 *
	 * - Handle Errors for Animators & Dispatchers
	 * 
	 */
	private static Plugin plugin;
	private static final TextAnimator textAnimator = new TextAnimator();

	@Getter
	private static final Display<BossBar, TextAnimation, Component> bossbar = Display
			.<BossBar, TextAnimation, Component>builder().animator(textAnimator).create(Displays.bossbarCreate())
			.update(Displays.bossbarUpdate()).destroy(Displays.bossbarDestroy()).build();

	@Getter
	private static final Display<ChatMessageType, TextAnimation, Component> chat = Display
			.<ChatMessageType, TextAnimation, Component>builder().intervalSupport(true).animator(textAnimator)
			.update(Displays.chatUpdate()).destroy(Displays.chatDestroy()).build();

	@Getter
	private static final Display<Destination, TextAnimation, Component> scoreboard = Display
			.<Destination, TextAnimation, Component>builder().animator(textAnimator).intervalSupport(true)
			.setup(Displays.scoreboardSetup()).cleanup(Displays.scoreboardCleanup())
			.condition(Displays.scoreboardCondition()).create(Displays.scoreboardCreate())
			.update(Displays.scoreboardUpdate()).destroy(Displays.scoreboardDestroy()).build();

	@Getter
	private static final Display<Integer, TextAnimation, Component> tablist = Display
			.<Integer, TextAnimation, Component>builder().animator(textAnimator).update(Displays.tablistUpdate())
			.destroy(Displays.tablistDestroy()).build();

	@Getter
	private static final Display<Boolean, TextAnimation, Component> title = Display
			.<Boolean, TextAnimation, Component>builder().animator(textAnimator).update(Displays.titleUpdate()).build();

	public DisplayAPI() {
		super();
	}

	protected DisplayAPI(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
		super(loader, description, dataFolder, file);
	}

	public void onLoad() {
		plugin = this;
	}

	public void onEnable() {

		List<Display<?, ?, ?>> displays = List.of(bossbar, chat, scoreboard, tablist, title);

		Bukkit.getScheduler().runTaskTimer(plugin, () -> displays.forEach(d -> display(d)), 0, 1);

		PluginManager pm = Bukkit.getPluginManager();

		pm.registerEvents(new Sidebar(), plugin);
		pm.registerEvents(new Title(), plugin);

	}

	public static Plugin plugin() {
		return plugin;
	}

	public static <A, S, F> void display(Display<A, S, F> display) {

		for (Entry<Player, Map<A, Buffer<A, S, F>>> entry : new ArrayList<>(display.schedules().entrySet())) {

			Player player = entry.getKey();

			if (!((Function<Player, Boolean>) display.condition()).apply(player)) {
				continue; // Skip current player
			}

			Map<?, Buffer<A, S, F>> schedule = entry.getValue();

			if (!player.isOnline()) {
				display.cleanup(player);
				break; // Skip current player
			}

			schedule.values().removeIf(b -> b.stage() == Stage.COMPLETE);

			List<Buffer<A, S, F>> create = filter(schedule.values(), b -> b.stage() != Stage.CREATE);

			List<Buffer<A, S, F>> destroy = filter(schedule.values(), b -> b.stage() != Stage.DESTROY);

			List<Buffer<A, S, F>> update = filter(schedule.values(), b -> create.contains(b) || destroy.contains(b));

			dispatchIfNotEmpty(player, create, display.create());

			// Create and Updates should happen on the same tick but create first then
			// update. The same is not for update and destroy. A frame should not be updated
			// then destroyed simultaneously

			create.removeIf(b -> List.of(Stage.CREATE, Stage.DESTROY, Stage.COMPLETE).contains(b.stage()));

			update.addAll(create);

			int p = update.size();
			update.removeIf(b -> {

				if (b.onIntervalGap()) {
					b.poll();// increment
					return true;
				}

				return false;

			});

			System.out.println("Before: " + p + " After: " + update.size() + " Schedule: " + schedule.size());

			dispatchIfNotEmpty(player, destroy, display.destroy());

			dispatchIfNotEmpty(player, update, display.update());

			schedule.values().removeIf(b -> b.stage() == Stage.COMPLETE);
		}

	}

	static <B> void dispatchIfNotEmpty(Player player, List<B> buffers, BiConsumer<Player, List<B>> dispatchFunction) {

		if (buffers.isEmpty()) {
			return;
		}
		// New Array list prevents buffer list from tampering
		dispatchFunction.accept(player, new ArrayList<>(buffers));

	}

	static <B> List<B> filter(Collection<B> buffers, Predicate<B> filter) {

		List<B> bs = new ArrayList<>(buffers);

		bs.removeIf(filter);

		return bs;
	}
}
