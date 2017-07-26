package com.dinglian.server.chuqulang.dao.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.dinglian.server.chuqulang.dao.ChatDao;
import com.dinglian.server.chuqulang.model.ChatRoom;

@Repository
public class ChatDaoImpl implements ChatDao {

	@Autowired
	private SessionFactory sessionFactory;

	private Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}
	
	@Override
	public void saveChatRoom(ChatRoom chatRoom) {
		getCurrentSession().save(chatRoom);
	}

	@Override
	public ChatRoom findChatRoomById(int roomid) {
		return (ChatRoom) getCurrentSession().get(ChatRoom.class, roomid);
	}

	@Override
	public ChatRoom findChatRoomByRoomId(int roomid) {
		String hql = "FROM ChatRoom WHERE chatRoomId = :roomid ";
		return (ChatRoom) getCurrentSession().createQuery(hql).setInteger("roomid", roomid).uniqueResult();
	}

}
