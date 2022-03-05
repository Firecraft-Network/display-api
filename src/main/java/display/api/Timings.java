package display.api;

import display.text.TextAnimation;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;

@Builder(builderMethodName = "of")
@Data
@Accessors(fluent = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(staticName = "fast")
@EqualsAndHashCode
public final class Timings {

	@Default
	final boolean reversed = false;

	@Default
	final int delay = 0, interval = 1, repeatDelay = 0, finalDelay = 0;

	@Default
	final Integer repeats = null, maxTicks = null;

	public int actualDelay() {
		return reversed ? finalDelay : delay;
	}

	public int actualFinalDelay() {
		return reversed ? delay : finalDelay;
	}

	public boolean isInfinite() {
		return repeats == null;
	}

	public boolean isMaxed() {
		return maxTicks != null;
	}

	public static Timings reversed(boolean reversed) {
		return Timings.of().reversed(reversed).build();
	}

	public static Timings delay(int delay) {
		return Timings.of().delay(delay).build();
	}

	public static Timings interval(int interval) {
		return Timings.of().interval(interval).build();
	}

	public static Timings repeatDelay(int repeatDelay) {
		return Timings.of().repeatDelay(repeatDelay).build();
	}

	public static Timings finalDelay(int finalDelay) {
		return Timings.of().finalDelay(finalDelay).build();
	}

	public static Timings repeats(int repeats) {
		return Timings.of().repeats(repeats).build();
	}

	public static Timings standard(int delay, int inverval, int repeats) {
		return Timings.of().delay(delay).interval(inverval).repeats(repeats).build();
	}

	public static Timings standard(int delay, int interval) {
		return Timings.of().delay(delay).interval(interval).build();
	}

	public static Timings delays(int delay, int repeatDelay, int finalDelay) {
		return Timings.of().delay(delay).repeatDelay(repeatDelay).finalDelay(finalDelay).build();
	}

	public static Timings advanced(int delay, int interval, int repeatDelay, int finalDelay, int repeats) {
		return Timings.of().delay(delay).interval(interval).repeatDelay(repeatDelay).finalDelay(finalDelay)
				.repeats(repeats).build();
	}
}
