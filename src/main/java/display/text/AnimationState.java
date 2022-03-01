package display.text;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.TextComponent;

@Data
@AllArgsConstructor
@Accessors(fluent = true)
final class AnimationState {

	final int frames;
	final TextComponent preText, subText;
	final String text;
	final int colors;

	int frame;

	public int textLength() {
		return text.length();
	}
}
