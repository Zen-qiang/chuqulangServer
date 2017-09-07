package com.dinglian.server.chuqulang.task;

import com.dinglian.server.chuqulang.model.Event;
import com.dinglian.server.chuqulang.service.ActivityService;

public class ActivityProcessStatusTask implements Runnable {

	private ActivityService activityService;

	private Event event;

	public ActivityProcessStatusTask() {
		// TODO Auto-generated constructor stub
	}

	public ActivityProcessStatusTask(Event event, ActivityService activityService) {
		super();
		this.event = event;
		this.activityService = activityService;
	}

	@Override
	public void run() {
		if (event.getStartTime() != null) {
			long countdown = event.getStartTime().getTime() - System.currentTimeMillis();
			System.out.println("start time countdown:" + countdown);
			try {
				Thread thread = Thread.currentThread();
				thread.sleep(countdown);
				int count = activityService.getActivityUserCount(event.getId());
				if (count < event.getMinCount()) {
					// 人数不足最低人数时，活动发起失败
					activityService.changeActivityStatus(event.getId(), Event.STATUS_OVER);
					// 向组织者推送通知
				} else {
					activityService.changeActivityStatus(event.getId(), Event.STATUS_PROCESS);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
