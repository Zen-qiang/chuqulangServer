package com.dinglian.server.chuqulang.task;

import java.io.IOException;

import org.apache.http.Consts;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.dinglian.server.chuqulang.base.ApplicationConfig;
import com.dinglian.server.chuqulang.service.WxMpService;

import net.sf.json.JSONObject;

@Component
public class UpdateWxMpAccessTokenTask {

	private static final Logger logger = LoggerFactory.getLogger(UpdateWxMpAccessTokenTask.class);
	
	@Autowired
	private WxMpService wxMpService;
	
	/**
	 * 每小时更新一次Access_token
	 */
	@Scheduled(cron="0 0 0/1 * * ?")
//	@Scheduled(cron="0 14 13/1 * * ?")
	public void run() {
		logger.info("Start the get WX mp acces token.");
		
		ApplicationConfig config = ApplicationConfig.getInstance();
		
		String url = config.getWxMpAccessTokenUrl();
		url = String.format(url, config.getWxMpAppId(), config.getWxMpAppSecret());
		
		try {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpGet httpGet = new HttpGet(url);
			CloseableHttpResponse httpResponse = httpclient.execute(httpGet);
			String response = EntityUtils.toString(httpResponse.getEntity(), Consts.UTF_8);
			
			if (response.indexOf("errcode") != -1) {
				logger.warn(response);
			} else {
				logger.info(response);
				JSONObject obj = JSONObject.fromObject(response);
				String accessToken = obj.getString("access_token");
				wxMpService.updateAccessToken(accessToken);
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		logger.info("Get WX mp access token end.");
	}

}
