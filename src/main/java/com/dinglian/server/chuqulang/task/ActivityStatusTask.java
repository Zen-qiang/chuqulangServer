package com.dinglian.server.chuqulang.task;

import com.dinglian.server.chuqulang.model.Event;
import com.dinglian.server.chuqulang.service.ActivityService;

public class ActivityStatusTask implements Runnable {
	
	private ActivityService activityService;
	
	private Event event;
	
	public ActivityStatusTask() {
		// TODO Auto-generated constructor stub
	}
	
	public ActivityStatusTask(Event event, ActivityService activityService) {
		super();
		this.event = event;
		this.activityService = activityService;
	}

	@Override
	public void run() {
		System.out.println(activityService);
		System.out.println(event);
		if (event.getStartTime() != null) {
			long countdown = event.getStartTime().getTime() - System.currentTimeMillis();
			System.out.println("countdown:" + countdown);
			try {
				Thread thread = Thread.currentThread();
				thread.sleep(countdown);
				// 修改状态
				activityService.changeActivityStatus(event.getId(), Event.STATUS_PROGRESS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

}
