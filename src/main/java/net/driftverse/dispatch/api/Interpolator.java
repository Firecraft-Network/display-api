package net.driftverse.dispatch.api;

public interface Interpolator<Dispatch> {

	/**
	 * Peek gets the documents that need to be dispatched, without incrementing to
	 * the next set of frames. This is useful for Dispatchers like titles where fade
	 * in and out are factors. During the fade in and outs of titles, no new frames
	 * can be sent. This internal Minecraft animation must be fully played out
	 * before another frame is sent.
	 * <p>
	 * Most dispatchers do not need this feature. If poll is never called, the
	 * system will be stuck on the same document for eternity.
	 * 
	 * @return The documents of each synthesizer that need to be dispatched
	 */
	Dispatch peek();

	/**
	 * Pool gets the documents that need to be dispatch, while simetaneouly
	 * incrementing to the next set of frames.
	 * 
	 * @return The documents of each synthesizer that need to be dispatched
	 */
	Dispatch poll();

	int delay();

	int interval();

	int cycles();

	int cycleDelay();

	int finalDelay();

	/**
	 * Sometimes you just gotta know when it all ends.
	 * 
	 * @return If any of the assigned synthesizers have an infinite duration
	 */
	boolean infinite();

	/**
	 * This is not needed in most cases but in cases like titles, you have to know
	 * when to play out the title timing fade in packet. This helps with knowing
	 * additional information
	 * 
	 * @return If this is the very first frame of the entire interpolator
	 */
	boolean firstInterpolation();

	/**
	 * This is not needed in most cases but in cases like titles, you have to know
	 * when to play out the title timing fade out packet. This helps with knowing
	 * additional information
	 * 
	 * @return If this is the very last frame of the entire interpolator
	 */
	boolean lastInterpolation();

}
