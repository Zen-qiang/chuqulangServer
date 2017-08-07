package com.dinglian.server.chuqulang.base;

import java.io.File;

import org.apache.commons.lang.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationConfig {
	private static final Logger logger = LoggerFactory.getLogger(ApplicationConfig.class);

	private static final ApplicationConfig instance = new ApplicationConfig();

	public static ApplicationConfig getInstance() {
		return instance;
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
		return System.getProperty("resource.user.profile.path", getResourceRoot() + getResourceFolder() + getResourceProfileFolder() + "/%s/");
	}
	
	public String getActivityPicturePath() {
		return System.getProperty("resource.activity.picture.path", getResourceRoot() + getResourceFolder() + getResourceActivityFolder() + "/%s/");
	}
	
	public String getCoterieCoverPath() {
		return System.getProperty("resource.coterie.cover.path", getResourceRoot() + getResourceFolder() + getResourceCoterieFolder() + "/%s/");
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
		return System.getProperty("wx.mp.oauth2.redirect.url", "http://www.dingliantech.com/api/getOAuth2AccessToken");
	}
	
	public String getWxMpAuthorizeRefreshAccessTokenUrl () {
		return System.getProperty("wx.mp.oauth2.access.token.refresh.url", "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=%s&grant_type=refresh_token&refresh_token=%s ");
	}
}
