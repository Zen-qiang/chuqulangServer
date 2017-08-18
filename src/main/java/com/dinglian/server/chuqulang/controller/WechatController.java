package com.dinglian.server.chuqulang.controller;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

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
import com.dinglian.server.chuqulang.model.CoterieGuy;
import com.dinglian.server.chuqulang.model.CoteriePicture;
import com.dinglian.server.chuqulang.model.CoterieTag;
import com.dinglian.server.chuqulang.model.Event;
import com.dinglian.server.chuqulang.model.EventPicture;
import com.dinglian.server.chuqulang.model.EventTag;
import com.dinglian.server.chuqulang.model.EventUser;
import com.dinglian.server.chuqulang.model.Tag;
import com.dinglian.server.chuqulang.model.Topic;
import com.dinglian.server.chuqulang.model.TopicComment;
import com.dinglian.server.chuqulang.model.TopicPraise;
import com.dinglian.server.chuqulang.model.User;
import com.dinglian.server.chuqulang.model.VerifyNo;
import com.dinglian.server.chuqulang.model.WxOAuth2AccessToken;
import com.dinglian.server.chuqulang.service.ActivityService;
import com.dinglian.server.chuqulang.service.DiscoverService;
import com.dinglian.server.chuqulang.service.UserService;
import com.dinglian.server.chuqulang.service.WxMpService;
import com.dinglian.server.chuqulang.utils.FileUploadHelper;
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
	private DiscoverService discoverService;

	@Autowired
	private ActivityService activityService;

	@Autowired
	private WxMpService wxMpService;
	
	@RequestMapping("/authorization")
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
	@RequestMapping(value = "/userAuthorization", method = RequestMethod.GET)
	public ModelAndView userAuthorization(@RequestParam("callbackUrl") String callbackUrl) {
		logger.info("=====> Start to user authorization <=====");
		String url = "";
		try {
			ApplicationConfig config = ApplicationConfig.getInstance();
			String redirectUrl = URLEncoder.encode(config.getWxMpAuthorizeRedirectUrl() + "?redirectUrl=" + callbackUrl);
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
	@RequestMapping(value = "/getAccessToken", method = RequestMethod.GET)
	public Map<String, Object> getAccessToken(@RequestParam("code") String code, @RequestParam("state") String state) {
		logger.info("=====> Start to get OAuth2 access token <=====");
		Map<String, Object> responseMap = new HashMap<String, Object>();
		Map<String, Object> dataMap = new HashMap<String, Object>();
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
				dataMap.put("accessToken", accessToken);
				dataMap.put("refreshToken", refreshToken);
				dataMap.put("scope", scope);
				
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
	
	/**
	 * 获取用户信息，自动注册
	 * @param openId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/getUser", method = RequestMethod.GET)
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
						UUID uuid = UUID.randomUUID();
						String folder = uuid.toString().replaceAll("-", "");
						user.setPicture(FileUploadHelper.saveNetProfilePicture(folder, headimgurl));
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

	/**
	 * 绑定手机号
	 * @param userId
	 * @param phoneNo
	 * @param verifyNo
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/bindPhoneNo", method = RequestMethod.GET)
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
	/*@ResponseBody
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

	/**
	 * 创建圈子
	 * 
	 * @param userId
	 * @param name
	 * @param typeNameId
	 * @param tags
	 * @param description
	 * @param picture
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/createCoterie", method = RequestMethod.POST)
	public Map<String, Object> createCoterie(@RequestParam("userId") int userId, @RequestParam("name") String name,
			@RequestParam("tags") String tags, @RequestParam(name = "description", required = false) String description,
			@RequestParam(name = "picture", required = false) String picture) {
		logger.info("=====> Start to create coterie <=====");
		Map<String, Object> responseMap = new HashMap<String, Object>();
		try {
			User user = userService.findUserById(userId);
			if (user == null) {
				throw new UserException(UserException.NOT_EXISTING);
			}

			Coterie coterie = new Coterie();
			coterie.setName(name);
			coterie.setDescription(description);
			coterie.setCreationDate(new Date());
			coterie.setCreator(user);
			coterie.setHot(0);
			
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

			if (StringUtils.isNotBlank(picture) && coterie.getId() != 0) {
				String coverPath = ApplicationConfig.getInstance().getCoterieCoverPath();
				if (picture.indexOf(",") > 0) {
					String coverFolderPath = String.format(coverPath, coterie.getId());
					String picPath = FileUploadHelper.uploadPicture(coverFolderPath, picture, "cover.jpg");

					CoteriePicture coteriePicture = new CoteriePicture();
					coteriePicture.setCoterie(coterie);
					coteriePicture.setCreationDate(new Date());
					coteriePicture.setUser(user);
					coteriePicture.setUrl(picPath);
					coterie.setCoteriePicture(coteriePicture);
				}
				discoverService.saveCoterie(coterie);
			}
			ResponseHelper.addResponseSuccessData(responseMap, null);
			logger.info("=====> Create coterie end <=====");
		} catch (UserException e) {
			ResponseHelper.addResponseFailData(responseMap, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.addResponseFailData(responseMap, e.getMessage());
		}
		return responseMap;
	}
	
	@ResponseBody
	@RequestMapping(value = "/editCoterie", method = RequestMethod.POST)
	public Map<String, Object> editCoterie(@RequestParam("userId") int userId, 
			@RequestParam("coterieId") int coterieId,
			@RequestParam(name = "name", required = false) String name, 
			@RequestParam(name = "tags", required = false) String tags, 
			@RequestParam(name = "description", required = false) String description,
			@RequestParam(name = "picture", required = false) String picture) {
		logger.info("=====> Start to edit coterie <=====");
		Map<String, Object> responseMap = new HashMap<String, Object>();
		try {
			Coterie coterie = discoverService.findCoterieById(coterieId);
			if (coterie == null) {
				throw new ApplicationServiceException(ApplicationServiceException.COTERIE_NOT_EXIST);
			}

			if (coterie.getCreator() != null && coterie.getCreator().getId() != userId) {
				throw new ApplicationServiceException(ApplicationServiceException.COTERIE_NOT_CREATOR);
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
			
			if (StringUtils.isNotBlank(picture) && picture.indexOf(",") > 0) {
				String coverPath = ApplicationConfig.getInstance().getCoterieCoverPath();
				String coverFolderPath = String.format(coverPath, coterie.getId());
				String picPath = FileUploadHelper.uploadPicture(coverFolderPath, picture, "cover.jpg");
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
	
	@ResponseBody
	@RequestMapping(value = "/getCoterieCarouselPictures", method = RequestMethod.GET)
	public Map<String, Object> getCoterieCarouselPictures() {
		logger.info("=====> Start to get coterie carousel pictures <=====");
		Map<String, Object> responseMap = new HashMap<String, Object>();
		try {
			
			List<String> urls = discoverService.getCoterieCarouselPictures();
			ResponseHelper.addResponseSuccessData(responseMap, urls);
			logger.info("=====> Get coterie carousel pictures end <=====");
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.addResponseFailData(responseMap, e.getMessage());
		}
		return responseMap;
	}

	/**
	 * 加入/退出圈子
	 * 
	 * @param userId
	 * @param coterieId
	 * @param isJoin
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/joinCoterie", method = RequestMethod.POST)
	public Map<String, Object> joinCoterie(@RequestParam("userId") int userId, @RequestParam("coterieId") int coterieId,
			@RequestParam("isJoin") boolean isJoin) {
		logger.info("=====> Start to join coterie <=====");
		Map<String, Object> responseMap = new HashMap<String, Object>();
		try {
			User user = userService.findUserById(userId);
			if (user == null) {
				throw new UserException(UserException.NOT_EXISTING);
			}

			Coterie coterie = discoverService.findCoterieById(coterieId);
			if (coterie == null) {
				throw new NullPointerException("圈子ID：" + coterie + " 不存在");
			}

			if (isJoin) {
				int nextOrderNo = coterie.getCoterieGuyNextOrderNo();
				CoterieGuy coterieGuy = new CoterieGuy(coterie, nextOrderNo, user, new Date(), false, true);
				discoverService.saveCoterieGuy(coterieGuy);
			} else {
				discoverService.exitCoterie(coterieId, user.getId());
			}
			ResponseHelper.addResponseSuccessData(responseMap, null);
			logger.info("=====> Join coterie end <=====");
		} catch (UserException e) {
			ResponseHelper.addResponseFailData(responseMap, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.addResponseFailData(responseMap, e.getMessage());
		}
		return responseMap;
	}

	/**
	 * 获取圈子列表
	 * 
	 * @param tags
	 * @param pageSize
	 * @param startRow
	 * @param orderBy
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/getCoterieList", method = RequestMethod.GET)
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
				String[] tag2Ids = secondLevelTagIds.split(",");
				for (String tagId : tag2Ids) {
					tags.add(Integer.parseInt(tagId));
				}
			} else if (firstLevelTagId != null) {
				tags.add(firstLevelTagId);
			}
			
			SearchCriteria searchCriteria = new SearchCriteria();
			if (tags != null) {
				searchCriteria.setTags(tags);
			}
			searchCriteria.setStartRow(startRow);
			searchCriteria.setPageSize(pageSize);
			// 默认最热排序
			searchCriteria.setOrderBy(Coterie.TYPE_HOT);
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
	
	/**
	 * 获取圈子详情
	 * @param coterieId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/getCoterieInfo", method = RequestMethod.GET)
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
			data.put("membersCnt", coterie.getCoterieGuys().size());
			data.put("isJoined", coterie.isJoined(userId));
			data.put("isCreator", coterie.isCreator(userId));
			
			List<String> tagList = new ArrayList<>();
			for (CoterieTag coterieTag : coterie.getTags()) {
				Tag tag = coterieTag.getTag();
				tagList.add(tag.getName());
			}
			// 标签
			data.put("tags", tagList);
			
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
	
	/**
	 * 获取话题列表
	 * @param coterieId
	 * @param pageSize
	 * @param startRow
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/getTopicList", method = RequestMethod.GET)
	public Map<String, Object> getTopicList(@RequestParam("coterieId") int coterieId,
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
//					map.put("description", topic.getDescription());
//					map.put("topicCreateTime", topic.getCreationDate());
					map.put("commentCnt", topic.getComments().size());
					map.put("praiseCnt", topic.getPraises().size());
					
					// 用户相关
					/*User topicUser = topic.getCreator();
					if (topicUser != null) {
						Map<String, Object> userMap = new HashMap<String, Object>();
						userMap.put("userId", topicUser.getId());
						userMap.put("nickName", topicUser.getNickName());
						userMap.put("picture", topicUser.getPicture());
						map.put("user", userMap);
					}*/
					
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

						List<String> tagList = new ArrayList<>();
						for (EventTag eventTag : event.getTags()) {
							Tag tag = eventTag.getTag();
							tagList.add(tag.getName());
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

	/**
	 * 搜索圈子/话题
	 * 
	 * @param keyword
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/searchActivityOrTopic", method = RequestMethod.GET)
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

	/**
	 * 我的圈子
	 * 
	 * @param dataType
	 *            1：我创建的 2：我参与的
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/getMyCoteries", method = RequestMethod.GET)
	public Map<String, Object> getMyCoteries(@RequestParam("userId") int userId,
			@RequestParam(name = "dataType", required = false) String dataType,
			@RequestParam(name = "showLastCoterie", required = false) Boolean showLastCoterie) {
		Map<String, Object> responseMap = new HashMap<String, Object>();
		try {
			logger.info("=====> Start to get my coteries <=====");

			List<Coterie> coteries = discoverService.getMyCoteries(dataType, userId);
			
			// 显示最近参与
			Coterie lastCoterie = null;
			if (showLastCoterie != null && showLastCoterie) {
				lastCoterie = discoverService.getLastCoterie(userId);
			}
			
			List<Map> resultList = new ArrayList<Map>();
			if (coteries != null) {
				for (Coterie coterie : coteries) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("id", coterie.getId());
					map.put("name", coterie.getName());
					map.put("cover", coterie.getCoteriePicture() != null ? coterie.getCoteriePicture().getUrl() : "");
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
	
	 /**
     * 获取活动类型（一级标签）
     * @return
     */
    @ResponseBody
	@RequestMapping(value = "/getActivityType")
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
    
	/**
     * 获取标签列表
     * @param parentId
     * @return
     */
    @ResponseBody
	@RequestMapping(value = "/getTagList")
	public Map<String, Object> getTagList(@RequestParam(name = "parentId", required = false) Integer parentId) {
		Map<String, Object> responseMap = new HashMap<String, Object>();
		try{
			logger.info("=====> Start to get tag list <=====");
			
			List<Tag> secondLevelTags = activityService.getSecondLevelTags(parentId);
			List<Map> resultList = new ArrayList<Map>();
			Map<String, Object> map = null;
			if (secondLevelTags != null) {
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
    @ResponseBody
    @RequestMapping(value = "/launchActivity", method = RequestMethod.POST)
    public Map<String, Object> launchActivity(
    		@RequestParam("userId") int userId,
    		@RequestParam(name = "coterieId",required = false) Integer coterieId,
    		@RequestParam("tags") String tags,
    		@RequestParam("name") String name,
    		@RequestParam(name = "pictures", required = false) String[] pictures,
    		@RequestParam(name = "startTime",required = false) Date startTime,
    		@RequestParam("gps") String gps,
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
        	
        	User user = userService.findUserById(userId);
        	if (user == null) {
				throw new UserException(UserException.NOT_EXISTING);
			}
        	
            Event event = new Event();
            event.setAllowSignUp(true);
            event.setCharge(charge);
            
            if (coterieId != null) {
            	Coterie coterie = discoverService.findCoterieById(coterieId);
            	if (coterie == null) {
            		throw new ApplicationServiceException(ApplicationServiceException.COTERIE_NOT_EXIST);
            	}
            	event.setCoterie(coterie);
			}
            
            // 如果活动不公开，设置密码
            event.setOpen(isOpen);
            if (isOpen) {
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
            
            if (pictures != null) {
            	int i = 1;
            	String basePicturePath = ApplicationConfig.getInstance().getActivityPicturePath();
            	for (String picBase64Str : pictures) {
            		if (picBase64Str.indexOf(",") > 0) {
            			String pictureFolderPath = String.format(basePicturePath, event.getId());
            			String picPath = FileUploadHelper.uploadPicture(pictureFolderPath, picBase64Str, i + ".jpg");
            			
            			EventPicture eventPicture = new EventPicture(event, picPath, i, user);
            			event.getEventPictures().add(eventPicture);
            			i++;
					}
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
            	Coterie coterie = new Coterie();
    			coterie.setName(name);
    			coterie.setDescription(description);
    			coterie.setCreationDate(new Date());
    			coterie.setCreator(user);
    			coterie.setHot(0);
    			
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
			}
            
            activityService.saveEvent(event);
            
            Map<String, Object> result = new HashMap<String, Object>();
			result.put("activityId", event.getId());
			result.put("coterieId", event.getCoterie().getId());
			
            logger.info("=====> Launch activity end <=====");
            ResponseHelper.addResponseSuccessData(resultMap, result);
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
    @ResponseBody
    @RequestMapping(value = "/editActivity", method = RequestMethod.POST)
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
    
    /**
     * 获取活动列表，可分页，搜索
     * @param startRow
     * @param keyword
     * @param pageSize
     * @return
     */
    @ResponseBody
	@RequestMapping(value = "/getActivityList", method = RequestMethod.GET)
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
					for (EventTag eventTag : event.getTags()) {
						Tag tag = eventTag.getTag();
						tagList.add(tag.getName());
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
			
			logger.info("=====> Get my activity list end <=====");
			
			ResponseHelper.addResponseSuccessData(responseMap, resultList);
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.addResponseFailData(responseMap, e.getMessage());
		}

		return responseMap;
	}
    
    /**
     * 获取活动详情
     * @param activityId
     * @return
     */
    @ResponseBody
	@RequestMapping(value = "/getActivityInfo", method = RequestMethod.GET)
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
			result.put("startTime", event.getStartTime());
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
				
				TopicComment lastComment = comments.get(0);
				if (lastComment != null) {
					topicMap.put("lastComment", lastComment.getContent());
					topicMap.put("lastCommentTime", lastComment.getCreationDate());
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
			List<String> tagList = new ArrayList<>();
			for (EventTag eventTag : event.getTags()) {
				Tag tag = eventTag.getTag();
				if (tag != null) {
					tagList.add(tag.getName());
				}
			}
			result.put("tags", tagList);
			
			// 参与活动人员
//			List<EventUser> eventUsers = new ArrayList<EventUser>(event.getEventUsers());
//			Collections.sort(eventUsers, new EventUserComparator());
			List<EventUser> eventUsers = event.getEffectiveMembers();
			
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
    
    /**
     * 修改活动信息
     * @param activityId
     * @param minCount
     * @param maxCount
     * @param allowSignUp
     * @return
     */
    @ResponseBody
   	@RequestMapping(value = "/updateActivityInfo", method = RequestMethod.POST)
   	public Map<String, Object> updateActivityInfo(@RequestParam("activityId") int activityId, 
   			@RequestParam(name = "minCount", required = false) Integer minCount, 
   			@RequestParam(name = "maxCount", required = false) Integer maxCount,
   			@RequestParam(name = "description", required = false) String description,
   			@RequestParam(name = "allowSignUp", required = false) Boolean allowSignUp) {
   		Map<String, Object> resultMap = new HashMap<String, Object>();
   		try {
   			logger.info("=====> Start to update activity info <=====");
   			
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
    
    /**
     * 关闭活动
     * @param activityId
     * @return
     */
    @ResponseBody
   	@RequestMapping(value = "/closeActivity", method = RequestMethod.GET)
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
    
    @ResponseBody
	@RequestMapping(value = "/getMyActivityList", method = RequestMethod.GET)
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
    @ResponseBody
	@RequestMapping(value = "/createActivityTopic", method = RequestMethod.POST)
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
    
    /**
     * 1.9	获取活动留言详情
     * @param topicId
     * @return
     */
    @ResponseBody
   	@RequestMapping(value = "/getActivityTopic", method = RequestMethod.GET)
   	public Map<String, Object> getActivityTopic(@RequestParam("topicId") int topicId) {
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
   			
   			List<String> tagList = new ArrayList<>();
   			for (EventTag eventTag : event.getTags()) {
   				Tag tag = eventTag.getTag();
   				tagList.add(tag.getName());
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
    
    /**
     * 获取留言评论列表
     * @param topicId
     * @return
     */
    @ResponseBody
   	@RequestMapping(value = "/getTopicCommentList", method = RequestMethod.GET)
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
    
    @ResponseBody
   	@RequestMapping(value = "/getTopicPraiseList", method = RequestMethod.GET)
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
    
    /**
     * 话题评论
     * @param userId
     * @param topicId
     * @param content
     * @return
     */
    @ResponseBody
	@RequestMapping(value = "/commentTopic", method = RequestMethod.POST)
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
    
    /**
     * 话题点赞
     * @param userId
     * @param topicId
     * @return
     */
    @ResponseBody
	@RequestMapping(value = "/praiseTopic")
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
    @ResponseBody
    @RequestMapping(value = "/signUp", method = RequestMethod.POST)
    public Map<String, Object> signUp(@RequestParam(name = "activityId") int activityId,
    		@RequestParam("userId") int userId,
    		@RequestParam(name="realName",required = false) String realName,
    		@RequestParam(name="phoneNo",required = false) String phoneNo,
    		@RequestParam("gender") int gender,
    		@RequestParam(name = "friends", required = false) String[] friends,
    		@RequestParam(name="password",required = false) String password
    		) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
        	logger.info("=====> Start to event signup <=====");
        	//获取当前用户报名信息
        	User user = userService.findUserById(userId);
            Event event = activityService.getEventById(activityId);
            if (event == null) {
            	throw new ApplicationServiceException(ApplicationServiceException.ACTIVITY_NOT_EXIST);
			}
            
            if (!event.isOpen() && !password.equals(event.getPassword())) {
				throw new ApplicationServiceException(ApplicationServiceException.ACTIVITY_INCORRECT_CREDENTIALS);
			}
            
            if (!event.isAllowSignUp()) {
            	throw new ApplicationServiceException(ApplicationServiceException.ACTIVITY_DONT_ALLOW_SINGNUP);
			}
            
            //检查活动是否满员，满员返回错误信息，否则继续
            List<EventUser> eventUsers = event.getEffectiveMembers();
            if (eventUsers.size() == event.getMaxCount()) {
            	throw new ApplicationServiceException(ApplicationServiceException.ACTIVITY_USER_FULL);
			}
            if (friends != null && (event.getMaxCount() - eventUsers.size()) <= (friends.length + 1)) {
        		throw new ApplicationServiceException(ApplicationServiceException.ACTIVITY_SPACE_INSUFFICIENT);
			}
            //检查重复报名
            boolean haveSignUp = event.haveSignUp(user.getId());
            if (haveSignUp){
                throw new ApplicationServiceException(ApplicationServiceException.ACTIVITY_HAS_SIGNUPED);
            }
            int maxOrderNo = event.getEventUserMaxOrderNo() + 1;
            EventUser fristeventUser = new EventUser(event, user, maxOrderNo++);
            fristeventUser.setRealName(realName == null ? user.getNickName() : "");
            fristeventUser.setPhoneNo(phoneNo == null ? user.getPhoneNo() : "");
            fristeventUser.setGender(gender);
            fristeventUser.setEffective(true);
            
            event.getEventUsers().add(fristeventUser);
            if (friends != null) {
				for (String friendsStr : friends) {
					JSONObject obj = JSONObject.fromObject(friendsStr);
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
            
            // 关闭允许报名
            if (event.getEventUsers().size() == event.getMaxCount()) {
				event.setAllowSignUp(false);
			}
            
            activityService.saveEvent(event);
            activityService.refresh(event);
            
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
            
            Coterie coterie = event.getCoterie();
            if (coterie != null) {
            	Map<String, Object> coterieMap = new HashMap<String, Object>();
            	coterieMap.put("id", coterie.getId());
            	coterieMap.put("cover", coterie.getCoteriePicture() != null ? coterie.getCoteriePicture().getUrl() : "");
            	coterieMap.put("name", coterie.getName());
            	result.put("coterie", coterieMap);
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
    
    /**
     * 取消报名
     * @param activityId
     * @param userId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/signOut", method = RequestMethod.POST)
    public Map<String, Object> signOut(@RequestParam(name = "activityId") int activityId,
    		@RequestParam("userId") int userId) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
        	logger.info("=====> Start to event signout <=====");
        	
            Event event = activityService.getEventById(activityId);
            if (event == null) {
            	throw new ApplicationServiceException(ApplicationServiceException.ACTIVITY_NOT_EXIST);
			}
            
            for (EventUser eventUser : event.getEventUsers()) {
            	User eu = eventUser.getUser();
            	User inviter = eventUser.getInviter();
				if ((eu != null && eu.getId() == userId) || (inviter != null && inviter.getId() == userId)) {
					eventUser.setEffective(false);
				}
			}
            activityService.saveEvent(event);
            
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
    
    /**
     * 获取活动成员信息
     * @param activityId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getActivityMembers", method = RequestMethod.GET)
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

            ResponseHelper.addResponseSuccessData(resultMap, resultList);
            logger.info("=====> Event signup end <=====");
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

}
