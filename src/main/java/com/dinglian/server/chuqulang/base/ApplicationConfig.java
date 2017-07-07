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
    
    public String getNeteaseImAppKey(){
    	String value = System.getProperty("netease.im.appkey", "92a54f3732261d964b5533e1b9b70d2e");
    	return value;
    }
    
    public String getNeteaseImAppSecret (){
    	String value = System.getProperty("netease.im.appsecret", "9ce13d5a0a6e");
    	return value;
    }
    
    public String getNeteaseImNonce (){
    	String value = System.getProperty("netease.im.nonce", "chuqulang");
    	return value;
    }

}
