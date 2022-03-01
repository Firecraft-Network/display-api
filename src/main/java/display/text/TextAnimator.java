package display.text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import display.api.Animator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent.Builder;
import net.kyori.adventure.text.format.TextDecoration.State;

public class TextAnimator implements Animator<TextAnimation, Component> {

	private final Map<TextAnimation, AnimationState> states = new HashMap<>();

	/**
	 * Creates on long line of text from all components
	 */
	@Override
	public Function<List<Component>, Component> concurency() {
		return (l) -> {
			Builder text = Component.text();

			l.forEach(c -> text.append(c));

			return text.build();
		};
	}

	/**
	 * Determined by the the animations pattern
	 */
	@Override
	public int frames(TextAnimation animation) {
		String text = animation.text().get();

		int colors = animation.colors();

		int frames = animation.pattern().frames().apply(colors, text.length());

		AnimationState state = new AnimationState(frames, animation.preText().get(), animation.subText().get(), text,
				colors, 0);

		states.put(animation, state);

		return frames;
	}

	/**
	 * Animated by the pattern, but decorations are appended through here.
	 */
	@Override
	public Component animate(TextAnimation animation, int frame) {

		AnimationState state = state(animation);

		state.frame(frame);
		Component text = animation.pattern().text().apply(animation, state);

		text = text
				.decorations(animation.decorations().stream().collect(Collectors.toMap((d) -> d, (s) -> State.TRUE)));

		return state.preText().append(text).append(state.subText());
	}

	/**
	 * Gets the current state of the animation
	 * 
	 * @param animation to check
	 * @return the state of the animation
	 */
	public AnimationState state(TextAnimation animation) {
		return states.get(animation);
	}

}
