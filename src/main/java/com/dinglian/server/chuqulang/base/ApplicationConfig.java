package com.dinglian.server.chuqulang.base;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.commons.lang.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationConfig {
	private static final Logger logger = LoggerFactory.getLogger(ApplicationConfig.class);

	private static final ApplicationConfig instance = new ApplicationConfig();

	private static final ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(10);
	private static ConcurrentHashMap<Integer, Thread> threadMap = new ConcurrentHashMap<Integer, Thread>();
	
	public static ApplicationConfig getInstance() {
		return instance;
	}
	
	public ScheduledExecutorService getScheduledThreadPool() {
		return scheduledThreadPool;
	}
	
	public ConcurrentHashMap<Integer, Thread> getActivityTaskThreadMap() {
		return threadMap;
	}

	public boolean isSmsDebugMode() {
		String value = System.getProperty("sms.debug.mode", "false");
		return BooleanUtils.toBoolean(value);
	}

	public String getNeteaseImAppKey() {
		String value = System.getProperty("netease.im.appkey", "92a54f3732261d964b5533e1b9b70d2e");
		return value;
	}

	public String getNeteaseImAppSecret() {
		String value = System.getProperty("netease.im.appsecret", "9ce13d5a0a6e");
		return value;
	}

	public String getNeteaseImNonce() {
		String value = System.getProperty("netease.im.nonce", "chuqulang");
		return value;
	}
	
	public String getAliyunOSSEndpoint() {
		String value = System.getProperty("netease.im.nonce", "oss-cn-shanghai.aliyuncs.com");
		return value;
	}
	public String getAliyunOSSAccessKeyId() {
		String value = System.getProperty("aliyun.oss.accesskeyid", "LTAI4YaSyHSEzGZ2");
		return value;
	}
	
	public String getAliyunOSSAccessSecret() {
		String value = System.getProperty("aliyun.oss.accessSecret", "TEifc7QUvgp3nX0Z8PlSPfNqRMW3qL");
		return value;
	}
	
	public String getAliyunOSSBucketName() {
		String value = System.getProperty("aliyun.oss.bucketName", "langlang2go");
		return value;
	}

	public int getDefaultPageSize() {
		String value = System.getProperty("default.pagesize", "20");
		return Integer.parseInt(value);
	}

	public String getPhoneNoRegex() {
		return System.getProperty("phone.no.regex", "^1(3|4|5|7|8)\\d{9}$");
	}

	public String getResourceRoot() {
		return System.getProperty("resource.root", "/Debug-Server/apache-tomcat-8.0.32/webapps");
	}
	
	public String getResourceFolder() {
		return System.getProperty("resource.folder", "/chuqulang-resource");
	}

	public String getResourceProfileFolder() {
		return System.getProperty("resource.profile.folder", "/profile");
	}
	
	public String getResourceActivityFolder() {
		return System.getProperty("resource.activity.folder", "/activity");
	}
	
	public String getResourceCoterieFolder() {
		return System.getProperty("resource.coterie.folder", "/coterie");
	}
	
	public String getUserProfilePath() {
		return System.getProperty("resource.user.profile.folder", "profile");
	}
	
	public String getActivityPicturePath() {
		return System.getProperty("resource.activity.picture.folder", "activity");
	}
	
	public String getCoterieCoverPath() {
		return System.getProperty("resource.coterie.cover.folder", "coterie");
	}

	public String getUserProfilePicturePath() {
		return System.getProperty("resource.profile.picture.path", "/profile/%s/avatar.png");
	}
	
	public String getWxMpToken () {
		return System.getProperty("wx.mp.token", "dingliantech");
	}
	
	public String getWxMpEncodingAESKey () {
		return System.getProperty("wx.mp.encoding.aeskey", "tOPAjsqxYq3hFsnA3ukdyxus6KfD7rVi0hdl3eBJS3E");
	}
	
	public String getWxMpAppId () {
		return System.getProperty("wx.mp.appid", "wxc9ec7e5bf6f50c00");
	}
	
	public String getWxMpOpenId () {
		return System.getProperty("wx.mp.openid", "gh_e3ce9c0e2b0a");
	}
	
	public String getWxMpAppSecret () {
		return System.getProperty("wx.mp.appsecret", "5b5e8f910bd61cb3a9214f0443eb4230");
	}
	
	public String getWxMpAccessTokenUrl () {
		return System.getProperty("wx.mp.access.token.url", "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s");
	}
	
	public String getWxMpOAuth2AccessTokenUrl () {
		return System.getProperty("wx.mp.oauth2.access.token.url", "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code");
	}
	
	public String getWxMpAuthorizeCodeUrl () {
		return System.getProperty("wx.mp.oauth2.code.url", "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect");
	}
	
	public String getWxMpAuthorizeRedirectUrl () {
		return System.getProperty("wx.mp.oauth2.redirect.url", "http://mp.dingliantech.com/#/authorization");
	}
	
	public String getWxMpAuthorizeRefreshAccessTokenUrl () {
		return System.getProperty("wx.mp.oauth2.access.token.refresh.url", "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=%s&grant_type=refresh_token&refresh_token=%s");
	}

	public String getWxMpUserInfoUrl() {
		return System.getProperty("wx.mp.user.info.url", "https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s&lang=zh_CN");
	}

	public String getWxJsApiTicketUrl() {
		return System.getProperty("wx.mp.jsapi.ticket.url", "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=%s&type=jsapi");
	}

	public String getWxTemplateMsgUrl() {
		return System.getProperty("wx.mp.activity.signup.msg.url", "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=%s");
	}
	
	// 活动报名通知
	public String getWxActivitySignUpTemplateId() {
		return System.getProperty("wx.mp.activity.signup.template.id", "P6O8aKh7VIUYQbeO5vJOETgufIirhMNumRpIa4vPhRg");
	}
	
	// 活动取消通知
	public String getWxActivitySignOutTemplateId() {
		return System.getProperty("wx.mp.activity.signout.template.id", "9fI5MUb9owz63iOkSBuvyeL8zdq_8Y0mJjb6B6gIPt0");
	}
	
	// 活动变动通知
	public String getWxActivityTemplateId() {
		return System.getProperty("wx.mp.activity.template.id", "HA5OlpWvC9nfAUkmzEMaH_2_gKVvrp7895ZdfCf9V8A");
	}
	
	// 加入圈子通知
	public String getWxCoterieJoinTemplateId() {
		return System.getProperty("wx.mp.coterie.join.template.id", "xvl-18koMMuIVgUww02Lkvjaq_txBY45KbGOKX81cNM");
	}
	
	public String getWxMpDomain() {
		return System.getProperty("wx.mp.domain", "http://mp.dingliantech.com");
	}
	
	public String getWxActivityDetails() {
		return System.getProperty("wx.mp.activity.path", "/activityDetails");
	}
	
	public String getWxCoterieDetails() {
		return System.getProperty("wx.mp.coterie.path", "/circleDetails");
	}

	public String getWxMpDownloadServerFileUrl() {
		return System.getProperty("wx.mp.download.server.file.url", "http://file.api.weixin.qq.com/cgi-bin/media/get?access_token=%s&media_id=%s");
	}
	
	public String getWxMpBasicUserInfoUrl() {
		return System.getProperty("wx.mp.basic.user.info.url", "https://api.weixin.qq.com/cgi-bin/user/info?access_token=%s&openid=%s&lang=zh_CN");
	}
	
	public boolean enableActivityStatusTask() {
		String value = System.getProperty("activity.status.task.enable", "false");
		return Boolean.parseBoolean(value);
	}
}
