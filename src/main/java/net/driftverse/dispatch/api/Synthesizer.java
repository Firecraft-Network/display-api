package net.driftverse.dispatch.api;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface Synthesizer<S extends Synthesizer<S, Frame>, Frame> extends Serializable {

	Class<Frame> frame();

	/**
	 * This method is in place to support functions which may change the cycle
	 * length. In an instance like swipe text synthesizer, the number documents to
	 * complete 1 cycle is dependent on the length of the string for the text. If
	 * this text changes, so would the cycle length.
	 * <p>
	 * Functions for options, are applied at the end of each cycle. The next cycle
	 * length may change in duration depending on the new options. Options are
	 * updated at the end of each cycle to ensure smooth generation of documents.
	 * 
	 * @param id      attached to the specific set of options
	 * @param options used to calculate the current cycle length
	 * @return the current cycle length in ticks
	 * 
	 * @since 1.0.0
	 */
	int frames();

	/**
	 * In some cases, synthesizers can be known as "Cumulative". This means their
	 * documents must be generated in a linear fashion and will not be able to take
	 * advantage of random order synthesis. Synthesizes that involve math can be
	 * calculated at any given moment where cumulative synthesizes rely on some or
	 * all of the documents before. Examples of cumulative synthesises are below:
	 * <p>
	 * - Gif Synthesizer
	 * <p>
	 * - Note Block Studio Synthesizer
	 * <p>
	 * Synthesizers as mentioned above require knowing the first document, then the
	 * next, and next, leading up to the desired document. They have to be linearly
	 * traversed.
	 * <p>
	 * Synthesizers can be both cumulative and random. Cumulative synthesis will be
	 * done before the random syntheses. This method can return null but may not
	 * have any null values;
	 *
	 *
	 * @see Synthesizer#randomSynthesis(Map, int)
	 * @return All the cumulative documents of this synthesizer
	 * 
	 * @since 1.0.0
	 */
	List<Frame> cumulativeSynthesis();

	/**
	 * A mathematical representation of what the synthesizer will do. This method
	 * can be accessed randomly using the number of ticks between 0-(cycleLength -
	 * 1). This is a non linear method. At any point it should be able to provide an
	 * output from the given options and ticks. If this synthesizer can not do so,
	 * consider using the cumulativeSynthesis method.
	 * 
	 * @see Synthesizer#cumulativeSynthesis(Map)
	 * @param ticks into the cycle
	 * 
	 * @return The document at the number of ticks in time
	 * @since 1.0.0
	 */
	Frame randomSynthesis(int frame);

	S reverse(boolean reverse);

	boolean reversed();

	S shuffle(boolean shuffle);

	boolean shuffled();

	S delay(int delay);

	int delay();

	S interval(int interval);

	int interval();

	S cycles(int cycles);

	int cycles();

	S cycleDelay(int cycleDelay);

	int cycleDelay();

	S finalDelay(int finalDelay);

	int finalDelay();

	S sync(boolean sync);

	boolean synced();

}
