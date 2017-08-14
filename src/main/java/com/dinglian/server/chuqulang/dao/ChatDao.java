package com.dinglian.server.chuqulang.dao;

import java.util.List;

import com.dinglian.server.chuqulang.model.ChatRoom;

public interface ChatDao {

	void saveChatRoom(ChatRoom chatRoom);

	ChatRoom findChatRoomById(int roomid);

	ChatRoom findChatRoomByRoomId(int roomid);

	List<ChatRoom> getUserChatRooms(int userId);

}
