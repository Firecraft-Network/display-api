package hyleo.animations;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
@Builder(builderMethodName = "custom")
@AllArgsConstructor(staticName = "custom")
@EqualsAndHashCode
@ToString
public class Destination implements Cloneable {

	public static enum Tag {
		OBJECTIVE_NAME, PREFIX, SUFFIX
	};

	@Default
	private final DisplaySlot slot = DisplaySlot.SIDEBAR;

	@Default
	private final Tag tag = Tag.OBJECTIVE_NAME;

	@Default
	private final Player player = null;

	@Default
	private final Integer score = null;

	public static Destination sidebarTitle(DisplaySlot sidebar) {
		return custom(sidebar, Tag.OBJECTIVE_NAME, null, null);

	}

	public static Destination sidebarLine(DisplaySlot sidebar, int line, boolean suffix) {
		return custom(sidebar, suffix ? Tag.SUFFIX : Tag.PREFIX, null, line);
	}

	public static Destination nametag(Player player, int score, boolean suffix) {
		return custom(null, suffix ? Tag.SUFFIX : Tag.PREFIX, player, score);
	}

	public static Destination nametag(Player player, boolean suffix) {
		return custom(null, suffix ? Tag.SUFFIX : Tag.PREFIX, player, null);
	}

	public static Destination prefix(Player player) {
		return nametag(player, false);
	}

	public static Destination suffix(Player player) {
		return nametag(player, true);
	}

	public static Destination blowname(Player player, int score) {
		return custom(DisplaySlot.BELOW_NAME, Tag.OBJECTIVE_NAME, player, score);
	}

	public boolean sidebarElement() {
		return ScoreboardUtil.isSidebarSlot(slot);
	}

	public boolean sidebarTitle() {
		return sidebarElement() && objective();
	}

	public boolean sidebarPrefix() {
		return sidebarElement() && tag == Tag.PREFIX;
	}

	public boolean sidebarSuffix() {
		return sidebarElement() && tag == Tag.SUFFIX;
	}

	public boolean nametagElement() {
		return slot == null && player != null;

	}

	public boolean nametagPrefix() {
		return nametagElement() && tag == Tag.PREFIX;
	}

	public boolean nametagSuffix() {
		return nametagElement() && tag == Tag.SUFFIX;
	}

	public boolean belowName() {
		return slot == DisplaySlot.BELOW_NAME && objective();
	}

	public boolean unknown() {
		return !sidebarElement() && !nametagElement() && !belowName();
	}

	public boolean scored() {
		return score != null;
	}

	public boolean objective() {
		return tag == Tag.OBJECTIVE_NAME;
	}

	public Destination clone() throws CloneNotSupportedException {
		return (Destination) super.clone();
	}

}
