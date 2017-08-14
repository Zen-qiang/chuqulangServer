package com.dinglian.server.chuqulang.service;

import java.util.List;

import com.dinglian.server.chuqulang.model.ChatRoom;

public interface ChatService {

	void saveChatRoom(ChatRoom chatRoom);

	ChatRoom findChatRoomById(int roomid);

	ChatRoom findChatRoomByRoomId(int roomid);

	List<ChatRoom> getUserChatRooms(int id);

}
