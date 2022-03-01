package display.text;

import java.util.List;
import java.util.function.Function;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NonNull;
import lombok.Singular;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.format.TextColor;

@Data
@Builder
@AllArgsConstructor
@Accessors(fluent = true)
public class Palette {

	public static Palette singular(TextColor color) {
		return builder().color(color).build();
	}

	public static Palette of(int depth, TextColor... colors) {
		return builder().depth(depth).colors(List.of(colors)).build();
	}

	@Singular
	@NonNull
	final List<TextColor> colors;

	@Default
	final int depth = 1;

	public int size() {
		return colors().size() * depth();
	}

	public TextColor color(int color) {

		TextColor color1 = colors().get(firstColor(depth, color));

		TextColor color2 = colors().get(secondColor(colors.size(), depth, color));

		int red = color(color % depth, depth, color1, color2, (c) -> c.red());
		int green = color(color % depth, depth, color1, color2, (c) -> c.green());
		int blue = color(color % depth, depth, color1, color2, (c) -> c.blue());

		return TextColor.color(red, green, blue);

	}

	public static int firstColor(int depth, int color) {
		return color / depth;
	}

	public static int secondColor(int colors, int depth, int color) {
		return ((color + depth) / depth % colors);
	}

	public static int distance(int depth, TextColor color1, TextColor color2, Function<TextColor, Integer> function) {

		int x1 = function.apply(color1);
		int x2 = function.apply(color2);

		return (Integer.max(x1, x2) - Integer.min(x1, x2)) / depth;

	}

	public static int direction(TextColor color1, TextColor color2, Function<TextColor, Integer> function) {
		return Integer.compare(function.apply(color2), function.apply(color1));
	}

	public static int color(int color, int depth, TextColor color1, TextColor color2,
			Function<TextColor, Integer> function) {
		return function.apply(color1)
				+ color * distance(depth, color1, color2, function) * direction(color1, color2, function);
	}

}
