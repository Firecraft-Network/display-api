package display.api;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Builder(builderMethodName = "of")
@Data
@Accessors(fluent = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(staticName = "simple")
@EqualsAndHashCode
public class Timings {

	@Default
	final boolean reversed = false;

	@Default
	final int delay = 0, interval = 1, cycleDelay = 0, finalDelay = 0, cycles = 0;

	@Default
	final int fadeIn = 0, fadeOut = 0;

}
