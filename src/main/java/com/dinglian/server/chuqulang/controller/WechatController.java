package com.dinglian.server.chuqulang.controller;

import java.net.URLEncoder;
import java.util.Date;

import org.apache.http.Consts;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dinglian.server.chuqulang.base.ApplicationConfig;
import com.dinglian.server.chuqulang.exception.AesException;
import com.dinglian.server.chuqulang.model.WxOAuth2AccessToken;
import com.dinglian.server.chuqulang.service.WxMpService;
import com.dinglian.server.chuqulang.utils.WXBizMsgCrypt;

import net.sf.json.JSONObject;

/**
 * 微信服务号
 * 
 * @author Mr.xu
 *
 */
@RestController
@RequestMapping("/api")
public class WechatController {

	private static final Logger logger = LoggerFactory.getLogger(WechatController.class);

//	@Autowired
//	private UserService userService;
	
	@Autowired
	private WxMpService wxMpService;

	/**
	 * 验证微信服务号服务器
	 * @param signature		微信加密签名，signature结合了开发者填写的token参数和请求中的timestamp参数、nonce参数。
	 * @param timestamp		时间戳
	 * @param nonce			随机数
	 * @param echostr		随机字符串
	 * @return
	 */
	@RequestMapping(value = "/checkSignature", method = RequestMethod.GET)
	public String checkSignature(String signature, String timestamp, String nonce, String echostr) {
		String result = "";
		try {
			ApplicationConfig config = ApplicationConfig.getInstance();
			WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(config.getWxMpToken(), config.getWxMpEncodingAESKey(), config.getWxMpAppId());
			result = wxcpt.checkSignature(signature, timestamp, nonce, echostr);
		} catch (AesException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	@RequestMapping(value = "/getUserInfo", method = RequestMethod.GET)
	public String getUserInfo() {
		logger.info("=====> Start to get user info <=====");
		String response = "";
		try {
			ApplicationConfig config = ApplicationConfig.getInstance();
			String redirectUrl = URLEncoder.encode(config.getWxMpAuthorizeRedirectUrl());
			String url = config.getWxMpAuthorizeCodeUrl();
			url = String.format(url, config.getWxMpAppId(), redirectUrl);
			logger.info("url : " + url);
			
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpGet httpGet = new HttpGet(url);
			CloseableHttpResponse httpResponse = httpclient.execute(httpGet);
			response = EntityUtils.toString(httpResponse.getEntity(), Consts.UTF_8);
			
			logger.info("response : " + response);
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("=====> Get user info end <=====");
		return response;
	}
	
	/**
	 * 通过code换取网页授权access_token
	 * @param code	换取access_token的票据
	 * @return
	 */
	@RequestMapping(value = "/getOAuth2AccessToken", method = RequestMethod.GET)
	public String getOAuth2AccessToken(@RequestParam("code") String code, @RequestParam("state") String state) {
		logger.info("=====> Start to get OAuth2 access token <=====");
		logger.info("code : " + code + ", state : " + state);
		String response = "";
		try {
			ApplicationConfig config = ApplicationConfig.getInstance();
			String url = config.getWxMpOAuth2AccessTokenUrl();
			url = String.format(url, config.getWxMpAppId(), config.getWxMpAppSecret(), code);
			
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpGet httpGet = new HttpGet(url);
			CloseableHttpResponse httpResponse = httpclient.execute(httpGet);
			response = EntityUtils.toString(httpResponse.getEntity(), Consts.UTF_8);
			
			if (response.indexOf("errcode") != -1) {
				logger.warn(response);
			} else {
				logger.info(response);
				JSONObject obj = JSONObject.fromObject(response);
				String accessToken = obj.getString("access_token");
				String refreshToken = obj.getString("refresh_token");
				String openId = obj.getString("openid");
				String scope = obj.getString("scope");
				
				WxOAuth2AccessToken wxOAuth2AccessToken = new WxOAuth2AccessToken();
				wxOAuth2AccessToken.setAccessToken(accessToken);
				wxOAuth2AccessToken.setRefreshToken(refreshToken);
				wxOAuth2AccessToken.setOpenId(openId);
				wxOAuth2AccessToken.setScope(scope);
				wxOAuth2AccessToken.setModifiedDate(new Date());
				
				wxMpService.updateWxOAuth2AccessToken(wxOAuth2AccessToken);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("=====> Get OAuth2 access token end <=====");
		return response;
	}
	
	@RequestMapping(value = "/refreshAuthAccessToken", method = RequestMethod.GET)
	public String refreshAuthAccessToken(@RequestParam("openId") String openId) {
		logger.info("=====> Start to refresh OAuth2 access token <=====");
		logger.info("openId : " + openId);
		String response = "";
		try {
			WxOAuth2AccessToken wxOAuth2AccessToken = wxMpService.findWxOAuth2AccessTokenByOpenId(openId);
			if (wxOAuth2AccessToken == null) {
				throw new NullPointerException("access token 不存在");
			}
			
			ApplicationConfig config = ApplicationConfig.getInstance();
			String url = config.getWxMpAuthorizeRefreshAccessTokenUrl();
			url = String.format(url, config.getWxMpAppId(), wxOAuth2AccessToken.getRefreshToken());
			
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpGet httpGet = new HttpGet(url);
			CloseableHttpResponse httpResponse = httpclient.execute(httpGet);
			response = EntityUtils.toString(httpResponse.getEntity(), Consts.UTF_8);
			
			if (response.indexOf("errcode") != -1) {
				logger.warn(response);
			} else {
				logger.info(response);
				JSONObject obj = JSONObject.fromObject(response);
				String accessToken = obj.getString("access_token");
				String refreshToken = obj.getString("refresh_token");
				String openIdVal = obj.getString("openid");
				String scope = obj.getString("scope");
				
				WxOAuth2AccessToken token = new WxOAuth2AccessToken();
				token.setAccessToken(accessToken);
				token.setRefreshToken(refreshToken);
				token.setOpenId(openIdVal);
				token.setScope(scope);
				token.setModifiedDate(new Date());
				
				wxMpService.updateWxOAuth2AccessToken(token);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("=====> Get OAuth2 access token end <=====");
		return response;
	}
	
	
//	@RequestMapping("/launchActivity")
//	public Map<String, Object> test() {
//		Map<String, Object> resultMap = new HashMap<String, Object>();
//		try {
//			logger.info("=====> Start to launch activity <=====");
//
//			Subject currentUser = SecurityUtils.getSubject();
//			User user = (User) currentUser.getSession().getAttribute(User.CURRENT_USER);
//
//			logger.info("=====> Launch activity end <=====");
//			ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_OK, "");
//		} catch (Exception e) {
//			e.printStackTrace();
//			ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_FAIL, e.getMessage());
//		}
//		return resultMap;
//	}

}
