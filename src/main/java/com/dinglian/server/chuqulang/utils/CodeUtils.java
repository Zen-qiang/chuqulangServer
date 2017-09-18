package com.dinglian.server.chuqulang.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.httpclient.URIException;
import org.apache.commons.lang.BooleanUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.dahantc.api.commons.EncryptUtil;
import com.dahantc.api.sms.json.JSONHttpClient;
import com.dinglian.server.chuqulang.base.ApplicationConfig;
import com.dinglian.server.chuqulang.model.Event;
import com.dinglian.server.chuqulang.model.EventUser;
import com.dinglian.server.chuqulang.model.VerifyNo;

import net.sf.json.JSONObject;

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

	public static String batchSubmit(Properties prop, Event event, List<EventUser> sendToList) throws ParseException, IOException {
		String account = prop.getProperty("sms.account");
		String password = prop.getProperty("sms.password");
		String sign = prop.getProperty("sms.sign");
		String subcode = prop.getProperty("sms.subcode");
		String notification = prop.getProperty("sms.template.notification");
		String uri = prop.getProperty("sms.batch.submit.uri");
		
    	JSONObject params = new JSONObject();
    	params.accumulate("account", account);
    	params.accumulate("password", EncryptUtil.MD5Encode(password));
    	
    	List<Map> dataMapList = new ArrayList<Map>();
    	for (EventUser sendTo : sendToList) {
    		if (sendTo.getUser() != null) {
    			String content = String.format(notification, sendTo.getRealName(), event.getName(), event.getAddress(), DateUtils.format(event.getStartTime(), DateUtils.yMdHmCN));
    			UUID uuid = UUID.randomUUID();
    			String msgid = uuid.toString().replaceAll("-", "");
    			
    			Map<String, Object> map = new HashMap<String, Object>();
    			map.put("msgid", msgid);
    			map.put("phones", sendTo.getPhoneNo());
    			map.put("content", content);
        		map.put("sign", sign);
        		map.put("subcode", subcode);
        		map.put("sendtime", "");
    			dataMapList.add(map);
			}
		}
    	params.accumulate("data", dataMapList);
    	
		return doJsonPost(uri, params);
	}
	
	public static String doJsonPost(String uri, JSONObject params) throws ParseException, IOException {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(uri);

		StringEntity stringEntity = new StringEntity(params.toString(), Consts.UTF_8);
		stringEntity.setContentEncoding("UTF-8");
		stringEntity.setContentType("application/json");

		httpPost.setEntity(stringEntity);

		CloseableHttpResponse httpResponse = httpclient.execute(httpPost);
		HttpEntity entity = httpResponse.getEntity();

		String response = EntityUtils.toString(entity, Consts.UTF_8);

		EntityUtils.consume(entity);
		httpResponse.close();
		return response;
	}
}
