package com.dinglian.server.chuqulang.controller;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dinglian.server.chuqulang.base.SearchCriteria;
import com.dinglian.server.chuqulang.comparator.EventUserComparator;
import com.dinglian.server.chuqulang.comparator.UserInterestComparator;
import com.dinglian.server.chuqulang.model.ChatRoom;
import com.dinglian.server.chuqulang.model.Contact;
import com.dinglian.server.chuqulang.model.Event;
import com.dinglian.server.chuqulang.model.EventPicture;
import com.dinglian.server.chuqulang.model.EventTag;
import com.dinglian.server.chuqulang.model.EventUser;
import com.dinglian.server.chuqulang.model.Tag;
import com.dinglian.server.chuqulang.model.TypeName;
import com.dinglian.server.chuqulang.model.User;
import com.dinglian.server.chuqulang.model.UserAttention;
import com.dinglian.server.chuqulang.model.UserInterest;
import com.dinglian.server.chuqulang.model.VerifyNo;
import com.dinglian.server.chuqulang.service.ActivityService;
import com.dinglian.server.chuqulang.service.UserService;
import com.dinglian.server.chuqulang.shiro.realms.CustomizedToken;
import com.dinglian.server.chuqulang.utils.AddressUtils;
import com.dinglian.server.chuqulang.utils.CodeUtils;
import com.dinglian.server.chuqulang.utils.DateUtils;
import com.dinglian.server.chuqulang.utils.EncryptHelper;
import com.dinglian.server.chuqulang.utils.RequestHelper;
import com.dinglian.server.chuqulang.utils.ResponseHelper;

@Controller
@RequestMapping("/user")
public class UserController {
	
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	private UserService userService;

	@Autowired
	private ActivityService activityService;

	@Autowired
	private HttpServletRequest request;

	@ResponseBody
	@RequestMapping(value = "/sendCode", method = RequestMethod.GET)
	public Map<String, Object> sendCode(@RequestParam(name = "phoneno") String phoneNo, @RequestParam(name = "dataType") String dataType) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		logger.info("=====> Start to send verify no <=====");
		
		// 添加手机号格式验证，等前台格式
		
		try {
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
				ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_FAIL, "同一号码一小时内只可以获取3次验证码，请稍后再试");
				return resultMap;
			}
			
			// 检查旧验证码是否存在，是否过期
			VerifyNo existVerifyNo = userService.getVerifyNo(phoneNo, dataType);
			if (existVerifyNo != null) {
				// 如果未过期，发送旧验证码
				Date createTime = existVerifyNo.getCreationTime();
				long difference = currentTime.getTime() - createTime.getTime();
				if (difference <= (60 * 1000)) {
					// 间隔一分钟
					ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_FAIL, "请" + (60 - Math.round(difference * 0.001)) + "秒后再重新获取");
					return resultMap;
				}
				
				// 10分钟有效期
				if (difference <= (10 * 60 * 1000)) {
					// 未过期,发送旧验证码
					sendhRes = CodeUtils.sendSms(existVerifyNo, prop);
				} else {
					// 如果过期，发送新验证码，删除旧验证码
					existVerifyNo.setValid(false);
					userService.saveVerifyNo(existVerifyNo);
					
					VerifyNo newVerifyNo = new VerifyNo(phoneNo, verifyNo, dataType, currentTime);
					userService.saveVerifyNo(newVerifyNo);
					
					// 发送短信
					sendhRes = CodeUtils.sendSms(newVerifyNo, prop);
				}
			} else {
				// 旧验证码不存在，发送新验证码
				VerifyNo newVerifyNo = new VerifyNo(phoneNo, verifyNo, dataType, currentTime);
				userService.saveVerifyNo(newVerifyNo);
				
				// 发送短信
				sendhRes = CodeUtils.sendSms(newVerifyNo, prop);
			}
			logger.info(sendhRes);
			ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_OK, "", null);
			
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_FAIL, e.getMessage());
		}
		
		logger.info("=====> Send verify no end <=====");
		return resultMap;
	}
	
	/**
	 * 注册用户
	 * @param userName
	 * @param password
	 * @param phoneNo
	 * @param verifyNo
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public Map<String, Object> register(@RequestParam(name = "password") String password, @RequestParam(name = "phoneno") String phoneNo, 
			@RequestParam(name = "verifyno") String verifyNo) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		if (StringUtils.isBlank(phoneNo) || StringUtils.isBlank(password) || StringUtils.isBlank(verifyNo)) {
			ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_FAIL, "参数不能为空，请重新输入。");
			return resultMap;
		}
		
		try {
			logger.info("=====> Start to check phoneno duplicate <=====");
			// 检查手机号码是否注册
			SearchCriteria searchCriteria = new SearchCriteria();
			searchCriteria.setPhoneNo(phoneNo);
			User existUser = userService.getUser(searchCriteria);
			if (existUser != null) {
				ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_FAIL, "该手机号已经注册。");
				return resultMap;
			}
			
			logger.info("=====> Start to register user <=====");
			// 检查验证码是否过期，是否正确
			VerifyNo existVerifyNo = userService.getVerifyNo(phoneNo, VerifyNo.VERIFY_NO_TYPE_REGISTER);
			if (existVerifyNo != null) {
				Date currentTime = new Date();
				Date createTime = existVerifyNo.getCreationTime();
				long difference = currentTime.getTime() - createTime.getTime();
				// 10分钟有效期
				if (difference <= (10 * 60 * 1000) && verifyNo.equalsIgnoreCase(existVerifyNo.getVerifyNo())) {
					User user = new User(phoneNo, EncryptHelper.encryptByMD5(phoneNo, password));
					// 用户类型
//					user.setTypeName("admin");
					userService.register(user);
//					注册成功，删除旧验证码
					existVerifyNo.setValid(false);
					userService.saveVerifyNo(existVerifyNo);
					
					ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_OK, "");
				} else {
					ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_FAIL, "验证码无效，请重新获取。");
					return resultMap;
				}
			} else {
				ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_FAIL, "验证码无效，请重新获取。");
				return resultMap;
			}
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_FAIL, e.getMessage());
		}
		logger.info("=====> User register end <=====");
		return resultMap;
	}
	
	@ResponseBody
	@RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
	public Map<String, Object> resetPassword(@RequestParam(name = "phoneno") String phoneNo, 
			@RequestParam(name = "verifyno") String verifyNo, @RequestParam(name = "newPassword") String newPassword) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		try {
			logger.info("=====> Start to reset password <=====");
			
			SearchCriteria searchCriteria = new SearchCriteria();
			searchCriteria.setPhoneNo(phoneNo);
			User user = userService.getUser(searchCriteria);
			if (user == null) {
				ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_FAIL, "用户不存在");
				return resultMap;
			}
			
			VerifyNo existVerifyNo = userService.getVerifyNo(phoneNo, VerifyNo.VERIFY_NO_TYPE_FORGOT);
			if (existVerifyNo != null) {
				if (existVerifyNo.getVerifyNo().equalsIgnoreCase(verifyNo)) {
					user.setPassword(EncryptHelper.encryptByMD5(phoneNo, newPassword));
					userService.saveOrUpdateUser(user);
					ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_OK, "", null);
				}
			} else {
				ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_FAIL, "验证码无效，请重新获取。");
				return resultMap;
			}
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_FAIL, e.getMessage());
		}
		logger.info("=====> Reset password end <=====");
		return resultMap;
	}
	
	/**
	 * 用户登录
	 * @param userName
	 * @param password
	 * @param phoneNo
	 * @param verifyNo
	 * @param type
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public Map<String, Object> login(@RequestParam("phoneno") String phoneNo, 
			@RequestParam(name = "password", required = false) String password, 
			@RequestParam(name = "verifyno", required = false) String verifyNo, 
			@RequestParam(name = "type") String type) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		logger.info("=====> Start to user login <=====");
		
		User user = null;
		Subject currentUser = SecurityUtils.getSubject();
		
		if (!currentUser.isAuthenticated()) {
			CustomizedToken token = null;
			String errorMsg = "";
			if (type.equalsIgnoreCase(User.LOGIN_TYPE_USERNAME)) {
				if (StringUtils.isBlank(phoneNo) || StringUtils.isBlank(password)) {
					ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_FAIL, "手机号码或密码不能为空");
					return resultMap;
				}
				
				token = new CustomizedToken(phoneNo, password, User.REALM_USERNAME);
				errorMsg = "用户名或密码错误";
			} else if (type.equalsIgnoreCase(User.LOGIN_TYPE_CHECKCODE)){
				if (StringUtils.isBlank(phoneNo) || StringUtils.isBlank(verifyNo)) {
					ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_FAIL, "手机号码或验证码不能为空");
					return resultMap;
				}
				
				token = new CustomizedToken(phoneNo, verifyNo, User.REALM_CHECKCODE);
				errorMsg = "验证码错误";
			}
			
			token.setRememberMe(true);
			try{
				currentUser.login(token);
				user = (User) currentUser.getSession().getAttribute(User.CURRENT_USER);
				logger.info("用户ID：" + user.getId() + "，用户昵称：" +  user.getNickName() + "，登录成功.");
				ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_OK, "登录成功");
			} catch (IncorrectCredentialsException e) {
				ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_FAIL, "用户名或密码错误");
				return resultMap;
			} catch (UnknownAccountException e) {
				ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_FAIL, "用户不存在!");
				return resultMap;
			} catch (AuthenticationException e) {
				ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_FAIL, errorMsg);
				return resultMap;
			}
		} else {
			user = (User) currentUser.getSession().getAttribute(User.CURRENT_USER);
			ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_FAIL, "请勿重复登录。");
			return resultMap;
		}
		
		if (user != null) {
			// IP不同提示登录地点
			String ip = request.getRemoteAddr();
			String address = "";
			try {
				address = AddressUtils.getAddresses(ip);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			String content = "";
			if (!ip.equalsIgnoreCase(user.getLastLoginIp()) && StringUtils.isNotBlank(user.getLastLoginCity())) {
				content = "最近一次登录地点：%s ,登录时间：%s ,IP地址：%s ";
				content = String.format(content, user.getLastLoginCity(), DateUtils.format(user.getLastLoginDate(), DateUtils.yMdHms), user.getLastLoginIp());
			}
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("userid", user.getId());
			result.put("info", content);
			resultMap.put("result", result);
			
			// 保存当前登录地点
			user.setLastLoginCity(address);
			user.setLastLoginIp(ip);
			user.setLastLoginDate(new Date());
			userService.saveOrUpdateUser(user);
		}
		logger.info("=====> User login end <=====");
		return resultMap;
	}
	
	
	/**
	 * 获取我的页面的用户信息
	 * @param userId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/getUser", method = RequestMethod.GET)
	public Map<String, Object> getUser() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			logger.info("=====> Start to get user information <=====");
			Subject currentUser = SecurityUtils.getSubject();
			User user = (User) currentUser.getSession().getAttribute(User.CURRENT_USER);
			if (user != null) {
				user = userService.findUserById(user.getId());
				
				Map<String, Object> result = new HashMap<String, Object>();
				
				result.put("id", user.getId());
				result.put("phoneno", user.getPhoneNo());
				result.put("nickname", user.getNickName());
				result.put("picture", user.getPicture());
				result.put("signLog", user.getSignLog());
				result.put("lastLoginIp", user.getLastLoginIp());
				result.put("lastLoginCity", user.getLastLoginCity());
				result.put("lastLoginDate", user.getLastLoginDate());
				result.put("lastLoginPhone", user.getLastLoginPhone());
				result.put("typename", user.getTypeName());
				resultMap.put("result", result);
			}
			ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_OK, "");
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_FAIL, e.getMessage());
		}
		logger.info("=====> Get user information end <=====");
		return resultMap;
	}
	
	/**
	 * 修改用户签名
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/changeSignLog", method = RequestMethod.POST)
	public Map<String, Object> changeSignLog(@RequestParam(name = "signLog") String signLog) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			logger.info("=====> Start to change signlog <=====");
			Subject currentUser = SecurityUtils.getSubject();
			User user = (User) currentUser.getSession().getAttribute(User.CURRENT_USER);
			if (user != null) {
				user = userService.findUserById(user.getId());
				user.setSignLog(signLog);
				userService.saveOrUpdateUser(user);
			}
			ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_OK, "", null);
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_FAIL, e.getMessage());
		}
		logger.info("=====> Change signlog end <=====");
		return resultMap;
	}
	
	/**
	 * 修改用户头像
	 * @param userId
	 * @param url
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/updatePicture", method = RequestMethod.POST)
	public Map<String, Object> updatePicture(@RequestParam(name = "url") String url) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			logger.info("=====> Start to change user picture <=====");
			Subject currentUser = SecurityUtils.getSubject();
			User user = (User) currentUser.getSession().getAttribute(User.CURRENT_USER);
			if (user != null) {
				user = userService.findUserById(user.getId());
				user.setPicture(url);
				// 图片文件需要保存到本地
				userService.saveOrUpdateUser(user);
			}
			ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_OK, "", null);
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_FAIL, e.getMessage());
		}
		logger.info("=====> Change user picture end <=====");
		return resultMap;
	}
	
	/**
	 * 修改密码
	 * @param userId
	 * @param newPassword
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/changePassword", method = RequestMethod.POST)
	public Map<String, Object> changePassword(@RequestParam(name = "newPassword") String newPassword) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			logger.info("=====> Start to change user password <=====");
			Subject currentUser = SecurityUtils.getSubject();
			User user = (User) currentUser.getSession().getAttribute(User.CURRENT_USER);
			if (user != null) {
				user = userService.findUserById(user.getId());
				// 密码MD5加密
				user.setPassword(EncryptHelper.encryptByMD5(user.getPhoneNo(), newPassword));
				userService.saveOrUpdateUser(user);
			}
			ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_OK, "", null);
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_FAIL, e.getMessage());
		}
		logger.info("=====> Change user password end <=====");
		return resultMap;
	}

	/**
	 * 获取活动列表
	 * @param userId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/getActivityList", method = RequestMethod.POST)
	public Map<String, Object> getActivityList() {
		Map<String, Object> responseMap = new HashMap<String, Object>();
		try {
			Subject currentUser = SecurityUtils.getSubject();
			User user = (User) currentUser.getSession().getAttribute(User.CURRENT_USER);
			List<Map> resultList = new ArrayList<Map>();
			if (user != null) {
				user = userService.findUserById(user.getId());
				Set<Event> events = user.getEventSet();
				for (Event event : events) {
					Map<String, Object> result = new HashMap<String, Object>();
					
					EventPicture cover = event.getCover();
					result.put("picture", cover != null ? cover.getUrl() : "");
					result.put("shortname", event.getShortName());
					result.put("rstime", event.getRsTime());
					result.put("publishtime", event.getStartTime());
					result.put("status", event.getNowStatus());
					result.put("charge", event.getCharge());
					result.put("gps", event.getGps());
					
					TypeName typeName = event.getTypeName();
					result.put("typename", typeName != null ? typeName.getName() : "");
					
					List<Map> tagList = new ArrayList<>();
					Set<EventTag> eventTags = event.getTags();
					for (EventTag eventTag : eventTags) {
						Tag tag = eventTag.getTag();
						Map<String, Object> tagsMap = new HashMap<String, Object>();
						tagsMap.put("tagid", tag.getId());
						tagsMap.put("tagname", tag.getName());
						tagList.add(tagsMap);
					}
					result.put("tag", tagList);
					
					Map<String, Object> numbersMap = new HashMap<String, Object>();
					numbersMap.put("num", event.getUserCount());
					numbersMap.put("enteringNum", event.getEventUsers().size());
					result.put("numbers", numbersMap);
					
					resultList.add(result);
				}
			}
			
			responseMap.put("result", resultList);
			ResponseHelper.addResponseData(responseMap, RequestHelper.RESPONSE_STATUS_OK, "");
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.addResponseData(responseMap, RequestHelper.RESPONSE_STATUS_FAIL, e.getMessage());
		}

		return responseMap;
	}
	
	/**
	 * 获取活动详情
	 * @param eventId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/getActivityInfo", method = RequestMethod.POST)
	public Map<String, Object> getActivityInfo(@RequestParam(name = "eventId") String eventId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			int id = Integer.parseInt(eventId);
			Event event = activityService.findEventById(id);
			Map<String, Object> result = new HashMap<String, Object>();
			
			EventPicture cover = event.getCover();
			result.put("picture", cover != null ? cover.getUrl() : "");
			result.put("shortname", event.getShortName());
			
			ChatRoom chatRoom = event.getChatRoom();
			result.put("chatroom_id", chatRoom != null ? chatRoom.getId() : "");
			result.put("rstime", event.getRsTime());
			result.put("publishtime", event.getStartTime());
			result.put("status", event.getNowStatus());
			
			TypeName typeName = event.getTypeName();
			result.put("typename", typeName!= null ? typeName.getName() : "");
			
			List<Map> tagList = new ArrayList<>();
			Set<EventTag> eventTags = event.getTags();
			for (EventTag eventTag : eventTags) {
				Tag tag = eventTag.getTag();
				if (tag != null) {
					Map<String, Object> tagsMap = new HashMap<String, Object>();
					tagsMap.put("tagid", tag.getId());
					tagsMap.put("tagname", tag.getName());
					tagList.add(tagsMap);
				}
			}
			result.put("tag", tagList);
			
			// 参与活动人员
			List<EventUser> eventUsers = new ArrayList<EventUser>(event.getEventUsers());
			Collections.sort(eventUsers, new EventUserComparator());
			
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
			result.put("eventUserList", eventUserList);
			
			Map<String, Object> numbersMap = new HashMap<String, Object>();
			numbersMap.put("num", event.getUserCount());
			numbersMap.put("enteringNum", eventUsers.size());
			result.put("numbers", numbersMap);
			
			result.put("charge", event.getCharge());
			result.put("gps", event.getGps());
			result.put("description", event.getDescription());
			
			User organizer = event.getCreator();
			if (organizer != null) {
				Map<String, Object> organizerMap = new HashMap<String, Object>();
				organizerMap.put("organizerId", organizer.getId());
				organizerMap.put("organizerNickName", organizer.getNickName());
				organizerMap.put("organizerPicture", organizer.getPicture());
				result.put("organizer", organizerMap);
			}
			
			resultMap.put("result", result);
			ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_OK, "");
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_FAIL, e.getMessage());
		}

		return resultMap;
	}
	
	/*获取联系人列表*/
	@ResponseBody
	@RequestMapping(value = "/getContacts", method = RequestMethod.POST)
	public Map<String, Object> getContacts() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			Subject currentUser = SecurityUtils.getSubject();
			User user = (User) currentUser.getSession().getAttribute(User.CURRENT_USER);
			List<Map> resultList = new ArrayList<Map>();
			if (user != null) {
				user = userService.findUserById(user.getId());
//				List<Contact> contacts = new ArrayList<>(user.getContacts());
				Map<String, Object> map = null;
				for (Contact contact : user.getContacts()) {
					if (contact.getContactUser() != null) {
						map = new HashMap<String, Object>();
						map.put("contactId", contact.getContactUser().getId());
						map.put("contactName", contact.getContactUser().getNickName());
						map.put("contactPicture", contact.getContactUser().getPicture());
						map.put("contactDegree", contact.getDegree());
						map.put("contactDescription", contact.getDescription());
						resultList.add(map);
					}
				}
			}
			ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_OK, "", resultList);
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_FAIL, e.getMessage());
		}
		return resultMap;
	}
	
	/*获取用户兴趣*/
	@ResponseBody
	@RequestMapping(value = "/getTags", method = RequestMethod.POST)
	public Map<String, Object> getUserInterest() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			Subject currentUser = SecurityUtils.getSubject();
			User user = (User) currentUser.getSession().getAttribute(User.CURRENT_USER);
			
			List<Map> resultList = new ArrayList<Map>();
			if (user != null) {
				user = userService.findUserById(user.getId());
				
				List<UserInterest> interests = new ArrayList<UserInterest>(user.getInterests());
				Collections.sort(interests, new UserInterestComparator());
				
				Map<String, Object> map = null;
				for (UserInterest interest : interests) {
					map = new HashMap<String, Object>();
					Tag tag = interest.getTag();
					if (tag != null) {
						map.put("id", tag.getId());
						map.put("name", tag.getName());
						map.put("typename", tag.getTypeName() != null ? tag.getTypeName().getName() : "");
					}
					resultList.add(map);
				}
			}
			ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_OK, "", resultList);
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_FAIL, e.getMessage());
		}
		return resultMap;
	}
	
	/*获取用户关注*/
	@ResponseBody
	@RequestMapping(value = "/getUserAttention", method = RequestMethod.POST)
	public Map<String, Object> getUserAttention() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			Subject currentUser = SecurityUtils.getSubject();
			User user = (User) currentUser.getSession().getAttribute(User.CURRENT_USER);
			List<Map> resultList = new ArrayList<Map>();
			if (user != null) {
				user = userService.findUserById(user.getId());
				
				Map<String, Object> map = null;
				Set<UserAttention> attentions = user.getAttentions();
				for (UserAttention userAttention : attentions) {
					if (userAttention.getAttentionUser() != null) {
						map = new HashMap<String, Object>();
						map.put("attentionUserId", userAttention.getAttentionUser().getId());
						map.put("attentionUserName", userAttention.getAttentionUser().getNickName());
						map.put("attentionUserPicture", userAttention.getAttentionUser().getPicture());
						resultList.add(map);
					}
				}
			}
			ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_OK, "", resultList);
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_FAIL, e.getMessage());
		}
		return resultMap;
	}
	
	/*获取用户粉丝*/
	@ResponseBody
	@RequestMapping(value = "/getUserFollow", method = RequestMethod.POST)
	public Map<String, Object> getUserFollow() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			Subject currentUser = SecurityUtils.getSubject();
			User user = (User) currentUser.getSession().getAttribute(User.CURRENT_USER);
			List<Map> resultList = new ArrayList<Map>();
			if (user != null) {
				user = userService.findUserById(user.getId());
				
				Map<String, Object> map = null;
				Set<UserAttention> followers = user.getFollowers();
				for (UserAttention userAttention : followers) {
					if (userAttention.getUser() != null) {
						map = new HashMap<String, Object>();
						map.put("followUserId", userAttention.getUser().getId());
						map.put("followUserName", userAttention.getUser().getNickName());
						map.put("followUserPicture", userAttention.getUser().getPicture());
						resultList.add(map);
					}
				}
			}
			ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_OK, "", resultList);
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_FAIL, e.getMessage());
		}
		return resultMap;
	}
	
	
	@ResponseBody
	@RequestMapping(value = "/test")
	public Map<String, Object> test() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		return resultMap;
	}

}
