package com.dinglian.server.chuqulang.controller;

import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.http.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.dinglian.server.chuqulang.base.ApplicationConfig;
import com.dinglian.server.chuqulang.base.SearchCriteria;
import com.dinglian.server.chuqulang.comparator.EventPictureComparator;
import com.dinglian.server.chuqulang.comparator.TopicCommentComparator;
import com.dinglian.server.chuqulang.comparator.TopicPraiseComparator;
import com.dinglian.server.chuqulang.exception.ActivityException;
import com.dinglian.server.chuqulang.exception.AesException;
import com.dinglian.server.chuqulang.exception.ApplicationServiceException;
import com.dinglian.server.chuqulang.exception.UserException;
import com.dinglian.server.chuqulang.model.Coterie;
import com.dinglian.server.chuqulang.model.CoterieCarouselPicture;
import com.dinglian.server.chuqulang.model.CoterieGuy;
import com.dinglian.server.chuqulang.model.CoteriePicture;
import com.dinglian.server.chuqulang.model.CoterieTag;
import com.dinglian.server.chuqulang.model.Event;
import com.dinglian.server.chuqulang.model.EventPicture;
import com.dinglian.server.chuqulang.model.EventTag;
import com.dinglian.server.chuqulang.model.EventUser;
import com.dinglian.server.chuqulang.model.NewsMaterial;
import com.dinglian.server.chuqulang.model.Tag;
import com.dinglian.server.chuqulang.model.Topic;
import com.dinglian.server.chuqulang.model.TopicComment;
import com.dinglian.server.chuqulang.model.TopicPraise;
import com.dinglian.server.chuqulang.model.User;
import com.dinglian.server.chuqulang.model.UserCoterieSetting;
import com.dinglian.server.chuqulang.model.VerifyNo;
import com.dinglian.server.chuqulang.model.WxOAuth2AccessToken;
import com.dinglian.server.chuqulang.service.ActivityService;
import com.dinglian.server.chuqulang.service.DiscoverService;
import com.dinglian.server.chuqulang.service.UserService;
import com.dinglian.server.chuqulang.service.WxMpService;
import com.dinglian.server.chuqulang.utils.AliyunOSSUtil;
import com.dinglian.server.chuqulang.utils.CodeUtils;
import com.dinglian.server.chuqulang.utils.DateUtils;
import com.dinglian.server.chuqulang.utils.FileUploadHelper;
import com.dinglian.server.chuqulang.utils.ResponseHelper;
import com.dinglian.server.chuqulang.utils.SensitiveWordUtil;
import com.dinglian.server.chuqulang.utils.WXBizMsgCrypt;
import com.dinglian.server.chuqulang.utils.WxRequestHelper;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 微信服务号
 * 
 * @author Mr.xu
 *
 */
@Api(value = "/api", description = "微信服务号相关接口", tags = "微信服务号API")
@RestController
@RequestMapping("/api")
public class WechatController {

	private static final Logger logger = LoggerFactory.getLogger(WechatController.class);

	@Autowired
	private UserService userService;

	@Autowired
	private DiscoverService discoverService;

	@Autowired
	private ActivityService activityService;

	@Autowired
	private WxMpService wxMpService;
	
//	@Autowired
//	private JobService jobService;
	
	@Autowired
	private HttpServletRequest request;
	
	private static ApplicationConfig config = ApplicationConfig.getInstance();
	private static SensitiveWordUtil sensitiveWordUtil = SensitiveWordUtil.getInstance();
	
	@ApiOperation(value = "跳转授权页面", httpMethod = "GET", notes = "跳转授权页面")
	@GetMapping("/authorization")
	public String authorization() {
		return "authorization";
	}

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
	@ApiOperation(value = "微信服务器验证", httpMethod = "GET", notes = "微信服务器验证")
	@GetMapping(value = "/security")
	public String checkSignature(String signature, String timestamp, String nonce, String echostr,
			HttpServletRequest request, HttpServletResponse response) {
		ApplicationConfig config = ApplicationConfig.getInstance();
		try {
			WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(config.getWxMpToken(), config.getWxMpEncodingAESKey(),config.getWxMpAppId());
			if (wxcpt.checkSignature(signature, timestamp, nonce)) {
				return echostr;
			}
		} catch (AesException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@ApiOperation(value = "接收微信推送的消息", httpMethod = "POST", notes = "接收微信推送的消息")
	@PostMapping(value = "/security")
	public void doPost(
			@RequestParam(name = "msg_signature", required = false) String msgSignature, 
			@RequestParam(name = "timestamp", required = false) String timeStamp,
			@RequestParam(name = "nonce", required = false) String nonce,
			HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/xml");
		String replyMsg = "success";
		try {
			PrintWriter out = response.getWriter();
	        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document document = db.parse(request.getInputStream());

			// 事件推送
			Element root = document.getDocumentElement();
			NodeList typeNode = root.getElementsByTagName("MsgType");
			String msgType = typeNode.item(0).getTextContent();
			
			if (msgType.equalsIgnoreCase("event")) {
				NodeList eventNode = root.getElementsByTagName("Event");
				String event = eventNode.item(0).getTextContent();
				
				NodeList eventKeyNode = root.getElementsByTagName("EventKey");
				String eventKey = eventKeyNode.item(0).getTextContent();
				// 关注事件
				NodeList fromUserNode = root.getElementsByTagName("FromUserName");
				String fromUserOpenId = fromUserNode.item(0).getTextContent();
				
				if (event.equalsIgnoreCase("subscribe")) {
					
					String content = "出趣浪，欢迎您～\n更多玩趣，组团活动小工具\n就在出趣浪～\n";
					replyMsg = "<xml><ToUserName><![CDATA[%s]]></ToUserName><FromUserName><![CDATA[%s]]></FromUserName><CreateTime>%d</CreateTime><MsgType><![CDATA[text]]></MsgType><Content><![CDATA[%s]]></Content></xml>";
					replyMsg = String.format(replyMsg, fromUserOpenId, config.getWxMpOpenId(), System.currentTimeMillis(), content);
				} else if (event.equalsIgnoreCase("click")) {
					if (eventKey.equalsIgnoreCase("CLASS_ROOM")) {
						replyMsg = "<xml><ToUserName><![CDATA[%s]]></ToUserName><FromUserName><![CDATA[%s]]></FromUserName><CreateTime>%d</CreateTime><MsgType><![CDATA[news]]></MsgType><ArticleCount>1</ArticleCount><Articles><item><Title><![CDATA[%s]]></Title> <Description><![CDATA[%s]]></Description><PicUrl><![CDATA[%s]]></PicUrl><Url><![CDATA[%s]]></Url></item></Articles></xml>";
						
						String title = "中秋国庆，谁的鸡儿也别想放假！";
						String description = "长假与小伙伴们的趣浪神器";
						String picUrl = "http://mmbiz.qpic.cn/mmbiz_jpg/lTicf4cPiaoSdyvDFsTwnaOwjqgEphFianfhXQriaF1DibItg4PUZkyCmr8eEW0VWnMAzkfOzEJpyf0r1PSbZ42cichw/0?wx_fmt=jpeg";
						String url = "http://mp.weixin.qq.com/s?__biz=MzIzNTg4NjQwOA==&mid=100000017&idx=1&sn=5f31de8fedd1e9483bcfc5744e61c54b&chksm=68e103365f968a20d4870ff1f125b498608aba0ee0bd5189e7f16bb76f6432ee7eeaad5826e3#rd";
						replyMsg = String.format(replyMsg, fromUserOpenId, config.getWxMpOpenId(), System.currentTimeMillis(), title, description, picUrl, url);
					} else if (eventKey.equalsIgnoreCase("CONTACT_US")) {
						replyMsg = "<xml><ToUserName><![CDATA[%s]]></ToUserName><FromUserName><![CDATA[%s]]></FromUserName><CreateTime>%d</CreateTime><MsgType><![CDATA[news]]></MsgType><ArticleCount>1</ArticleCount><Articles><item><Title><![CDATA[%s]]></Title> <Description><![CDATA[%s]]></Description><PicUrl><![CDATA[%s]]></PicUrl><Url><![CDATA[%s]]></Url></item></Articles></xml>";
						
						String title = "联系我们";
						String description = "很正经的联系方式";
						String picUrl = "http://mmbiz.qpic.cn/mmbiz_jpg/lTicf4cPiaoSdyvDFsTwnaOwjqgEphFianfyVoiaCCQWStmfKzKzvrKchBP7MrlMTSyoziaS7jmyswlbNDEGkDRSuvA/0?wx_fmt=jpeg";
						String url = "http://mp.weixin.qq.com/s?__biz=MzIzNTg4NjQwOA==&mid=100000018&idx=1&sn=0eb277bc7ca140cc292e32541de625e7&chksm=68e103355f968a239507caa8cc4c95f53beab7758f856f42df2e6b9d9aa32acd23fa18c1c635#rd";
						replyMsg = String.format(replyMsg, fromUserOpenId, config.getWxMpOpenId(), System.currentTimeMillis(), title, description, picUrl, url);
					}
				}
			}
			out.print(replyMsg);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
//		return replyMsg;
	}

	@ApiOperation(value = "跳转用户授权", httpMethod = "GET", notes = "跳转用户授权")
	@GetMapping(value = "/userAuthorization")
	public String userAuthorization(/*@RequestParam("callbackUrl") String callbackUrl*/) {
		logger.info("=====> Start to user authorization <=====");
		String url = "";
		try {
			ApplicationConfig config = ApplicationConfig.getInstance();
			String redirectUrl = URLEncoder.encode(config.getWxMpAuthorizeRedirectUrl()/* + "?redirectUrl=" + callbackUrl*/);
			url = String.format(config.getWxMpAuthorizeCodeUrl(), config.getWxMpAppId(), redirectUrl);
			logger.info("url : " + url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("=====> User authorization info end <=====");
//		return new ModelAndView(new RedirectView(url));
		return url;
	}

	@ApiOperation(value = "获取网页授权", httpMethod = "GET", notes = "通过code换取网页授权access_token")
	@GetMapping(value = "/getAccessToken")
	public Map<String, Object> getAccessToken(@RequestParam("code") String code, @RequestParam("state") String state) {
		logger.info("=====> Start to get OAuth2 access token <=====");
		Map<String, Object> responseMap = new HashMap<String, Object>();
		Map<String, Object> dataMap = new HashMap<String, Object>();
		try {
			String response = "";
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
				String openid = obj.getString("openid");
				String scope = obj.getString("scope");

				WxOAuth2AccessToken wxOAuth2AccessToken = new WxOAuth2AccessToken();
				wxOAuth2AccessToken.setAccessToken(accessToken);
				wxOAuth2AccessToken.setRefreshToken(refreshToken);
				wxOAuth2AccessToken.setOpenId(openid);
				wxOAuth2AccessToken.setScope(scope);
				wxOAuth2AccessToken.setModifiedDate(new Date());

				wxMpService.updateWxOAuth2AccessToken(wxOAuth2AccessToken);

				dataMap.put("openId", openid);
//				dataMap.put("accessToken", accessToken);
//				dataMap.put("refreshToken", refreshToken);
//				dataMap.put("scope", scope);
				
				/*User user = userService.getUserByOpenId(openid);
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
				userMap.put("birthday", user.getBirthday());*/

				ResponseHelper.addResponseSuccessData(responseMap, dataMap);
			}
			logger.info("=====> Get OAuth2 access token end <=====");
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.addResponseFailData(responseMap, e.getMessage());
		}
		return responseMap;
	}
	
	@ApiOperation(value = "检查用户是否关注", httpMethod = "GET", notes = "检查用户是否关注")
	@GetMapping(value = "/checkSubscribe")
	public Map<String, Object> checkSubscribe(@RequestParam("openId") String openId) {
		logger.info("=====> Start to check subscribe <=====");
		Map<String, Object> responseMap = new HashMap<String, Object>();
		try {
			String accessToken = wxMpService.getWxAccessToken();
			String url = String.format(config.getWxMpBasicUserInfoUrl(), accessToken, openId);
			String response = WxRequestHelper.doGet(url);
			
			JSONObject obj = JSONObject.fromObject(response);
			if (obj != null) {
				int subscribe = obj.getInt("subscribe");
				if (subscribe == 1) {
					ResponseHelper.addResponseSuccessData(responseMap, null);
				} else {
					ResponseHelper.addResponseFailData(responseMap, null);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.addResponseFailData(responseMap, e.getMessage());
		}
		logger.info("=====> Check subscribe end <=====");
		return responseMap;
	}
	
	@ApiOperation(value = "获取微信Config", httpMethod = "GET", notes = "获取微信Config")
	@GetMapping(value = "/getWxConfig")
	public Map<String, Object> getWxConfig(@RequestParam("url") String url) {
		Map<String, Object> responseMap = new HashMap<String, Object>();
		logger.info("=====> Start to get wx config <=====");
		
        Map<String, Object> ret = new HashMap<String, Object>();

        String appId = ApplicationConfig.getInstance().getWxMpAppId(); // 必填，公众号的唯一标识

        String requestUrl = url;

        String timestamp = Long.toString(System.currentTimeMillis() / 1000); // 必填，生成签名的时间戳

        String nonceStr = UUID.randomUUID().toString(); // 必填，生成签名的随机串

        String signature = "";
        // 注意这里参数名必须全部小写，且必须有序
        try {
        	String jsApiTicket = wxMpService.getWxJsApiTicket();

        	String sign = "jsapi_ticket=" + jsApiTicket + "&noncestr=" + nonceStr+ "&timestamp=" + timestamp + "&url=" + requestUrl;
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(sign.getBytes("UTF-8"));
            signature = byteToHex(crypt.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ParseException e) {
			e.printStackTrace();
		}
        
        ret.put("appId", appId);
        ret.put("timestamp", timestamp);
        ret.put("nonceStr", nonceStr);
        ret.put("signature", signature);
        ResponseHelper.addResponseSuccessData(responseMap, ret);
        logger.info("=====> Get wx config end <=====");
        return responseMap;
    }
	
	private static String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;

    }
	
	@ApiOperation(value = "获取用户信息", httpMethod = "GET", notes = "获取用户信息，用户不存在自动注册")
	@GetMapping(value = "/getUser")
	public Map<String, Object> getUser(@RequestParam("openId") String openId) {
		logger.info("=====> Start to get user <=====");
		Map<String, Object> responseMap = new HashMap<String, Object>();
		try {
			User user = userService.getUserByOpenId(openId);
			if (user == null) {
//				throw new UserException(UserException.NOT_REGISTER);
				WxOAuth2AccessToken wxOAuth2AccessToken = wxMpService.findWxOAuth2AccessTokenByOpenId(openId);
				if (wxOAuth2AccessToken == null) {
					throw new ApplicationServiceException(ApplicationServiceException.ACCESS_TOKEN_NOT_EXIST);
				}
				String responseStr = WxRequestHelper.getWxMpUserInfo(wxOAuth2AccessToken);

				if (responseStr.indexOf("errcode") != -1) {
					logger.warn(responseStr);
					JSONObject obj = JSONObject.fromObject(responseStr);
					ResponseHelper.addResponseFailData(responseMap, obj.getInt("errcode"), obj.getString("errmsg"));
					return responseMap;
				} else {
					logger.info(responseStr);
					JSONObject obj = JSONObject.fromObject(responseStr);
					String headimgurl = obj.getString("headimgurl");
					String sex = obj.getString("sex");
					String nickName = obj.getString("nickname");

					user = new User();
					user.setGender(Integer.parseInt(sex));
					user.setNickName(nickName);
					user.setOpenId(openId);

					if (StringUtils.isNotBlank(headimgurl)) {
//						UUID uuid = UUID.randomUUID();
//						String folder = uuid.toString().replaceAll("-", "");
						user.setPicture(headimgurl);
					}

					userService.register(user);
				}
			}

			Map<String, Object> userMap = new HashMap<String, Object>();
			userMap.put("openId", user.getOpenId());
			userMap.put("userId", user.getId());
			userMap.put("phoneNo", user.getPhoneNo());
			userMap.put("nickName", user.getNickName());
			userMap.put("picture", user.getPicture());
			userMap.put("gender", user.getGender());
			
			request.getSession().setAttribute(User.CURRENT_USER, user);

			ResponseHelper.addResponseSuccessData(responseMap, userMap);
			logger.info("=====> Get user end <=====");
		} catch (UserException e) {
			ResponseHelper.addResponseFailData(responseMap, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.addResponseFailData(responseMap, e.getMessage());
		}
		return responseMap;
	}

	@ApiOperation(value = "绑定手机号", httpMethod = "GET", notes = "绑定手机号")
	@GetMapping(value = "/bindPhoneNo")
	public Map<String, Object> bindPhoneNo(@RequestParam("userId") int userId, @RequestParam("phoneNo") String phoneNo, 
			@RequestParam("verifyNo") String verifyNo) {
		logger.info("=====> Start to get user <=====");
		Map<String, Object> responseMap = new HashMap<String, Object>();
		try {
			User user = userService.findUserById(userId);
			if (user == null) {
				throw new ApplicationServiceException(ApplicationServiceException.USER_NOT_EXIST);
			}
			
			if (!isMobile(phoneNo)) {
				throw new ApplicationServiceException(ApplicationServiceException.PHONE_NO_INVALID);
			}
			
			// 检查手机号码是否注册
			SearchCriteria searchCriteria = new SearchCriteria();
			searchCriteria.setPhoneNo(phoneNo);
			User existUser = userService.getUser(searchCriteria);
			if (existUser != null) {
				throw new ApplicationServiceException(ApplicationServiceException.PHONE_REGISTERED);
			}

			VerifyNo existVerifyNo = userService.getVerifyNo(phoneNo, VerifyNo.VERIFY_NO_TYPE_REGISTER);
			if (existVerifyNo == null) {
				throw new ApplicationServiceException(ApplicationServiceException.VERIFY_CODE_NOT_EXIST);
			}
			
			Date currentTime = new Date();
			Date createTime = existVerifyNo.getCreationTime();
			long difference = currentTime.getTime() - createTime.getTime();

			if (difference > (10 * 60 * 1000)) {
				throw new ApplicationServiceException(ApplicationServiceException.VERIFY_CODE_EXPIRE);
			}
			if (!verifyNo.equalsIgnoreCase(existVerifyNo.getVerifyNo())) {
				throw new ApplicationServiceException(ApplicationServiceException.VERIFY_CODE_INVALID);
			}

			user.setPhoneNo(phoneNo);
			userService.saveOrUpdateUser(user);
			
			ResponseHelper.addResponseSuccessData(responseMap, null);
			logger.info("=====> Get user end <=====");
		} catch (ApplicationServiceException e) {
			ResponseHelper.addResponseFailData(responseMap, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.addResponseFailData(responseMap, e.getMessage());
		}
		return responseMap;
	}
	
	@ApiOperation(value = "刷新网页授权", httpMethod = "GET", notes = "刷新网页授权AccessToken")
	@GetMapping(value = "/refreshAuthAccessToken")
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
			logger.info("=====> Get OAuth2 access token end <=====");
		} catch (Exception e) {
			e.printStackTrace();
		}
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
	/*
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public Map<String, Object> register(@RequestParam("openId") String openId, @RequestParam("phoneNo") String phoneNo,
			@RequestParam("verifyNo") String verifyNo) {
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
				user.setOpenId(openId);

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
			}
			logger.info("=====> Register user end <=====");
		} catch (UserException e) {
			ResponseHelper.addResponseFailData(responseMap, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.addResponseFailData(responseMap, e.getMessage());
		}
		return responseMap;
	}*/

	@ApiOperation(value = "创建圈子", httpMethod = "POST", notes = "创建圈子")
	@PostMapping(value = "/createCoterie")
	public Map<String, Object> createCoterie(@RequestParam("userId") int userId, 
			@RequestParam("name") String name,
			@RequestParam("tags") String tags, 
			@RequestParam(name = "description", required = false) String description,
//			@RequestParam(name = "file") CommonsMultipartFile picture
			@RequestParam("serverId") String serverId
			) {
		logger.info("=====> Start to create coterie <=====");
		Map<String, Object> responseMap = new HashMap<String, Object>();
		try {
			User user = userService.findUserById(userId);
			if (user == null) {
				throw new ApplicationServiceException(ApplicationServiceException.USER_NOT_EXIST);
			}
			
			if (StringUtils.isBlank(name) || StringUtils.isBlank(tags) || StringUtils.isBlank(serverId)) {
				throw new ApplicationServiceException(ApplicationServiceException.COTERIE_PARAM_IS_EMPTY);
			}
			
			if (StringUtils.isNotBlank(name)) {
				String sensitiveWord = sensitiveWordUtil.firstLevelFilter(name);
				if (StringUtils.isNotBlank(sensitiveWord)) {
					throw new ApplicationServiceException(sensitiveWord);
				}
			}
			
			if (StringUtils.isNotBlank(description)) {
				String sensitiveWord = sensitiveWordUtil.secondLevelFilter(description);
				if (StringUtils.isNotBlank(sensitiveWord)) {
					throw new ApplicationServiceException(sensitiveWord);
				}
			}

			Coterie coterie = new Coterie();
			coterie.setName(name);
			coterie.setDescription(description);
			coterie.setCreationDate(new Date());
			coterie.setCreator(user);
			coterie.setHot(0);
			coterie.setStatus(Coterie.STATUS_NORMAL);
			
			if (StringUtils.isNotBlank(tags)) {
				String[] tagsSplit = tags.split(",");
				for (int j = 1; j <= tagsSplit.length; j++) {
					Tag tag = activityService.findTagById(Integer.parseInt(tagsSplit[j - 1]));
					if (tag != null) {
						List<Tag> secondLevelTags = activityService.getSecondLevelTags(tag.getId());
						CoterieTag coterieTag = new CoterieTag(coterie, tag, secondLevelTags.size() > 0 ? -1 : j);
						coterie.getTags().add(coterieTag);
					}
				}
			}

			coterie.getCoterieGuys().add(new CoterieGuy(coterie, 1, user, new Date(), true, true));

			discoverService.saveCoterie(coterie);
			
			/*if (picture != null) {
				String folder = config.getCoterieCoverPath() + "/" + coterie.getId();
				String fileName = FileUploadHelper.generateTempImageFileName();
				
				String picPath = AliyunOSSUtil.getInstance().upload(folder +"/" + fileName, picture);
				CoteriePicture coteriePicture = new CoteriePicture();
				coteriePicture.setCoterie(coterie);
				coteriePicture.setCreationDate(new Date());
				coteriePicture.setUser(user);
				coteriePicture.setUrl(picPath);
				coterie.setCoteriePicture(coteriePicture);
				discoverService.saveCoterie(coterie);
			}*/
			if (StringUtils.isNotBlank(serverId)) {
				String folder = config.getCoterieCoverPath() + "/" + coterie.getId();
				String[] serverIds = {serverId};
				String accessToken = wxMpService.getWxAccessToken();
				List<String> pictures = WxRequestHelper.downloadServerFileToAliyunOSS(folder, serverIds, accessToken);
				if (pictures != null && pictures.size() > 0) {
					String picPath = pictures.get(0);
					CoteriePicture coteriePicture = new CoteriePicture();
					coteriePicture.setCoterie(coterie);
					coteriePicture.setCreationDate(new Date());
					coteriePicture.setUser(user);
					coteriePicture.setUrl(picPath);
					coterie.setCoteriePicture(coteriePicture);
					discoverService.saveCoterie(coterie);
				}
			}
			ResponseHelper.addResponseSuccessData(responseMap, null);
			logger.info("=====> Create coterie end <=====");
		} catch (ApplicationServiceException e) {
			ResponseHelper.addResponseFailData(responseMap, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.addResponseFailData(responseMap, e.getMessage());
		}
		return responseMap;
	}
	
	@ApiOperation(value = "编辑圈子", httpMethod = "POST", notes = "编辑圈子")
	@PostMapping(value = "/editCoterie")
	public Map<String, Object> editCoterie(@RequestParam("userId") int userId, 
			@RequestParam("coterieId") int coterieId,
			@RequestParam(name = "name", required = false) String name, 
			@RequestParam(name = "tags", required = false) String tags, 
			@RequestParam(name = "description", required = false) String description,
			@RequestParam(name = "serverId", required = false) String serverId) {
		logger.info("=====> Start to edit coterie <=====");
		Map<String, Object> responseMap = new HashMap<String, Object>();
		try {
			Coterie coterie = discoverService.findCoterieById(coterieId);
			if (coterie == null) {
				throw new ApplicationServiceException(ApplicationServiceException.COTERIE_NOT_EXIST);
			}
			// 非创建者不可编辑
			if (coterie.getCreator() != null && coterie.getCreator().getId() != userId) {
				throw new ApplicationServiceException(ApplicationServiceException.COTERIE_NOT_CREATOR);
			}
			
			// 解散中/已解散圈子不能修改
			if (coterie.getStatus() == Coterie.STATUS_DISMISSING || coterie.getStatus() == Coterie.STATUS_DISMISSED) {
				throw new ApplicationServiceException(ApplicationServiceException.COTERIE_DISMISSED);
			}
			
			// 敏感词汇检查
			if (StringUtils.isNotBlank(name)) {
				String sensitiveWord = sensitiveWordUtil.firstLevelFilter(name);
				if (StringUtils.isNotBlank(sensitiveWord)) {
					throw new ApplicationServiceException(sensitiveWord);
				}
			}
			
			if (StringUtils.isNotBlank(description)) {
				String sensitiveWord = sensitiveWordUtil.secondLevelFilter(description);
				if (StringUtils.isNotBlank(sensitiveWord)) {
					throw new ApplicationServiceException(sensitiveWord);
				}
			}
			
			User user = userService.findUserById(userId);
			if (user == null) {
				throw new ApplicationServiceException(ApplicationServiceException.USER_NOT_EXIST);
			}
			
			if (StringUtils.isNotBlank(name)) {
				coterie.setName(name);
			}
			
			if (description != null) {
				coterie.setDescription(description);
			}
			
			if (StringUtils.isNotBlank(tags)) {
				coterie.getTags().clear();
				String[] tagsSplit = tags.split(",");
				int i = 1;
				for (String tagIdstr : tagsSplit) {
					Tag tag = activityService.findTagById(Integer.parseInt(tagIdstr));
					if (tag != null) {
						List<Tag> secondLevelTags = activityService.getSecondLevelTags(tag.getId());
	            		CoterieTag coterieTag = new CoterieTag(coterie, tag, secondLevelTags.size() > 0 ? -1 : i++);
						coterie.getTags().add(coterieTag);
					}
				}
				
			}
			
			if (StringUtils.isNotBlank(serverId)) {
				// 删除旧图片
				if (coterie.getCoteriePicture() != null) {
					String oldUrl = coterie.getCoteriePicture().getUrl();
					String[] paths = oldUrl.split("com/");
					if (paths != null && paths.length > 1) {
						AliyunOSSUtil.getInstance().deleteObject(paths[1]);
					}
				}
				
				String folder = config.getCoterieCoverPath() + "/" + coterie.getId();
				
				String[] serverIds = {serverId};
				String accessToken = wxMpService.getWxAccessToken();
				List<String> pictures = WxRequestHelper.downloadServerFileToAliyunOSS(folder, serverIds, accessToken);
				if (pictures != null && pictures.size() > 0) {
					String picPath = pictures.get(0);
					if (coterie.getCoteriePicture() != null) {
						coterie.getCoteriePicture().setUser(user);
						coterie.getCoteriePicture().setUrl(picPath);
					} else {
						CoteriePicture coteriePicture = new CoteriePicture();
						coteriePicture.setCoterie(coterie);
						coteriePicture.setCreationDate(new Date());
						coteriePicture.setUser(user);
						coteriePicture.setUrl(picPath);
						coterie.setCoteriePicture(coteriePicture);
					}
				}
			}

			discoverService.saveCoterie(coterie);

			ResponseHelper.addResponseSuccessData(responseMap, null);
			logger.info("=====> Edit coterie end <=====");
		} catch (ApplicationServiceException e) {
			ResponseHelper.addResponseFailData(responseMap, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.addResponseFailData(responseMap, e.getMessage());
		}
		return responseMap;
	}
	
	@ApiOperation(value = "解散圈子", httpMethod = "GET", notes = "解散圈子")
	@GetMapping(value = "/dismissCoterie")
	public Map<String, Object> dismissCoterie(@RequestParam("userId") int userId, @RequestParam("coterieId") int coterieId) {
		logger.info("=====> Start to dismiss coterie <=====");
		Map<String, Object> responseMap = new HashMap<String, Object>();
		try {
			Coterie coterie = discoverService.findCoterieById(coterieId);
			if (coterie == null) {
				throw new ApplicationServiceException(ApplicationServiceException.COTERIE_NOT_EXIST);
			}
			
			if (coterie.getCreator().getId() != userId) {
				throw new ApplicationServiceException(ApplicationServiceException.COTERIE_NOT_CREATOR);
			}

			if (coterie.getStatus() != Coterie.STATUS_NORMAL) {
				throw new ApplicationServiceException(ApplicationServiceException.COTERIE_CAN_NOT_DISMISS);
			}
			// 判断是否有进行中的活动
			boolean hasActivityProcess = discoverService.hasActivityProcess(coterieId);
			if (hasActivityProcess) {
				// 如果有，状态改成解散中
				coterie.setStatus(Coterie.STATUS_DISMISSING);
			} else {
				// 如果没有，状态改成已解散
				coterie.setStatus(Coterie.STATUS_DISMISSED);
			}
			
			discoverService.saveCoterie(coterie);
			
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("status", coterie.getStatus());
			
			ResponseHelper.addResponseSuccessData(responseMap, data);
			logger.info("=====> Dismiss coterie end <=====");
		} catch (ApplicationServiceException e) {
			ResponseHelper.addResponseFailData(responseMap, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.addResponseFailData(responseMap, e.getMessage());
		}
		return responseMap;
	}
	
	@ApiOperation(value = "解散圈子", httpMethod = "GET", notes = "解散圈子")
	@GetMapping(value = "/getCoterieCarouselPictures")
	public Map<String, Object> getCoterieCarouselPictures() {
		logger.info("=====> Start to get coterie carousel pictures <=====");
		Map<String, Object> responseMap = new HashMap<String, Object>();
		try {
			
			List<CoterieCarouselPicture> pictures = discoverService.getCoterieCarouselPictures();
			List<Map> resultList = new ArrayList<Map>();
			for (CoterieCarouselPicture coterieCarouselPicture : pictures) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("url", coterieCarouselPicture.getUrl());
				map.put("redirectUrl", coterieCarouselPicture.getRedirectUrl());
				resultList.add(map);
			}
			ResponseHelper.addResponseSuccessData(responseMap, resultList);
			logger.info("=====> Get coterie carousel pictures end <=====");
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.addResponseFailData(responseMap, e.getMessage());
		}
		return responseMap;
	}

	@ApiOperation(value = "加入/退出圈子", httpMethod = "POST", notes = "加入/退出圈子")
	@PostMapping(value = "/joinCoterie")
	public Map<String, Object> joinCoterie(@RequestParam("userId") int userId, @RequestParam("coterieId") int coterieId,
			@RequestParam("isJoin") boolean isJoin) {
		logger.info("=====> Start to join coterie <=====");
		Map<String, Object> responseMap = new HashMap<String, Object>();
		try {
			User user = userService.findUserById(userId);
			if (user == null) {
				throw new ApplicationServiceException(ApplicationServiceException.USER_NOT_EXIST);
			}

			Coterie coterie = discoverService.findCoterieById(coterieId);
			if (coterie == null) {
				throw new ApplicationServiceException(ApplicationServiceException.COTERIE_NOT_EXIST);
			}
			
			if (coterie.getStatus() == Coterie.STATUS_DISMISSED) {
				throw new ApplicationServiceException(ApplicationServiceException.COTERIE_DISMISSED);
			}

			if (isJoin) {
				int nextOrderNo = coterie.getCoterieGuyNextOrderNo();
				CoterieGuy coterieGuy = new CoterieGuy(coterie, nextOrderNo, user, new Date(), false, true);
				discoverService.saveCoterieGuy(coterieGuy);
				
				String accessToken = wxMpService.getWxAccessToken();
				if (StringUtils.isNotBlank(accessToken)) {
					WxRequestHelper.sendCoterieJoinMsg(accessToken, coterie, user);
				}
			} else {
				discoverService.exitCoterie(coterieId, user.getId());
			}
			ResponseHelper.addResponseSuccessData(responseMap, null);
			logger.info("=====> Join coterie end <=====");
		} catch (ApplicationServiceException e) {
			ResponseHelper.addResponseFailData(responseMap, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.addResponseFailData(responseMap, e.getMessage());
		}
		return responseMap;
	}

	@ApiOperation(value = "获取圈子列表", httpMethod = "GET", notes = "获取圈子列表")
	@GetMapping(value = "/getCoterieList")
	public Map<String, Object> getCoterieList(
			@RequestParam(name = "firstLevelTagId", required = false) Integer firstLevelTagId,
			@RequestParam(name = "secondLevelTagIds", required = false) String secondLevelTagIds,
			@RequestParam(name = "start", required = false) Integer startRow,
			@RequestParam(name = "pageSize", required = false) Integer pageSize,
			@RequestParam(name = "keyword", required = false) String keyword) {
		logger.info("=====> Start to get coterie list <=====");
		Map<String, Object> responseMap = new HashMap<String, Object>();
		try {
			if (startRow == null) {
				startRow = 0;
			}
			if (pageSize == null) {
				pageSize = ApplicationConfig.getInstance().getDefaultPageSize();
			}
			List<Integer> tags = new ArrayList<Integer>();
			if (StringUtils.isNotBlank(secondLevelTagIds)) {
				Tag unlimitedTag = activityService.findTagByName(Tag.TAG_UNLIMITED);
				List<String> secTagList = new ArrayList<String>(Arrays.asList(secondLevelTagIds.split(",")));
				if (unlimitedTag != null && !secTagList.contains(String.valueOf(unlimitedTag.getId()))) {
					String[] tag2Ids = secondLevelTagIds.split(",");
					for (String tagId : tag2Ids) {
						tags.add(Integer.parseInt(tagId));
					}
				} else if (firstLevelTagId != null){
					tags.add(firstLevelTagId);
				}
			} else if (firstLevelTagId != null){
				tags.add(firstLevelTagId);
			}
			
			SearchCriteria searchCriteria = new SearchCriteria();
			if (tags.size() > 0) {
				searchCriteria.setTags(tags);
			}
			searchCriteria.setStartRow(startRow);
			searchCriteria.setPageSize(pageSize);
			// 默认最热排序
			searchCriteria.setOrderBy(Coterie.TYPE_NEW);
			if (StringUtils.isNotBlank(keyword)) {
				searchCriteria.setKeyword(keyword);
			}

			Map<String, Object> coterieMap = discoverService.getCoterieList(searchCriteria);
			List<Coterie> coteries = (List<Coterie>) coterieMap.get("resultList");
			List<Map> dataList = new ArrayList<Map>();
			if (coteries != null) {
				for (Coterie coterie : coteries) {
					Map<String, Object> data = new HashMap<String, Object>();
					data.put("coterieId", coterie.getId());
					data.put("cover", coterie.getCoteriePicture() != null ? coterie.getCoteriePicture().getUrl() : "");
					data.put("name", coterie.getName());
					data.put("description", coterie.getDescription());
					data.put("membersCnt", coterie.getCoterieGuys().size());
					data.put("activityCnt", coterie.getEvents().size());
					
					List<String> tagList = new ArrayList<>();
					for (CoterieTag coterieTag : coterie.getTags()) {
						if (coterieTag.getTag() != null) {
							tagList.add(coterieTag.getTag().getName());
						}
					}
					data.put("tags", tagList);
					dataList.add(data);
				}
			}

			ResponseHelper.addResponseSuccessData(responseMap, dataList);
			logger.info("=====> Get coterie list end <=====");
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.addResponseFailData(responseMap, e.getMessage());
		}
		return responseMap;
	}
	
	@ApiOperation(value = "获取圈子详情", httpMethod = "GET", notes = "获取圈子详情")
	@GetMapping(value = "/getCoterieInfo")
	public Map<String, Object> getCoterieInfo(@RequestParam("userId") int userId, @RequestParam("coterieId") int coterieId) {
		logger.info("=====> Start to get coterie info <=====");
		Map<String, Object> responseMap = new HashMap<String, Object>();
		try {
			Coterie coterie = discoverService.findCoterieById(coterieId);
			if (coterie == null) {
				throw new ApplicationServiceException(ApplicationServiceException.COTERIE_NOT_EXIST);
			}
			
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("coterieId", coterie.getId());
			data.put("cover", coterie.getCoteriePicture() != null ? coterie.getCoteriePicture().getUrl() : "");
			data.put("name", coterie.getName());
			data.put("description", coterie.getDescription());
			data.put("activityCnt", coterie.getEvents().size());
			data.put("membersCnt", coterie.getCoterieGuys().size());
			data.put("isJoined", coterie.isJoined(userId));
			data.put("isCreator", coterie.isCreator(userId));
			data.put("status", coterie.getStatus());
			data.put("creationDate", DateUtils.format(coterie.getCreationDate(), DateUtils.yMdHm));
			
			List<Map> tagList = new ArrayList<>();
			for (CoterieTag coterieTag : coterie.getTags()) {
				Tag tag = coterieTag.getTag();
				if (tag != null) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("id", tag.getId());
					map.put("name", tag.getName());
					tagList.add(map);
				}
			}
			// 标签
			data.put("tags", tagList);
			
			List<CoterieGuy> coterieGuys = new ArrayList<CoterieGuy>(coterie.getCoterieGuys());
			Collections.sort(coterieGuys, new Comparator<CoterieGuy>() {
				@Override
				public int compare(CoterieGuy o1, CoterieGuy o2) {
					return o2.getOrderNo() - o1.getOrderNo();
				}
			});
			
			int i = 1;
			List<String> resultList = new ArrayList<String>();
			for (CoterieGuy coterieGuy : coterieGuys) {
				if (i <= 3 && coterieGuy.getUser() != null && coterieGuy.getUser().getId() != coterie.getCreator().getId()) {
					resultList.add(coterieGuy.getUser().getPicture());
					i++;
				}
			}
			data.put("members", resultList);
			data.put("coterieMembersCnt", coterieGuys.size());
			
			UserCoterieSetting userCoterieSetting = userService.loadUserCoterieSetting(userId, coterieId);
			data.put("allowPush", userCoterieSetting.isAllowPush());
			
			ResponseHelper.addResponseSuccessData(responseMap, data);
			logger.info("=====> Get coterie info end <=====");
		} catch (ApplicationServiceException e) {
			ResponseHelper.addResponseFailData(responseMap, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.addResponseFailData(responseMap, e.getMessage());
		}
		return responseMap;
	}
	
	@ApiOperation(value = "获取圈子成员列表", httpMethod = "GET", notes = "获取圈子成员列表")
	@GetMapping(value = "/getCoterieMembers")
	public Map<String, Object> getCoterieMembers(@RequestParam("coterieId") int coterieId, 
			@RequestParam(name = "start", required = false) Integer startRow, 
			@RequestParam(name = "pageSize", required = false) Integer pageSize) {
		logger.info("=====> Start to get coterie members <=====");
		Map<String, Object> responseMap = new HashMap<String, Object>();
		try {
			Coterie coterie = discoverService.findCoterieById(coterieId);
			if (coterie == null) {
				throw new ApplicationServiceException(ApplicationServiceException.COTERIE_NOT_EXIST);
			}
			
			if (startRow == null) {
				startRow = 0;
			}
			if (pageSize == null) {
				pageSize = ApplicationConfig.getInstance().getDefaultPageSize();
			}
			
			Map<String, Object> data = new HashMap<String, Object>();
			// 创建者
			User creator = coterie.getCreator();
			Map<String, Object> managerMap = new HashMap<String, Object>();
			managerMap.put("userId", creator.getId());
			managerMap.put("nickName", creator.getNickName());
			managerMap.put("picture", creator.getPicture());
			managerMap.put("gender", creator.getGender());
			data.put("manager", managerMap);
			
			List<CoterieGuy> coterieMembers = discoverService.getCoterieMembers(coterieId, startRow, pageSize);
			
			List<Map> resultList = new ArrayList<Map>();
			for (CoterieGuy coterieGuy : coterieMembers) {
				User user = coterieGuy.getUser();
				if (user != null && user.getId() != creator.getId()) {
					Map map = new HashMap<>();
					map.put("userId", user.getId());
					map.put("nickName", user.getNickName());
					map.put("picture", user.getPicture());
					map.put("gender", user.getGender());
					resultList.add(map);
				}
			}
			
			data.put("members", resultList);
			data.put("coterieMembersCnt", resultList.size());
			
			ResponseHelper.addResponseSuccessData(responseMap, data);
			logger.info("=====> Get coterie members end <=====");
		} catch (ApplicationServiceException e) {
			ResponseHelper.addResponseFailData(responseMap, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.addResponseFailData(responseMap, e.getMessage());
		}
		return responseMap;
	}
	
	@ApiOperation(value = "切换消息免打扰", httpMethod = "GET", notes = "切换消息免打扰")
	@GetMapping(value = "/changeCoteriePush")
	public Map<String, Object> changeCoteriePush(@RequestParam("coterieId") int coterieId, 
			@RequestParam("userId") int userId, @RequestParam("allowPush") boolean allowPush) {
		logger.info("=====> Start to change user coterie push setting <=====");
		Map<String, Object> responseMap = new HashMap<String, Object>();
		try {
			UserCoterieSetting userCoterieSetting = userService.loadUserCoterieSetting(userId, coterieId);

			userCoterieSetting.setAllowPush(allowPush);
			userService.saveUserCoterieSetting(userCoterieSetting);
			
			ResponseHelper.addResponseSuccessData(responseMap, null);
			logger.info("=====> Change user coterie push setting end <=====");
		} catch (ApplicationServiceException e) {
			ResponseHelper.addResponseFailData(responseMap, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.addResponseFailData(responseMap, e.getMessage());
		}
		return responseMap;
	}
	
	@ApiOperation(value = "获取话题列表", httpMethod = "GET", notes = "获取话题列表")
	@GetMapping(value = "/getTopicList")
	public Map<String, Object> getTopicList(@RequestParam("coterieId") int coterieId,
			@RequestParam("userId") int userId,
			@RequestParam(name = "pageSize", required = false) Integer pageSize,
			@RequestParam(name = "start", required = false) Integer startRow,
			@RequestParam(name = "dataType", required = false) String dataType) {
		logger.info("=====> Start to get topic list <=====");
		Map<String, Object> responseMap = new HashMap<String, Object>();
		try {
			if (startRow == null) {
				startRow = 0;
			}
			if (pageSize == null) {
				pageSize = ApplicationConfig.getInstance().getDefaultPageSize();
			}
			
			SearchCriteria searchCriteria = new SearchCriteria();
			searchCriteria.setCoterieId(coterieId);
			searchCriteria.setStartRow(startRow);
			searchCriteria.setPageSize(pageSize);
			// 默认活动开始时间排序
			searchCriteria.setOrderBy(Event.ORDER_BY_START_TIME);
			searchCriteria.setTopicDataType(Topic.TYPE_ATIVITY);
			if (StringUtils.isNotBlank(dataType) && dataType.equals(Topic.DATATYPE_HISTROY)) {
				searchCriteria.setDataType(dataType);
			}
			
			Map<String, Object> topicMap = discoverService.getTopicList(searchCriteria);
			List<Topic> topics = (List<Topic>) topicMap.get("resultList");
			
			List<Map> resultList= new ArrayList<>();
			if (topics != null) {
				for (Topic topic : topics) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("topicId", topic.getId());
					map.put("commentCnt", topic.getComments().size());
					map.put("praiseCnt", topic.getPraises().size());
					map.put("hasPraise", topic.hasPraise(userId));
					
					// 活动相关
					Event event = topic.getEvent();
					if (event != null) {
						Map<String, Object> eventMap = new HashMap<String, Object>();
						eventMap.put("activityId", event.getId());
						List<EventPicture> eventPictures = new ArrayList<EventPicture>(event.getEventPictures());
						EventPicture eventPicture = null;
						if (eventPictures != null && eventPictures.size() > 0) {
							Collections.sort(eventPictures, new EventPictureComparator());
							eventPicture = eventPictures.get(0);
						}
						eventMap.put("cover", eventPicture != null ? eventPicture.getUrl() : "");

						eventMap.put("name", event.getName());
						eventMap.put("startTime", event.getStartTime());
						eventMap.put("status", event.getStatus());
						eventMap.put("gps", event.getGps());
						eventMap.put("address", event.getAddress());
						eventMap.put("charge", event.getCharge());

						List<EventTag> eventTags = event.getTags();
						List<String> tagList = new ArrayList<>();
						for (EventTag eventTag : eventTags) {
							Tag tag = eventTag.getTag();
							tagList.add(tag.getName());
						}
						// 当只有一个一级标签时，自动添加二级标签不限
						if (eventTags.size() == 1) {
							Tag et = eventTags.get(0).getTag();
							if (et != null && et.getType().equalsIgnoreCase(Tag.TYPE_FIRST_LEVEL)) {
								tagList.add(Tag.TAG_UNLIMITED);
							}
						}
						eventMap.put("tags", tagList);
						
						if (event.getCoterie() != null) {
							Map<String, Object> coterieMap = new HashMap<String, Object>();
							coterieMap.put("id", event.getCoterie().getId());
							coterieMap.put("name", event.getCoterie().getName());
							eventMap.put("coterie", coterieMap);
						}

						Map<String, Object> countMap = new HashMap<String, Object>();
						countMap.put("maxCount", event.getMaxCount());
						countMap.put("minCount", event.getMinCount());
						countMap.put("currentCount", event.getEffectiveMembers().size());
						eventMap.put("userCount", countMap);
						
						map.put("activity", eventMap);
					}
					resultList.add(map);
				}
			}

			ResponseHelper.addResponseSuccessData(responseMap, resultList);
			logger.info("=====> Get topic list end <=====");
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.addResponseFailData(responseMap, e.getMessage());
		}
		return responseMap;
	}

	@ApiOperation(value = "搜索圈子/话题", httpMethod = "GET", notes = "搜索圈子/话题")
	@GetMapping(value = "/searchActivityOrTopic")
	public Map<String, Object> searchActivityOrTopic(@RequestParam("keyword") String keyword) {
		Map<String, Object> responseMap = new HashMap<String, Object>();
		try {
			logger.info("=====> Start to search activity or topic <=====");

			Map<String, Object> resultMap = discoverService.searchActivityOrTopic(keyword);
			List<Coterie> coteries = (List<Coterie>) resultMap.get("coterieList");
			List<Topic> topics = (List<Topic>) resultMap.get("topicList");

			Map<String, Object> result = new HashMap<String, Object>();
			if (coteries != null) {
				List<Map> coterieList = new ArrayList<Map>();
				for (Coterie coterie : coteries) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("id", coterie.getId());
					map.put("name", coterie.getName());
					map.put("description", coterie.getDescription());
					map.put("hot", coterie.getHot());

					List<String> tags = new ArrayList<String>();
					for (CoterieTag coterieTag : coterie.getTags()) {
						if (coterieTag.getTag() != null) {
							tags.add(coterieTag.getTag().getName());
						}
					}
					map.put("tags", tags);
					coterieList.add(map);
				}
				result.put("coteries", coterieList);
				result.put("coterieCnt", coterieList.size());
			}
			if (topics != null) {
				List<Map> topicList = new ArrayList<Map>();
				for (Topic topic : topics) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("id", topic.getId());
					map.put("coterieId", topic.getCoterie().getId());
					map.put("description", topic.getDescription());
					topicList.add(map);
				}
				result.put("topics", topicList);
				result.put("topicCnt", topicList.size());
			}

			logger.info("=====> Search activity or topic type end <=====");
			ResponseHelper.addResponseSuccessData(responseMap, result);
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.addResponseFailData(responseMap, e.getMessage());
		}
		return responseMap;
	}

	@ApiOperation(value = "我的圈子", httpMethod = "GET", notes = "我的圈子")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "dataType", value = "'1'：我创建的 '2'：我参与的", required = false, paramType = "query", dataType = "String")
	})
	@GetMapping(value = "/getMyCoteries")
	public Map<String, Object> getMyCoteries(@RequestParam("userId") int userId,
			@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(name = "dataType", required = false) String dataType,
			@RequestParam(name = "showLastCoterie", required = false) Boolean showLastCoterie) {
		Map<String, Object> responseMap = new HashMap<String, Object>();
		try {
			logger.info("=====> Start to get my coteries <=====");

			List<Coterie> coteries = discoverService.getMyCoteries(dataType, userId, keyword);
			
			// 显示最近参与
			Coterie lastCoterie = null;
			if (showLastCoterie != null && showLastCoterie) {
				lastCoterie = discoverService.getLastCoterie(userId);
			}
			
			List<Map> resultList = new ArrayList<Map>();
			if (coteries != null) {
				if (showLastCoterie != null && showLastCoterie) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("id", "");
					map.put("name", "以活动名创建");
					resultList.add(map);
				}
				for (Coterie coterie : coteries) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("id", coterie.getId());
					map.put("name", coterie.getName());
					map.put("cover", coterie.getCoteriePicture() != null ? coterie.getCoteriePicture().getUrl() : "");
					map.put("isCreator", coterie.isCreator(userId));
					if (lastCoterie != null) {
						if (coterie.getId() == lastCoterie.getId()) {
							map.put("isLastCoterie", true);
						} else {
							map.put("isLastCoterie", false);
						}
					}
					resultList.add(map);
				}
			}

			logger.info("=====> Get my coteries end <=====");
			ResponseHelper.addResponseSuccessData(responseMap, resultList);
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.addResponseFailData(responseMap, e.getMessage());
		}

		return responseMap;
	}
	
	@ApiOperation(value = "获取活动类型（一级标签）", httpMethod = "GET", notes = "获取活动类型（一级标签）")
	@GetMapping(value = "/getActivityType")
	public Map<String, Object> getActivityType(@RequestParam(name = "fixed", required = false) Boolean fixed) {
		Map<String, Object> responseMap = new HashMap<String, Object>();
		try{
			logger.info("=====> Start to get activity type <=====");
			
			List<Tag> firstLevelTags = activityService.getTags(Tag.TYPE_FIRST_LEVEL);
			
			if (fixed != null && fixed) {
				Collections.sort(firstLevelTags, new Comparator<Tag>() {
					@Override
					public int compare(Tag o1, Tag o2) {
						if (o1.getName().equalsIgnoreCase(Tag.TAG_STREET_DANCE)) {
							return -1;
						} else if (o1.getName().equalsIgnoreCase(Tag.TAG_STREET_DANCE)) {
							return 0;
						} else {
							return 1;
						}
					}
				});
			}
			
			List<Map> resultList = new ArrayList<Map>();
			Map<String, Object> map = null;
			if (firstLevelTags != null) {
				for (Tag tag : firstLevelTags) {
					map = new HashMap<String, Object>();
					map.put("id", tag.getId());
					map.put("name", tag.getName());
					map.put("times", tag.getTimes());
					resultList.add(map);
				}
			}
			
			logger.info("=====> Get activity type end <=====");
			ResponseHelper.addResponseSuccessData(responseMap, resultList);
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.addResponseFailData(responseMap, e.getMessage());
		}
		return responseMap;
	}
    
	@ApiOperation(value = "获取标签列表", httpMethod = "GET", notes = "获取标签列表")
	@GetMapping(value = "/getTagList")
	public Map<String, Object> getTagList(@RequestParam(name = "parentId", required = false) Integer parentId) {
		Map<String, Object> responseMap = new HashMap<String, Object>();
		try{
			logger.info("=====> Start to get tag list <=====");
			
			List<Tag> secondLevelTags = activityService.getSecondLevelTags(parentId);
			List<Map> resultList = new ArrayList<Map>();
			Map<String, Object> map = null;
			if (secondLevelTags != null) {
				Collections.sort(secondLevelTags, new Comparator<Tag>() {
					@Override
					public int compare(Tag o1, Tag o2) {
						if (o1.getName().equalsIgnoreCase(Tag.TAG_UNLIMITED)) {
							return -1;
						}
						return 0;
					}
				});
				
				for (Tag tag : secondLevelTags) {
					map = new HashMap<String, Object>();
					map.put("id", tag.getId());
					map.put("name", tag.getName());
					map.put("times", tag.getTimes());
					resultList.add(map);
				}
			}
			
			logger.info("=====> Get tag list end <=====");
			ResponseHelper.addResponseSuccessData(responseMap, resultList);
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.addResponseFailData(responseMap, e.getMessage());
		}
		return responseMap;
	}
    
    /**
     * 发起活动
     * @param userId				用户ID
     * @param coterieId				圈子ID
     * @param tags					标签ID
     * @param name					活动名称
     * @param startTime				开始时间
     * @param minCount				最少人数
     * @param maxCount				最大人数
     * @param gps					定位
     * @param address				地址
     * @param description			描述
     * @param isOpen				是否公开
     * @param password				密码
     * @param pictures				活动图片
     * @param phoneNo				手机号
     * @return
     */
	@ApiOperation(value = "发起活动", httpMethod = "POST", notes = "发起活动")
	@PostMapping(value = "/launchActivity")
    public Map<String, Object> launchActivity(
    		@RequestParam("userId") int userId,
    		@RequestParam(name = "coterieId",required = false) Integer coterieId,
    		@RequestParam("tags") String tags,
    		@RequestParam("name") String name,
    		@RequestParam("serverIds") String[] serverIds,
//    		@RequestParam("startTime") long startTimeMillisecond,
//    		@RequestParam("endTime") long endTimeMillisecond,
    		@RequestParam("startTime") String startTimeStr,
    		@RequestParam("endTime") String endTimeStr,
    		@RequestParam(name = "gps",required = false) String gps,
    		@RequestParam("address") String address,
            @RequestParam("minCount") int minCount,
            @RequestParam("maxCount") int maxCount,
            @RequestParam("charge") String charge,
            @RequestParam("isOpen") boolean isOpen,
            @RequestParam(name = "password",required = false) String password,
            @RequestParam(name = "description", required = false) String description,
            @RequestParam(name = "phoneNo", required = false) String phoneNo) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
        	logger.info("=====> Start to launch activity <=====");
        	Coterie coterie = null;
        	if (coterieId != null) {
            	coterie = discoverService.findCoterieById(coterieId);
            	if (coterie == null) {
            		throw new ApplicationServiceException(ApplicationServiceException.COTERIE_NOT_EXIST);
            	}
            	// 已解散的圈子不能创建新活动
            	if (coterie.getStatus() == Coterie.STATUS_DISMISSING || coterie.getStatus() == Coterie.STATUS_DISMISSED) {
    				throw new ApplicationServiceException(ApplicationServiceException.COTERIE_DISMISSED);
    			}
			}
        	
        	User user = userService.findUserById(userId);
        	if (user == null) {
				throw new ApplicationServiceException(ApplicationServiceException.USER_NOT_EXIST);
			}
        	
        	if (StringUtils.isBlank(name) || StringUtils.isBlank(tags) || StringUtils.isBlank(address) || serverIds == null /*|| StringUtils.isBlank(gps)*/ || StringUtils.isBlank(charge)) {
				throw new ApplicationServiceException(ApplicationServiceException.ACTIVITY_PARAM_IS_EMPTY);
			}
        	
        	if (minCount < 0 || maxCount < 0 || minCount > maxCount) {
        		throw new ApplicationServiceException(ApplicationServiceException.ACTIVITY_USER_COUNT_INPUT_ERROR);
			}
        	
        	Date startTime = DateUtils.parse(startTimeStr, DateUtils.yMdHm);
        	Date endTime = DateUtils.parse(endTimeStr, DateUtils.yMdHm);
        	if (startTime.getTime() > endTime.getTime()) {
        		throw new ApplicationServiceException(ApplicationServiceException.ACTIVITY_TIME_ERROR);
			}
        	if (startTime.getTime() <= new Date().getTime()) {
				throw new ApplicationServiceException(ApplicationServiceException.ACTIVITY_STARTTIME_MUST_BE_AFTER_CURRENT);
			}
        	
        	// 敏感词汇检查
        	if (StringUtils.isNotBlank(name)) {
				String sensitiveWord = sensitiveWordUtil.firstLevelFilter(name);
				if (StringUtils.isNotBlank(sensitiveWord)) {
					throw new ApplicationServiceException(sensitiveWord);
				}
			}
			
			if (StringUtils.isNotBlank(description)) {
				String sensitiveWord = sensitiveWordUtil.secondLevelFilter(description);
				if (StringUtils.isNotBlank(sensitiveWord)) {
					throw new ApplicationServiceException(sensitiveWord);
				}
			}
        	
            Event event = new Event();
            event.setAllowSignUp(true);
            event.setCharge(charge);
            event.setNotified(false);
            
            if (coterie != null) {
            	event.setCoterie(coterie);
			}
            
            // 如果活动不公开，设置密码
            event.setOpen(isOpen);
            if (!isOpen) {
				event.setPassword(password);
			}
            
            if (StringUtils.isNotBlank(tags)) {
				String[] tagsSplit = tags.split(",");
				for (int j = 1; j <= tagsSplit.length; j++) {
					Tag tag = activityService.findTagById(Integer.parseInt(tagsSplit[j - 1]));
					if (tag != null) {
						List<Tag> secondLevelTags = activityService.getSecondLevelTags(tag.getId());
	            		EventTag eventTag = new EventTag(event, tag, secondLevelTags.size() > 0 ? -1 : j);
	            		event.getTags().add(eventTag);
					}
				}
			}
            
            event.setName(name);
            
            event.setStartTime(startTime);
            event.setEndTime(endTime);
            
            event.setMinCount(minCount);
            event.setMaxCount(maxCount);
            event.setGps(gps);
            event.setAddress(address);
            event.setDescription(description);
            event.setCreator(user);
            event.setCreationDate(new Date());
            event.setStatus(Event.STATUS_SIGNUP);
            event.setPhoneNo(phoneNo);
            
            activityService.saveEvent(event);
            
            // 创建定时任务
//            config.getScheduledThreadPool().submit(new ActivityProcessStatusTask(event, jobService));
//            config.getScheduledThreadPool().submit(new ActivityOverStatusTask(event, jobService));
            
            Topic topic = new Topic();
            topic.setCreationDate(new Date());
            topic.setCreator(user);
            topic.setEvent(event);
            topic.setTopicType(Topic.TYPE_ATIVITY);
            event.setActivityTopic(topic);
            if (event.getCoterie() != null) {
            	topic.setCoterie(event.getCoterie());
            }
            discoverService.saveTopic(topic);
            
            if (serverIds != null) {
            	String folder = config.getActivityPicturePath() + "/" + event.getId();
            	String accessToken = wxMpService.getWxAccessToken();
            	List<String> pictures = WxRequestHelper.downloadServerFileToAliyunOSS(folder, serverIds, accessToken);
				int j = 1;
				for (String url : pictures) {
					EventPicture eventPicture = new EventPicture(event, url, j++, user);
        			event.getEventPictures().add(eventPicture);
				}
			}
            
            EventUser eventUser = new EventUser(event, user, 1);
            eventUser.setRealName(user.getNickName());
            eventUser.setPhoneNo(phoneNo);
            eventUser.setGender(user.getGender());
            eventUser.setEffective(true);
            event.getEventUsers().add(eventUser);
            
            if (event.getCoterie() == null) {
				// 创建圈子
            	coterie = new Coterie();
    			coterie.setName(name);
    			coterie.setDescription(description);
    			coterie.setCreationDate(new Date());
    			coterie.setCreator(user);
    			coterie.setHot(0);
    			coterie.setStatus(Coterie.STATUS_NORMAL);
    			
    			List<EventPicture> eventPictures = new ArrayList<EventPicture>(event.getEventPictures());
    			if (eventPictures.size() > 0) {
    				Collections.sort(eventPictures, new EventPictureComparator());
    				
    				CoteriePicture coteriePicture = new CoteriePicture();
    				coteriePicture.setCoterie(coterie);
    				coteriePicture.setCreationDate(new Date());
    				coteriePicture.setUser(user);
    				coteriePicture.setUrl(eventPictures.get(0).getUrl());
    				coterie.setCoteriePicture(coteriePicture);
				}
				
    			List<EventTag> eventTags = event.getTags();
    			for (int i = 1; i <= eventTags.size(); i++) {
    				Tag tag = eventTags.get(i - 1).getTag();
    				if (tag != null) {
    					List<Tag> secondLevelTags = activityService.getSecondLevelTags(tag.getId());
                		CoterieTag coterieTag = new CoterieTag(coterie, tag, secondLevelTags.size() > 0 ? -1 : i);
                		coterie.getTags().add(coterieTag);
					}
    			}
    			coterie.getCoterieGuys().add(new CoterieGuy(coterie, 1, user, new Date(), true,	true));
    			
    			discoverService.saveCoterie(coterie);
    			event.setCoterie(coterie);
    			event.getActivityTopic().setCoterie(coterie);
			} else {
				// 给圈子用户发送活动通知
				coterie = event.getCoterie();
				Set<CoterieGuy> coterieGuys = coterie.getCoterieGuys();
				String accessToken = wxMpService.getWxAccessToken();
				for (CoterieGuy coterieGuy : coterieGuys) {
					User coterieUser = coterieGuy.getUser();
					if (coterieUser != null) {
						UserCoterieSetting userCoterieSetting = userService.loadUserCoterieSetting(coterieUser.getId(), coterie.getId());
						if (userCoterieSetting.isAllowPush()) {
							WxRequestHelper.sendCoterieActivityLauncher(accessToken, event, coterie, coterieGuy.getUser());
						}
					}
				}
			}
            
            activityService.saveEvent(event);
            
            Map<String, Object> result = new HashMap<String, Object>();
            coterie = event.getCoterie();
			result.put("activityId", event.getId());
			result.put("coterieId", coterie.getId());
			result.put("coterieName", coterie.getName());
			result.put("coterieCover", coterie.getCoteriePicture() != null ? coterie.getCoteriePicture().getUrl() : "");
			
            logger.info("=====> Launch activity end <=====");
            ResponseHelper.addResponseSuccessData(resultMap, result);
        } catch (ApplicationServiceException e) {
        	ResponseHelper.addResponseFailData(resultMap, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            ResponseHelper.addResponseFailData(resultMap, e.getMessage());
        }
        return resultMap;
    }
    
    /**
     * 编辑活动
     * @param userId
     * @param activityId
     * @param coterieId
     * @param tags
     * @param name
     * @param pictures
     * @param startTime
     * @param gps
     * @param address
     * @param minCount
     * @param maxCount
     * @param charge
     * @param isOpen
     * @param password
     * @param description
     * @param phoneNo
     * @return
     */
	@ApiOperation(value = "编辑活动", httpMethod = "POST", notes = "编辑活动")
	@PostMapping(value = "/editActivity")
    public Map<String, Object> editActivity(
    		@RequestParam("userId") int userId,
    		@RequestParam("activityId") int activityId,
    		@RequestParam(name = "coterieId",required = false) Integer coterieId,
    		@RequestParam(name = "tags",required = false) String tags,
    		@RequestParam(name = "name",required = false) String name,
    		@RequestParam(name = "pictures", required = false) String[] pictures,// 传参方式还需再对
    		@RequestParam(name = "startTime",required = false) Date startTime,
    		@RequestParam(name = "gps",required = false) String gps,
    		@RequestParam(name = "address",required = false) String address,
            @RequestParam(name = "minCount",required = false) Integer minCount,
            @RequestParam(name = "maxCount",required = false) Integer maxCount,
            @RequestParam(name = "charge",required = false) String charge,
            @RequestParam(name = "isOpen",required = false) Boolean isOpen,
            @RequestParam(name = "password",required = false) String password,
            @RequestParam(name = "description", required = false) String description,
            @RequestParam(name = "phoneNo", required = false) String phoneNo) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
        	logger.info("=====> Start to edit activity <=====");
        	
        	Event event = activityService.findEventById(activityId);
        	if (event == null) {
				throw new ApplicationServiceException(ApplicationServiceException.ACTIVITY_NOT_EXIST);
			}
        	
        	if (event.getCreator() != null && event.getCreator().getId() != userId) {
        		throw new ApplicationServiceException(ApplicationServiceException.ACTIVITY_NOT_CREATOR);
			}
        	
        	User user = userService.findUserById(userId);
        	if (user == null) {
        		throw new UserException(UserException.NOT_EXISTING);
        	}
        	
        	if (coterieId != null) {
				Coterie coterie = discoverService.findCoterieById(coterieId);
				if (coterie == null) {
					throw new ApplicationServiceException(ApplicationServiceException.COTERIE_NOT_EXIST);
				}
				event.setCoterie(coterie);
			}
        	
        	if (StringUtils.isNotBlank(name)) {
				event.setName(name);
			}
        	
        	if (startTime != null) {
        		event.setStartTime(startTime);
			}
        	
        	if (gps != null) {
				event.setGps(gps);
			}
        	
        	if (address != null) {
				event.setAddress(address);
			}
        	
        	if (minCount != null) {
				event.setMinCount(minCount);
			}
        	
        	if (maxCount != null) {
				event.setMaxCount(maxCount);
			}
        	
        	if (charge != null) {
				event.setCharge(charge);
			}
        	
        	if (isOpen != null) {
				event.setOpen(isOpen);
				if (!isOpen) {
					event.setPassword(password);
				}
			}
        	
        	if (description != null) {
				event.setDescription(description);
			}
        	
        	if (phoneNo != null) {
				event.setPhoneNo(phoneNo);
			}
        	
        	event.setAllowSignUp(true);
        	event.setStatus(Event.STATUS_SIGNUP);
        	
        	if (StringUtils.isNotBlank(tags)) {
				String[] tagsSplit = tags.split(",");
				
				event.getTags().clear();
				int i = 1;
				for (String tagIdstr : tagsSplit) {
					Tag tag = activityService.findTagById(Integer.parseInt(tagIdstr));
					if (tag != null) {
						List<Tag> secondLevelTags = activityService.getSecondLevelTags(tag.getId());
	            		EventTag eventTag = new EventTag(event, tag, secondLevelTags.size() > 0 ? -1 : i++);
	            		event.getTags().add(eventTag);
					}
				}
			}
        	
        	if (pictures != null) {
        		event.getEventPictures().clear();
        		
        		String basePicturePath = ApplicationConfig.getInstance().getActivityPicturePath();
        		String pictureFolderPath = String.format(basePicturePath, event.getId());
        		File folder = new File(pictureFolderPath);
        		if (folder.exists()) {
        			for (File file : folder.listFiles()) {
        				if (file.exists()) {
        					file.delete();
        				}
        			}
				}
        		
            	int i = 1;
            	for (String picBase64Str : pictures) {
            		if (picBase64Str.indexOf(",") > 0) {
            			String picPath = FileUploadHelper.uploadPicture(pictureFolderPath, picBase64Str, i + ".jpg");
            			
            			EventPicture eventPicture = new EventPicture(event, picPath, i, user);
            			event.getEventPictures().add(eventPicture);
            			i++;
					}
				}
			}
         	
            activityService.saveEvent(event);
            
            Map<String, Object> result = new HashMap<String, Object>();
			result.put("activityId", event.getId());
			result.put("coterieId", event.getCoterie().getId());
			
            logger.info("=====> Edit activity end <=====");
            ResponseHelper.addResponseSuccessData(resultMap, result);
        } catch (Exception e) {
            e.printStackTrace();
            ResponseHelper.addResponseFailData(resultMap, e.getMessage());
        }
        return resultMap;
    }
    
	@ApiOperation(value = "获取活动列表", httpMethod = "GET", notes = "获取活动列表")
	@GetMapping(value = "/getActivityList")
	public Map<String, Object> getActivityList(
			@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(name = "start", required = false) Integer startRow,
			@RequestParam(name = "pageSize", required = false) Integer pageSize) {
		Map<String, Object> responseMap = new HashMap<String, Object>();
		try {
			logger.info("=====> Start to get activity list <=====");
			
			if (startRow == null) {
				startRow = 0;
			}
			if (pageSize == null) {
				pageSize = ApplicationConfig.getInstance().getDefaultPageSize();
			}
			
			SearchCriteria searchCriteria = new SearchCriteria();
			searchCriteria.setStartRow(startRow);
			searchCriteria.setPageSize(pageSize);
			searchCriteria.setKeyword(keyword);

			Map<String, Object> eventListMap = activityService.getActivityList(searchCriteria);
			List<Event> events = (List<Event>) eventListMap.get("resultList");
			List<Map> resultList = new ArrayList<Map>();
			if (events != null) {
				for (Event event : events) {
					Map<String, Object> result = new HashMap<String, Object>();
					result.put("activityId", event.getId());
					
					List<EventPicture> eventPictures = new ArrayList<EventPicture>(event.getEventPictures());
					EventPicture eventPicture = null;
					if (eventPictures != null && eventPictures.size() > 0) {
						Collections.sort(eventPictures, new EventPictureComparator());
						eventPicture = eventPictures.get(0);
					}
					// 封面
					result.put("cover", eventPicture != null ? eventPicture.getUrl() : "");
					// 活动名称
					result.put("name", event.getName());
//					result.put("releaseTime", event.getCreationDate());
					// 开始时间
					result.put("startTime", event.getStartTime());
					// 状态
					result.put("status", event.getStatus());
					// 地址定位
					result.put("gps", event.getGps());
					result.put("address", event.getAddress());
//					result.put("isOpen", event.isOpen());
					// 费用类型
					result.put("charge", event.getCharge());
					
					List<String> tagList = new ArrayList<>();
					List<EventTag> eventTags = event.getTags();
					for (EventTag eventTag : eventTags) {
						Tag tag = eventTag.getTag();
						tagList.add(tag.getName());
					}
					// 当只有一个一级标签时，自动添加二级标签不限
					if (eventTags.size() == 1) {
						Tag et = eventTags.get(0).getTag();
						if (et != null && et.getType().equalsIgnoreCase(Tag.TYPE_FIRST_LEVEL)) {
							tagList.add(Tag.TAG_UNLIMITED);
						}
					}
					// 标签
					result.put("tags", tagList);
					
					Map<String, Object> numbersMap = new HashMap<String, Object>();
					numbersMap.put("maxCount", event.getMaxCount());
					numbersMap.put("minCount", event.getMinCount());
					numbersMap.put("currentCount", event.getEffectiveMembers().size());
					// 人数情况
					result.put("userCount", numbersMap);
					
					// 圈子
					Coterie coterie = event.getCoterie();
					if (coterie != null) {
						Map<String, Object> coterieMap = new HashMap<String, Object>();
						coterieMap.put("id", coterie.getId());
						coterieMap.put("name", coterie.getName());
						result.put("coterie", coterieMap);
					}
					
					resultList.add(result);
				}
			}
			
			logger.info("=====> Get activity list end <=====");
			
			ResponseHelper.addResponseSuccessData(responseMap, resultList);
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.addResponseFailData(responseMap, e.getMessage());
		}

		return responseMap;
	}
    
	@ApiOperation(value = "获取活动详情", httpMethod = "GET", notes = "获取活动详情")
	@GetMapping(value = "/getActivityInfo")
	public Map<String, Object> getActivityInfo(@RequestParam(name = "userId") int userId, @RequestParam(name = "activityId") int activityId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			logger.info("=====> Start to get activity info <=====");
			
			Event event = activityService.findEventById(activityId);
			if (event == null) {
				throw new ApplicationServiceException(ApplicationServiceException.ACTIVITY_NOT_EXIST);
			}
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("activityId", activityId);
			result.put("name", event.getName());
			result.put("status", event.getStatus());
			result.put("startTime", DateUtils.format(event.getStartTime(), DateUtils.yMdHm));
			result.put("endTime", DateUtils.format(event.getEndTime(), DateUtils.yMdHm));
			result.put("gps", event.getGps());
			result.put("address", event.getAddress());
			result.put("isOpen", event.isOpen());
			result.put("description", event.getDescription());
			result.put("charge", event.getCharge());
			result.put("isCreator", event.isCreator(userId));
			result.put("allowSignUp", event.isAllowSignUp());
			
			// 活动留言
			Topic topic = event.getActivityTopic();
			if (topic != null) {
				Map<String, Object> topicMap = new HashMap<String, Object>();
				topicMap.put("topicId", topic.getId());
				
				List<TopicComment> comments = new ArrayList<TopicComment>(topic.getComments());
				Collections.sort(comments, new TopicCommentComparator());
				topicMap.put("commentCount", comments.size());
				
				if (comments.size() > 0) {
					TopicComment lastComment = comments.get(0);
					if (lastComment != null) {
						topicMap.put("lastComment", lastComment.getContent());
						topicMap.put("lastCommentTime", lastComment.getCreationDate());
					}
				}
				result.put("topic", topicMap);
			}
			
			// 活动图片
			List<String> pictureList = new ArrayList<String>();
			List<EventPicture> eventPictures = new ArrayList<EventPicture>(event.getEventPictures());
			Collections.sort(eventPictures, new EventPictureComparator());
			for (EventPicture eventPicture : eventPictures) {
				pictureList.add(eventPicture.getUrl());
			}
			result.put("pictures", pictureList);
			
			// 组织者
			User organizer = event.getCreator();
			if (organizer != null) {
				Map<String, Object> organizerMap = new HashMap<String, Object>();
				organizerMap.put("userId", organizer.getId());
				organizerMap.put("nickName", organizer.getNickName());
				organizerMap.put("picture", organizer.getPicture());
				organizerMap.put("phoneNo", organizer.getPhoneNo());
				result.put("organizer", organizerMap);
			}
			
			// 标签
			List<Map> tagList = new ArrayList<>();
			for (EventTag eventTag : event.getTags()) {
				Tag tag = eventTag.getTag();
				if (tag != null) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("id", tag.getId());
					map.put("name", tag.getName());
					tagList.add(map);
				}
			}
			result.put("tags", tagList);
			
			// 参与活动人员
//			List<EventUser> eventUsers = new ArrayList<EventUser>(event.getEventUsers());
//			Collections.sort(eventUsers, new EventUserComparator());
			List<EventUser> eventUsers = event.getEffectiveMembers();
			
			result.put("isSignUp", event.isSignUp(userId));
			
			List<Map> eventUserList = new ArrayList<>();
			for (EventUser eventUser : eventUsers) {
				if (eventUser.getUser() != null) {
					Map<String, Object> userMap = new HashMap<String, Object>();
					userMap.put("userId", eventUser.getUser().getId());
					userMap.put("nickName", eventUser.getUser().getNickName());
					userMap.put("picture", eventUser.getUser().getPicture());
					eventUserList.add(userMap);
				}
			}
			result.put("activityMembers", eventUserList);
			
			Map<String, Object> numbersMap = new HashMap<String, Object>();
			numbersMap.put("maxCount", event.getMaxCount());
			numbersMap.put("minCount", event.getMinCount());
			numbersMap.put("currentCount", eventUsers.size());
			result.put("userCount", numbersMap);
			
			Coterie coterie = event.getCoterie();
			if (coterie != null) {
				Map<String, Object> coterieMap = new HashMap<String, Object>();
				coterieMap.put("id", coterie.getId());
				coterieMap.put("name", coterie.getName());
				result.put("coterie", coterieMap);
			}
			
			logger.info("=====> Get activity info end <=====");
			ResponseHelper.addResponseSuccessData(resultMap, result);
		} catch (ActivityException e) {
			ResponseHelper.addResponseFailData(resultMap, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.addResponseFailData(resultMap, e.getMessage());
		}

		return resultMap;
	}
    
	@ApiOperation(value = "修改活动信息", httpMethod = "POST", notes = "修改活动信息")
	@PostMapping(value = "/updateActivityInfo")
   	public Map<String, Object> updateActivityInfo(@RequestParam("activityId") int activityId, 
   			@RequestParam(name = "minCount", required = false) Integer minCount, 
   			@RequestParam(name = "maxCount", required = false) Integer maxCount,
   			@RequestParam(name = "description", required = false) String description,
   			@RequestParam(name = "allowSignUp", required = false) Boolean allowSignUp,
   			@RequestParam(name = "isOpen", required = false) Boolean isOpen,
   			@RequestParam(name = "password", required = false) String password) {
   		Map<String, Object> resultMap = new HashMap<String, Object>();
   		try {
   			logger.info("=====> Start to update activity info <=====");
   			if (StringUtils.isNotBlank(description)) {
				String sensitiveWord = sensitiveWordUtil.secondLevelFilter(description);
				if (StringUtils.isNotBlank(sensitiveWord)) {
					throw new ApplicationServiceException(sensitiveWord);
				}
			}
   			
   			Event event = activityService.findEventById(activityId);
   			if (event == null) {
   				throw new ApplicationServiceException(ApplicationServiceException.ACTIVITY_NOT_EXIST);
   			}
   			if (minCount != null) {
   				event.setMinCount(minCount);
			}
   			if (maxCount != null) {
   				event.setMaxCount(maxCount);
			}
   			if (allowSignUp != null) {
   				event.setAllowSignUp(allowSignUp);
			}
   			if (description != null) {
				event.setDescription(description);
			}
   			if (isOpen != null) {
   				event.setOpen(isOpen);
   				if (!isOpen && password != null) {
   					event.setPassword(password);
				}
			}
   			activityService.saveEvent(event);
   			logger.info("=====> Update activity info end <=====");
   			ResponseHelper.addResponseSuccessData(resultMap, null);
   		} catch (ApplicationServiceException e) {
   			ResponseHelper.addResponseFailData(resultMap, e.getMessage());
   		} catch (Exception e) {
   			e.printStackTrace();
   			ResponseHelper.addResponseFailData(resultMap, e.getMessage());
   		}

   		return resultMap;
   	}
    
	@ApiOperation(value = "关闭活动", httpMethod = "GET", notes = "关闭活动")
	@GetMapping(value = "/closeActivity")
   	public Map<String, Object> closeActivity(@RequestParam("activityId") int activityId) {
   		Map<String, Object> resultMap = new HashMap<String, Object>();
   		try {
   			logger.info("=====> Start to close activity <=====");
   			
   			Event event = activityService.findEventById(activityId);
   			if (event == null) {
   				throw new ApplicationServiceException(ApplicationServiceException.ACTIVITY_NOT_EXIST);
   			}
   			event.setStatus(Event.STATUS_OVER);
   			event.setAllowSignUp(false);
   			
   			activityService.saveEvent(event);
   			
   			// 关闭活动时给所有人发送通知
   			String accessToken = wxMpService.getWxAccessToken();
			if (StringUtils.isNotBlank(accessToken)) {
				WxRequestHelper.sendActivityClose(accessToken, event);
			}
   			
   			// 判断是否还有进行中的活动，没有的话把解散中的圈子改成已解散
   			Coterie coterie = event.getCoterie();
   			if (coterie.getStatus() == Coterie.STATUS_DISMISSING) {
   				boolean hasActivityProcess = discoverService.hasActivityProcess(coterie.getId());
   				if (!hasActivityProcess) {
   					coterie.setStatus(coterie.STATUS_DISMISSED);
				}
   			}
   			discoverService.saveCoterie(coterie);
   			
   			logger.info("=====> Close activity end <=====");
   			ResponseHelper.addResponseSuccessData(resultMap, null);
   		} catch (ApplicationServiceException e) {
   			ResponseHelper.addResponseFailData(resultMap, e.getMessage());
   		} catch (Exception e) {
   			e.printStackTrace();
   			ResponseHelper.addResponseFailData(resultMap, e.getMessage());
   		}

   		return resultMap;
   	}
    
	@ApiOperation(value = "我的活动列表", httpMethod = "GET", notes = "我的活动列表")
	@GetMapping(value = "/getMyActivityList")
	public Map<String, Object> getMyActivityList(
			@RequestParam("userId") int userId, 
			@RequestParam(name = "dataType", required = false) String dataType, 
			@RequestParam(name = "start", required = false) Integer startRow,
			@RequestParam(name = "pageSize", required = false) Integer pageSize) {
		Map<String, Object> responseMap = new HashMap<String, Object>();
		try {
			logger.info("=====> Start to get user activity list <=====");
			
			User user = userService.findUserById(userId);
        	if (user == null) {
				throw new UserException(UserException.NOT_EXISTING);
			}
        	
			if (startRow == null) {
				startRow = 0;
			}
			if (pageSize == null) {
				pageSize = ApplicationConfig.getInstance().getDefaultPageSize();
			}
			
			SearchCriteria searchCriteria = new SearchCriteria();
			searchCriteria.setStartRow(startRow);
			searchCriteria.setPageSize(pageSize);
			searchCriteria.setUserId(user.getId());
			searchCriteria.setDataType(StringUtils.isBlank(dataType) ? Event.DATATYPE_ALL : dataType);
			
			Map<String, Object> eventListMap = activityService.getUserActivityList(searchCriteria);
			List<Event> events = (List<Event>) eventListMap.get("resultList");
			
			List<Map> resultList = new ArrayList<Map>();
			if (events != null) {
				for (Event event : events) {
					Map<String, Object> result = new HashMap<String, Object>();
					result.put("activityId", event.getId());
					result.put("name", event.getName());
					result.put("charge", event.getCharge());
					result.put("startTime", event.getStartTime());
					result.put("status", event.getStatus());
					result.put("gps", event.getGps());
					result.put("address", event.getAddress());
					result.put("isOpen", event.isOpen());
					result.put("isCreator", event.isCreator(userId));
					
					// 封面
					List<EventPicture> eventPictures = new ArrayList<EventPicture>(event.getEventPictures());
					EventPicture eventPicture = null;
					if (eventPictures != null && eventPictures.size() > 0) {
						Collections.sort(eventPictures, new EventPictureComparator());
						eventPicture = eventPictures.get(0);
					}
					result.put("cover", eventPicture != null ? eventPicture.getUrl() : "");
					
					// 标签
					List<String> tagList = new ArrayList<>();
					for (EventTag eventTag : event.getTags()) {
						Tag tag = eventTag.getTag();
						tagList.add(tag.getName());
					}
					result.put("tags", tagList);
					
					// 圈子
					if (event.getCoterie() != null) {
						Map<String, Object> coterieMap = new HashMap<String, Object>();
						coterieMap.put("id", event.getCoterie().getId());
						coterieMap.put("name", event.getCoterie().getName());
						result.put("coterie", coterieMap);
					}
					
					// 活动人数情况
					Map<String, Object> numbersMap = new HashMap<String, Object>();
					numbersMap.put("maxCount", event.getMaxCount());
					numbersMap.put("minCount", event.getMinCount());
					numbersMap.put("currentCount", event.getEffectiveMembers().size());
					result.put("userCount", numbersMap);
					
					result.put("isSignUp", event.isSignUp(userId));
					
					resultList.add(result);
				}
			}
			
			logger.info("=====> Get user activity list end <=====");
			
			ResponseHelper.addResponseSuccessData(responseMap, resultList);
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.addResponseFailData(responseMap, e.getMessage());
		}

		return responseMap;
	}
    
    /**
     * 创建活动群聊
     * @param userId
     * @param activityId
     * @param content
     * @return
     */
	@ApiOperation(value = "创建活动群聊", httpMethod = "POST", notes = "创建活动群聊")
	@PostMapping(value = "/createActivityTopic")
	public Map<String, Object> createActivityTopic(@RequestParam("userId") int userId, @RequestParam("activityId") int activityId,
			@RequestParam("description") String description) {
		logger.info("=====> Start to create activity topic <=====");
		Map<String, Object> responseMap = new HashMap<String, Object>();
		try {
			User user = userService.findUserById(userId);
			if (user == null) {
				throw new UserException(UserException.NOT_EXISTING);
			}
			Event event = activityService.findEventById(activityId);
			if (event == null) {
				throw new ActivityException(ActivityException.NOT_EXISTING);
			}
			if (event.getActivityTopic() != null) {
				throw new ActivityException(ActivityException.TOPIC_EXISTING);
			}

			Topic topic  = new Topic();
			topic.setCreationDate(new Date());
			topic.setCoterie(event.getCoterie());
			topic.setCreator(user);
			topic.setDescription(description);
			topic.setEvent(event);
			topic.setTopicType(Topic.TYPE_ATIVITY);
			
			discoverService.saveTopic(topic);
			
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("topicId", topic.getId());
			
			ResponseHelper.addResponseSuccessData(responseMap, result);
			logger.info("=====> Create avtivity topic end <=====");
		} catch (ActivityException e) {
			ResponseHelper.addResponseFailData(responseMap, e.getMessage());
		} catch (UserException e) {
			ResponseHelper.addResponseFailData(responseMap, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.addResponseFailData(responseMap, e.getMessage());
		}
		return responseMap;
	}
    
	@ApiOperation(value = "获取活动群聊详情", httpMethod = "GET", notes = "获取活动群聊详情")
	@GetMapping(value = "/getActivityTopic")
   	public Map<String, Object> getActivityTopic(@RequestParam("topicId") int topicId, @RequestParam("userId") int userId) {
   		logger.info("=====> Start to get activity topic <=====");
   		Map<String, Object> responseMap = new HashMap<String, Object>();
   		try {
   			Topic topic = discoverService.findTopicById(topicId);
   			if (topic == null) {
   				throw new ApplicationServiceException(ApplicationServiceException.ACTIVITY_TOPIC_NOT_EXIST);
   			}
   			Event event = topic.getEvent();
   			if (event == null) {
   				throw new ApplicationServiceException(ApplicationServiceException.ACTIVITY_NOT_EXIST);
			}
   			
   			User user = topic.getCreator();
   			if (user == null) {
   				throw new ApplicationServiceException(ApplicationServiceException.USER_NOT_EXIST);
			}
   			
   			Map<String, Object> resultMap = new HashMap<String, Object>();
   			resultMap.put("topicId", topicId);
   			resultMap.put("description", topic.getDescription());
   			resultMap.put("topicCreateTime", topic.getCreationDate());
   			
   			// 用户相关
   			Map<String, Object> userMap = new HashMap<String, Object>();
   			userMap.put("userId", user.getId());
   			userMap.put("nickName", user.getNickName());
   			userMap.put("picture", user.getPicture());
   			resultMap.put("user", userMap);
   			
   			// 活动相关
   			Map<String, Object> activityMap = new HashMap<String, Object>();
   			activityMap.put("activityId", event.getId());
   			activityMap.put("name", event.getName());
   			activityMap.put("startTime", event.getStartTime());
   			activityMap.put("status", event.getStatus());
   			activityMap.put("gps", event.getGps());
   			activityMap.put("address", event.getAddress());
   			activityMap.put("charge", event.getCharge());
   			
   			List<EventPicture> eventPictures = new ArrayList<EventPicture>(event.getEventPictures());
   			EventPicture eventPicture = null;
   			if (eventPictures != null && eventPictures.size() > 0) {
   				Collections.sort(eventPictures, new EventPictureComparator());
   				eventPicture = eventPictures.get(0);
   			}
   			activityMap.put("cover", eventPicture != null ? eventPicture.getUrl() : "");
   			
   			List<Map> tagList = new ArrayList<>();
   			for (EventTag eventTag : event.getTags()) {
				Tag tag = eventTag.getTag();
				if (tag != null) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("id", tag.getId());
					map.put("name", tag.getName());
					tagList.add(map);
				}
			}
			activityMap.put("tags", tagList);
   			
   			Map<String, Object> countMap = new HashMap<String, Object>();
   			countMap.put("maxCount", event.getMaxCount());
   			countMap.put("minCount", event.getMinCount());
   			countMap.put("currentCount", event.getEffectiveMembers().size());
   			activityMap.put("userCount", countMap);
   			
   			if (event.getCoterie() != null) {
   				Map<String, Object> coterieMap = new HashMap<String, Object>();
   				coterieMap.put("id", event.getCoterie().getId());
   				coterieMap.put("name", event.getCoterie().getName());
   				coterieMap.put("status", event.getCoterie().getStatus());
   				activityMap.put("coterie", coterieMap);
			}
   			
   			resultMap.put("activity", activityMap);
   			
   			List<Map> praiseList = new ArrayList<Map>();
   			List<TopicPraise> topicPraises = new ArrayList<>(topic.getPraises());
   			Collections.sort(topicPraises, new TopicPraiseComparator());
   			for (TopicPraise topicPraise : topicPraises) {
   				if (topicPraise.getUser() != null) {
   					Map<String, Object> praiseMap = new HashMap<String, Object>();
   					praiseMap.put("userId", topicPraise.getUser().getId());
   					praiseMap.put("nickName", topicPraise.getUser().getNickName());
   					praiseMap.put("picture", topicPraise.getUser().getPicture());
   					praiseList.add(praiseMap);
				}
   				
			}
   			resultMap.put("praise", praiseList);
   			resultMap.put("commentCnt", topic.getComments().size());
   			resultMap.put("praiseCnt", topic.getPraises().size());
   			resultMap.put("hasPraise", topic.hasPraise(userId));
   			
   			ResponseHelper.addResponseSuccessData(responseMap, resultMap);
   			logger.info("=====> Get avtivity topic end <=====");
   		} catch (ApplicationServiceException e) {
   			ResponseHelper.addResponseFailData(responseMap, e.getMessage());
   		} catch (Exception e) {
   			e.printStackTrace();
   			ResponseHelper.addResponseFailData(responseMap, e.getMessage());
   		}
   		return responseMap;
   	}
    
	@ApiOperation(value = "获取话题评论", httpMethod = "GET", notes = "获取话题评论")
	@GetMapping(value = "/getTopicCommentList")
   	public Map<String, Object> getTopicCommentList(@RequestParam("topicId") int topicId) {
   		logger.info("=====> Start to get topic comment list <=====");
   		Map<String, Object> responseMap = new HashMap<String, Object>();
   		try {
   			Topic topic = discoverService.findTopicById(topicId);
   			if (topic == null) {
   				throw new ApplicationServiceException(ApplicationServiceException.ACTIVITY_TOPIC_NOT_EXIST);
   			}
   			
   			List<TopicComment> topicComments = new ArrayList<TopicComment>(topic.getComments());
			Collections.sort(topicComments, new TopicCommentComparator());

			List<Map> comments = new ArrayList<Map>();
			for (TopicComment topicComment : topicComments) {
				Map<String, Object> comment = new HashMap<String, Object>();
				User commentUser = topicComment.getUser();
				Map<String, Object> commentMap = new HashMap<String, Object>();
				if (commentUser != null) {
					commentMap.put("userId", commentUser.getId());
					commentMap.put("nickName", commentUser.getNickName());
					commentMap.put("picture", commentUser.getPicture());
				}
				comment.put("user", commentMap);
				comment.put("comment", topicComment.getContent());
				comment.put("commentTime", topicComment.getCreationDate());
				comments.add(comment);
			}
   			
   			ResponseHelper.addResponseSuccessData(responseMap, comments);
   			logger.info("=====> Get topic comment list end <=====");
   		} catch (ApplicationServiceException e) {
   			ResponseHelper.addResponseFailData(responseMap, e.getMessage());
   		} catch (Exception e) {
   			e.printStackTrace();
   			ResponseHelper.addResponseFailData(responseMap, e.getMessage());
   		}
   		return responseMap;
   	}
    
	@ApiOperation(value = "获取点赞列表", httpMethod = "GET", notes = "获取点赞列表")
	@GetMapping(value = "/getTopicPraiseList")
   	public Map<String, Object> getTopicPraiseList(@RequestParam("topicId") int topicId) {
   		logger.info("=====> Start to get topic parise list <=====");
   		Map<String, Object> responseMap = new HashMap<String, Object>();
   		try {
   			Topic topic = discoverService.findTopicById(topicId);
   			if (topic == null) {
   				throw new ApplicationServiceException(ApplicationServiceException.ACTIVITY_TOPIC_NOT_EXIST);
   			}
   			
			List<TopicPraise> topicPraises = new ArrayList<TopicPraise>(topic.getPraises());
			Collections.sort(topicPraises, new TopicPraiseComparator());
			List<Map> praises = new ArrayList<Map>();
			Map<String, Object> praise = null;
			for (TopicPraise topicPraise : topicPraises) {
				praise = new HashMap<String, Object>();
				User praiseUser = topicPraise.getUser();
				if (praiseUser != null) {
					praise.put("userId", praiseUser.getId());
					praise.put("nickName", praiseUser.getNickName());
					praise.put("picture", praiseUser.getPicture());
					praise.put("gender", praiseUser.getGender());
					praise.put("createTime", topicPraise.getCreationDate());
				}
				praises.add(praise);
			}
   			
   			ResponseHelper.addResponseSuccessData(responseMap, praises);
   			logger.info("=====> Get topic parise list end <=====");
   		} catch (ApplicationServiceException e) {
   			ResponseHelper.addResponseFailData(responseMap, e.getMessage());
   		} catch (Exception e) {
   			e.printStackTrace();
   			ResponseHelper.addResponseFailData(responseMap, e.getMessage());
   		}
   		return responseMap;
   	}
    
	@ApiOperation(value = "话题评论", httpMethod = "POST", notes = "话题评论")
	@PostMapping(value = "/commentTopic")
	public Map<String, Object> commentTopic(@RequestParam(name = "userId") int userId,
			@RequestParam(name = "topicId") int topicId,
			@RequestParam(name = "comment") String content) {
		Map<String, Object> responseMap = new HashMap<String, Object>();
		try {
			logger.info("=====> Start to comment topic <=====");
			
			User user = userService.findUserById(userId);
			if (user == null) {
				throw new UserException(UserException.NOT_EXISTING);
			}
			
			Topic topic = discoverService.findTopicById(topicId);
			if (topic == null) {
				throw new ActivityException(ActivityException.TOPIC_NOT_EXISTING);
			}
			
			// 已解散的圈子不能评论活动
			if (topic.getEvent() != null) {
				Coterie coterie = topic.getEvent().getCoterie();
				if (coterie != null) {
					if (coterie.getStatus() == Coterie.STATUS_DISMISSED) {
						throw new ApplicationServiceException(ApplicationServiceException.COTERIE_DISMISSED);
					}
				}
			}
			
			// 敏感词汇检查
			if (StringUtils.isNotBlank(content)) {
				String sensitiveWord = sensitiveWordUtil.thirdLevelFilter(content);
				if (StringUtils.isNotBlank(sensitiveWord)) {
					throw new ApplicationServiceException(sensitiveWord);
				}
			}
			
			TopicComment topicComment = new TopicComment(topic, user, content);
			topic.getComments().add(topicComment);
			discoverService.saveTopic(topic);
			
			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("commentCnt", topic.getComments().size());

			logger.info("=====> Comment topic end <=====");
			ResponseHelper.addResponseSuccessData(responseMap, resultMap);
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.addResponseFailData(responseMap, e.getMessage());
		}

		return responseMap;
	}
    
	@ApiOperation(value = "话题点赞", httpMethod = "GET", notes = "话题点赞")
	@PostMapping(value = "/praiseTopic")
	public Map<String, Object> praiseTopic(@RequestParam("userId")int userId, @RequestParam("topicId")int topicId) {
		Map<String, Object> responseMap = new HashMap<String, Object>();
		try{
			logger.info("=====> Start to praise topic <=====");
			User user = userService.findUserById(userId);
   			if (user == null) {
   				throw new UserException(UserException.NOT_EXISTING);
			}
			
			Topic topic = discoverService.findTopicById(topicId);
			if (topic == null) {
				throw new ActivityException(ActivityException.TOPIC_NOT_EXISTING);
			}
			
			int nextOrderNo = topic.getNextOrderNo();
			topic.getPraises().add(new TopicPraise(topic, user, nextOrderNo));
			discoverService.saveTopic(topic);
			
			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("praiseCnt", topic.getPraises().size());
			
			logger.info("=====> Praise topic type end <=====");
			ResponseHelper.addResponseSuccessData(responseMap, resultMap);
		} catch (DataIntegrityViolationException e) {
			ResponseHelper.addResponseFailData(responseMap, "不能重复点赞");
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.addResponseFailData(responseMap, e.getMessage());
		}
		return responseMap;
	}
    
    /**
     * 微信活动报名
     * @param eventIdStr	活动ID
     * @param userId		用户ID
     * @param 
     * @return
     */
	@ApiOperation(value = "活动报名", httpMethod = "POST", notes = "活动报名")
	@PostMapping(value = "/signUp")
    public Map<String, Object> signUp(@RequestParam(name = "activityId") int activityId,
    		@RequestParam("userId") int userId,
    		@RequestParam("realName") String realName,
    		@RequestParam("phoneNo") String phoneNo,
    		@RequestParam("gender") int gender,
    		@RequestParam(name = "friends", required = false) String friends,
//    		@RequestParam(name="password",required = false) String password,
    		@RequestParam("isEditSignUp") boolean isEditSignUp
    		) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
        	logger.info("=====> Start to event signup <=====");
        	//获取当前用户报名信息
        	User user = userService.findUserById(userId);
            Event event = activityService.findEventById(activityId);
            if (event == null) {
            	throw new ApplicationServiceException(ApplicationServiceException.ACTIVITY_NOT_EXIST);
			}
            
            /*if (!event.isOpen() && !password.equals(event.getPassword())) {
				throw new ApplicationServiceException(ApplicationServiceException.ACTIVITY_INCORRECT_CREDENTIALS);
			}*/
            
            if (StringUtils.isBlank(realName) || StringUtils.isBlank(phoneNo)) {
				throw new ApplicationServiceException(ApplicationServiceException.ACTIVITY_PARAM_IS_EMPTY);
			}
            
            if (!event.isAllowSignUp()) {
            	throw new ApplicationServiceException(ApplicationServiceException.ACTIVITY_DONT_ALLOW_SINGNUP);
			}
            
            //检查活动是否满员，满员返回错误信息，否则继续
            List<EventUser> eventUsers = event.getEffectiveMembers();
            if (eventUsers.size() == event.getMaxCount()) {
            	throw new ApplicationServiceException(ApplicationServiceException.ACTIVITY_USER_FULL);
			}
            
            // 活动报名前人数
            int beforeCount = eventUsers.size();
            
            JSONArray friendArray = null;
            if (StringUtils.isNotBlank(friends)) {
            	friendArray = JSONArray.fromObject(friends);
            	if ((event.getMaxCount() - eventUsers.size()) < (friendArray.size() + 1)) {
            		throw new ApplicationServiceException(ApplicationServiceException.ACTIVITY_SPACE_INSUFFICIENT);
    			}
			}
            
            if (!isEditSignUp) {
            	//检查重复报名
                boolean haveSignUp = event.haveSignUp(user.getId());
                if (haveSignUp){
                    throw new ApplicationServiceException(ApplicationServiceException.ACTIVITY_HAS_SIGNUPED);
                }
			}
            
            //清空旧报名信息
            List<EventUser> needRemoveUsers = new ArrayList<EventUser>();
            for (EventUser eventUser : event.getEventUsers()) {
				if ((eventUser.getUser() != null && eventUser.getUser().getId() == user.getId()) 
						|| (eventUser.getInviter() != null && eventUser.getInviter().getId() == user.getId())) {
					needRemoveUsers.add(eventUser);
				}
			}
            event.getEventUsers().removeAll(needRemoveUsers);
            
            int maxOrderNo = event.getEventUserMaxOrderNo() + 1;
            EventUser fristeventUser = new EventUser(event, user, maxOrderNo++);
            fristeventUser.setRealName(realName != null ? realName : user.getNickName());
            fristeventUser.setPhoneNo(phoneNo != null ? phoneNo : user.getPhoneNo());
            fristeventUser.setGender(gender);
            fristeventUser.setEffective(true);
            
            event.getEventUsers().add(fristeventUser);
            if (friendArray != null) {
            	for (int i = 0; i < friendArray.size(); i++) {
            		JSONObject obj = friendArray.getJSONObject(i);
					if (obj != null) {
						EventUser friend = new EventUser();
						friend.setRealName(obj.getString("name"));
						friend.setGender(obj.getInt("gender"));
						friend.setCreationDate(new Date());
						friend.setEvent(event);
						friend.setOrderNo(maxOrderNo++);
						friend.setInviter(user);
						friend.setEffective(true);
						event.getEventUsers().add(friend);
					}
				}
			}
            // 报名后人数
            int afterCount = event.getEffectiveMembers().size();
            if (afterCount == event.getMaxCount()) {
            	// 关闭允许报名
				event.setAllowSignUp(false);
			}
            
            // 当活动报名前低于最小人数，报名后大于最小人数时触发
            boolean isSignFull = false;
            if (afterCount >= event.getMinCount() && beforeCount < event.getMinCount()) {
            	isSignFull = true;
			}
            
            activityService.saveEvent(event);
            activityService.refresh(event);
            
            Coterie coterie = event.getCoterie();
            String accessToken = wxMpService.getWxAccessToken();
            // 活动报名成功，自动关注圈子
            
            // 判断是否加入圈子
            boolean hasJoin = discoverService.checkExistCoterieGuy(coterie.getId(), user.getId());
            if (!hasJoin) {
            	int nextOrderNo = coterie.getCoterieGuyNextOrderNo();
    			CoterieGuy coterieGuy = new CoterieGuy(coterie, nextOrderNo, user, new Date(), false, true);
    			discoverService.saveCoterieGuy(coterieGuy);
    			
    			if (StringUtils.isNotBlank(accessToken)) {
    				WxRequestHelper.sendCoterieJoinMsg(accessToken, coterie, user);
    			}
			}
            
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("activityId", activityId);
            
            List<Map> activityMembers = new ArrayList<Map>();
            List<EventUser> list = new ArrayList<>(event.getEventUsers());
            Collections.sort(list, new Comparator<EventUser>() {
				@Override
				public int compare(EventUser o1, EventUser o2) {
					return o2.getOrderNo() - o1.getOrderNo();
				}
			});
            
            for (EventUser eu : list) {
            	if (eu.getUser() != null) {
            		Map<String, Object> memberMap = new HashMap<String, Object>();
            		memberMap.put("userId", eu.getUser().getId());
            		memberMap.put("nickName", eu.getUser().getNickName());
            		memberMap.put("picture", eu.getUser().getPicture());
            		activityMembers.add(memberMap);
				}
			}
            result.put("activityMembers", activityMembers);
            
            if (coterie != null) {
            	Map<String, Object> coterieMap = new HashMap<String, Object>();
            	coterieMap.put("id", coterie.getId());
            	coterieMap.put("cover", coterie.getCoteriePicture() != null ? coterie.getCoteriePicture().getUrl() : "");
            	coterieMap.put("name", coterie.getName());
            	result.put("coterie", coterieMap);
			}
            
            Map<String, Object> countMap = new HashMap<String, Object>();
			countMap.put("maxCount", event.getMaxCount());
			countMap.put("minCount", event.getMinCount());
			countMap.put("currentCount", event.getEffectiveMembers().size());
			result.put("userCount", countMap);
			
			// 报名成功给用户发送通知
//			String accessToken = wxMpService.getWxAccessToken();
			if (StringUtils.isNotBlank(accessToken)) {
				WxRequestHelper.sendActivitySignUpToUser(accessToken, event, fristeventUser);
//				WxRequestHelper.sendActivitySignUpToCreator(accessToken, event, fristeventUser);
				
				if (isSignFull) {
					// 当活动报名前低于最小人数，报名后大于最小人数时触发
					for (EventUser eventUser : event.getEffectiveMembers()) {
						if (eventUser.getUser() != null) {
							WxRequestHelper.sendActivitySignFull(accessToken, event, eventUser);
						}
					}
				}
			}
            
            ResponseHelper.addResponseSuccessData(resultMap, result);
            logger.info("=====> Event signup end <=====");
        } catch (ApplicationServiceException e) {
        	ResponseHelper.addResponseFailData(resultMap, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            ResponseHelper.addResponseFailData(resultMap, e.getMessage());
        }
        return resultMap;
    }
    
	@ApiOperation(value = "取消报名", httpMethod = "POST", notes = "取消报名")
	@PostMapping(value = "/signOut")
    public Map<String, Object> signOut(@RequestParam(name = "activityId") int activityId,
    		@RequestParam("userId") int userId) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
        	logger.info("=====> Start to event signout <=====");
        	
            Event event = activityService.getEventById(activityId);
            if (event == null) {
            	throw new ApplicationServiceException(ApplicationServiceException.ACTIVITY_NOT_EXIST);
			}
            
            EventUser currentUser = null;
//            boolean hasRetinues = false;
            for (EventUser eventUser : event.getEventUsers()) {
            	User eu = eventUser.getUser();
            	User inviter = eventUser.getInviter();
				if (eu != null && eu.getId() == userId) {
					currentUser = eventUser;
					eventUser.setEffective(false);
				}
				if (inviter != null && inviter.getId() == userId) {
//					hasRetinues = true;
					eventUser.setEffective(false);
				}
			}
            event.setAllowSignUp(true);
            activityService.saveEvent(event);
            
            // 取消报名，给组织者发送通知
            String accessToken = wxMpService.getWxAccessToken();
			if (StringUtils.isNotBlank(accessToken) && currentUser != null) {
				WxRequestHelper.sendActivitySignOutToCreator(accessToken, event, currentUser);
			}
            
            ResponseHelper.addResponseSuccessData(resultMap, null);
            logger.info("=====> Event signup end <=====");
        } catch (ApplicationServiceException e) {
        	ResponseHelper.addResponseFailData(resultMap, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            ResponseHelper.addResponseFailData(resultMap, e.getMessage());
        }
        return resultMap;
    }
    
	@ApiOperation(value = "获取活动成员信息", httpMethod = "GET", notes = "获取活动成员信息")
	@GetMapping(value = "/getActivityMembers")
    public Map<String, Object> getActivityMembers(@RequestParam(name = "userId") int userId, @RequestParam(name = "activityId") int activityId) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
        	logger.info("=====> Start to get activity members <=====");
        	//获取当前用户报名信息
            Event event = activityService.getEventById(activityId);
            if (event == null) {
            	throw new ApplicationServiceException(ApplicationServiceException.ACTIVITY_NOT_EXIST);
			}
            
            boolean isCreator = event.isCreator(userId);
            // 所有随从人员
            Map<String, List<EventUser>> retinueMap = new HashMap<String, List<EventUser>>();
            for (EventUser eu : event.getEventUsers()) {
            	User inviter = eu.getInviter();
            	if (inviter != null) {
            		List<EventUser> retinueList =  retinueMap.get(String.valueOf(inviter.getId()));
            		if (retinueList != null) {
            			retinueList.add(eu);
					} else {
						retinueList = new ArrayList<EventUser>();
						retinueList.add(eu);
						retinueMap.put(String.valueOf(inviter.getId()), retinueList);
					}
				}
            }
            
//            List<EventUser> userList =  new ArrayList<EventUser>(event.getEventUsers());
//            Collections.sort(userList, new EventUserComparator());
            
            List<Map> resultList = new ArrayList<Map>();
            for (EventUser eventUser : event.getEffectiveMembers()) {
            	User user = eventUser.getUser();
				if (user != null) {
					Map<String, Object> data = new HashMap<String, Object>();
					data.put("userId", user.getId());
					data.put("gender", eventUser.getGender());
//					data.put("phoneNo", eventUser.getPhoneNo());
					data.put("signUpTime", eventUser.getCreationDate());
					data.put("name", eventUser.getRealName());
					data.put("picture", user.getPicture());
					if (isCreator) {
						data.put("phoneNo", eventUser.getPhoneNo());
					}
					
					List<EventUser> retinueList = retinueMap.get(String.valueOf(user.getId()));
					if (retinueList != null) {
						List<Map> list = new ArrayList<Map>();
						for (EventUser eu2 : retinueList) {
							if (eu2.isEffective()) {
								Map<String, Object> retinue = new HashMap<String, Object>();
								retinue.put("name", eu2.getRealName());
								retinue.put("gender", eu2.getGender());
								list.add(retinue);
							}
						}
						data.put("retinues", list);
					}
					
					resultList.add(data);
				}
			}
            
            Map<String, Object> dataMap = new HashMap<String, Object>();
            dataMap.put("isCreator", event.isCreator(userId));
            dataMap.put("notified", event.isNotified());
            dataMap.put("members", resultList);
            dataMap.put("activityMembersCnt", event.getEffectiveMembers().size());

            ResponseHelper.addResponseSuccessData(resultMap, dataMap);
            logger.info("=====> Event signup end <=====");
        } catch (ApplicationServiceException e) {
        	ResponseHelper.addResponseFailData(resultMap, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            ResponseHelper.addResponseFailData(resultMap, e.getMessage());
        }
        return resultMap;
    }
    
	@ApiOperation(value = "发送验证码", httpMethod = "GET", notes = "发送验证码")
	@GetMapping(value = "/sendCode")
	public Map<String, Object> sendCode(@RequestParam(name = "phoneno") String phoneNo) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		logger.info("=====> Start to send verify no <=====");
		
		try {
			// 添加手机号格式验证
			if (!isMobile(phoneNo)) {
				throw new ApplicationServiceException(ApplicationServiceException.PHONE_NO_INVALID);
			}

			// 读取短信接口配置文件
			Properties prop =  new Properties();
			logger.info("loading sms.properties...");
			InputStream in = getClass().getClassLoader().getResourceAsStream("sms.properties");
			prop.load(in);
			in.close();
			
			Date currentTime = new Date();
			String verifyNo = CodeUtils.generateVerifyNo();
			String sendhRes = "";
			
			// 同一号码，一小时内只可以接收三条验证码
			boolean canSendSms = userService.checkUserVerifyNoByIp(phoneNo);
			if (!canSendSms) {
				throw new ApplicationServiceException(ApplicationServiceException.VERIFY_FREQUENT);
			}
			
			// 检查旧验证码是否存在，是否过期
			VerifyNo existVerifyNo = userService.getVerifyNo(phoneNo, VerifyNo.VERIFY_NO_TYPE_REGISTER);
			if (existVerifyNo != null) {
				// 如果未过期，发送旧验证码
				Date createTime = existVerifyNo.getCreationTime();
				long difference = currentTime.getTime() - createTime.getTime();
				if (difference <= (60 * 1000)) {
					// 间隔一分钟
					throw new RuntimeException("请" + (60 - Math.round(difference * 0.001)) + "秒后再重新获取");
				}
				
				// 10分钟有效期
				if (difference <= (10 * 60 * 1000)) {
					// 未过期,发送旧验证码
					sendhRes = CodeUtils.sendSms(existVerifyNo, prop);
				} else {
					// 如果过期，发送新验证码，删除旧验证码
					existVerifyNo.setValid(false);
					userService.saveVerifyNo(existVerifyNo);
					
					VerifyNo newVerifyNo = new VerifyNo(phoneNo, verifyNo, VerifyNo.VERIFY_NO_TYPE_REGISTER, currentTime);
					userService.saveVerifyNo(newVerifyNo);
					
					// 发送短信
					sendhRes = CodeUtils.sendSms(newVerifyNo, prop);
				}
			} else {
				// 旧验证码不存在，发送新验证码
				VerifyNo newVerifyNo = new VerifyNo(phoneNo, verifyNo, VerifyNo.VERIFY_NO_TYPE_REGISTER, currentTime);
				userService.saveVerifyNo(newVerifyNo);
				
				// 发送短信
				sendhRes = CodeUtils.sendSms(newVerifyNo, prop);
			}
			logger.info(sendhRes);
			ResponseHelper.addResponseSuccessData(resultMap, null);
		} catch (ApplicationServiceException e) {
        	ResponseHelper.addResponseFailData(resultMap, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.addResponseFailData(resultMap, e.getMessage());
		}
		logger.info("=====> Send verify no end <=====");
		return resultMap;
	}
    
	@ApiOperation(value = "发送活动短信通知", httpMethod = "GET", notes = "发送活动短信通知")
	@GetMapping(value = "/sendActivityNotification")
	public Map<String, Object> sendActivityNotification(@RequestParam("activityId") int activityId, @RequestParam("userId") int userId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		logger.info("=====> Start to send activity notification <=====");
		try {
			Event event = activityService.findEventById(activityId);
            if (event == null) {
            	throw new ApplicationServiceException(ApplicationServiceException.ACTIVITY_NOT_EXIST);
			}
            
            if (event.getCreator() != null && event.getCreator().getId() != userId) {
        		throw new ApplicationServiceException(ApplicationServiceException.ACTIVITY_NOT_CREATOR);
			}
            
            List<EventUser> sendToList = new ArrayList<EventUser>();
            for (EventUser eventUser : event.getEffectiveMembers()) {
            	// 不是创建者，并且有手机号
				if (eventUser.getUser() != null && eventUser.getUser().getId() != userId && StringUtils.isNotBlank(eventUser.getPhoneNo()) && isMobile(eventUser.getPhoneNo())) {
					sendToList.add(eventUser);
				}
			}
            
            if (!sendToList.isEmpty() && !event.isNotified()) {
            	// 读取短信接口配置文件
            	Properties prop =  new Properties();
            	logger.info("loading sms.properties...");
            	InputStream in = getClass().getClassLoader().getResourceAsStream("sms.properties");
            	prop.load(in);
            	in.close();
            	
            	String sendhRes = CodeUtils.batchSubmit(prop, event, sendToList);
				logger.info(sendhRes);
				
				event.setNotified(true);
				activityService.saveEvent(event);
			}
            
            ResponseHelper.addResponseSuccessData(resultMap, null);
		} catch (ApplicationServiceException e) {
        	ResponseHelper.addResponseFailData(resultMap, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.addResponseFailData(resultMap, e.getMessage());
		}
		logger.info("=====> Send activity notification end <=====");
		return resultMap;
	}
    
	@ApiOperation(value = "获取报名信息", httpMethod = "GET", notes = "获取报名信息")
	@GetMapping(value = "/getSignInfo")
    public Map<String, Object> getSignInfo(@RequestParam(name = "userId") int userId, @RequestParam(name = "activityId") int activityId) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
        	logger.info("=====> Start to get sign up info <=====");
        	
            Event event = activityService.findEventById(activityId);
            if (event == null) {
            	throw new ApplicationServiceException(ApplicationServiceException.ACTIVITY_NOT_EXIST);
			}
            
            Set<EventUser> eventUsers = event.getEventUsers();
            EventUser currentUser = null;
            List<EventUser> retinues = new ArrayList<>();
            for (EventUser eu : eventUsers) {
				if (eu.getUser() != null && eu.getUser().getId() == userId) {
					currentUser = eu;
					continue;
				}
				if (eu.getInviter() != null && eu.getInviter().getId() == userId) {
					retinues.add(eu);
				}
			}
            
            Map<String, Object> data = new HashMap<String, Object>();
            if (currentUser != null) {
            	data.put("realName", currentUser.getRealName());
            	data.put("phoneNo", currentUser.getPhoneNo());
            	data.put("gender", currentUser.getGender());
            	List<Map> retinueList = new ArrayList<Map>();
            	for (EventUser retinue : retinues) {
            		Map<String, Object> retinueMap = new HashMap<String, Object>();
            		retinueMap.put("name", retinue.getRealName());
            		retinueMap.put("gender", retinue.getGender());
            		retinueList.add(retinueMap);
				}
            	data.put("retinues", retinueList);
			}

            ResponseHelper.addResponseSuccessData(resultMap, data);
            logger.info("=====> Get signup info end <=====");
        } catch (ApplicationServiceException e) {
        	ResponseHelper.addResponseFailData(resultMap, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            ResponseHelper.addResponseFailData(resultMap, e.getMessage());
        }
        return resultMap;
    }

	@ApiOperation(value = "活动密码校验", httpMethod = "GET", notes = "活动密码校验")
	@GetMapping(value = "/validActivityPassword")
   	public Map<String, Object> validActivityPassword(@RequestParam("activityId") int activityId,
   			@RequestParam(name = "password") String password) {
   		Map<String, Object> resultMap = new HashMap<String, Object>();
   		try {
   			logger.info("=====> Start to validActivity Password info <=====");
   			
   			Event event = activityService.findEventById(activityId);
   			if (event == null) {
   				throw new ApplicationServiceException(ApplicationServiceException.ACTIVITY_NOT_EXIST);
   			}
   			if(!event.getPassword().equals(password)){
   				throw new ApplicationServiceException(ApplicationServiceException.ACTIVITY_INCORRECT_CREDENTIALS);
   			}
   			logger.info("=====> validActivity Password info end <=====");
   			ResponseHelper.addResponseSuccessData(resultMap, null);
   		} catch (ApplicationServiceException e) {
   			ResponseHelper.addResponseFailData(resultMap, e.getMessage());
   		} catch (Exception e) {
   			e.printStackTrace();
   			ResponseHelper.addResponseFailData(resultMap, e.getMessage());
   		}

   		return resultMap;
   	}
    
	private static boolean isMobile(String phoneNo) {
		String regex = ApplicationConfig.getInstance().getPhoneNoRegex();
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(phoneNo);
		return m.matches();
	}
	
	@ApiOperation(value = "更新图文素材", httpMethod = "PUT", notes = "更新图文素材")
	@PutMapping(value = "/updateNewsMaterial")
	public Map<String, Object> updateNewsMaterial() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			logger.info("=====> update news material start <=====");
			String accessToken = wxMpService.getWxAccessToken();
			
			// 获取素材数量
			String responseJson = "";
			JSONObject responseObj = null;
			int page = 1;
			int offset = 0;
			
			String uri = String.format(config.getWxMaterialListUrl(), accessToken);
			JSONObject json = new JSONObject();
			
			List<NewsMaterial> materialList = new ArrayList<NewsMaterial>();
			while (true) {
				json = new JSONObject();
				json.accumulate("type", "news").accumulate("offset", offset).accumulate("count", 20);
				
				responseJson = WxRequestHelper.doJsonPost(uri, json);
				responseObj = JSONObject.fromObject(responseJson);
				
				if (responseObj != null) {
					JSONArray itemArr = responseObj.getJSONArray("item");
					int itemCount = responseObj.getInt("item_count");
					if (itemCount == 0) {
						break;
					}
					for (int i = 0; i < itemArr.size(); i++) {
						JSONObject materialObj = itemArr.getJSONObject(i);
						if (materialObj != null) {
							String mediaId = materialObj.getString("media_id");
							JSONObject content = materialObj.getJSONObject("content");
							JSONArray newsItem = content.getJSONArray("news_item");
							// 单个图文素材
							JSONObject item = newsItem.getJSONObject(0);
							String title = item.getString("title");
							String digest = item.getString("digest");
							String url = item.getString("url");
							String thumb_url = item.getString("thumb_url");
							
							NewsMaterial material = new NewsMaterial(mediaId, title, digest, url, thumb_url);
							materialList.add(material);
						}
					}
				}
				
				page++;
				offset = (page * 20) - 1;
			}
			
			if (!materialList.isEmpty()) {
				wxMpService.updateNewsMaterial(materialList);
			}

			ResponseHelper.addResponseSuccessData(resultMap, null);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			ResponseHelper.addResponseFailData(resultMap, e.getMessage());
		}
		logger.info("=====> update news material end <=====");
		return resultMap;
	}
	
	@ApiOperation(value = "群发图文消息", httpMethod = "POST", notes = "群发图文消息")
	@PostMapping(value = "/sendAllNews")
	public Map<String, Object> sendAllNews(@RequestParam("mediaId") String mediaId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			logger.info("=====> send all news start <=====");
			String accessToken = wxMpService.getWxAccessToken();
			String url = String.format(config.getWxSendAllNewsUrl(), accessToken);
			
			JSONObject json = new JSONObject();
			Map<String, Object> filterMap = new HashMap<String, Object>();
			filterMap.put("is_to_all", true);
			json.accumulate("filter", filterMap);
			
			Map<String, Object> mpnews = new HashMap<String, Object>();
			mpnews.put("media_id", mediaId);

			json.accumulate("mpnews", mpnews);
			json.accumulate("msgtype", "mpnews");
			json.accumulate("send_ignore_reprint", 1);
			
			logger.info(json.toString());
			
			String response = WxRequestHelper.doJsonPost(url, json);
			logger.info(response);

			ResponseHelper.addResponseSuccessData(resultMap, null);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			ResponseHelper.addResponseFailData(resultMap, e.getMessage());
		}
		logger.info("=====> send all news end <=====");
		return resultMap;
	}
	
	@GetMapping(value = "/test")
	public void test(@RequestParam("id") int id) {
		User user = (User) request.getSession().getAttribute(User.CURRENT_USER);
		System.out.println(user.getId());
		System.out.println(user.getNickName());
	}

}
