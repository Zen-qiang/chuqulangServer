package com.dinglian.server.chuqulang.utils;

import com.dahantc.api.sms.json.JSONHttpClient;
import com.dahantc.api.sms.json.SmsData;
import com.dinglian.server.chuqulang.base.ApplicationConfig;
import com.dinglian.server.chuqulang.model.Event;
import com.dinglian.server.chuqulang.model.EventUser;
import com.dinglian.server.chuqulang.model.VerifyNo;
import org.apache.commons.httpclient.URIException;

import java.util.*;

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

	public static String batchSubmit(Properties prop, Event event, List<EventUser> sendToList) throws URIException {
		String account = prop.getProperty("sms.account");
		String password = prop.getProperty("sms.password");
		String sign = prop.getProperty("sms.sign");
		String subcode = prop.getProperty("sms.subcode");
		String notification = prop.getProperty("sms.template.notification");
		String url = prop.getProperty("sms.url");
		
    	String sendtime = "";
    	List<SmsData> list = new ArrayList<SmsData>();
    	for (EventUser sendTo : sendToList) {
    		if (sendTo.getUser() != null) {
    			String content = String.format(notification, sendTo.getRealName(), event.getName(), event.getAddress(), DateUtils.format(event.getStartTime(), DateUtils.yMdHmCN));
    			UUID uuid = UUID.randomUUID();
    			String msgid = uuid.toString().replaceAll("-", "");
    			
    			list.add(new SmsData(sendTo.getPhoneNo(), content, msgid, sign, subcode, sendtime));
			}
		}
		
    	JSONHttpClient jsonHttpClient = new JSONHttpClient(url);
		jsonHttpClient.setRetryCount(1);
		String sendBatchRes = jsonHttpClient.sendBatchSms(account, password, list);
		return sendBatchRes;
	}

}
