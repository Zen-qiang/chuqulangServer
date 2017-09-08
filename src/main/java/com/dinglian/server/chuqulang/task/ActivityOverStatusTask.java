package com.dinglian.server.chuqulang.task;

import org.springframework.stereotype.Component;

import com.dinglian.server.chuqulang.model.Event;
import com.dinglian.server.chuqulang.service.JobService;

@Component
public class ActivityOverStatusTask implements Runnable {

	private JobService jobService;

	private Event event;

	public ActivityOverStatusTask() {
		// TODO Auto-generated constructor stub
	}

	public ActivityOverStatusTask(Event event, JobService jobService) {
		super();
		this.event = event;
		this.jobService = jobService;
	}

	@Override
	public void run() {
		if (event.getEndTime() != null) {
			long countdown = event.getEndTime().getTime() - System.currentTimeMillis();
			System.out.println("end time countdown:" + countdown);
			if (countdown > 0) {
				try {
					Thread thread = Thread.currentThread();
					thread.sleep(countdown);
					jobService.changeActivityStatus(event.getId(), Event.STATUS_OVER);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

	}

}
