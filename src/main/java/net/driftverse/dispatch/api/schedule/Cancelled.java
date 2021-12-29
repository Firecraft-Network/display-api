package net.driftverse.dispatch.api.schedule;

import net.driftverse.dispatch.api.enums.Result;

public final class Cancelled implements ScheduleResult {

	@Override
	public int id() {
		return -1;
	}

	@Override
	public Result result() {
		return Result.CANCELLED;
	}

	public int hashCode() {
		return result().hashCode();
	}
	
	public String toString() {
		return result().toString();
	}

}
