package com.dinglian.server.chuqulang.listener;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.dinglian.server.chuqulang.base.ApplicationConfig;
import com.dinglian.server.chuqulang.model.Event;
import com.dinglian.server.chuqulang.service.ActivityService;
import com.dinglian.server.chuqulang.task.ActivityStatusTask;

public class ApplicationServletListener implements ServletContextListener {
	
	private static ScheduledExecutorService executorService = ApplicationConfig.getInstance().getScheduledThreadPool();
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(sce.getServletContext());
		ActivityService activityService = webApplicationContext.getBean(ActivityService.class);
		
		// 把所有报名中的活动，放到线程池中
		List<Event> singnUpActivitys = activityService.getSingnUpActivitys();
		for (Event event : singnUpActivitys) {
			/*executorService.submit(new Runnable() {
				
				@Override
				public void run() {
					if (event.getStartTime() != null) {
						long countdown = event.getStartTime().getTime() - System.currentTimeMillis();
						try {
							Thread thread = Thread.currentThread();
							thread.sleep(countdown);
							
							// 修改状态
							activityService.changeActivityStatus(event.getId(), Event.STATUS_PROGRESS);
							
//							thread.join();
							
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					
				}
			});*/
			executorService.submit(new ActivityStatusTask(event, activityService));
//			threadMap.put(event.getId(), task);
		}
	}

}
