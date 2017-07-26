package com.dinglian.server.chuqulang.service;

import com.dinglian.server.chuqulang.model.ChatRoom;

public interface ChatService {

	void saveChatRoom(ChatRoom chatRoom);

	ChatRoom findChatRoomById(int roomid);

	ChatRoom findChatRoomByRoomId(int roomid);

}
