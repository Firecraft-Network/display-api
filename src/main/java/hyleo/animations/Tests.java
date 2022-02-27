package hyleo.animations;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.DisplaySlot;

import hyleo.animations.api.AnimationInfo;
import hyleo.animations.text.Palette;
import hyleo.animations.text.TextAnimation;
import hyleo.animations.text.TextAnimation.TextAnimationBuilder;
import hyleo.animations.text.TextAnimationType;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.bossbar.BossBar.Color;
import net.kyori.adventure.bossbar.BossBar.Overlay;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class Tests implements Listener {

	@EventHandler
	void join(PlayerJoinEvent event) {

		Player player = event.getPlayer();

		Hyleo.scoreboard().schedule(player, Destination.sidebarTitle(DisplaySlot.SIDEBAR),
				AnimationInfo.builder().interval(1).cycleDelay(20).build(), driftverse().build());

		for (Player p : Bukkit.getOnlinePlayers()) {
			Hyleo.scoreboard().schedule(player, Destination.blowname(p, 69),
					AnimationInfo.builder().interval(1).build(), driftverse().build());

			Hyleo.scoreboard().schedule(player, Destination.prefix(p),
					AnimationInfo.builder().interval(1).cycleDelay(20).build(), driftverse().build());

			Hyleo.scoreboard().schedule(player, Destination.suffix(p),
					AnimationInfo.builder().interval(1).cycleDelay(20).build(), driftverse().build());

			Bukkit.getScheduler().scheduleSyncDelayedTask(Hyleo.plugin(),
					() -> ScoreboardUtil.score(player, p, DisplaySlot.BELOW_NAME, 69), 180);
		}
		TextAnimationBuilder line1 = TextAnimation.builder();

		line1.palette(Palette.builder().depth(1)
				.colors(List.of(TextColor.fromHexString("#b37474"), TextColor.fromHexString("#689475"))).build());

		line1.preText(Component.text("Time Left: ").color(NamedTextColor.RED));
		line1.text(timer(System.currentTimeMillis() + 1000 * 500));
		line1.type(TextAnimationType.STATIC);

		Hyleo.scoreboard().schedule(player, Destination.sidebarLine(DisplaySlot.SIDEBAR, 10, false),
				AnimationInfo.builder().interval(10).build(), line1.build());

		Hyleo.tablist().schedule(player, 4, AnimationInfo.builder().interval(1).build(), welcome().build(),
				driftverse().build());

		Hyleo.tablist().schedule(player, 0, AnimationInfo.builder().interval(20).build(),
				TextAnimation.builder().text(
						() -> ChatColor.GOLD + "Online players: " + ChatColor.GREEN + Bukkit.getOnlinePlayers().size())
						.build());

		Hyleo.tablist().schedule(player, -1, AnimationInfo.builder().interval(1).cycleDelay(0).build(),
				TextAnimation.builder()
						.text(() -> ChatColor.RED + "Avg Tick: " + ChatColor.YELLOW + Bukkit.getAverageTickTime())
						.build());

		Hyleo.tablist().schedule(player, -4, AnimationInfo.builder().interval(20).cycleDelay(0).build(),
				TextAnimation.builder().text(
						() -> ChatColor.DARK_PURPLE + "Avg Tick: " + ChatColor.DARK_RED + Bukkit.getAverageTickTime())
						.build());

		Hyleo.bossbar().schedule(player, BossBar.bossBar(Component.empty(), 0f, Color.WHITE, Overlay.NOTCHED_10),
				AnimationInfo.simple(), driftverse().build());

		Bukkit.getScheduler().scheduleSyncDelayedTask(Hyleo.plugin(),
				() -> ScoreboardUtil.criteria(player, DisplaySlot.BELOW_NAME, "health"), 80);

		Bukkit.getScheduler().scheduleSyncDelayedTask(Hyleo.plugin(),
				() -> ScoreboardUtil.criteria(player, DisplaySlot.BELOW_NAME, "dummy"), 160);

	}

	TextAnimationBuilder driftverse() {
		TextAnimationBuilder text = TextAnimation.builder();

		text.palette(Palette.builder().depth(40)
				.colors(List.of(TextColor.color(255, 0, 0), TextColor.color(255, 127, 0), TextColor.color(255, 255, 0),
						TextColor.color(0, 255, 0), TextColor.color(0, 0, 255), TextColor.color(127, 0, 255)))
				.build());

		text.text(() -> "[DRIFTVERSE]");
		text.type(TextAnimationType.STATIC);
		text.decorations(List.of(TextDecoration.BOLD));

		return text;

	}

	TextAnimationBuilder welcome() {
		TextAnimationBuilder text = TextAnimation.builder();

		text.palette(Palette.builder().depth(40)
				.colors(List.of(TextColor.color(255, 0, 0), TextColor.color(255, 127, 0))).build());

		text.text(() -> "Welcome to" + " ");
		text.type(TextAnimationType.STATIC);
		text.decorations(List.of(TextDecoration.BOLD));

		return text;

	}

	Supplier<String> timer(final long time) {
		return () -> time - System.currentTimeMillis() < 0 ? "00:00"
				: String.format("%02d:%02d",
						TimeUnit.MILLISECONDS.toMinutes(time - System.currentTimeMillis()) - TimeUnit.HOURS
								.toMinutes(TimeUnit.MILLISECONDS.toHours(time - System.currentTimeMillis())),
						TimeUnit.MILLISECONDS.toSeconds(time - System.currentTimeMillis()) - TimeUnit.MINUTES
								.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time - System.currentTimeMillis())));

	}

}
