package hyleo.animations.text;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@Accessors(fluent = true)
final class AnimationState {

	final int cycleLength;
	final String text;
	final int textLength;
	final int colors;

	int frame;
}
