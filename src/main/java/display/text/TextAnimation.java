package display.text;

import java.util.List;
import java.util.function.Supplier;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Singular;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

@Builder(buildMethodName = "create")
@Data
@Accessors(fluent = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class TextAnimation {

	@Default
	final boolean inverse = false;

	@Default
	@NonNull
	final Supplier<String> text = () -> "";

	@NonNull
	final Palette palette;

	@Singular
	@NonNull
	public final List<TextDecoration> decorations;

	@Default
	final Pattern pattern = Pattern.flash();

	@Default
	final Supplier<TextComponent> preText = () -> Component.empty(), subText = () -> Component.empty();

	public int colors() {

		return palette().size();
	}

	public TextColor color(int color) {

		return palette().color(color);
	}

	public static TextAnimation none() {
		return TextAnimation.builder().palette(Palette.singular(NamedTextColor.GRAY)).create();
	}

	public static TextAnimation of(TextColor color, Supplier<TextComponent> preText, Supplier<String> text,
			Supplier<TextComponent> subText) {

		return of(Pattern.flash(), Palette.singular(color), false, preText, text, subText);
	}

	public static TextAnimation of(Pattern pattern, Palette palette, Supplier<String> text,
			TextDecoration... decoration) {

		return of(pattern, palette, false, text, decoration);
	}

	public static TextAnimation of(Pattern pattern, Palette palette, Supplier<TextComponent> preText,
			Supplier<String> text, TextDecoration... decoration) {

		return of(pattern, palette, false, preText, text, decoration);
	}

	public static TextAnimation of(Pattern pattern, Palette palette, Supplier<TextComponent> preText,
			Supplier<String> text, Supplier<TextComponent> subText, TextDecoration... decoration) {

		return of(pattern, palette, false, preText, text, subText, decoration);
	}

	public static TextAnimation of(Pattern pattern, Palette palette, boolean inverse, Supplier<String> text,
			TextDecoration... decoration) {

		return builder().pattern(pattern).palette(palette).inverse(inverse).text(text).decorations(List.of(decoration))
				.create();
	}

	public static TextAnimation of(Pattern pattern, Palette palette, boolean inverse, Supplier<TextComponent> preText,
			Supplier<String> text, TextDecoration... decoration) {

		return of(pattern, palette, inverse, preText, text, () -> Component.text(""), decoration);
	}

	public static TextAnimation of(Pattern pattern, Palette palette, boolean inverse, Supplier<TextComponent> preText,
			Supplier<String> text, Supplier<TextComponent> subText, TextDecoration... decoration) {

		return builder().pattern(pattern).palette(palette).inverse(inverse).preText(preText).text(text).subText(subText)
				.decorations(List.of(decoration)).create();
	}

	public static TextAnimation buildRight(Palette palette, Supplier<String> text, TextDecoration... decoration) {
		return of(Pattern.build(), palette, false, text, decoration);
	}

	public static TextAnimation buildLeft(Palette palette, Supplier<String> text, TextDecoration... decoration) {
		return of(Pattern.build(), palette, true, text, decoration);
	}

	public static TextAnimation build(Palette palette, boolean inverse, Supplier<String> text,
			TextDecoration... decoration) {
		return of(Pattern.build(), palette, inverse, text, decoration);
	}

	public static TextAnimation dialate(Palette palette, Supplier<String> text, TextDecoration... decoration) {
		return of(Pattern.dialate(), palette, text, decoration);
	}

	public static TextAnimation flash(Palette palette, Supplier<String> text, TextDecoration... decoration) {
		return of(Pattern.flash(), palette, text, decoration);
	}

	public static TextAnimation replaceRight(Palette palette, Supplier<String> text, TextDecoration... decoration) {
		return of(Pattern.replace(), palette, false, text, decoration);
	}

	public static TextAnimation replaceLeft(Palette palette, Supplier<String> text, TextDecoration... decoration) {
		return of(Pattern.replace(), palette, true, text, decoration);
	}

	public static TextAnimation replace(Palette palette, boolean inverse, Supplier<String> text,
			TextDecoration... decoration) {
		return of(Pattern.replace(), palette, inverse, text, decoration);
	}

	public static TextAnimation swipe(Palette palette, Supplier<String> text, TextDecoration... decoration) {
		return of(Pattern.swipe(), palette, text, decoration);
	}

	public static TextAnimation slide(Palette palette, Supplier<String> text, TextDecoration... decoration) {
		return of(Pattern.slide(), palette, text, decoration);
	}
}
