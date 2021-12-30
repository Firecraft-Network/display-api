package net.driftverse.dispatch;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import net.driftverse.dispatch.api.AbstractSynthesizer;

public class NumSyn extends AbstractSynthesizer<NumSyn, Integer> {

	private static final long serialVersionUID = -2323949806366244434L;

	private final int frames;

	public NumSyn(int frames) {
		this.frames = frames;
	}

	@Override
	public Class<Integer> frame() {
		return Integer.class;
	}

	@Override
	public int frames(UUID interpolator) {
		return frames;
	}

	@Override
	public List<Integer> cumulativeSynthesis() {
		return IntStream.range(0, frames(null)).boxed().collect(Collectors.toList());
	}

	@Override
	public Integer randomSynthesis(UUID interpolator, int ticks) {
		return ticks;
	}

}
