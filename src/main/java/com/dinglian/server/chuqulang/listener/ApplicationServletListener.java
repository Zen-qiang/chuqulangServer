package com.dinglian.server.chuqulang.listener;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.dinglian.server.chuqulang.base.ApplicationConfig;
import com.dinglian.server.chuqulang.model.Event;
import com.dinglian.server.chuqulang.service.JobService;
import com.dinglian.server.chuqulang.task.ActivityOverStatusTask;
import com.dinglian.server.chuqulang.task.ActivityProcessStatusTask;

public class ApplicationServletListener implements ServletContextListener {
	
	private static ScheduledExecutorService executorService = ApplicationConfig.getInstance().getScheduledThreadPool();
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(sce.getServletContext());
		JobService jobService = webApplicationContext.getBean(JobService.class);
		
		// 把所有报名中的活动，放到线程池中
		List<Event> singnUpActivitys = jobService.getSingnUpActivitys();
		for (Event event : singnUpActivitys) {
			executorService.submit(new ActivityProcessStatusTask(event, jobService));
			executorService.submit(new ActivityOverStatusTask(event, jobService));
		}
	}

}
