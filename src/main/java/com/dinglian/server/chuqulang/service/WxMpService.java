package com.dinglian.server.chuqulang.service;

import java.util.List;

import com.dinglian.server.chuqulang.model.NewsMaterial;
import com.dinglian.server.chuqulang.model.WxOAuth2AccessToken;

public interface WxMpService {

	void updateAccessToken(String accessToken);

	void updateWxOAuth2AccessToken(WxOAuth2AccessToken wxOAuth2AccessToken);

	WxOAuth2AccessToken findWxOAuth2AccessTokenByOpenId(String openId);

	String getWxAccessToken();

	void updateJsApiTicket(String ticket);

	String getWxJsApiTicket();

	void updateNewsMaterial(List<NewsMaterial> materialList);

}
