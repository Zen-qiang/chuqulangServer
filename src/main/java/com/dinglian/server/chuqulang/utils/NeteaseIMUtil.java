package com.dinglian.server.chuqulang.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dinglian.server.chuqulang.base.ApplicationConfig;
import com.dinglian.server.chuqulang.controller.UserController;

import net.sf.json.JSONObject;

public class NeteaseIMUtil {

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	private static final NeteaseIMUtil instance = new NeteaseIMUtil();

	public static NeteaseIMUtil getInstance() {
		return instance;
	}

	private ApplicationConfig config = ApplicationConfig.getInstance();
	private String appKey = config.getNeteaseImAppKey();
	private String appSecret = config.getNeteaseImAppSecret();
	private String nonce = config.getNeteaseImNonce();

	// 创建通信ID
	private static final String URI_USER_CREATE = "https://api.netease.im/nimserver/user/create.action";
	// 更新通信ID
	private static final String URI_USER_UPDATE = "https://api.netease.im/nimserver/user/update.action";
	// 刷新通信ID获取TOKEN
	private static final String URI_USER_REFRESH = "https://api.netease.im/nimserver/user/refreshToken.action";

	// 获取用户名片
	private static final String URI_UINFO_GET = "https://api.netease.im/nimserver/user/getUinfos.action";
	// 更新用户名片
	private static final String URI_UINFO_UPDATE = "https://api.netease.im/nimserver/user/updateUinfo.action";

	// 添加好友
	private static final String URI_FRIEND_ADD = "https://api.netease.im/nimserver/friend/add.action";
	// 更新好友备注
	private static final String URI_FRIEND_UPDATE = "https://api.netease.im/nimserver/friend/update.action";
	// 获取好友
	private static final String URI_FRIEND_GET = "https://api.netease.im/nimserver/friend/get.action";
	// 删除好友
	private static final String URI_FRIEND_DELETE = "https://api.netease.im/nimserver/friend/delete.action";

	// 发送普通消息
	private static final String URI_BASIC_SENDMSG = "https://api.netease.im/nimserver/msg/sendMsg.action";

	// 创建聊天室
	private static final String URI_CHATROOM_CREATE = "https://api.netease.im/nimserver/chatroom/create.action";
	// 查询聊天室
	private static final String URI_CHATROOM_GET = "https://api.netease.im/nimserver/chatroom/get.action";
	// 更新聊天室
	private static final String URI_CHATROOM_UPDATE = "https://api.netease.im/nimserver/chatroom/update.action";
	// 切换聊天室关闭状态
	private static final String URI_CHATROOM_TOGGLE_CLOSE = "https://api.netease.im/nimserver/chatroom/toggleCloseStat.action";
	// 请求聊天室地址
	private static final String URI_CHATROOM_REQUEST_ADDR = "https://api.netease.im/nimserver/chatroom/requestAddr.action";
	// 发送聊天室消息
	private static final String URI_CHATROOM_SENDMSG = "https://api.netease.im/nimserver/chatroom/sendMsg.action";
	// 分页获取成员列表
	private static final String URI_CHATROOM_MEMBERS_BY_PAGE = "https://api.netease.im/nimserver/chatroom/membersByPage.action";
	// 批量获取在线成员信息
	private static final String URI_CHATROOM_QUERY_MEMBERS = "https://api.netease.im/nimserver/chatroom/queryMembers.action";

	// 单聊云端历史消息查询
	private static final String URI_HISTORY_SESSION_MSG = "https://api.netease.im/nimserver/history/querySessionMsg.action";
	// 聊天室云端历史消息查询
	private static final String URI_HISTORY_CHATROOM_MSG = "https://api.netease.im/nimserver/history/queryChatroomMsg.action";

	private String execute(String uri, Map<String, String> parameterMap) throws ClientProtocolException, IOException {
		logger.info(uri);
		logger.info(parameterMap.toString());

		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(uri);

		String curTime = String.valueOf(Calendar.getInstance().getTimeInMillis() / 1000L);
		String checkSum = CheckSumBuilder.getCheckSum(appSecret, nonce, curTime);

		// 设置请求的header
		httpPost.addHeader("AppKey", appKey);
		httpPost.addHeader("Nonce", nonce);
		httpPost.addHeader("CurTime", curTime);
		httpPost.addHeader("CheckSum", checkSum);
		httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

		// 添加参数
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		Iterator<Entry<String, String>> it = parameterMap.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, String> entry = it.next();
			nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
		}

		httpPost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));

		CloseableHttpResponse httpResponse = httpclient.execute(httpPost);
		HttpEntity entity = httpResponse.getEntity();

		String response = EntityUtils.toString(httpResponse.getEntity(), Consts.UTF_8);
		logger.info("response : " + response);

		// 关闭流
		EntityUtils.consume(entity);
		httpResponse.close();
		return response;
	}

	/**
	 * 创建网易云通信ID
	 * 
	 * @param accid
	 *            网易云通信ID，最大长度32字符，必须保证一个APP内唯一（只允许字母、数字、半角下划线_、@、半角点以及半角-组成，不区分大小写，会统一小写处理，请注意以此接口返回结果中的accid为准）。
	 * @param name
	 *            网易云通信ID昵称，最大长度64字符，用来PUSH推送时显示的昵称
	 * @param props
	 *            json属性，第三方可选填，最大长度1024字符
	 * @param icon
	 *            网易云通信ID头像URL，第三方可选填，最大长度1024
	 * @param token
	 *            网易云通信ID可以指定登录token值，最大长度128字符，并更新，如果未指定，会自动生成token，并在创建成功后返回
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String create(String accid, String name, String props, String icon, String token)
			throws ClientProtocolException, IOException {
		logger.info("=====> Start to create acccid <=====");
		if (StringUtils.isBlank(accid)) {
			throw new NullPointerException("参数 accid 不能为空！");
		}

		Map<String, String> parameterMap = new HashMap<String, String>();
		parameterMap.put("accid", accid);
		if (StringUtils.isNotBlank(name)) {
			parameterMap.put("name", name);
		}
		if (StringUtils.isNotBlank(props)) {
			parameterMap.put("props", props);
		}
		if (StringUtils.isNotBlank(icon)) {
			parameterMap.put("icon", icon);
		}
		if (StringUtils.isNotBlank(token)) {
			parameterMap.put("token", token);
		}
		String response = execute(URI_USER_CREATE, parameterMap);

		logger.info("=====> Create acccid end <=====");
		return response;
	}

	/**
	 * 网易云通信ID更新
	 * 
	 * @param accid
	 *            网易云通信ID，最大长度32字符，必须保证一个APP内唯一
	 * @param props
	 *            json属性，第三方可选填，最大长度1024字符
	 * @param token
	 *            网易云通信ID可以指定登录token值，最大长度128字符
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String update(String accid, String props, String token) throws ClientProtocolException, IOException {
		logger.info("=====> Start to update user info <=====");
		if (StringUtils.isBlank(accid)) {
			throw new NullPointerException("参数 accid 不能为空！");
		}

		Map<String, String> parameterMap = new HashMap<String, String>();
		parameterMap.put("accid", accid);
		if (StringUtils.isNotBlank(props)) {
			parameterMap.put("props", props);
		}
		if (StringUtils.isNotBlank(token)) {
			parameterMap.put("token", token);
		}
		String response = execute(URI_USER_UPDATE, parameterMap);

		logger.info("=====> Update user info end <=====");
		return response;
	}

	/**
	 * 更新并获取新token
	 * 
	 * @param accid
	 *            网易云通信ID，最大长度32字符，必须保证一个APP内唯一
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String refresh(String accid) throws ClientProtocolException, IOException {
		logger.info("=====> Start to refresh user token <=====");
		if (StringUtils.isBlank(accid)) {
			throw new NullPointerException("参数 accid 不能为空！");
		}

		Map<String, String> parameterMap = new HashMap<String, String>();
		parameterMap.put("accid", accid);
		String response = execute(URI_USER_REFRESH, parameterMap);

		logger.info("=====> Refresh user token end <=====");
		return response;
	}

	/**
	 * 获取用户名片
	 * 
	 * @param accid
	 *            用户帐号（例如：JSONArray对应的accid串，如：["zhangsan"]，如果解析出错，会报414）（一次查询最多为200）
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String getUinfos(String accids) throws ClientProtocolException, IOException {
		logger.info("=====> Start to get user info <=====");
		if (StringUtils.isBlank(accids)) {
			throw new NullPointerException("参数 accid 不能为空！");
		}

		Map<String, String> parameterMap = new HashMap<String, String>();
		parameterMap.put("accids", accids);
		String response = execute(URI_UINFO_GET, parameterMap);

		logger.info("=====> Get user info end <=====");
		return response;
	}

	/**
	 * 更新用户名片
	 * 
	 * @param accid
	 *            用户帐号，最大长度32字符，必须保证一个APP内唯一
	 * @param name
	 *            用户昵称，最大长度64字符
	 * @param icon
	 *            用户icon，最大长度1024字符
	 * @param sign
	 *            用户签名，最大长度256字符
	 * @param email
	 *            用户email，最大长度64字符
	 * @param birth
	 *            用户生日，最大长度16字符
	 * @param mobile
	 *            用户mobile，最大长度32字符，只支持国内号码
	 * @param gender
	 *            用户性别，0表示未知，1表示男，2女表示女，其它会报参数错误
	 * @param ex
	 *            用户名片扩展字段，最大长度1024字符，用户可自行扩展，建议封装成JSON字符串
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String updateUinfo(String accid, String name, String icon, String sign, String email, String birth,
			String mobile, String gender, String ex) throws ClientProtocolException, IOException {
		logger.info("=====> Start to update user info <=====");
		if (StringUtils.isBlank(accid)) {
			throw new NullPointerException("参数 accid 不能为空！");
		}

		Map<String, String> parameterMap = new HashMap<String, String>();
		parameterMap.put("accid", accid);
		if (name != null) {
			parameterMap.put("name", name);
		}
		if (StringUtils.isNotBlank(icon)) {
			parameterMap.put("icon", icon);
		}
		if (sign != null) {
			parameterMap.put("sign", sign);
		}
		if (email != null) {
			parameterMap.put("email", email);
		}
		if (birth != null) {
			parameterMap.put("birth", birth);
		}
		if (mobile != null) {
			parameterMap.put("mobile", mobile);
		}
		if (StringUtils.isNotBlank(gender)) {
			parameterMap.put("gender", gender);
		}
		if (ex != null) {
			parameterMap.put("ex", ex);
		}

		String response = execute(URI_UINFO_UPDATE, parameterMap);

		logger.info("=====> Update user info end <=====");
		return response;
	}

	/**
	 * 添加好友
	 * 
	 * @param accid
	 *            加好友发起者accid
	 * @param faccid
	 *            加好友接收者accid
	 * @param type
	 *            1直接加好友，2请求加好友，3同意加好友，4拒绝加好友
	 * @param msg
	 *            加好友对应的请求消息，第三方组装，最长256字符
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String addFriend(String accid, String faccid, int type, String msg)
			throws ClientProtocolException, IOException {
		logger.info("=====> Start to add friend <=====");
		if (StringUtils.isBlank(accid)) {
			throw new NullPointerException("参数 accid 不能为空！");
		}
		if (StringUtils.isBlank(faccid)) {
			throw new NullPointerException("参数 faccid 不能为空！");
		}
		if (!(type == 1 || type == 2 || type == 3 || type == 4)) {
			throw new RuntimeException("参数 type 必须为1/2/3/4！");
		}

		Map<String, String> parameterMap = new HashMap<String, String>();
		parameterMap.put("accid", accid);
		parameterMap.put("faccid", faccid);
		parameterMap.put("type", String.valueOf(type));
		if (StringUtils.isNotBlank(msg)) {
			parameterMap.put("msg", msg);
		}

		String response = execute(URI_FRIEND_ADD, parameterMap);

		logger.info("=====> Add friend end <=====");
		return response;
	}

	/**
	 * 更新好友相关信息
	 * 
	 * @param accid
	 *            发起者accid
	 * @param faccid
	 *            要修改朋友的accid
	 * @param alias
	 *            给好友增加备注名，限制长度128
	 * @param ex
	 *            修改ex字段，限制长度256
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String updateFriend(String accid, String faccid, String alias, String ex)
			throws ClientProtocolException, IOException {
		logger.info("=====> Start to update friend <=====");
		if (StringUtils.isBlank(accid)) {
			throw new NullPointerException("参数 accid 不能为空！");
		}
		if (StringUtils.isBlank(faccid)) {
			throw new NullPointerException("参数 faccid 不能为空！");
		}

		Map<String, String> parameterMap = new HashMap<String, String>();
		parameterMap.put("accid", accid);
		parameterMap.put("faccid", faccid);
		if (alias != null) {
			parameterMap.put("alias", alias);
		}
		if (ex != null) {
			parameterMap.put("ex", ex);
		}

		String response = execute(URI_FRIEND_UPDATE, parameterMap);

		logger.info("=====> Update friend end <=====");
		return response;
	}

	/**
	 * 获取好友关系
	 * 
	 * @param accid
	 *            发起者accid
	 * @param createtime
	 *            查询的时间点
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String getFriend(String accid, String createtime) throws ClientProtocolException, IOException {
		logger.info("=====> Start to get friend <=====");
		if (StringUtils.isBlank(accid)) {
			throw new NullPointerException("参数 accid 不能为空！");
		}
		if (StringUtils.isBlank(createtime)) {
			throw new NullPointerException("参数 createtime 不能为空！");
		}

		Map<String, String> parameterMap = new HashMap<String, String>();
		parameterMap.put("accid", accid);
		parameterMap.put("createtime", createtime);

		String response = execute(URI_FRIEND_GET, parameterMap);

		logger.info("=====> Get friend end <=====");
		return response;
	}

	/**
	 * 删除好友
	 * 
	 * @param accid
	 *            发起者accid
	 * @param faccid
	 *            要删除朋友的accid
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String deleteFriend(String accid, String faccid) throws ClientProtocolException, IOException {
		logger.info("=====> Start to delete friend <=====");
		if (StringUtils.isBlank(accid)) {
			throw new NullPointerException("参数 accid 不能为空！");
		}
		if (StringUtils.isBlank(faccid)) {
			throw new NullPointerException("参数 faccid 不能为空！");
		}

		Map<String, String> parameterMap = new HashMap<String, String>();
		parameterMap.put("accid", accid);
		parameterMap.put("faccid", faccid);

		String response = execute(URI_FRIEND_DELETE, parameterMap);

		logger.info("=====> Delete friend end <=====");
		return response;
	}

	/**
	 * 发送普通消息
	 * 
	 * @param from
	 *            发送者accid，用户帐号，最大32字符，
	 * @param ope
	 *            0：点对点个人消息，1：群消息（高级群），其他返回414
	 * @param to
	 *            ope==0是表示accid即用户id，ope==1表示tid即群id
	 * @param type
	 *            0 表示文本消息,1 表示图片，2 表示语音，3 表示视频，4 表示地理位置信息，6 表示文件，100 自定义消息类型
	 * @param msg
	 *            请参考下方消息示例说明中对应消息的body字段，最大长度5000字符，为一个JSON串
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String basicSendMsg(String from, int ope, String to, int type, String msg)
			throws ClientProtocolException, IOException {
		logger.info("=====> Start to basic send msg <=====");
		if (StringUtils.isBlank(from)) {
			throw new NullPointerException("参数 from 不能为空！");
		}
		if (!(ope == 0 || ope == 1)) {
			throw new RuntimeException("参数 ope 必须为0/1！");
		}
		if (StringUtils.isBlank(to)) {
			throw new NullPointerException("参数 to 不能为空！");
		}
		if (!(type == 0 || type == 1 || type == 2 || type == 3 || type == 4 || type == 6 || type == 100)) {
			throw new RuntimeException("参数 type 必须为0/1/2/3/4/6/100！");
		}
		if (StringUtils.isBlank(msg)) {
			throw new NullPointerException("参数 msg 不能为空！");
		}

		Map<String, String> parameterMap = new HashMap<String, String>();
		parameterMap.put("from", from);
		parameterMap.put("ope", String.valueOf(ope));
		parameterMap.put("to", to);
		parameterMap.put("type", String.valueOf(type));

		JSONObject body = new JSONObject();
		body.accumulate("msg", msg);
		parameterMap.put("body", body.toString());

		String response = execute(URI_BASIC_SENDMSG, parameterMap);

		logger.info("=====> Basic send msg end <=====");
		return response;
	}

	/**
	 * 创建聊天室
	 * 
	 * @param creator
	 *            聊天室属主的账号accid
	 * @param name
	 *            聊天室名称，长度限制128个字符
	 * @param announcement
	 *            公告，长度限制4096个字符
	 * @param broadcasturl
	 *            直播地址，长度限制1024个字符
	 * @param ext
	 *            扩展字段，最长4096字符
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String createChatroom(String creator, String name, String announcement, String broadcasturl, String ext)
			throws ClientProtocolException, IOException {
		logger.info("=====> Start to create chatroom <=====");
		if (StringUtils.isBlank(creator)) {
			throw new NullPointerException("参数 creator 不能为空！");
		}
		if (StringUtils.isBlank(name)) {
			throw new NullPointerException("参数 name 不能为空！");
		}

		Map<String, String> parameterMap = new HashMap<String, String>();
		parameterMap.put("creator", creator);
		parameterMap.put("name", name);
		if (StringUtils.isNotBlank(announcement)) {
			parameterMap.put("announcement", announcement);
		}
		if (StringUtils.isNotBlank(broadcasturl)) {
			parameterMap.put("broadcasturl", broadcasturl);
		}
		if (StringUtils.isNotBlank(ext)) {
			parameterMap.put("ext", ext);
		}

		String response = execute(URI_CHATROOM_CREATE, parameterMap);

		logger.info("=====> Create chatroom end <=====");
		return response;
	}

	/**
	 * 获取聊天室
	 * 
	 * @param roomid
	 *            聊天室ID
	 * @param needOnlineUserCount
	 *            是否需要返回在线人数，true或false，默认false
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String getChatroom(long roomid, boolean needOnlineUserCount) throws ClientProtocolException, IOException {
		logger.info("=====> Start to get chatroom <=====");

		Map<String, String> parameterMap = new HashMap<String, String>();
		parameterMap.put("roomid", String.valueOf(roomid));
		parameterMap.put("needOnlineUserCount", String.valueOf(needOnlineUserCount));

		String response = execute(URI_CHATROOM_GET, parameterMap);

		logger.info("=====> Get chatroom end <=====");
		return response;
	}

	/**
	 * 更新聊天室
	 * 
	 * @param roomid
	 *            聊天室id
	 * @param name
	 *            聊天室名称，长度限制128个字符
	 * @param announcement
	 *            公告，长度限制4096个字符
	 * @param broadcasturl
	 *            直播地址，长度限制1024个字符
	 * @param ext
	 *            扩展字段，长度限制4096个字符
	 * @param needNotify
	 *            true或false,是否需要发送更新通知事件，默认true
	 * @param notifyExt
	 *            通知事件扩展字段，长度限制2048
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String updateChatroom(long roomid, String name, String announcement, String broadcasturl, String ext,
			boolean needNotify, String notifyExt) throws ClientProtocolException, IOException {
		logger.info("=====> Start to update chatroom <=====");
		Map<String, String> parameterMap = new HashMap<String, String>();
		parameterMap.put("roomid", String.valueOf(roomid));
		if (StringUtils.isNotBlank(name)) {
			parameterMap.put("name", name);
		}
		if (announcement != null) {
			parameterMap.put("announcement", announcement);
		}
		if (broadcasturl != null) {
			parameterMap.put("broadcasturl", broadcasturl);
		}
		if (ext != null) {
			parameterMap.put("ext", ext);
		}
		if (!needNotify) {
			parameterMap.put("needNotify", String.valueOf(needNotify));
		}
		if (notifyExt != null) {
			parameterMap.put("notifyExt", notifyExt);
		}

		String response = execute(URI_CHATROOM_UPDATE, parameterMap);

		logger.info("=====> Update chatroom end <=====");
		return response;
	}

	/**
	 * 切换聊天室关闭状态
	 * 
	 * @param roomid
	 *            聊天室id
	 * @param operator
	 *            操作者账号，必须是创建者才可以操作
	 * @param valid
	 *            true或false，false:关闭聊天室；true:打开聊天室
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String toggleCloseStat(long roomid, String operator, boolean valid)
			throws ClientProtocolException, IOException {
		logger.info("=====> Start to toggle chatroom close stat <=====");
		if (StringUtils.isBlank(operator)) {
			throw new NullPointerException("参数 operator 不能为空！");
		}

		Map<String, String> parameterMap = new HashMap<String, String>();
		parameterMap.put("roomid", String.valueOf(roomid));
		parameterMap.put("operator", operator);
		parameterMap.put("valid", String.valueOf(valid));

		String response = execute(URI_CHATROOM_TOGGLE_CLOSE, parameterMap);

		logger.info("=====> Toggle chatroom close stat end <=====");
		return response;
	}

	/**
	 * 请求聊天室地址
	 * 
	 * @param roomid
	 * @param accid
	 *            进入聊天室的账号
	 * @param clienttype
	 *            1:weblink; 2:commonlink, 默认1
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String requestAddr(long roomid, String accid, int clienttype) throws ClientProtocolException, IOException {
		logger.info("=====> Start to request chatroom addr <=====");
		if (StringUtils.isBlank(accid)) {
			throw new NullPointerException("参数 accid 不能为空！");
		}
		if (!(clienttype == 1 || clienttype == 2)) {
			throw new RuntimeException("参数 clienttype 必须为1/2！");
		}

		Map<String, String> parameterMap = new HashMap<String, String>();
		parameterMap.put("roomid", String.valueOf(roomid));
		parameterMap.put("accid", accid);
		if (clienttype == 2) {
			parameterMap.put("clienttype", String.valueOf(clienttype));
		}

		String response = execute(URI_CHATROOM_REQUEST_ADDR, parameterMap);

		logger.info("=====> Request chatroom addr end <=====");
		return response;
	}

	/**
	 * 发送聊天室消息
	 * 
	 * @param roomid
	 * @param msgId
	 * @param fromAccid
	 * @param msgType
	 * @param resendFlag
	 * @param attach
	 * @param ext
	 * @param antispam
	 * @param antispamCustom
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String sendChatroomMsg(long roomid, String msgId, String fromAccid, int msgType, Integer resendFlag,
			String attach/*
							 * , String ext, String antispam, String antispamCustom
							 */) throws ClientProtocolException, IOException {
		logger.info("=====> Start to send chatroom message <=====");
		if (StringUtils.isBlank(msgId)) {
			throw new NullPointerException("参数 msgId 不能为空！");
		}
		if (StringUtils.isBlank(fromAccid)) {
			throw new NullPointerException("参数 fromAccid 不能为空！");
		}
		if (!(msgType == 0 || msgType == 1 || msgType == 2 || msgType == 3 || msgType == 4 || msgType == 6
				|| msgType == 10 || msgType == 100)) {
			throw new RuntimeException("参数 msgType 必须为0/1/2/3/4/6/10/100！");
		}
		if (resendFlag != null && !(resendFlag.intValue() == 0 || resendFlag.intValue() == 1)) {
			throw new RuntimeException("参数 resendFlag 必须为0/1！");
		}

		Map<String, String> parameterMap = new HashMap<String, String>();
		parameterMap.put("roomid", String.valueOf(roomid));
		parameterMap.put("msgId", msgId);
		parameterMap.put("fromAccid", fromAccid);
		parameterMap.put("msgType", String.valueOf(msgType));
		if (resendFlag != null) {
			parameterMap.put("resendFlag", String.valueOf(resendFlag));
		}
		if (attach != null) {
			parameterMap.put("attach", attach);
		}
		/*
		 * if (ext != null) { parameterMap.put("ext", ext); } if (antispam !=
		 * null) { parameterMap.put("antispam", antispam); } if (antispamCustom
		 * != null) { parameterMap.put("antispamCustom", antispamCustom); }
		 */

		String response = execute(URI_CHATROOM_SENDMSG, parameterMap);

		logger.info("=====> Send chatroom message end <=====");
		return response;
	}

	/**
	 * 分页获取成员列表
	 * 
	 * @param roomid
	 * @param type
	 *            需要查询的成员类型,0:固定成员;1:非固定成员;2:仅返回在线的固定成员
	 * @param endtime
	 *            单位毫秒，按时间倒序最后一个成员的时间戳,0表示系统当前时间
	 * @param limit
	 *            返回条数，<=100
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String membersByPage(long roomid, int type, long endtime, long limit)
			throws ClientProtocolException, IOException {
		logger.info("=====> Start to members by page <=====");
		if (!(type == 0 || type == 1 || type == 2)) {
			throw new RuntimeException("参数 type 必须为0/1/2！");
		}
		if (limit > 100) {
			throw new RuntimeException("参数 limit 必须为<= 100！");
		}

		Map<String, String> parameterMap = new HashMap<String, String>();
		parameterMap.put("roomid", String.valueOf(roomid));
		parameterMap.put("type", String.valueOf(type));
		parameterMap.put("endtime", String.valueOf(endtime));
		parameterMap.put("limit", String.valueOf(limit));

		String response = execute(URI_CHATROOM_MEMBERS_BY_PAGE, parameterMap);

		logger.info("=====> Members by page end <=====");
		return response;
	}

	/**
	 * 批量获取在线成员信息
	 * 
	 * @param roomid
	 * @param accids
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String queryMembers(long roomid, String accids) throws ClientProtocolException, IOException {
		logger.info("=====> Start to query members <=====");
		if (StringUtils.isBlank(accids)) {
			throw new NullPointerException("参数 accids 不能为空！");
		}

		Map<String, String> parameterMap = new HashMap<String, String>();
		parameterMap.put("roomid", String.valueOf(roomid));
		parameterMap.put("accids", accids);

		String response = execute(URI_CHATROOM_QUERY_MEMBERS, parameterMap);

		logger.info("=====> Query members end <=====");
		return response;
	}

	/**
	 * 单聊云端历史消息查询
	 * 
	 * @param from
	 *            发送者accid
	 * @param to
	 *            接收者accid
	 * @param begintime
	 *            开始时间，ms
	 * @param endtime
	 *            截止时间，ms
	 * @param limit
	 *            本次查询的消息条数上限(最多100条),小于等于0，或者大于100，会提示参数错误
	 * @param reverse
	 *            1按时间正序排列，2按时间降序排列。其它返回参数414错误.默认是按降序排列
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String querySessionMsg(String from, String to, String begintime, String endtime, Integer limit, int reverse)
			throws ClientProtocolException, IOException {
		logger.info("=====> Start to query session msg <=====");
		if (StringUtils.isBlank(from)) {
			throw new NullPointerException("参数 from 不能为空！");
		}
		if (StringUtils.isBlank(to)) {
			throw new NullPointerException("参数 to 不能为空！");
		}
		if (StringUtils.isBlank(begintime)) {
			throw new NullPointerException("参数 begintime 不能为空！");
		}
		if (StringUtils.isBlank(endtime)) {
			throw new NullPointerException("参数 endtime 不能为空！");
		}
		if (limit != null && (limit.intValue() <= 0 || limit.intValue() > 100)) {
			throw new RuntimeException("参数 limit 应该为 0-100 之间!");
		}
		if (!(reverse == 1 || reverse == 2)) {
			throw new RuntimeException("参数 reverse 应该为1/2!");
		}

		Map<String, String> parameterMap = new HashMap<String, String>();
		parameterMap.put("from", from);
		parameterMap.put("to", to);
		parameterMap.put("begintime", begintime);
		parameterMap.put("endtime", endtime);
		if (limit != null) {
			parameterMap.put("limit", String.valueOf(limit));
		}
		if (reverse != 2) {
			parameterMap.put("reverse", String.valueOf(reverse));
		}

		String response = execute(URI_HISTORY_SESSION_MSG, parameterMap);

		logger.info("=====> Query session msg end <=====");
		return response;
	}
	
	/**
	 * 聊天室云端历史消息查询
	 * @param roomid
	 * @param accid
	 * @param timetag
	 * @param limit
	 * @param reverse
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String queryChatroomMsg(long roomid, String accid, long timetag, Integer limit, int reverse)
			throws ClientProtocolException, IOException {
		logger.info("=====> Start to query session msg <=====");
		if (StringUtils.isBlank(accid)) {
			throw new NullPointerException("参数 accid 不能为空！");
		}
		if (limit == null) {
			throw new NullPointerException("参数 limit 不能为空！");
		}
		if (limit.intValue() <= 0 || limit.intValue() > 100) {
			throw new RuntimeException("参数 limit 应该为 0-100 之间!");
		}
		if (!(reverse == 1 || reverse == 2)) {
			throw new RuntimeException("参数 reverse 应该为1/2!");
		}

		Map<String, String> parameterMap = new HashMap<String, String>();
		parameterMap.put("roomid", String.valueOf(roomid));
		parameterMap.put("accid", accid);
		parameterMap.put("timetag", String.valueOf(timetag));
		if (limit != null) {
			parameterMap.put("limit", String.valueOf(limit));
		}
		if (reverse != 2) {
			parameterMap.put("reverse", String.valueOf(reverse));
		}

		String response = execute(URI_HISTORY_CHATROOM_MSG, parameterMap);

		logger.info("=====> Query session msg end <=====");
		return response;
	}
}
