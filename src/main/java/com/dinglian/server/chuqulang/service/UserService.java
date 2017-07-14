package com.dinglian.server.chuqulang.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dinglian.server.chuqulang.base.SearchCriteria;
import com.dinglian.server.chuqulang.model.User;
import com.dinglian.server.chuqulang.model.UserAttention;
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

	public Map<String, Object> getUserAttentions(SearchCriteria searchCriteria);

	public void saveUserAttention(UserAttention attention);

}
