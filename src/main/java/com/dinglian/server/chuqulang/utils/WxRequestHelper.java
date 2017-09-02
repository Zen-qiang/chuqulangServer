package com.dinglian.server.chuqulang.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.dinglian.server.chuqulang.base.ApplicationConfig;
import com.dinglian.server.chuqulang.model.Event;
import com.dinglian.server.chuqulang.model.EventUser;
import com.dinglian.server.chuqulang.model.User;
import com.dinglian.server.chuqulang.model.WxOAuth2AccessToken;

import net.sf.json.JSONObject;

public class WxRequestHelper {
	
	private static ApplicationConfig config = ApplicationConfig.getInstance();
	
	public static String doGet(String url) throws ParseException, IOException{
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(url);
		CloseableHttpResponse httpResponse = httpclient.execute(httpGet);
		return EntityUtils.toString(httpResponse.getEntity(), Consts.UTF_8);
	}
	
	public static String doJsonPost(String uri, JSONObject params) throws ParseException, IOException{
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
	
	public static String getWxMpUserInfo(WxOAuth2AccessToken wxOAuth2AccessToken) throws ParseException, IOException {
		String url = String.format(config.getWxMpUserInfoUrl(), wxOAuth2AccessToken.getAccessToken(), wxOAuth2AccessToken.getOpenId());
		return doGet(url);
	}

	public static String getWxMpAuthorizeRefreshAccessToken(WxOAuth2AccessToken wxOAuth2AccessToken) throws ParseException, IOException {
		String url = String.format(config.getWxMpAuthorizeRefreshAccessTokenUrl(), config.getWxMpAppId(), wxOAuth2AccessToken.getRefreshToken());
		return doGet(url);
	}

	public static String getWxJsApiTicket(String accessToken) throws ParseException, IOException {
		String url = String.format(config.getWxJsApiTicketUrl(), accessToken);
		return doGet(url);
	}
	
	private static String getTemplateMsgUrl(String accessToken) {
		return String.format(config.getWxActivitySignUpMsgUrl(), accessToken);
	}
	
	public static void sendActivitySignUpMsg(String accessToken, Event event, User user) throws ParseException, IOException {
		String uri = getTemplateMsgUrl(accessToken);
		
		JSONObject params = new JSONObject();
		params.accumulate("touser", user.getOpenId());
		params.accumulate("template_id", config.getWxActivitySignUpTemplateId());
		params.accumulate("url", config.getWxMpDomain() + config.getWxActivityDetails() + "/" + user.getId() + "/" + event.getId());
		
		Map<String, Object> dataMap = new HashMap<String, Object>();
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("value", "您好，您已报名成功。");
		dataMap.put("first", map);
		map = new HashMap<>();
		map.put("value", user.getNickName());
		dataMap.put("keyword1", map);
		map = new HashMap<>();
		map.put("value", event.getName());
		dataMap.put("keyword2", map);
		map = new HashMap<>();
		map.put("value", DateUtils.format(event.getStartTime(), DateUtils.yMdHm));
		dataMap.put("keyword3", map);
		map = new HashMap<>();
		map.put("value", event.getAddress());
		dataMap.put("keyword4", map);
		map = new HashMap<>();
		String remark = "当前报名人数：%d人（最多%d人）";
		map.put("value", String.format(remark, event.getEffectiveMembers().size(), event.getMaxCount()));
		dataMap.put("remark", map);
		
		params.accumulate("data", dataMap);
		doJsonPost(uri, params);
	}
	
	public static void sendActivitySignOutMsg(String accessToken, Event event, EventUser currentUser, boolean hasRetinues) throws ParseException, IOException {
		String uri = getTemplateMsgUrl(accessToken);
		
		JSONObject params = new JSONObject();
		params.accumulate("touser", event.getCreator().getOpenId());
		params.accumulate("template_id", config.getWxActivitySignOutTemplateId());
		params.accumulate("url", config.getWxMpDomain() + config.getWxActivityDetails() + "/" + currentUser.getId() + "/" + event.getId());
		
		Map<String, Object> dataMap = new HashMap<String, Object>();
		
		Map<String, String> map = new HashMap<String, String>();
		String firstStr = "%s 已经退出“%s”：";
		String name = currentUser.getRealName();
		if (hasRetinues) {
			name += "和TA的小伙伴";
		}
		firstStr = String.format(firstStr, name, event.getName());
		map.put("value", firstStr);
		dataMap.put("first", map);
		
		map = new HashMap<>();
		map.put("value", currentUser.getRealName());
		dataMap.put("keyword1", map);
		
		map = new HashMap<>();
		map.put("value", DateUtils.format(new Date(), DateUtils.yMdHms));
		dataMap.put("keyword2", map);
		
		map = new HashMap<>();
		String remark = "联系电话：%s\n当前已报名人数 %d人，活动最大可报名人数 %d人）";
		map.put("value", String.format(remark, currentUser.getPhoneNo(), event.getEffectiveMembers().size(), event.getMaxCount()));
		dataMap.put("remark", map);
		
		params.accumulate("data", dataMap);
		doJsonPost(uri, params);
	}

	public static List<String> downloadServerFileToAliyunOSS(String folder, String[] serverIds, String accessToken) throws ClientProtocolException, IOException {
		List<String> resultList = new ArrayList<>();
		for (String serverId : serverIds) {
			String url = String.format(config.getWxMpDownloadServerFileUrl(), accessToken, serverId);
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpGet httpGet = new HttpGet(url);
			CloseableHttpResponse httpResponse = httpclient.execute(httpGet);
			HttpEntity entry = httpResponse.getEntity();
			
			AliyunOSSUtil util = AliyunOSSUtil.getInstance();
			String fileName = FileUploadHelper.generateTempImageFileName();
			String picPath = util.putObject(folder +"/" + fileName, entry.getContent());
			if (StringUtils.isNotBlank(picPath)) {
				resultList.add(picPath);
			}
		}
		return resultList;
	}

}
