package com.dinglian.server.chuqulang.base;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import org.apache.http.client.ClientProtocolException;

import com.dinglian.server.chuqulang.utils.NeteaseIMUtil;

import net.sf.json.JSONArray;

public class Test {

	public static void main(String[] args) {
		try {
			NeteaseIMUtil util = NeteaseIMUtil.getInstance();
			
//			util.updateUinfo("helloworld", "nickname1", null, "im sign", "133@aa.com", null, null, "1", "{\"city\":\"上海\"}");
			
			JSONArray array = new JSONArray();
			array.add("helloworld1");
			array.add("helloworld");
//			System.out.println(array.toString());
//			System.out.println(util.getUinfos(array.toString()));
			
//			(util.refresh("helloworld");
			
//			util.addFriend("helloworld", "helloworld1", 1, "请求加好友");
			
//			util.updateFriend("helloworld", "helloworld1", "好友A", null);
			
			Calendar c = Calendar.getInstance();
			c.add(Calendar.DAY_OF_MONTH, -2);
			
//			util.getFriend("helloworld", String.valueOf(c.getTimeInMillis()));
			
//			util.deleteFriend("helloworld", "helloworld1");
			
//			util.basicSendMsg("helloworld", 0, "helloworld1", 0, "发送普通消息");
			
//			util.createChatroom("helloworld", "金桥广场舞", null, null, null);
			
//			util.updateChatroom(9766850, "外滩广场舞", "8点集合", null, null, true, null);
			
//			util.toggleCloseStat(9766850, "helloworld", true);
//			
//			util.getChatroom(9766850, true);
			
//			util.requestAddr(9766850, "helloworld", 1);
			
//			util.sendChatroomMsg(9766850, "asda123sad342", "helloworld", 0, 0, "发送聊天室消息");
			
//			util.membersByPage(9766850, 0, 0, 20);
			
//			util.queryMembers(9766850, array.toString());
			
//			util.querySessionMsg("helloworld", "helloworld1", String.valueOf(c.getTimeInMillis()), String.valueOf(new Date().getTime()), 20, 1);
			
			util.queryChatroomMsg(9766850, "helloworld", c.getTimeInMillis(), 20, 1);
			
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
