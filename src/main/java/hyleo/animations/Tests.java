package hyleo.animations;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import hyleo.animations.api.AnimationInfo;
import hyleo.animations.text.Palette;
import hyleo.animations.text.TextAnimation;
import hyleo.animations.text.TextAnimation.TextAnimationBuilder;
import hyleo.animations.text.TextAnimationType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class Tests implements Listener {

	@EventHandler
	void join(PlayerJoinEvent event) {

		Player player = event.getPlayer();

		TextAnimationBuilder title = TextAnimation.builder();

		title.palette(Palette.builder().depth(40)
				.colors(List.of(TextColor.color(255, 0, 0), TextColor.color(255, 127, 0), TextColor.color(255, 255, 0),
						TextColor.color(0, 255, 0), TextColor.color(0, 0, 255), TextColor.color(127, 0, 255)))
				.build());

		title.text(() -> "DRIFTVERSE");
		title.type(TextAnimationType.DIALATE);
		title.decorations(List.of(TextDecoration.BOLD));

		Hyleo.sidebar().schedule(player, 0, AnimationInfo.builder().interval(1).cycleDelay(20).build(), title.build());

		TextAnimationBuilder line1 = TextAnimation.builder();

		line1.palette(Palette.builder().depth(1)
				.colors(List.of(TextColor.fromHexString("#b37474"), TextColor.fromHexString("#689475"))).build());

		line1.preText(Component.text("Time Left: ").color(NamedTextColor.RED));
		line1.text(timer(System.currentTimeMillis() + 1000 * 500));
		line1.type(TextAnimationType.STATIC);

		Hyleo.sidebar().schedule(player, 10, AnimationInfo.builder().interval(10).build(), line1.build());

	}

	Supplier<String> timer(final long time) {
		return () -> time - System.currentTimeMillis() < 0 ? "00:00"
				: String.format("%02d:%02d",
						TimeUnit.MILLISECONDS.toMinutes(time - System.currentTimeMillis()) - TimeUnit.HOURS
								.toMinutes(TimeUnit.MILLISECONDS.toHours(time - System.currentTimeMillis())),
						TimeUnit.MILLISECONDS.toSeconds(time - System.currentTimeMillis()) - TimeUnit.MINUTES
								.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time - System.currentTimeMillis())));

	}

	List<TextColor> hexColors(int colors, TextColor color1, TextColor color2) {
		List<TextColor> converted = new ArrayList<>();

		for (int color = 0; color < colors; color++) {

			int red = color(color, colors, color1, color2, (c) -> c.red());
			int green = color(color, colors, color1, color2, (c) -> c.green());
			int blue = color(color, colors, color1, color2, (c) -> c.blue());

			TextColor c = TextColor.color(red, green, blue);

			converted.add(c);
		}

		return converted;
	}

	int distance(int colors, TextColor color1, TextColor color2, Function<TextColor, Integer> function) {

		int x1 = function.apply(color1);
		int x2 = function.apply(color2);

		return (Integer.max(x1, x2) - Integer.min(x1, x2)) / colors;

	}

	int direction(TextColor color1, TextColor color2, Function<TextColor, Integer> function) {
		return Integer.compare(function.apply(color2), function.apply(color1));
	}

	int color(int color, int colors, TextColor color1, TextColor color2, Function<TextColor, Integer> function) {
		return function.apply(color1)
				+ color * distance(colors, color1, color2, function) * direction(color1, color2, function);
	}

}
