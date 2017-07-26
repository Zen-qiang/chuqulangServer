package com.dinglian.server.chuqulang.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dinglian.server.chuqulang.dao.ChatDao;
import com.dinglian.server.chuqulang.model.ChatRoom;
import com.dinglian.server.chuqulang.service.ChatService;

@Service
public class ChatServiceImpl implements ChatService {

	@Autowired
	private ChatDao chatDao;
	
	@Override
	public void saveChatRoom(ChatRoom chatRoom) {
		chatDao.saveChatRoom(chatRoom);
	}

	@Override
	public ChatRoom findChatRoomById(int roomid) {
		return chatDao.findChatRoomById(roomid);
	}

	@Override
	public ChatRoom findChatRoomByRoomId(int roomid) {
		return chatDao.findChatRoomByRoomId(roomid);
	}

}
