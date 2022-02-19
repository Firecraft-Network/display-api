package hyleo.animations;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;

import hyleo.animations.displays.Bar;
import hyleo.animations.displays.Chat;
import hyleo.animations.displays.Nametag;
import hyleo.animations.displays.Sidebar;
import hyleo.animations.displays.Tablist;
import hyleo.animations.displays.Title;

public class Hyleo extends JavaPlugin implements Listener {

	private static Plugin plugin;
	private static final Map<Player, Scoreboard> scoreboards = new HashMap<>();

	public void onLoad() {
		plugin = this;
	}

	public void onEnable() {
		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(this, plugin);

		bar().runTaskTimer(plugin, 0, 1);
		chat().runTaskTimer(plugin, 0, 1);
		nametag().runTaskTimer(plugin, 0, 1);

		sidebar().runTaskTimer(plugin, 0, 1);
		pm.registerEvents(Sidebar.instance(), plugin);

		tablist().runTaskTimer(plugin, 0, 1);
		title().runTaskTimer(plugin, 0, 1);

		pm.registerEvents(new Tests(), plugin);

	}

	public void onDisable() {

	}

	public static Plugin plugin() {
		return plugin;
	}

	public static Scoreboard scoreboard(Player player) {
		return scoreboards.computeIfAbsent(player, (p) -> Bukkit.getScoreboardManager().getNewScoreboard());
	}

	public static void showScoreboard(Player player) {
		player.setScoreboard(scoreboard(player));
	}

	public static boolean viewingScoreboard(Player player) {
		return player.getScoreboard() == scoreboard(player);
	}

	public static Bar bar() {
		return Bar.instance();
	}

	public static Chat chat() {
		return Chat.instance();
	}

	public static Nametag nametag() {
		return Nametag.instance();
	}

	public static Sidebar sidebar() {
		return Sidebar.instance();
	}

	public static Tablist tablist() {
		return Tablist.istance();
	}

	public static Title title() {
		return Title.instance();
	}

	@EventHandler
	void join(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		showScoreboard(player);
	}

	@EventHandler
	void quit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		scoreboards.remove(player);

		Sidebar.instance().empty(player);
	}
}
