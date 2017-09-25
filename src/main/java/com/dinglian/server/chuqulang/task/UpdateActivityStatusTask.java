package com.dinglian.server.chuqulang.task;

import java.io.IOException;
import java.util.List;

import org.apache.http.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.dinglian.server.chuqulang.base.ApplicationConfig;
import com.dinglian.server.chuqulang.model.Coterie;
import com.dinglian.server.chuqulang.model.Event;
import com.dinglian.server.chuqulang.model.EventUser;
import com.dinglian.server.chuqulang.model.User;
import com.dinglian.server.chuqulang.service.JobService;
import com.dinglian.server.chuqulang.utils.WxRequestHelper;

@Component
public class UpdateActivityStatusTask {

	private static final Logger logger = LoggerFactory.getLogger(UpdateActivityStatusTask.class);

	@Autowired
	private JobService jobService;

	/**
	 * 每分钟检测更新活动状态
	 */
	@Scheduled(cron = "10 0/1 * * * ?")
	public void run() {

		ApplicationConfig config = ApplicationConfig.getInstance();
		if (config.enableActivityStatusTask()) {

			logger.info("Start to update activity status");
			// System.out.println("execute update ...!!!");
			long currentTimeMillis = System.currentTimeMillis();

			// 获取当天所有报名中/进行中的活动
			List<Event> activitys = jobService.getTodayAllActivitys();

			for (Event event : activitys) {
				try {
					if (event.getStatus().equalsIgnoreCase(Event.STATUS_SIGNUP)) {
						List<User> activityMembers = jobService.getActivityMembers(event.getId());
						// 人数不足最低人数时，活动发起失败
						if (activityMembers.size() < event.getMinCount()) {
							jobService.changeActivityStatus(event.getId(), Event.STATUS_OVER);

							String accessToken = jobService.getWxAccessToken();
							// 向组织者推送通知
							User creator = jobService.getActivityCreator(event.getId());
							if (creator != null) {
								WxRequestHelper.sendActivityLaunchFailureToCreator(accessToken, event, creator);
							}

							// 向成员推送通知
							for (User user : activityMembers) {
								if (user.getId() != creator.getId()) {
									WxRequestHelper.sendActivityLaunchFailureToUser(accessToken, event, user);
								}
							}
							continue;
						}
						// 当前时间超过开始时间，则状态改成进行中
						if (currentTimeMillis > event.getStartTime().getTime()) {
							jobService.changeActivityStatus(event.getId(), Event.STATUS_PROCESS);
							continue;
						}
					} else if (event.getStatus().equalsIgnoreCase(Event.STATUS_PROCESS)) {
						if (currentTimeMillis > event.getEndTime().getTime()) {
							// 检查活动结束
							jobService.changeActivityStatus(event.getId(), Event.STATUS_OVER);
							
							// 检查活动所属圈子，若解散中，判断是否存在进行中的活动，没有则改成已解散
							Coterie coterie = jobService.getCoterieByActivityId(event.getId());
							// 判断圈子是否解散中
							if (coterie != null && coterie.getStatus() == Coterie.STATUS_DISMISSING) {
								// 检查是否有进行中的活动
								boolean hasActivityProcess = jobService.hasActivityProcess(coterie.getId());
								if (!hasActivityProcess) {
									// 修改圈子状态
									jobService.changeCoterieStatus(coterie.getId(), Coterie.STATUS_DISMISSED);
								}
							}
						}
					}
				} catch (ParseException | IOException e) {
					// TODO Auto-generated catch block
					logger.error(e.getMessage());
					e.printStackTrace();
					continue;
				}
			}

			// 当前时间超过结束时间，则检查圈子是否解散

			logger.info("Update activity status end");

		}
	}

}
