package hyleo.animations.api;

import java.io.Serializable;
import java.util.List;
import java.util.function.Function;

public interface Animator<Animation, Frame> extends Serializable {

	Function<List<Frame>, Frame> coroperator();

	/**
	 * This method is in place to support functions which may change the cycle
	 * length. In an instance like swipe text synthesizer, the number frames to
	 * complete 1 cycle is dependent on the length of the string for the text. If
	 * this text changes, so would the cycle length.
	 * <p>
	 * Functions for options, are applied at the end of each cycle. The next cycle
	 * length may change in duration depending on the new options. Options are
	 * updated at the end of each cycle to ensure smooth generation of frames.
	 * 
	 * @param id      attached to the specific set of options
	 * @param options used to calculate the current cycle length
	 * @return the current cycle length in ticks
	 * 
	 * @since 1.0.0
	 */
	int frames(Animation animation);

	/**
	 * A mathematical representation of what the synthesizer will do. This method
	 * can be accessed randomly using the number of ticks between 0-(cycleLength -
	 * 1). This is a non linear method. At any point it should be able to provide an
	 * output from the given options and ticks.
	 * 
	 * @param ticks into the cycle
	 * 
	 * @return The frame at the number of ticks in time
	 * @since 1.0.0
	 */
	Frame animate(Animation animation, int frame);

}
