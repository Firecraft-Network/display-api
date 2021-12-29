package net.driftverse.dispatch.api.schedule;

import net.driftverse.dispatch.api.enums.Result;

public final class Sucess implements ScheduleResult {

	private final int id;

	Sucess(int id) {
		this.id = id;
	}

	@Override
	public int id() {
		return id;
	}

	@Override
	public Result result() {
		return Result.SUCESS;
	}

	public int hashCode() {
		return id();
	}

	public String toString() {
		return result().toString();
	}
}
