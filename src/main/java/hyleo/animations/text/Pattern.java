package hyleo.animations.text;

import java.util.function.BiFunction;

import net.kyori.adventure.text.TextComponent;

public interface Pattern {

	BiFunction<Integer, Integer, Integer> frames();

	BiFunction<TextAnimation, AnimationState, TextComponent> pattern();

	public static Pattern build() {
		return create(null, null);
	}
	
	public static Pattern dialate() {
		return create(null, null);
	}
	public static Pattern flash() {
		return create(null, null);
	}
	public static Pattern replace() {
		return create(null, null);
	}
	public static Pattern swipe() {
		return create(null, null);
	}

	public static Pattern create(BiFunction<Integer, Integer, Integer> frames,
			BiFunction<TextAnimation, AnimationState, TextComponent> pattern) {

		return new Pattern() {

			@Override
			public BiFunction<Integer, Integer, Integer> frames() {
				return frames;
			}

			@Override
			public BiFunction<TextAnimation, AnimationState, TextComponent> pattern() {
				return pattern;
			}

		};
	}
}
