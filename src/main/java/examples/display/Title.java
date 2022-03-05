package examples.display;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import display.DisplayAPI;
import display.api.Timings;
import display.text.Palette;
import display.text.Pattern;
import display.text.TextAnimation;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

public class Title implements Listener {

	@EventHandler
	void join(@NotNull PlayerJoinEvent event) {

		Player player = event.getPlayer();

		subtitle(player);
	}

	void subtitle(Player player) {

		Palette palette = Palette.of(30, NamedTextColor.RED, NamedTextColor.GOLD, NamedTextColor.YELLOW,
				NamedTextColor.GREEN, NamedTextColor.BLUE, NamedTextColor.DARK_PURPLE);

		TextAnimation animation = TextAnimation.of(Pattern.flash(), palette, Component::empty, () -> "Welcome ",
				() -> Component.text(player.getName()).color(NamedTextColor.YELLOW));

		DisplayAPI.title().schedule(player, false, Timings.repeats(1), animation);

	}

}
