package net.driftverse.dispatch;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import net.driftverse.dispatch.api.AbstractSynthesizer;

public class NumSyn extends AbstractSynthesizer<NumSyn, Integer> {

	private static final long serialVersionUID = -2323949806366244434L;

	private final int frames;
	private final Boolean mode;

	public NumSyn(int frames, Boolean mode) {
		this.frames = frames;
		this.mode = mode;
	}

	@Override
	public Logger logger() {
		return Logger.getLogger("Num-Syn");
	}

	@Override
	public Class<Integer> frame() {
		return Integer.class;
	}

	@Override
	public int frames() {
		return frames;
	}

	@Override
	public List<Integer> cumulativeSynthesis() {
		// if null or false
		return mode == null || !mode ? IntStream.range(0, frames()).boxed().collect(Collectors.toList()) : null;
	}

	@Override
	public Integer randomSynthesis(int ticks) {
		return mode == null || mode ? ticks : null; // If null or true
	}

}
