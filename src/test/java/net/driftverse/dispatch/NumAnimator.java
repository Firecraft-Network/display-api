package net.driftverse.dispatch;

import java.util.List;
import java.util.function.Function;

import display.api.Animator;

public class NumAnimator implements Animator<Integer, Integer> {

	private static final long serialVersionUID = -2323949806366244434L;

	@Override
	public Function<List<Integer>, Integer> concurency() {
		return l -> l.get(0);
	}

	@Override
	public int frames(Integer animation) {
		return animation;
	}

	@Override
	public Integer animate(Integer animation, int frame) {
		return frame;
	}

}
