package net.driftverse.dispatch;

import java.util.List;

import display.api.Animator;

public class NumAnimator implements Animator<Integer, Integer> {

	@Override
	public Integer combine(List<Integer> frames) {
		return frames.get(0);
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
