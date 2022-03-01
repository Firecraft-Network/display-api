package examples.display;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Supplier;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import display.DisplayAPI;
import display.api.Timings;
import display.hyleo.Destination;
import display.text.Palette;
import display.text.Pattern;
import display.text.TextAnimation;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class Sidebar implements Listener {

	@EventHandler
	void join(PlayerJoinEvent event) {

		Player player = event.getPlayer();

		assignTitle(player);
		assignAccount(player);
		assignClock(player);
		assignWorld(player);
		assignLocation(player);

	}

	void assignTitle(Player player) {

		Palette palette = Palette.of(15, NamedTextColor.RED, NamedTextColor.BLUE);

		TextAnimation animation = TextAnimation.slide(palette, () -> "Display API", TextDecoration.BOLD);

		DisplayAPI.scoreboard().schedule(player, Destination.sidebarTitle(), Timings.of().cycleDelay(200).build(),
				animation);

	}

	void assignAccount(Player player) {

		DisplayAPI.scoreboard().schedule(player, Destination.sidebarLine(8, false), Timings.simple(),
				TextAnimation.none());

		Palette palette = Palette.of(15, NamedTextColor.GREEN, NamedTextColor.GOLD);

		TextAnimation animation = TextAnimation.of(Pattern.slide(), palette, () -> Component.text("Account: "),
				() -> player.getName());

		DisplayAPI.scoreboard().schedule(player, Destination.sidebarLine(7, false),
				Timings.of().cycleDelay(120).build(), animation);

	}

	void assignClock(Player player) {
		Palette palette = Palette.singular(NamedTextColor.GREEN);

		Supplier<String> clock = () -> new SimpleDateFormat("hh:mm:ss").format(new Date(System.currentTimeMillis()));

		TextAnimation animation = TextAnimation.of(Pattern.flash(), palette, () -> Component.text("Time: "), clock);

		DisplayAPI.scoreboard().schedule(player, Destination.sidebarLine(5, false), Timings.simple(), animation);
	}

	void assignWorld(Player player) {
		Palette palette = Palette.of(20, NamedTextColor.GREEN, NamedTextColor.DARK_PURPLE);

		TextAnimation animation = TextAnimation.of(Pattern.flash(), palette, () -> Component.text("World: "),
				() -> player.getWorld().getName());

		DisplayAPI.scoreboard().schedule(player, Destination.sidebarLine(3, false), Timings.simple(), animation);

	}

	void assignLocation(Player player) {

		TextAnimation animation = TextAnimation.of(NamedTextColor.YELLOW,
				() -> Component.text("X: " + player.getLocation().getBlockX() + " ").color(NamedTextColor.RED),
				() -> "Y: " + player.getLocation().getBlockY() + " ",
				() -> Component.text("Z: " + player.getLocation().getBlockZ()).color(NamedTextColor.LIGHT_PURPLE));

		DisplayAPI.scoreboard().schedule(player, Destination.sidebarLine(1, false), Timings.simple(), animation);

	}

}
