package com.dinglian.server.chuqulang.listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.dinglian.server.chuqulang.base.ApplicationConfig;
import com.dinglian.server.chuqulang.model.Event;
import com.dinglian.server.chuqulang.model.SensitiveWord;
import com.dinglian.server.chuqulang.service.JobService;
import com.dinglian.server.chuqulang.task.ActivityOverStatusTask;
import com.dinglian.server.chuqulang.task.ActivityProcessStatusTask;
import com.dinglian.server.chuqulang.utils.SensitiveWordUtil;

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
		
		// 加载敏感词汇
		List<SensitiveWord> sensitiveWords = jobService.loadAllSensitiveWord();
		if (sensitiveWords != null) {
			SensitiveWordUtil sensitiveWordUtil = SensitiveWordUtil.getInstance();
			for (SensitiveWord sensitiveWord : sensitiveWords) {
				String[] array = sensitiveWord.getSensitiveWord().split(";");
				sensitiveWordUtil.init(sensitiveWord.getId(), new HashSet(Arrays.asList(array)));
			}
		}
		
		// 把所有报名中的活动，放到线程池中
		List<Event> singnUpActivitys = jobService.getSingnUpActivitys();
		for (Event event : singnUpActivitys) {
			// 开始时间倒计时，仅报名中活动
			if (event.getStatus().equalsIgnoreCase(Event.STATUS_SIGNUP)) {
				executorService.submit(new ActivityProcessStatusTask(event, jobService));
			}
			// 结束时间倒计时
			executorService.submit(new ActivityOverStatusTask(event, jobService));
		}
	}

}
