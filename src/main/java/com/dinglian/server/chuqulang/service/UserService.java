package com.dinglian.server.chuqulang.service;

import java.util.Date;
import java.util.List;

import com.dinglian.server.chuqulang.base.SearchCriteria;
import com.dinglian.server.chuqulang.model.Contact;
import com.dinglian.server.chuqulang.model.User;
import com.dinglian.server.chuqulang.model.VerifyNo;

public interface UserService {

	public void register(User user) throws Exception;

	public User getUser(SearchCriteria searchCriteria);

	public void saveVerifyNo(VerifyNo vn);

	public VerifyNo getVerifyNo(String phoneNo, String dataType);

	public void deleteVerifyNo(VerifyNo existVerifyNo);

	public User findUserById(int userId);

	public void saveOrUpdateUser(User user);

	public User login(String userName, String md5Encode);

	public boolean checkUserVerifyNoByIp(String phoneNo);

	public void verifyNoCleanUp(Date time);

	public User getUserByUsername(String username);

//	public List<Contact> getContactList(int id);

}
