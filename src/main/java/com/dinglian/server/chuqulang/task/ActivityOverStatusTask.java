package com.dinglian.server.chuqulang.task;

import com.dinglian.server.chuqulang.model.Event;
import com.dinglian.server.chuqulang.service.ActivityService;

public class ActivityOverStatusTask implements Runnable {

	private ActivityService activityService;

	private Event event;

	public ActivityOverStatusTask() {
		// TODO Auto-generated constructor stub
	}

	public ActivityOverStatusTask(Event event, ActivityService activityService) {
		super();
		this.event = event;
		this.activityService = activityService;
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
					activityService.changeActivityStatus(event.getId(), Event.STATUS_OVER);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

	}

}
