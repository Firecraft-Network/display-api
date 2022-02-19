package hyleo.animations.text;

import java.util.function.BiFunction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;

@AllArgsConstructor
@Getter
@Accessors(fluent = true)
public enum TextAnimationType {

	BUILD((c, l) -> c * (l + 1), (a, s) -> {

		String text = s.text();
		int length = s.textLength() + 1;
		TextColor color = a.color(s.frame() / length % a.colors());

		int i = s.frame() % length;

		return Component.text().color(color)
				.append(Component.text(text.substring(a.inverse() ? i : 0, a.inverse() ? length : i))).build();

	}),

	REPLACE((c, l) -> l * c + 1, (a, s) -> {

		String text = s.text();
		int colors = s.colors();
		int length = s.textLength();

		int i = s.frame() % length;

		if (s.cycleLength() == s.frame()) {
			return Component.text(text).color(a.color(0));
		}

		TextColor color1 = a.color((int) Math.floor(s.frame() / length));
		TextColor color2 = a.color(((int) Math.floor(s.frame() / length) + 1) % colors);

		return Component.text(text.substring(0, i)).color(color2)
				.append(Component.text(text.substring(i, length)).color(color1));

	}), SWIPE((c, l) -> c * (l + 2), (a, s) -> {

		String text = s.text();
		int colors = s.colors();
		int length = s.textLength() + 2;

		int i = s.frame() % length;

		TextColor color1 = a.color((int) Math.floor(s.frame() / length));
		TextColor color2 = a.color(((int) Math.floor(s.frame() / length) + 1) % colors);

		if (i == length - 1 || i == 0) {
			return Component.text(text).color(color1);
		}

		i -= 1;

		if (a.inverse()) {
			i = s.textLength() - (i + 1);
		}

		return Component.text(text.substring(0, i - 1)).color(color1)
				.append(Component.text(text.substring(i - 1, i)).color(color2))
				.append(Component.text(text.substring(i, text.length())).color(color1));

	}),

	DIALATE((c, l) -> 4 * c, (a, s) -> {

		int frame = a.inverse() ? s.cycleLength() - s.frame() : s.frame();

		TextColor color = a.color(frame % s.colors());
		String text = s.text();

		char[] chars = text.toCharArray();

		text = chars[0] + "";
		String spacing = new String(" ").repeat(frame % (4 + 1));

		for (int r = 1; r < chars.length; r++) {

			text = text + spacing + chars[r];

		}

		return Component.text(text).color(color);

	}), STATIC((c, l) -> c, (a, s) -> Component.text(s.text()).color(a.color(s.frame())));

	final BiFunction<Integer, Integer, Integer> framesFunction;
	final BiFunction<TextAnimation, AnimationState, TextComponent> textFunction;
}
