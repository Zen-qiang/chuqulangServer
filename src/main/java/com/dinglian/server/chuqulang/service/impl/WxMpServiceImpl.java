package com.dinglian.server.chuqulang.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dinglian.server.chuqulang.dao.GeneralDao;
import com.dinglian.server.chuqulang.model.WxAccessToken;
import com.dinglian.server.chuqulang.model.WxJsApiTicket;
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
		if (token == null) {
			generalDao.saveWxOAuth2AccessToken(wxOAuth2AccessToken);
		} else {
			token.setAccessToken(wxOAuth2AccessToken.getAccessToken());
			token.setModifiedDate(new Date());
			token.setRefreshToken(wxOAuth2AccessToken.getRefreshToken());
			token.setScope(wxOAuth2AccessToken.getScope());
		}
	}

	@Override
	public WxOAuth2AccessToken findWxOAuth2AccessTokenByOpenId(String openId) {
		return generalDao.findWxOAuth2AccessTokenByOpenId(openId);
	}

	@Override
	public String getWxAccessToken() {
		WxAccessToken token = generalDao.findWxAccessTokenById(WxAccessToken.ACCESS_TOKEN_ID);
		if (token != null) {
			return token.getAccessToken();
		}
		return null;
	}

	@Override
	public void updateJsApiTicket(String ticket) {
		WxJsApiTicket jsApiTicket = generalDao.findWxJsApiTicketById(WxJsApiTicket.JSAPI_TICKET_ID);
		if (jsApiTicket != null) {
			jsApiTicket.setTicket(ticket);
			jsApiTicket.setModifiedDate(new Date());
		} else {
			jsApiTicket = new WxJsApiTicket();
			jsApiTicket.setTicket(ticket);
			jsApiTicket.setModifiedDate(new Date());
			generalDao.saveWxJsApiTicket(jsApiTicket);
		}
		
	}

	@Override
	public String getWxJsApiTicket() {
		WxJsApiTicket ticket = generalDao.findWxJsApiTicketById(WxJsApiTicket.JSAPI_TICKET_ID);
		if (ticket != null) {
			return ticket.getTicket();
		}
		return null;
	}

}
