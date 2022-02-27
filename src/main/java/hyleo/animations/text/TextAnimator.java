package hyleo.animations.text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;

import hyleo.animations.api.Animator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent.Builder;
import net.kyori.adventure.text.format.TextDecoration.State;

public class TextAnimator implements Animator<TextAnimation, Component> {

	private static final long serialVersionUID = 1L;
	private final Map<TextAnimation, AnimationState> states = new HashMap<>();

	@Override
	public Function<List<Component>, Component> coroperator() {
		return (l) -> {

			@NotNull
			Builder text = Component.text();

			l.forEach(c -> text.append(c));

			return text.build();

		};
	}

	@Override
	public int frames(TextAnimation animation) {
		String text = animation.text().get();

		int colors = animation.colors();

		int cycleLength = animation.type().framesFunction().apply(colors, text.length());

		AnimationState state = new AnimationState(text.length(), text, cycleLength, colors, 0);

		states.put(animation, state);

		return animation.type().framesFunction().apply(colors, text.length());
	}

	@Override
	public Component animate(TextAnimation animation, int frame) {

		AnimationState state = state(animation);

		state.frame(frame);
		Component text = animation.type().textFunction().apply(animation, state);

		text = text
				.decorations(animation.decorations().stream().collect(Collectors.toMap((d) -> d, (s) -> State.TRUE)));

		return animation.preText().append(text).append(animation.subText());
	}

	public AnimationState state(TextAnimation animation) {
		return states.get(animation);
	}

}
