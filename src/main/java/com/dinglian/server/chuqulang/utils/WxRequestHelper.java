package com.dinglian.server.chuqulang.utils;

import java.io.IOException;
import java.util.ArrayList;
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
import com.dinglian.server.chuqulang.model.Coterie;
import com.dinglian.server.chuqulang.model.Event;
import com.dinglian.server.chuqulang.model.EventUser;
import com.dinglian.server.chuqulang.model.User;
import com.dinglian.server.chuqulang.model.WxOAuth2AccessToken;

import net.sf.json.JSONObject;

public class WxRequestHelper {

	private static ApplicationConfig config = ApplicationConfig.getInstance();

	public static String doGet(String url) throws ParseException, IOException {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(url);
		CloseableHttpResponse httpResponse = httpclient.execute(httpGet);
		return EntityUtils.toString(httpResponse.getEntity(), Consts.UTF_8);
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

	public static String getWxMpUserInfo(WxOAuth2AccessToken wxOAuth2AccessToken) throws ParseException, IOException {
		String url = String.format(config.getWxMpUserInfoUrl(), wxOAuth2AccessToken.getAccessToken(),
				wxOAuth2AccessToken.getOpenId());
		return doGet(url);
	}

	public static String getWxMpAuthorizeRefreshAccessToken(WxOAuth2AccessToken wxOAuth2AccessToken)
			throws ParseException, IOException {
		String url = String.format(config.getWxMpAuthorizeRefreshAccessTokenUrl(), config.getWxMpAppId(),
				wxOAuth2AccessToken.getRefreshToken());
		return doGet(url);
	}

	public static String getWxJsApiTicket(String accessToken) throws ParseException, IOException {
		String url = String.format(config.getWxJsApiTicketUrl(), accessToken);
		return doGet(url);
	}

	public static List<String> downloadServerFileToAliyunOSS(String folder, String[] serverIds, String accessToken)
			throws ClientProtocolException, IOException {
		List<String> resultList = new ArrayList<>();
		for (String serverId : serverIds) {
			String url = String.format(config.getWxMpDownloadServerFileUrl(), accessToken, serverId);
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpGet httpGet = new HttpGet(url);
			CloseableHttpResponse httpResponse = httpclient.execute(httpGet);
			HttpEntity entry = httpResponse.getEntity();

			AliyunOSSUtil util = AliyunOSSUtil.getInstance();
			String fileName = FileUploadHelper.generateTempImageFileName();
			String picPath = util.upload(folder + "/" + fileName, entry.getContent());
			if (StringUtils.isNotBlank(picPath)) {
				resultList.add(picPath);
			}
		}
		return resultList;
	}

	private static String getActivityRedirectUrl(int activityId) {
		return config.getWxMpDomain() + "/#" + config.getWxActivityDetails() + "/" + activityId;
	}

	private static String getCoterieRedirectUrl(int coterieId) {
		return config.getWxMpDomain() + "/#" + config.getWxCoterieDetails() + "/" + coterieId;
	}

	private static String getTemplateMsgUrl(String accessToken) {
		return String.format(config.getWxTemplateMsgUrl(), accessToken);
	}

	private static void sendTemplateMsg(String uri, String templateId, String touser, String url, String first,
			List<String> keywordList, String remark) throws ParseException, IOException {
		Map<String, Object> dataMap = new HashMap<String, Object>();

		Map<String, String> map;
		if (StringUtils.isNotBlank(first)) {
			map = new HashMap<String, String>();
			map.put("value", first);
			dataMap.put("first", map);
		}

		for (int i = 0; i < keywordList.size(); i++) {
			map = new HashMap<>();
			map.put("value", keywordList.get(i));
			dataMap.put("keyword" + (i + 1), map);
		}

		if (StringUtils.isNotBlank(remark)) {
			map = new HashMap<>();
			map.put("value", remark);
			dataMap.put("remark", map);
		}
		JSONObject params = new JSONObject();
		params.accumulate("touser", touser);
		params.accumulate("template_id", templateId);
		params.accumulate("url", url);
		params.accumulate("data", dataMap);
		doJsonPost(uri, params);
	}

	public static void sendActivityLaunchFailureToCreator(String accessToken, Event event, User creator)
			throws ParseException, IOException {
		String uri = getTemplateMsgUrl(accessToken);
		String templateId = config.getWxActivityTemplateId();
		String url = getActivityRedirectUrl(event.getId());

		String first = "亲爱的%s，由于人数不达最低限制，您的活动已失效。";
		first = String.format(first, creator.getNickName());

		List<String> keywordList = new ArrayList<String>();
		keywordList.add(event.getName());
		keywordList.add(DateUtils.format(event.getStartTime(), DateUtils.yMdHm) + " - "
				+ DateUtils.format(event.getEndTime(), DateUtils.yMdHm));
		keywordList.add(event.getAddress());

		String remark = "";
		sendTemplateMsg(uri, templateId, creator.getOpenId(), url, first, keywordList, remark);
	}

	public static void sendActivityLaunchFailureToUser(String accessToken, Event event, User user)
			throws ParseException, IOException {
		String uri = getTemplateMsgUrl(accessToken);
		String templateId = config.getWxActivityTemplateId();
		String url = getActivityRedirectUrl(event.getId());

		String first = "亲爱的%s，组织者取消了本次活动。";
		first = String.format(first, user.getNickName());

		List<String> keywordList = new ArrayList<String>();
		keywordList.add(event.getName());
		keywordList.add(DateUtils.format(event.getStartTime(), DateUtils.yMdHm) + " - "
				+ DateUtils.format(event.getEndTime(), DateUtils.yMdHm));
		keywordList.add(event.getAddress());

		String remark = "";
		sendTemplateMsg(uri, templateId, user.getOpenId(), url, first, keywordList, remark);
	}

	// 报名成功通知 - 参与者
	public static void sendActivitySignUpToUser(String accessToken, Event event, EventUser eventUser)
			throws ParseException, IOException {
		String uri = getTemplateMsgUrl(accessToken);
		String templateId = config.getWxActivitySignUpTemplateId();
		String url = getActivityRedirectUrl(event.getId());

		String first = "您好，您已报名成功。";

		List<String> keywordList = new ArrayList<String>();
		keywordList.add(eventUser.getRealName());
		keywordList.add(event.getName());
		keywordList.add(DateUtils.format(event.getStartTime(), DateUtils.yMdHm));
		keywordList.add(event.getAddress());

		String remark = "当前报名人数：%d人（最多%d人）";
		remark = String.format(remark, event.getEffectiveMembers().size(), event.getMaxCount());
		sendTemplateMsg(uri, templateId, eventUser.getUser().getOpenId(), url, first, keywordList, remark);
	}

	// 报名成功通知 - 创建者
	public static void sendActivitySignUpToCreator(String accessToken, Event event, EventUser eventUser)
			throws ParseException, IOException {
		String uri = getTemplateMsgUrl(accessToken);
		String templateId = config.getWxActivityTemplateId();
		String url = getActivityRedirectUrl(event.getId());

		String first = "%s报名了您的活动，已报名人数%d，最大可报名人数%d";
		first = String.format(first, eventUser.getRealName(), event.getEffectiveMembers().size(), event.getMaxCount());

		List<String> keywordList = new ArrayList<String>();
		keywordList.add(event.getName());
		keywordList.add(DateUtils.format(event.getStartTime(), DateUtils.yMdHm) + " - "
				+ DateUtils.format(event.getEndTime(), DateUtils.yMdHm));
		keywordList.add(event.getAddress());

		String remark = "";
		sendTemplateMsg(uri, templateId, event.getCreator().getOpenId(), url, first, keywordList, remark);
	}

	// 取消报名通知 - 创建者
	public static void sendActivitySignOutToCreator(String accessToken, Event event, EventUser eventUser)
			throws ParseException, IOException {
		String uri = getTemplateMsgUrl(accessToken);
		String templateId = config.getWxActivityTemplateId();
		String url = getActivityRedirectUrl(event.getId());

		String first = "%s退出了您的活动，已报名人数%d，最大可报名人数%d";
		first = String.format(first, eventUser.getRealName(), event.getEffectiveMembers().size(), event.getMaxCount());

		List<String> keywordList = new ArrayList<String>();
		keywordList.add(event.getName());
		keywordList.add(DateUtils.format(event.getStartTime(), DateUtils.yMdHm) + " - "
				+ DateUtils.format(event.getEndTime(), DateUtils.yMdHm));
		keywordList.add(event.getAddress());

		String remark = "";
		sendTemplateMsg(uri, templateId, event.getCreator().getOpenId(), url, first, keywordList, remark);
	}

	// 圈子发起活动通知
	public static void sendCoterieActivityLauncher(String accessToken, Event event, Coterie coterie, User user)
			throws ParseException, IOException {
		String uri = getTemplateMsgUrl(accessToken);
		String templateId = config.getWxActivityTemplateId();
		String url = getActivityRedirectUrl(event.getId());

		String first = "亲爱的%s，您关注的圈子有新活动。";
		first = String.format(first, user.getNickName());

		List<String> keywordList = new ArrayList<String>();
		keywordList.add(event.getName());
		keywordList.add(DateUtils.format(event.getStartTime(), DateUtils.yMdHm) + " - "
				+ DateUtils.format(event.getEndTime(), DateUtils.yMdHm));
		keywordList.add(event.getAddress());

		String remark = "所属圈子：" + coterie.getName();
		sendTemplateMsg(uri, templateId, user.getOpenId(), url, first, keywordList, remark);
	}

	// 组团完成通知
	public static void sendActivitySignFull(String accessToken, Event event, EventUser eventUser)
			throws ParseException, IOException {
		String uri = getTemplateMsgUrl(accessToken);
		String templateId = config.getWxActivityTemplateId();
		String url = getActivityRedirectUrl(event.getId());

		String first = "你的活动组织成功，请准时参与。";

		List<String> keywordList = new ArrayList<String>();
		keywordList.add(event.getName());
		keywordList.add(DateUtils.format(event.getStartTime(), DateUtils.yMdHm) + " - "
				+ DateUtils.format(event.getEndTime(), DateUtils.yMdHm));
		keywordList.add(event.getAddress());

		String remark = "";
		sendTemplateMsg(uri, templateId, eventUser.getUser().getOpenId(), url, first, keywordList, remark);
	}

	/**
	 * 关闭活动通知
	 * @param accessToken
	 * @param event 
	 * @param effectiveMembers
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public static void sendActivityClose(String accessToken, Event event) throws ParseException, IOException {
		String uri = getTemplateMsgUrl(accessToken);
		String templateId = config.getWxActivityTemplateId();
		String url = getActivityRedirectUrl(event.getId());

		String first = "您参与的活动已被组织者解散";

		List<String> keywordList = new ArrayList<String>();
		keywordList.add(event.getName());
		keywordList.add(DateUtils.format(event.getStartTime(), DateUtils.yMdHm) + " - "
				+ DateUtils.format(event.getEndTime(), DateUtils.yMdHm));
		keywordList.add(event.getAddress());

		String remark = "";
		
		for (EventUser eventUser : event.getEffectiveMembers()) {
			sendTemplateMsg(uri, templateId, eventUser.getUser().getOpenId(), url, first, keywordList, remark);
		}
	}
}
