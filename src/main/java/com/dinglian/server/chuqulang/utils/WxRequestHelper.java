package com.dinglian.server.chuqulang.utils;

import java.io.IOException;

import org.apache.http.Consts;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.dinglian.server.chuqulang.base.ApplicationConfig;
import com.dinglian.server.chuqulang.model.WxOAuth2AccessToken;

public class WxRequestHelper {
	
	public static String doGet(String url) throws ParseException, IOException{
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(url);
		CloseableHttpResponse httpResponse = httpclient.execute(httpGet);
		return EntityUtils.toString(httpResponse.getEntity(), Consts.UTF_8);
	}
	
	public static String getWxMpUserInfo(WxOAuth2AccessToken wxOAuth2AccessToken) throws ParseException, IOException {
		ApplicationConfig config = ApplicationConfig.getInstance();
		String url = String.format(config.getWxMpUserInfoUrl(), wxOAuth2AccessToken.getAccessToken(), wxOAuth2AccessToken.getOpenId());
		return doGet(url);
	}

	public static String getWxMpAuthorizeRefreshAccessToken(WxOAuth2AccessToken wxOAuth2AccessToken) throws ParseException, IOException {
		ApplicationConfig config = ApplicationConfig.getInstance();
		String url = String.format(config.getWxMpAuthorizeRefreshAccessTokenUrl(), config.getWxMpAppId(), wxOAuth2AccessToken.getRefreshToken());
		return doGet(url);
	}

}
