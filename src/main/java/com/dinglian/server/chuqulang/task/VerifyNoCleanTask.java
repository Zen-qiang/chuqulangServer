package com.dinglian.server.chuqulang.task;

import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.dinglian.server.chuqulang.service.UserService;

@Component
public class VerifyNoCleanTask {

	private static final Logger logger = LoggerFactory.getLogger(VerifyNoCleanTask.class);
	
	@Autowired
	private UserService userService;
	
	@Scheduled(cron="00 00 00 * * MON")
//	@Scheduled(cron="00 11 13 * * ?")
	public void cleanUp() {
		logger.info("Start the verify no clean up.");
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, -7);
		
		userService.verifyNoCleanUp(calendar.getTime());
		
		logger.info("Clean up verify no finished.");
	}

}
