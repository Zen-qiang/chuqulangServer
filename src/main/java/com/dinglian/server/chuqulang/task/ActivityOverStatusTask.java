package com.dinglian.server.chuqulang.task;

import org.springframework.stereotype.Component;

import com.dinglian.server.chuqulang.model.Coterie;
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
					
					// 检查活动所属圈子，若解散中，判断是否存在进行中的活动，没有则改成已解散
					// 获取所属圈子
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
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

	}

}
