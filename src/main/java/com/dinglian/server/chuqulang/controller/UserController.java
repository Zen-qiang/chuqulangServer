package com.dinglian.server.chuqulang.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.subject.Subject;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.dinglian.server.chuqulang.base.ApplicationConfig;
import com.dinglian.server.chuqulang.base.SearchCriteria;
import com.dinglian.server.chuqulang.comparator.UserInterestComparator;
import com.dinglian.server.chuqulang.model.Contact;
import com.dinglian.server.chuqulang.model.Tag;
import com.dinglian.server.chuqulang.model.User;
import com.dinglian.server.chuqulang.model.UserAttention;
import com.dinglian.server.chuqulang.model.UserInterest;
import com.dinglian.server.chuqulang.model.VerifyNo;
import com.dinglian.server.chuqulang.service.UserService;
import com.dinglian.server.chuqulang.shiro.realms.CustomizedToken;
import com.dinglian.server.chuqulang.utils.AddressUtils;
import com.dinglian.server.chuqulang.utils.CodeUtils;
import com.dinglian.server.chuqulang.utils.DateUtils;
import com.dinglian.server.chuqulang.utils.EncryptHelper;
import com.dinglian.server.chuqulang.utils.FileUploadHelper;
import com.dinglian.server.chuqulang.utils.NeteaseIMUtil;
import com.dinglian.server.chuqulang.utils.RequestHelper;
import com.dinglian.server.chuqulang.utils.ResponseHelper;

import net.sf.json.JSONObject;

@Controller
@RequestMapping("/user")
public class UserController {
	
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	private UserService userService;

	@Autowired
	private HttpServletRequest request;
	
	private static boolean isMobile(String phoneNo){
		String regex = ApplicationConfig.getInstance().getPhoneNoRegex();
		Pattern p = Pattern.compile(regex);  
		Matcher m = p.matcher(phoneNo);  
		return m.matches();
	}

	/**
	 * 发送验证码
	 * @param phoneNo	手机号
	 * @param dataType	验证码类型
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/sendCode", method = RequestMethod.GET)
	public Map<String, Object> sendCode(@RequestParam(name = "phoneno") String phoneNo, @RequestParam(name = "dataType") String dataType) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		logger.info("=====> Start to send verify no <=====");
		
		try {
			// 添加手机号格式验证
			if (!isMobile(phoneNo)) {
				throw new RuntimeException("请输入正确的手机号码");
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
	 * @param password	密码
	 * @param phoneNo	手机号码
	 * @param verifyNo	验证码
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
			
			// 添加手机号格式验证
			if (!isMobile(phoneNo)) {
				throw new RuntimeException("请输入正确的手机号码");
			}
						
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
					
					ApplicationConfig config = ApplicationConfig.getInstance();
					String profilePicture = config.getUserProfilePicturePath();
					
					// 注册云信ID
					String response = NeteaseIMUtil.getInstance().create(user.getPhoneNo(), user.getPhoneNo(), "", String.format(profilePicture, user.getPhoneNo()), "");
					JSONObject responseObj = JSONObject.fromObject(response);
					if (responseObj.getInt("code") == 200) {
						JSONObject info = responseObj.getJSONObject("info");
						user.setAccid(info.getString("accid"));
						user.setToken(info.getString("token"));
						userService.saveOrUpdateUser(user);
						ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_OK, "");
					} else {
						Map<String, Object> result = new HashMap<String, Object>();
						result.put("code", responseObj.getInt("code"));
						result.put("desc", responseObj.getInt("desc"));
						ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_OK, "",result);
					}
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
	
	/**
	 * 重置密码
	 * @param phoneNo		手机号码
	 * @param verifyNo		验证码
	 * @param newPassword	新密码
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
	public Map<String, Object> resetPassword(@RequestParam(name = "phoneno") String phoneNo, 
			@RequestParam(name = "verifyno") String verifyNo, @RequestParam(name = "newPassword") String newPassword) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		try {
			logger.info("=====> Start to reset password <=====");
			if (!isMobile(phoneNo)) {
				throw new RuntimeException("请输入正确的手机号码");
			}
						
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
	 * @param phoneNo	手机号码
	 * @param password	密码
	 * @param verifyNo	验证码
	 * @param type		登录方式
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
		
		Subject currentUser = SecurityUtils.getSubject();
		
//		if (!currentUser.isAuthenticated()) {
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
				User user = (User) currentUser.getSession().getAttribute(User.CURRENT_USER);
				logger.info("用户ID：" + user.getId() + "，用户昵称：" +  user.getNickName() + "，登录成功.");
				
				// IP不同提示登录地点
				String ip = request.getRemoteAddr();
				String address = AddressUtils.getLoginAddresses(ip);
				String content = "";
				if (!ip.equalsIgnoreCase(user.getLastLoginIp()) && StringUtils.isNotBlank(user.getLastLoginCity())) {
					content = "最近一次登录地点：%s ,登录时间：%s ,IP地址：%s ";
					content = String.format(content, user.getLastLoginCity(), DateUtils.format(user.getLastLoginDate(), DateUtils.yMdHms), user.getLastLoginIp());
				}
				Map<String, Object> result = new HashMap<String, Object>();
				result.put("userid", user.getId());
				result.put("info", content);
				
				// 保存当前登录地点
				user.setLastLoginCity(address);
				user.setLastLoginIp(ip);
				user.setLastLoginDate(new Date());
				userService.saveOrUpdateUser(user);
				
				ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_OK, "登录成功", result);
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
		/*} else {
			ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_FAIL, "请勿重复登录。");
			return resultMap;
		}*/
		
		logger.info("=====> User login end <=====");
		return resultMap;
	}
	
	
	/**
	 * 获取我的页面的用户信息
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/getUser", method = RequestMethod.GET)
	public Map<String, Object> getUser(@RequestParam(name = "userId", required = false) Integer userId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			logger.info("=====> Start to get user information <=====");
			User user = null;
			if (userId == null || userId == 0) {
				Subject currentUser = SecurityUtils.getSubject();
				user = (User) currentUser.getSession().getAttribute(User.CURRENT_USER);
				userId = user.getId();
			}
			
			user = userService.findUserById(userId);
			if (user == null) {
				throw new NullPointerException("用户ID：" + userId + " , 用户不存在。");
			}
				
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
			result.put("typename", user.getTypeName() != null ? user.getTypeName().getName() : "");
			result.put("accid", user.getAccid());
			result.put("token", user.getToken());
			
			ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_OK, "", result);
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_FAIL, e.getMessage());
		}
		logger.info("=====> Get user information end <=====");
		return resultMap;
	}
	
	@ResponseBody
	@RequestMapping(value = "/getUserByAccid", method = RequestMethod.GET)
	public Map<String, Object> getUserByAccid(@RequestParam("accid") String accid) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			logger.info("=====> Start to get user information by accid <=====");
			
			User user = userService.getUserByAccid(accid);
			if (user == null) {
				throw new NullPointerException("用户ACCID：" + accid + " , 用户不存在");
			}
				
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
			result.put("typename", user.getTypeName() != null ? user.getTypeName().getName() : "");
			result.put("accid", user.getAccid());
			result.put("token", user.getToken());
			
			ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_OK, "", result);
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_FAIL, e.getMessage());
		}
		logger.info("=====> Get user information by accid end <=====");
		return resultMap;
	}
	
	@ResponseBody
	@RequestMapping(value = "/getUserInfo", method = RequestMethod.GET)
	public Map<String, Object> getUserInfo(@RequestParam(name = "userId", required = false) Integer userId,
			@RequestParam(name = "accid", required = false) String accid) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			logger.info("=====> Start to get user information by accid <=====");
			if (userId == null && StringUtils.isBlank(accid)) {
				throw new NullPointerException("必须有一个参数userId/accid");
			}
			if (userId != null && StringUtils.isNotBlank(accid)) {
				throw new RuntimeException("只需一个参数userId/accid");
			}
			
			User user = null;
			if (userId != null) {
				user = userService.findUserById(userId);
			} else if (StringUtils.isNotBlank(accid)) {
				user = userService.getUserByAccid(accid);
			}
			
			if (user == null) {
				throw new NullPointerException("用户不存在");
			}
			
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("userId", user.getId());
			result.put("accid", user.getAccid());
			result.put("phoneno", user.getPhoneNo());
			result.put("nickname", user.getNickName());
			result.put("picture", user.getPicture());
			result.put("signLog", user.getSignLog());
			
			SearchCriteria searchCriteria = new SearchCriteria();
			searchCriteria.setUserId(user.getId());
			// 我的粉丝
			searchCriteria.setAttention(true);
			int attentionsCount = userService.getUserAttentionsTotalCount(searchCriteria);
			result.put("attentionsCount", attentionsCount);
			// 我的关注
			searchCriteria.setAttention(false);
			int followersCount = userService.getUserAttentionsTotalCount(searchCriteria);
			result.put("followersCount", followersCount);
			// 兴趣标签
			List<Map> tags = new ArrayList<Map>();
			List<UserInterest> interests = new ArrayList<UserInterest>(user.getInterests());
			Collections.sort(interests, new UserInterestComparator());
			
			Map<String, Object> map = null;
			for (UserInterest interest : interests) {
				Tag tag = interest.getTag();
				if (tag != null) {
					map = new HashMap<String, Object>();
					map.put("id", tag.getId());
					map.put("name", tag.getName());
					map.put("typename", tag.getTypeName() != null ? tag.getTypeName().getName() : "");
					tags.add(map);
				}
			}
			result.put("tags", tags);
			
			ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_OK, "", result);
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_FAIL, e.getMessage());
		}
		logger.info("=====> Get user information by accid end <=====");
		return resultMap;
	}
	
	/**
	 * 修改用户签名
	 * @param signLog	签名内容
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

			/*int userId = user.getId();
			user = userService.findUserById(userId);
			if (user == null) {
				throw new NullPointerException("用户ID：" + userId + " , 用户不存在。");
			}*/
			
			user.setSignLog(signLog);
			userService.saveOrUpdateUser(user);
			
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
	 * @param url	路径
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/updatePicture", method = RequestMethod.POST)
	public Map<String, Object> updatePicture(@RequestParam(name = "file") CommonsMultipartFile uploadFile) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			logger.info("=====> Start to change user picture <=====");
			if (uploadFile.getSize() == 0 || !uploadFile.getFileItem().getContentType().contains("image")) {
				resultMap.put("success", false);
				resultMap.put("errorMsg", "请选择正确的图片");
				return resultMap;
			}
			
			Subject currentUser = SecurityUtils.getSubject();
			User user = (User) currentUser.getSession().getAttribute(User.CURRENT_USER);
			
        	String picturePath = FileUploadHelper.uploadProfilePicture(user.getPhoneNo(), uploadFile.getInputStream());
        	
			user.setPicture(picturePath);
			userService.saveOrUpdateUser(user);
			
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("imgurl", picturePath);

			ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_OK, "", result);
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_FAIL, e.getMessage());
		}
		logger.info("=====> Change user picture end <=====");
		return resultMap;
	}
	
	/*@ResponseBody
	@RequestMapping(value = "/updatePicture", method = RequestMethod.POST)
	public Map<String, Object> updatePicture(@RequestParam(name = "file") String uploadFile) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			BASE64Decoder decoder = new BASE64Decoder();
			FileOutputStream write = new FileOutputStream(new File("d:/test.png"));
			byte[] decoderBytes = decoder.decodeBuffer(uploadFile);
			write.write(decoderBytes);
			write.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultMap;
	}*/
	
	/**
	 * 修改密码
	 * @param newPassword	新密码
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
			
			/*int userId = user.getId();
			user = userService.findUserById(userId);
			if (user == null) {
				throw new NullPointerException("用户ID：" + userId + " , 用户不存在。");
			}*/
			
			// 密码MD5加密
			user.setPassword(EncryptHelper.encryptByMD5(user.getPhoneNo(), newPassword));
			userService.saveOrUpdateUser(user);
			
			ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_OK, "");
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_FAIL, e.getMessage());
		}
		logger.info("=====> Change user password end <=====");
		return resultMap;
	}

	/**
	 * 获取联系人
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/getContacts", method = RequestMethod.GET)
	public Map<String, Object> getContacts() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			logger.info("=====> Start to get contacts <=====");
			
			Subject currentUser = SecurityUtils.getSubject();
			User user = (User) currentUser.getSession().getAttribute(User.CURRENT_USER);
			
			int userId = user.getId();
			user = userService.findUserById(userId);
			if (user == null) {
				throw new NullPointerException("用户ID：" + userId + " , 用户不存在。");
			}
			
			List<Map> resultList = new ArrayList<Map>();
			Map<String, Object> map = null;
			for (Contact contact : user.getContacts()) {
				if (contact.getContactUser() != null) {
					map = new HashMap<String, Object>();
					map.put("userId", contact.getContactUser().getId());
					map.put("nickName", contact.getContactUser().getNickName());
					map.put("picture", contact.getContactUser().getPicture());
					map.put("degree", contact.getDegree());
					map.put("accid", contact.getContactUser().getAccid());
//					map.put("description", contact.getDescription());
					resultList.add(map);
				}
			}
			logger.info("=====> Get contacts end <=====");
			
			ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_OK, "", resultList);
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_FAIL, e.getMessage());
		}
		return resultMap;
	}
	
	/**
	 * 获取我的兴趣标签
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/getTags", method = RequestMethod.POST)
	public Map<String, Object> getInterestTags() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			logger.info("=====> Start to get user interest tags <=====");
			
			Subject currentUser = SecurityUtils.getSubject();
			User user = (User) currentUser.getSession().getAttribute(User.CURRENT_USER);
			
			int userId = user.getId();
			user = userService.findUserById(userId);
			if (user == null) {
				throw new NullPointerException("用户ID：" + userId + " , 用户不存在。");
			}
			
			List<Map> resultList = new ArrayList<Map>();
			List<UserInterest> interests = new ArrayList<UserInterest>(user.getInterests());
			Collections.sort(interests, new UserInterestComparator());
			
			Map<String, Object> map = null;
			for (UserInterest interest : interests) {
				Tag tag = interest.getTag();
				if (tag != null) {
					map = new HashMap<String, Object>();
					map.put("id", tag.getId());
					map.put("name", tag.getName());
					map.put("typename", tag.getTypeName() != null ? tag.getTypeName().getName() : "");
					resultList.add(map);
				}
			}
			logger.info("=====> Get user interest tags end <=====");
			
			ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_OK, "", resultList);
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_FAIL, e.getMessage());
		}
		return resultMap;
	}
	
	/**
	 * 获取用户关注/粉丝
	 * @param isAttention	true：返回我的关注	false：返回我的粉丝
	 * @param pageSize
	 * @param startRow
	 * @param orderBy
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/getUserAttention", method = RequestMethod.GET)
	public Map<String, Object> getUserAttention(
			@RequestParam("isAttention") boolean isAttention,
			@RequestParam(name = "pagesize", required = false) Integer pageSize,
			@RequestParam(name = "start", required = false) Integer startRow,
			@RequestParam(name = "orderby", required = false) String orderBy) {
		Map<String, Object> responseMap = new HashMap<String, Object>();
		try {
			logger.info("=====> Start to get user attention <=====");
			
			Subject currentUser = SecurityUtils.getSubject();
			User user = (User) currentUser.getSession().getAttribute(User.CURRENT_USER);
			
			/*int userId = user.getId();
			user = userService.findUserById(userId);
			if (user == null) {
				throw new NullPointerException("用户ID：" + userId + " , 用户不存在。");
			}*/
			// default value
			if (startRow == null) {
				startRow = 0;
			}
			if (pageSize == null) {
				pageSize = ApplicationConfig.getInstance().getDefaultPageSize();
			}
			
			SearchCriteria searchCriteria = new SearchCriteria();
			searchCriteria.setStartRow(startRow);
			searchCriteria.setPageSize(pageSize);
			searchCriteria.setOrderBy(orderBy);
			searchCriteria.setUserId(user.getId());
			searchCriteria.setAttention(isAttention);
			
			Map<String, Object> attentionMap = userService.getUserAttentions(searchCriteria);
			List<UserAttention> attentions = (List<UserAttention>) attentionMap.get("resultList");
			int total = (int) attentionMap.get("totalCount");
			
			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("total", total);
			resultMap.put("start", startRow);
			
			List<Map> resultList = new ArrayList<Map>();
			Map<String, Object> map = null;
			for (UserAttention userAttention : attentions) {
				User attentionUser = null;
				if (isAttention) {
					attentionUser = userAttention.getAttentionUser();
				} else {
					attentionUser = userAttention.getUser();
				}
				if (attentionUser != null) {
					map = new HashMap<String, Object>();
					map.put("userId", attentionUser.getId());
					map.put("nickName", attentionUser.getNickName());
					map.put("picture", attentionUser.getPicture());
					map.put("accid", attentionUser.getAccid());
					resultList.add(map);
				}
			}
			resultMap.put("cnt", resultList.size());
			resultMap.put("lists", resultList);
			
			logger.info("=====> Get user attention end <=====");
			
			ResponseHelper.addResponseData(responseMap, RequestHelper.RESPONSE_STATUS_OK, "", resultMap);
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.addResponseData(responseMap, RequestHelper.RESPONSE_STATUS_FAIL, e.getMessage());
		}
		return responseMap;
	}
	
	/**
	 * 关注用户
	 * @param followUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/followUser", method = RequestMethod.POST)
	public Map<String, Object> followUser(@RequestParam("userId") int followUserId) {
		Map<String, Object> responseMap = new HashMap<String, Object>();
		try {
			logger.info("=====> Start to follow user <=====");
			User currentUser = (User) SecurityUtils.getSubject().getSession().getAttribute(User.CURRENT_USER);
			
			/*int userId = currentUser.getId();
			currentUser = userService.findUserById(userId);
			if (currentUser == null) {
				throw new NullPointerException("用户ID：" + userId + " , 用户不存在。");
			}*/
			
			User followUser = userService.findUserById(followUserId);
			if (followUser == null) {
				throw new NullPointerException("用户ID：" + followUserId + " , 用户不存在。");
			}
			
			UserAttention attention = new UserAttention(currentUser, followUser);
			userService.saveUserAttention(attention);
			
			logger.info("=====> Follow user end <=====");
			ResponseHelper.addResponseData(responseMap, RequestHelper.RESPONSE_STATUS_OK, "");
		} catch (ConstraintViolationException e) {
			ResponseHelper.addResponseData(responseMap, RequestHelper.RESPONSE_STATUS_FAIL, "已经关注用户");
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.addResponseData(responseMap, RequestHelper.RESPONSE_STATUS_FAIL, e.getMessage());
		}
		return responseMap;
	}
	
	@RequestMapping(value = "/profilePicture")
	public void profilePicture(HttpServletResponse response){
		try {
			logger.info("=====> Start to preview profile picture <=====");
			User currentUser = (User) SecurityUtils.getSubject().getSession().getAttribute(User.CURRENT_USER);
			
			FileUploadHelper.readLocalImage(response, currentUser.getPicture());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		logger.info("=====> Preview profile picture end <=====");
	}
	
	@ResponseBody
	@RequestMapping(value = "/test")
	public Map<String, Object> test() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		return resultMap;
	}

}
