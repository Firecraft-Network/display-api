package net.driftverse.dispatch.api.enums;

import org.apache.commons.lang3.Range;

/**
 * An enum value representing the type of a numerical timing.
 * 
 * 
 * @since 1.0.0
 * @author Tyler Frydenlund
 *
 */
public enum Timing {
	/**
	 * Sometimes refereed to as "initial delay". This specifies the time (in ticks)
	 * the system will wait before displaying the first document of a synthesizer.
	 * This must be greater than or equal to 0. By default this is 0.
	 */
	DELAY(0, Range.between(0, Integer.MAX_VALUE)),
	/**
	 * Specifies the time (in ticks) that the system will wait before repeating a
	 * synthesizer. This value is used each time before a repeat occurs. This must
	 * be greater than or equal to 0. By default this is 0.
	 */
	CYCLE_DELAY(0, Range.between(0, Integer.MAX_VALUE)),
	/**
	 * Specifies the time (in ticks) that the system will wait before the next
	 * synthesizer of an interpolator is used, or before the system will destroy a
	 * synthesizer. This value only applies after all cycles have been completed.
	 * This must be greater than or equal to 0. By default this is 0.
	 */
	FINAL_DELAY(0, Range.between(0, Integer.MAX_VALUE)),
	/**
	 * Sometimes refereed to as "repeats". This value specifies the number of times
	 * the system will repeat a synthesizer. Any value denoted as less than or equal
	 * to 0 will result in the number of cycles being infinite. By default this is
	 * 0.
	 */
	CYCLES(0, Range.between(Integer.MIN_VALUE, Integer.MAX_VALUE)),
	/**
	 * Specifies the time (in ticks) the system will wait before incrementing to the
	 * next document. This value must be greater than or equal to 1. By default this
	 * is 1.
	 */
	INTERVAL(1, Range.between(1, Integer.MAX_VALUE));

	private final int defaultValue;
	private final Range<Integer> range;

	Timing(int defaultValue, Range<Integer> range) {
		this.defaultValue = defaultValue;
		this.range = range;

	}

	/**
	 * @return The default length/cycles if not set
	 */
	public int getDefaultValue() {
		return defaultValue;
	}

	/*
	 * @return The range that this value can be set to
	 */
	public Range<Integer> getRange() {
		return range;
	}

}
