package com.dinglian.server.chuqulang.task;

import java.util.List;

import org.springframework.stereotype.Component;

import com.dinglian.server.chuqulang.model.Event;
import com.dinglian.server.chuqulang.model.User;
import com.dinglian.server.chuqulang.service.JobService;
import com.dinglian.server.chuqulang.utils.WxRequestHelper;

@Component
public class ActivityProcessStatusTask implements Runnable {

	private JobService jobService;

	private Event event;

	public ActivityProcessStatusTask() {
		// TODO Auto-generated constructor stub
	}

	public ActivityProcessStatusTask(Event event, JobService jobService) {
		super();
		this.event = event;
		this.jobService = jobService;
	}

	@Override
	public void run() {
		if (event.getStartTime() != null) {
			long countdown = event.getStartTime().getTime() - System.currentTimeMillis();
			System.out.println("start time countdown:" + countdown);
			try {
				Thread thread = Thread.currentThread();
				thread.sleep(countdown);
				int count = jobService.getActivityUserCount(event.getId());
				if (count < event.getMinCount()) {
					// 人数不足最低人数时，活动发起失败
					jobService.changeActivityStatus(event.getId(), Event.STATUS_OVER);
					// 向组织者推送通知
					User creator = jobService.getActivityCreator(event.getId());
					List<User> activityMembers = jobService.getActivityMembers(event.getId());
					String accessToken = jobService.getWxAccessToken();
					WxRequestHelper.sendActivityLaunchFailureToCreator(accessToken, event, creator);
					if (activityMembers != null) {
						for (User user : activityMembers) {
							if (creator.getId() != user.getId()) {
								WxRequestHelper.sendActivityLaunchFailureToUser(accessToken, event, user);
							}
						}
					}
				} else {
					jobService.changeActivityStatus(event.getId(), Event.STATUS_PROCESS);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
