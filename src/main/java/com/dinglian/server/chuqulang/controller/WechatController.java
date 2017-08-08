package com.dinglian.server.chuqulang.controller;

import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Consts;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.dinglian.server.chuqulang.base.ApplicationConfig;
import com.dinglian.server.chuqulang.base.SearchCriteria;
import com.dinglian.server.chuqulang.exception.AesException;
import com.dinglian.server.chuqulang.exception.UserException;
import com.dinglian.server.chuqulang.model.User;
import com.dinglian.server.chuqulang.model.VerifyNo;
import com.dinglian.server.chuqulang.model.WxOAuth2AccessToken;
import com.dinglian.server.chuqulang.service.UserService;
import com.dinglian.server.chuqulang.service.WxMpService;
import com.dinglian.server.chuqulang.utils.FileUploadHelper;
import com.dinglian.server.chuqulang.utils.NeteaseIMUtil;
import com.dinglian.server.chuqulang.utils.ResponseHelper;
import com.dinglian.server.chuqulang.utils.WXBizMsgCrypt;
import com.dinglian.server.chuqulang.utils.WxRequestHelper;

import net.sf.json.JSONObject;

/**
 * 微信服务号
 * 
 * @author Mr.xu
 *
 */
@Controller
@RequestMapping("/api")
public class WechatController {

	private static final Logger logger = LoggerFactory.getLogger(WechatController.class);

	@Autowired
	private UserService userService;

	@Autowired
	private WxMpService wxMpService;

	/**
	 * 验证微信服务号服务器
	 * 
	 * @param signature
	 *            微信加密签名，signature结合了开发者填写的token参数和请求中的timestamp参数、nonce参数。
	 * @param timestamp
	 *            时间戳
	 * @param nonce
	 *            随机数
	 * @param echostr
	 *            随机字符串
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/checkSignature", method = RequestMethod.GET)
	public String checkSignature(String signature, String timestamp, String nonce, String echostr) {
		String result = "";
		try {
			ApplicationConfig config = ApplicationConfig.getInstance();
			WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(config.getWxMpToken(), config.getWxMpEncodingAESKey(),
					config.getWxMpAppId());
			result = wxcpt.checkSignature(signature, timestamp, nonce, echostr);
		} catch (AesException e) {
			e.printStackTrace();
		}
		return result;
	}

	@ResponseBody
	@RequestMapping(value = "/getUserAuthorization", method = RequestMethod.GET)
	public ModelAndView userAuthorization() {
		logger.info("=====> Start to user authorization <=====");
		String url = "";
		try {
			ApplicationConfig config = ApplicationConfig.getInstance();
			String redirectUrl = URLEncoder.encode(config.getWxMpAuthorizeRedirectUrl());
			url = String.format(config.getWxMpAuthorizeCodeUrl(), config.getWxMpAppId(), redirectUrl);
			logger.info("url : " + url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("=====> User authorization info end <=====");
		return new ModelAndView(new RedirectView(url));
	}

	/**
	 * 通过code换取网页授权access_token
	 * 
	 * @param code
	 *            换取access_token的票据
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/getUserInfo", method = RequestMethod.GET)
	public Map<String, Object> getUserInfo(@RequestParam("code") String code, @RequestParam("state") String state) {
		logger.info("=====> Start to get OAuth2 access token <=====");
		logger.info("code : " + code + ", state : " + state);
		Map<String, Object> responseMap = new HashMap<String, Object>();
		try {
			String response = "";
			// 通过code换取网页授权access_token
			ApplicationConfig config = ApplicationConfig.getInstance();
			String url = String.format(config.getWxMpOAuth2AccessTokenUrl(), config.getWxMpAppId(),
					config.getWxMpAppSecret(), code);
			response = WxRequestHelper.doGet(url);

			if (response.indexOf("errcode") != -1) {
				logger.warn(response);
				JSONObject obj = JSONObject.fromObject(response);
				ResponseHelper.addResponseFailData(responseMap, obj.getInt("errcode"), obj.getString("errmsg"));
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

				User user = userService.getUserByOpenId(openId);
				if (user == null) {
					throw new UserException(UserException.NOT_REGISTER);
				}

				Map<String, Object> userMap = new HashMap<String, Object>();
				userMap.put("openId", user.getOpenId());
				userMap.put("userId", user.getId());
				userMap.put("phoneNo", user.getPhoneNo());
				userMap.put("nickName", user.getNickName());
				userMap.put("picture", user.getPicture());
				userMap.put("gender", user.getGender());
				userMap.put("birthday", user.getBirthday());

				ResponseHelper.addResponseSuccessData(responseMap, userMap);
			}
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.addResponseFailData(responseMap, e.getMessage());
		}
		logger.info("=====> Get OAuth2 access token end <=====");
		return responseMap;
	}

	/**
	 * 刷新网页授权AccessToken
	 * 
	 * @param openId
	 * @return
	 */
	@ResponseBody
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

			response = WxRequestHelper.getWxMpAuthorizeRefreshAccessToken(wxOAuth2AccessToken);

			if (response.indexOf("errcode") != -1) {
				logger.warn(response);
			} else {
				logger.info(response);
				JSONObject obj = JSONObject.fromObject(response);
				String accessToken = obj.getString("access_token");
				String refreshToken = obj.getString("refresh_token");
				String openIdVal = obj.getString("openid");
				String scope = obj.getString("scope");

				wxOAuth2AccessToken.setAccessToken(accessToken);
				wxOAuth2AccessToken.setRefreshToken(refreshToken);
				wxOAuth2AccessToken.setOpenId(openIdVal);
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

	/**
	 * 用户注册
	 * 
	 * @param openId
	 * @param phoneNo
	 * @param verifyNo
	 * @param birthday
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public Map<String, Object> register(@RequestParam("openId") String openId, @RequestParam("phoneNo") String phoneNo,
			@RequestParam("verifyNo") String verifyNo, @RequestParam("birthday") Date birthday) {
		logger.info("=====> Start to register user <=====");
		Map<String, Object> responseMap = new HashMap<String, Object>();
		try {
			if (!isMobile(phoneNo)) {
				throw new UserException(UserException.PHONE_NO_INVALID);
			}

			// 检查手机号码是否注册
			SearchCriteria searchCriteria = new SearchCriteria();
			searchCriteria.setPhoneNo(phoneNo);
			User existUser = userService.getUser(searchCriteria);
			if (existUser != null) {
				throw new UserException(UserException.PHONE_NO_REGISTERED);
			}

			VerifyNo existVerifyNo = userService.getVerifyNo(phoneNo, VerifyNo.VERIFY_NO_TYPE_REGISTER);
			if (existVerifyNo == null) {
				throw new UserException(UserException.VERIFY_CODE_INVALID);
			}
			Date currentTime = new Date();
			Date createTime = existVerifyNo.getCreationTime();
			long difference = currentTime.getTime() - createTime.getTime();

			if (difference > (10 * 60 * 1000)) {
				throw new UserException(UserException.VERIFY_CODE_EXPIRE);
			}
			if (!verifyNo.equalsIgnoreCase(existVerifyNo.getVerifyNo())) {
				throw new UserException(UserException.VERIFY_CODE_INVALID);
			}

			// 注册新用户
			// 拉取微信用户信息
			WxOAuth2AccessToken wxOAuth2AccessToken = wxMpService.findWxOAuth2AccessTokenByOpenId(openId);
			if (wxOAuth2AccessToken == null) {
				throw new NullPointerException("access token 不存在");
			}

			String responseStr = WxRequestHelper.getWxMpUserInfo(wxOAuth2AccessToken);

			if (responseStr.indexOf("errcode") != -1) {
				logger.warn(responseStr);
				JSONObject obj = JSONObject.fromObject(responseStr);
				ResponseHelper.addResponseFailData(responseMap, obj.getInt("errcode"), obj.getString("errmsg"));
			} else {
				logger.info(responseStr);
				JSONObject obj = JSONObject.fromObject(responseStr);
				String headimgurl = obj.getString("headimgurl");
				String sex = obj.getString("sex");
				String nickName = obj.getString("nickname");

				User user = new User();
				user.setPhoneNo(phoneNo);
				user.setGender(Integer.parseInt(sex));
				user.setNickName(nickName);
				user.setBirthday(birthday);

				if (StringUtils.isNotBlank(headimgurl)) {
					user.setPicture(FileUploadHelper.saveNetProfilePicture(phoneNo, headimgurl));
				}

				userService.register(user);
				existVerifyNo.setValid(false);
				userService.saveVerifyNo(existVerifyNo);

				Map<String, Object> userMap = new HashMap<String, Object>();
				userMap.put("openId", user.getOpenId());
				userMap.put("userId", user.getId());
				userMap.put("phoneNo", user.getPhoneNo());
				userMap.put("nickName", user.getNickName());
				userMap.put("picture", user.getPicture());
				userMap.put("gender", user.getGender());
				userMap.put("birthday", user.getBirthday());

				ResponseHelper.addResponseSuccessData(responseMap, userMap);
				
				// 注册云信ID
				String userProfilePicturePath = ApplicationConfig.getInstance().getUserProfilePicturePath();
				String response = NeteaseIMUtil.getInstance().create(user.getPhoneNo(), user.getPhoneNo(), "",
						String.format(userProfilePicturePath, user.getPhoneNo()), "");
				JSONObject responseObj = JSONObject.fromObject(response);
				if (responseObj.getInt("code") == 200) {
					JSONObject info = responseObj.getJSONObject("info");
					user.setAccid(info.getString("accid"));
					user.setToken(info.getString("token"));
					userService.saveOrUpdateUser(user);
				} else {
					ResponseHelper.addResponseFailData(responseMap, responseObj.getInt("code"),
							responseObj.getString("desc"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.addResponseFailData(responseMap, e.getMessage());
		}
		logger.info("=====> Register user end <=====");
		return responseMap;
	}

	private static boolean isMobile(String phoneNo) {
		String regex = ApplicationConfig.getInstance().getPhoneNoRegex();
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(phoneNo);
		return m.matches();
	}

}
