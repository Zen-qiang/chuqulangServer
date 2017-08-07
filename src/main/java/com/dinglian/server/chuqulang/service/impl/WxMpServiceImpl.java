package com.dinglian.server.chuqulang.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dinglian.server.chuqulang.dao.GeneralDao;
import com.dinglian.server.chuqulang.model.WxAccessToken;
import com.dinglian.server.chuqulang.model.WxOAuth2AccessToken;
import com.dinglian.server.chuqulang.service.WxMpService;

@Service
public class WxMpServiceImpl implements WxMpService {

	@Autowired
	private GeneralDao generalDao;

	@Override
	public void updateAccessToken(String accessToken) {
		WxAccessToken token = generalDao.findWxAccessTokenById(WxAccessToken.ACCESS_TOKEN_ID);
		if (token != null) {
			token.setAccessToken(accessToken);
			token.setModifiedDate(new Date());
		} else {
			token = new WxAccessToken();
			token.setAccessToken(accessToken);
			token.setModifiedDate(new Date());
			generalDao.saveWxAccessToken(token);
		}
		
	}

	@Override
	public void updateWxOAuth2AccessToken(WxOAuth2AccessToken wxOAuth2AccessToken) {
		WxOAuth2AccessToken token = generalDao.findWxOAuth2AccessTokenByOpenId(wxOAuth2AccessToken.getOpenId());
		if (token != null) {
			token.setAccessToken(wxOAuth2AccessToken.getAccessToken());
			token.setRefreshToken(wxOAuth2AccessToken.getRefreshToken());
			token.setOpenId(wxOAuth2AccessToken.getOpenId());
			token.setScope(wxOAuth2AccessToken.getScope());
			token.setModifiedDate(wxOAuth2AccessToken.getModifiedDate());
		} else {
			generalDao.saveWxOAuth2AccessToken(wxOAuth2AccessToken);
		}
	}

	@Override
	public WxOAuth2AccessToken findWxOAuth2AccessTokenByOpenId(String openId) {
		return generalDao.findWxOAuth2AccessTokenByOpenId(openId);
	}

}
