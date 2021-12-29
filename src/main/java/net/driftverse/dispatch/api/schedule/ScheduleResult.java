package net.driftverse.dispatch.api.schedule;

import net.driftverse.dispatch.api.enums.Result;

public sealed interface ScheduleResult permits Sucess,Cancelled,Failed {

	int id();

	Result result();

	static ScheduleResult sucess(int id) {
		return new Sucess(id);
	}

	static ScheduleResult cancelled() {
		return new Cancelled();
	}

	static ScheduleResult failed() {
		return new Failed();
	}
}
