package display.text;

import java.util.List;
import java.util.function.BiFunction;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextComponent.Builder;
import net.kyori.adventure.text.format.TextColor;

public interface Pattern {

	BiFunction<Integer, Integer, Integer> frames();

	BiFunction<TextAnimation, AnimationState, TextComponent> text();

	default TextComponent render(TextAnimation animation, AnimationState state) {
		return text().apply(animation, state);
	}

	public static Pattern build() {
		return create((c, l) -> c * (l + 1), (a, s) -> {

			String text = s.text();
			int length = s.textLength() + 1;
			TextColor color = a.color(s.frame() / length % a.colors());

			int i = s.frame() % length;
			String spaces = new String(" ").repeat(s.textLength() - i);

			return Component.text().color(color).append(a.inverse() ? Component.text(spaces) : Component.text(""))
					.append(Component.text(text.substring(a.inverse() ? i : 0, a.inverse() ? length : i)))
					.append(!a.inverse() ? Component.text(spaces) : Component.text("")).build();

		});
	}

	public static Pattern dialate() {
		return create((c, l) -> 4 * c, (a, s) -> {

			int frame = a.inverse() ? s.frames() - s.frame() : s.frame();

			TextColor color = a.color(frame % s.colors());
			String text = s.text();

			char[] chars = text.toCharArray();

			text = chars[0] + "";
			String spacing = new String(" ").repeat(frame % (4 + 1));

			for (int r = 1; r < chars.length; r++) {

				text = text + spacing + chars[r];

			}

			return Component.text(text).color(color);

		});
	}

	public static Pattern flash() {
		return create((c, l) -> c, (a, s) -> Component.text(s.text()).color(a.color(s.frame())));
	}

	public static Pattern replace() {
		return create((c, l) -> l * c + 1, (a, s) -> {

			String text = s.text();
			int colors = s.colors();
			int length = s.textLength();

			int i = s.frame() % length;

			if (s.frames() - 1 == s.frame()) {
				return Component.text(text).color(a.color(0));
			}

			TextColor color1 = a.color((int) Math.floor(s.frame() / length));
			TextColor color2 = a.color(((int) Math.floor(s.frame() / length) + 1) % colors);

			return Component.text(text.substring(0, i)).color(color2)
					.append(Component.text(text.substring(i, length)).color(color1));

		});
	}

	public static Pattern swipe() {
		return create((c, l) -> c * (l + 2), (a, s) -> {

			String text = s.text();
			int colors = s.colors();
			int length = s.textLength() + 2;

			int i = s.frame() % length;

			TextColor color1 = a.color((int) Math.floor(s.frame() / length));
			TextColor color2 = a.color(((int) Math.floor(s.frame() / length) + 1) % colors);

			if (i == length - 1 || i == 0) {
				return Component.text(text).color(color1);
			}

			return Component.text(text.substring(0, i - 1)).color(color1)
					.append(Component.text(text.substring(i - 1, i)).color(color2))
					.append(Component.text(text.substring(i, text.length())).color(color1));

		});
	}

	public static Pattern slide() {
		return create((c, l) -> c + l, (a, s) -> {
			String text = s.text();

			int frame = s.frame();
			int colors = s.colors();
			int length = s.textLength();

			Builder builder = Component.text();

			for (int i = 0; i < length; ++i) {

				int x = frame - (i - 1);

				if (x < 0 || frame >= colors) {
					x = 0;
				}

				TextColor color = a.color(x % colors);
				builder.append(Component.text(text.charAt(i)).color(color));
			}

			return builder.build();
		});
	}

	public static Pattern create(BiFunction<Integer, Integer, Integer> frames,
			BiFunction<TextAnimation, AnimationState, TextComponent> pattern) {

		return new Pattern() {

			@Override
			public BiFunction<Integer, Integer, Integer> frames() {
				return frames;
			}

			@Override
			public BiFunction<TextAnimation, AnimationState, TextComponent> text() {
				return pattern;
			}

		};
	}
}
