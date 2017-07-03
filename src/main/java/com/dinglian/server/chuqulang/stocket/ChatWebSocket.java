package com.dinglian.server.chuqulang.stocket;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.json.JSONObject;

@ServerEndpoint(value = "/chatWebSocket/{userId}/{uuid}")
public class ChatWebSocket {
	// 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
	private static int onlineCount = 0;

	private static Logger logger = LoggerFactory.getLogger(ChatWebSocket.class);
	// concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。若要实现服务端与单一客户端通信的话，可以使用Map来存放，其中Key可以为用户标识
	private static Map<String, ChatWebSocket> mapsocket = Collections.synchronizedMap(new HashMap<String, ChatWebSocket>());// 为hashmap加上同步
	private static Map<String, String> uuids = Collections.synchronizedMap(new HashMap<String, String>());// 为hashmap加上同步
	// 与某个客户端的连接会话，需要通过它来给客户端发送数据
	private Session session;

	/**
	 * 连接建立成功调用的方法
	 * 
	 * @param session
	 *            可选的参数。session为与某个客户端的连接会话，需要通过它来给客户端发送数据
	 */
	@OnOpen
	public void onOpen(Session session, @PathParam(value = "userId") String userId,@PathParam(value = "uuid") String uuid) {
		this.session = session;
		/*String sendType = userId.split("_")[1];
		if(sendType.equals("admin")){
			return;
		}*/
		if (mapsocket.get(userId) == null) {
			mapsocket.put(userId, this);
			uuids.put(userId, uuid);
			addOnlineCount(); // 在线数加1
			logger.info("欢迎" + userId + "，当前在线人数为" + getOnlineCount());
		} else {
			try {
				logger.info(uuids.get(userId)+','+uuid);
				if(uuids.get(userId)==uuid){
					mapsocket.put(userId, this);
					uuids.put(userId, uuid);
					addOnlineCount(); // 在线数加1
					logger.info("欢迎" + userId + "，当前在线人数为" + getOnlineCount());
				}else{
					mapsocket.get(userId).sendMessage("getOut");
					mapsocket.remove(userId);
					uuids.remove(userId);
					subOnlineCount(); // 在线数减1
					logger.info("原登录" + userId + "被挤下线，当前在线人数：" + getOnlineCount());
					mapsocket.put(userId, this);
					uuids.put(userId, uuid);
					addOnlineCount(); // 在线数加1
					logger.info("欢迎新" + userId + "，当前在线人数为" + getOnlineCount());
				}
			} catch (IOException e) {
				e.printStackTrace();
				logger.error("发生错误");
			}
		}
	}

	/**
	 * 连接关闭调用的方法
	 */
	@OnClose
	public void onClose(@PathParam(value = "userId") String userId,@PathParam(value = "uuid") String uuid) {
		if(uuids.get(userId)==uuid){
			mapsocket.remove(userId);
			subOnlineCount(); // 在线数减1
			logger.info(userId + "退出登录，当前在线人数：" + getOnlineCount());
		}
	}
	
	/**
	 * 收到客户端消息后调用的方法
	 * 
	 * @param message
	 *            客户端发送过来的消息
	 * @param session
	 *            可选的参数
	 */
	@OnMessage
	public void onMessage(String message, Session session,@PathParam(value = "userId") String userId) {
		logger.info("来自客户端的消息:" + message);
		JSONObject msgJson = JSONObject.fromObject(message);
		try {
			JSONObject json = new JSONObject();
			json.accumulate("sender", userId); // msgJson.getString("sender")
			json.accumulate("type", msgJson.getString("type"));
			json.accumulate("content", msgJson.getString("content"));
			
			Iterator<Entry<String, ChatWebSocket>> it = mapsocket.entrySet().iterator();
			while (it.hasNext()){
				Entry<String, ChatWebSocket> entry = it.next();
				if (entry.getKey().equals(userId)) {
					continue;
				}
				ChatWebSocket webSocket = entry.getValue();
				webSocket.sendMessage(json.toString());
			}
			/*for (ChatWebSocket webSocket : mapsocket.values()) {
				JSONObject json = new JSONObject();
				json.accumulate("sender", userId);
				json.accumulate("type", msgJson.getString("type"));
				json.accumulate("content", msgJson.getString("content"));
				webSocket.sendMessage(json.toString());
			}*/
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 发生错误时调用
	 * 
	 * @param session
	 * @param error
	 */
	@OnError
	public void onError(Session session, Throwable error) {
		logger.info("发生错误");
		error.printStackTrace();
	}

	/**
	 * 这个方法与上面几个方法不一样。没有用注解，是根据自己需要添加的方法。
	 * 
	 * @param message
	 * @throws IOException
	 */
	public void sendMessage(String message) throws IOException {
		this.session.getBasicRemote().sendText(message);
	}

	public static synchronized int getOnlineCount() {
		return onlineCount;
	}

	public static synchronized void addOnlineCount() {
		ChatWebSocket.onlineCount++;
	}

	public static synchronized void subOnlineCount() {
		ChatWebSocket.onlineCount--;
	}
}