package display.hyleo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import display.api.Buffer;
import display.text.TextAnimation;
import net.kyori.adventure.text.Component;

public class ScoreboardUtil {

	private static final Map<Player, Scoreboard> scoreboards = new HashMap<>();

	private static final Map<Team, String> fakePlayers = new HashMap<>(16 * Bukkit.getMaxPlayers()); // 16 sidebar lines
																										// per player
	// All displayslots for a dummy objective
	private static final Map<String, String> objectiveAliases = new HashMap<>(DisplaySlot.values().length);

	static {
		// Populate the map with aliases for all slots using dummy criteria
		List.of(DisplaySlot.values()).forEach(s -> objectiveAlias(s, "dummy"));
	}

	/**
	 * Creates an invisible player name of chatcolors with a chatcolor reset
	 * trailing. This name is used to register scores to a fake player to appear on
	 * a sidebar.
	 * 
	 * For sidebars to work as a display, we assign a fake player to a team with a
	 * score (the line number). The prefix and suffix are used for displaying text.
	 * 
	 * This needs to be chatcolors (which can not be seen as text characters) so a
	 * set of text does not appear on the sidebar between the lines prefix and
	 * suffix.
	 * 
	 * @param team to create a fake player for.
	 * @return A length of 7 chatcolors with a rest on the end
	 */
	public static String fakePlayer(Team team) {

		String fakePlayer = fakePlayers.get(team);

		if (fakePlayer != null) {
			return fakePlayer;
		}

		StringBuilder entry = new StringBuilder();

		int hashCode = team.getName().hashCode();

		IntStream.range(0, 6)
				.forEach(i -> entry.append(ChatColor.getByChar(Integer.toHexString((hashCode * i) % 16)).toString()));

		fakePlayer = entry.toString() + ChatColor.RESET;

		fakePlayers.put(team, fakePlayer);

		return fakePlayer;
	}

	public static boolean fakeExists(Team team) {
		return fakePlayers.containsKey(team);
	}

	/**
	 * An objectives criteria can not be reassigned after being registered to the
	 * scoreboard. As a result, new objectives must be made. For our system to not
	 * "loose" the existing objectives, we created a naming scheme for an objective
	 * at a display slot with a specific criteria.
	 * 
	 * This in turn allows developers to still have the best of both worlds when
	 * changing the criteria of an objective. They have the Display and the original
	 * functionality.
	 * 
	 * WARNING: This is still a some what limiting system. A display slot can only
	 * have one of each criteria type. Ex: There can not be 2 objectives with player
	 * kill criteria assigned to the same DisplaySlot
	 * 
	 * 
	 * This is probably going to be an unused feature for sidebars but may get used
	 * for teams in the tablist, or below name.
	 * 
	 * 
	 * @param slot     the objective with the criteria will be assigned to
	 * @param criteria to be assigned to the objective
	 * @return The alias of the combined slot and criteria
	 */
	public static String objectiveAlias(DisplaySlot slot, String criteria) {

		String objectiveName = slot.name() + "_" + criteria;

		return objectiveAliases.computeIfAbsent(objectiveName, (s) -> UUID.randomUUID().toString().substring(0, 15));
	}

	/**
	 * Checks if a display slot is a sidebar slot
	 * 
	 * @param slot to test
	 * @return if the slot is one of the sidebar slots
	 */
	public static boolean isSidebarSlot(DisplaySlot slot) {
		return slot != null && slot.name().toLowerCase().contains("sidebar");
	}

	public static String lineToTeamName(int line) {
		return "line-" + line;
	}

	public static void criteria(Player player, DisplaySlot slot, String criteria) {
		Scoreboard scoreboard = scoreboard(player);

		String name = objectiveAlias(slot, criteria);

		Objective previous = scoreboard.getObjective(slot);

		Objective objective = scoreboard.getObjective(name);

		objective = objective != null ? objective
				: scoreboard.registerNewObjective(name, criteria,
						previous != null ? previous.displayName() : Component.empty());

		if (previous == objective) {
			return;
		}

		for (String entry : scoreboard.getEntries()) {

			if (previous == null) {
				break;
			}

			Score score = previous.getScore(entry);

			Player theroetical = Bukkit.getPlayerExact(entry); // Is a real player assigned
			boolean exists = ScoreboardUtil.fakeExists(scoreboard.getEntryTeam(entry)); // Is a fake player assigned

			// We dont want to copy other entries that do not belong to us from the
			// objective
			if (!score.isScoreSet() || (theroetical == null && !exists)) {
				continue;
			}

			objective.getScore(score.getEntry()).setScore(score.getScore());

		}

		if (previous != null) {
			previous.setDisplaySlot(null);
		}
		objective.setDisplaySlot(slot);

	}

	public static void score(Player boardOwner, Player entry, DisplaySlot slot, Integer score) {
		Score s = scoreboard(boardOwner).getObjective(slot).getScore(entry);

		if (score == null) {
			s.resetScore();
			return;
		}

		s.setScore(score);

	}

	public static Objective getOrRegisterObjective(Player player, DisplaySlot slot) {
		Scoreboard scoreboard = scoreboard(player);
		Objective objective = scoreboard.getObjective(slot);

		if (objective == null) {
			criteria(player, slot, "dummy");
			objective = scoreboard.getObjective(slot);
		}

		return objective;

	}

	public static Team getOrRegisterTeam(Player player, Destination destination) {

		if (destination.isObjective()) {
			return null;
		}

		Player p = destination.player();
		Integer score = destination.score();

		// Checks if we will get/create a sidebar team or nametag team
		String teamName = destination.isNametagElement() ? p.getName() : ScoreboardUtil.lineToTeamName(score);

		Scoreboard scoreboard = scoreboard(player);

		Team team = scoreboard.getTeam(teamName);

		return team != null ? team : scoreboard.registerNewTeam(teamName);
	}

	public static Scoreboard scoreboard(Player player) {
		return player.isOnline()
				? scoreboards.computeIfAbsent(player, (p) -> Bukkit.getScoreboardManager().getNewScoreboard())
				: scoreboards.compute(player, (p, s) -> null);
	}

	public static void showScoreboard(Player player) {
		player.setScoreboard(scoreboard(player));
	}

	public static boolean viewingScoreboard(Player player) {
		return player.getScoreboard() == scoreboard(player);
	}

	public static void padSidebar(List<Destination> destinations,
			List<Buffer<Destination, TextAnimation, Component>> buffers) {
		int lastLine = 0;

		for (Buffer<Destination, TextAnimation, Component> buffer : buffers) {

			Destination destination = buffer.slot();

			int line = destination.score();

			IntStream.range(lastLine + 1, line).forEach(i -> destinations.add(Destination.custom()
					.tag(destination.tag()).player(destination.player()).score(i).slot(destination.slot()).build()));

			lastLine = line;
		}
	}

	public static String playerName(Destination destination, Team team) {

		Player player = destination.player();

		if (destination.isSidebarTitle()) {
			return null;
		}

		boolean correctElement = destination.isNametagElement() || destination.isBelowName();
		// Checks to see if we will use a real player, or fake player
		return (correctElement || team == null) && player != null ? player.getName() : ScoreboardUtil.fakePlayer(team);
	}

	public static void intializeDestinations(Player player, List<Destination> destinations) {

		for (Destination destination : destinations) {

			if (destination.isUnknown()) {
				throw new RuntimeException("Unknown Board Destination: " + destination.toString());
			}

			DisplaySlot slot = destination.slot();
			Integer score = destination.score();

			Team team = getOrRegisterTeam(player, destination);
			String playerName = playerName(destination, team);

			if (playerName == null) {
				continue;
			}

			if (team != null) {
				// Registers the fake/real player to the team
				team.addEntry(playerName);
			}
			// Will only set the fake/real players score if the score is set
			if (score != null) {
				getOrRegisterObjective(player, slot).getScore(playerName).setScore(score);
			}

		}
	}

	public static boolean hasEntries(Objective objective) {

		Scoreboard scoreboard = objective.getScoreboard();

		for (String entry : scoreboard.getEntries()) {
			if (objective.getScore(entry).isScoreSet()) {
				return true;
			}
		}

		return false;

	}
}
