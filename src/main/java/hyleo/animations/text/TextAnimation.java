package hyleo.animations.text;

import java.util.List;
import java.util.function.Supplier;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

@Builder
@Data
@Accessors(fluent = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(staticName = "none")
@EqualsAndHashCode
public class TextAnimation {
	@Default
	final boolean inverse = false;

	@Default
	final Supplier<String> text = () -> "";

	@Default
	final Palette palette = Palette.gray();

	@Default
	final List<TextDecoration> decorations = List.of();

	@Default
	final TextAnimationType type = TextAnimationType.STATIC;

	@Default
	final Component preText = Component.empty(), subText = Component.empty();

	public int colors() {
		return palette().size();
	}

	public TextColor color(int color) {
		return palette().color(color);
	}

}
