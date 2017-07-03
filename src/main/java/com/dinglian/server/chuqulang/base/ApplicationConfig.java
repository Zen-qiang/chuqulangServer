package com.dinglian.server.chuqulang.base;

import org.apache.commons.lang.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationConfig {
	private static final Logger logger = LoggerFactory.getLogger(ApplicationConfig.class);
	
	private static final ApplicationConfig instance = new ApplicationConfig();

    public static ApplicationConfig getInstance(){
        return instance;
    }
    
    public boolean isSmsDebugMode(){
    	String value = System.getProperty("sms.debug.mode", "false");
        return BooleanUtils.toBoolean(value);
    }

}
