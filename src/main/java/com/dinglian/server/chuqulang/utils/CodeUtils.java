package com.dinglian.server.chuqulang.utils;

import java.util.Properties;
import java.util.Random;

import org.apache.commons.httpclient.URIException;
import org.apache.commons.lang.BooleanUtils;

import com.dahantc.api.sms.json.JSONHttpClient;
import com.dinglian.server.chuqulang.base.ApplicationConfig;
import com.dinglian.server.chuqulang.model.VerifyNo;

public class CodeUtils {
	
	public static String generateVerifyNo(){
		StringBuffer verifyNo = new StringBuffer();
		Random random = new Random();
		String[] code = {"0","1","2","3","4","5","6","7","8","9"};
		for (int j = 0; j < 6; j++) {
			verifyNo.append(code[random.nextInt(10)]);
		}
		return verifyNo.toString();
		
	}
	
	public static String sendSms(VerifyNo verifyNo, Properties prop) throws URIException{
		String url = prop.getProperty("sms.url");
		String account = prop.getProperty("sms.account");
		String password = prop.getProperty("sms.password");
		String sign = prop.getProperty("sms.sign");
		String subcode = prop.getProperty("sms.subcode");
		String template = prop.getProperty("sms.template.verifyno");
		
		String content = String.format(template, verifyNo.getVerifyNo());
		
		ApplicationConfig config = ApplicationConfig.getInstance();
		if (config.isSmsDebugMode()) {
			return content;
		} else {
			JSONHttpClient jsonHttpClient = new JSONHttpClient(url);
			jsonHttpClient.setRetryCount(1);
			String sendhRes = jsonHttpClient.sendSms(account, password, verifyNo.getPhoneNo(), content, sign, subcode);
			return sendhRes;
			
		}
	}
}
