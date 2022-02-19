package hyleo.animations.displays;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import hyleo.animations.Hyleo;
import hyleo.animations.api.Animator;
import hyleo.animations.api.Buffer;
import hyleo.animations.api.Display;
import hyleo.animations.text.TextAnimation;
import hyleo.animations.text.TextAnimator;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.kyori.adventure.text.Component;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Sidebar extends Display<Integer, TextAnimation, Component>
		implements Listener, Comparator<Buffer<Integer, TextAnimation, Component>> {

	private static final Sidebar sidebar = new Sidebar();
	private static final String objectiveName = "hyleoSidebar";
	private static final String teamName = "hyleoSidebarTeam-";

	public static Sidebar instance() {
		return sidebar;
	}

	@EventHandler
	void join(PlayerJoinEvent event) {

		Player player = event.getPlayer();

		Scoreboard scoreboard = Hyleo.scoreboard(player);

		IntStream.range(0, 16).forEach(index -> scoreboard.registerNewTeam(teamName + index));
	}

	@Override
	public boolean intervalSupport() {
		return false;
	}

	@Override
	public Animator<TextAnimation, Component> animator() {
		return new TextAnimator();
	}

	@Override
	public boolean shouldDisplay(Player player) {
		return Hyleo.viewingScoreboard(player);
	}

	@Override
	public void create(Player player, List<Buffer<Integer, TextAnimation, Component>> buffers) {
		Collections.sort(buffers, this); // Ensure Orderw
		/**
		 * We know we are viewing the correct scoreboard because of shouldDisplay().
		 * This is quicker than calling Hyleo.scoreboard(player)
		 **/
		Scoreboard scoreboard = player.getScoreboard();

		Objective objective = getOrCreateObjective(scoreboard);

		for (Buffer<Integer, TextAnimation, Component> buffer : buffers) {
			int line = buffer.slot();
			buffer.poll();

			if (line == 0) {
				continue;
			}

			Buffer<Integer, TextAnimation, Component> max = Collections.max(buffers, this);

			objective.getScore(score(line)).setScore(line);

			if (max.slot() == line) {
				IntStream.range(1, line + 1).forEach(i -> objective.getScore(score(i)).setScore(i));
			}

			Team team = scoreboard.getTeam(teamName + line);
			team.addEntry(score(line));
		}

	}

	@Override
	public void update(Player player, List<Buffer<Integer, TextAnimation, Component>> buffers) {

		Collections.sort(buffers, this); // Ensure Order
		/**
		 * We know we are viewing the correct scoreboard because of shouldDisplay().
		 * This is quicker than calling Hyleo.scoreboard(player)
		 **/
		Scoreboard scoreboard = player.getScoreboard();

		Objective objective = getOrCreateObjective(scoreboard);

		for (Buffer<Integer, TextAnimation, Component> buffer : buffers) {
			int line = buffer.slot();
			Component text = buffer.poll();

			if (line == 0) {
				objective.displayName(text);
				continue;
			}

			Team team = scoreboard.getTeam(teamName + line);
			team.prefix(text);
		}

	}

	@Override
	public void destroy(Player player, List<Buffer<Integer, TextAnimation, Component>> buffers) {

		Collections.sort(buffers, this); // Ensure Order
		/**
		 * We know we are viewing the correct scoreboard because of shouldDisplay().
		 * This is quicker than calling Hyleo.scoreboard(player)
		 **/
		Scoreboard scoreboard = player.getScoreboard();

		Objective objective = scoreboard.getObjective(objectiveName);

		for (Buffer<Integer, TextAnimation, Component> buffer : buffers) {
			int line = buffer.slot();

			if (line == 0) {
				objective.unregister();
				continue;
			}

			Team team = scoreboard.getTeam(teamName + line);
			team.prefix(Component.text(""));

			IntStream.range(line, 16).forEach(i -> scoreboard.getScores(score(i)));
		}

	}

	@Override
	public int compare(Buffer<Integer, TextAnimation, Component> buffer1,
			Buffer<Integer, TextAnimation, Component> buffer2) {
		return Integer.compare(buffer1.slot(), buffer2.slot());
	}

	String score(int line) {
		return ChatColor.getByChar(Integer.toHexString(line)).toString() + ChatColor.RESET;
	}

	Objective getOrCreateObjective(Scoreboard scoreboard) {
		Objective objective = scoreboard.getObjective(objectiveName);

		if (objective == null) {
			objective = scoreboard.registerNewObjective(objectiveName, "dummy", Component.text(""));
		}

		Objective attached = scoreboard.getObjective(DisplaySlot.SIDEBAR);

		if (attached == null || attached.getName() != objectiveName) {

			objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		}

		return objective;
	}

}
