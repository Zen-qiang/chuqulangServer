package com.dinglian.server.chuqulang.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dinglian.server.chuqulang.base.SearchCriteria;
import com.dinglian.server.chuqulang.dao.GeneralDao;
import com.dinglian.server.chuqulang.dao.UserDao;
import com.dinglian.server.chuqulang.dao.VerifyNoDao;
import com.dinglian.server.chuqulang.model.User;
import com.dinglian.server.chuqulang.model.UserAttention;
import com.dinglian.server.chuqulang.model.VerifyNo;
import com.dinglian.server.chuqulang.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private VerifyNoDao verifyNoDao;
    
    @Autowired
    private GeneralDao generalDao;

    @Override
    public void register(User user) throws Exception {
        userDao.save(user);
    }

    @Override
    public User getUser(SearchCriteria searchCriteria) {
        return userDao.getUser(searchCriteria);
    }

    @Override
    public void saveVerifyNo(VerifyNo vn) {
        verifyNoDao.saveOrUpdate(vn);
    }

    @Override
    public VerifyNo getVerifyNo(String phoneNo, String dataType) {
        return verifyNoDao.getVerifyNo(phoneNo, dataType);
    }

    @Override
    public void deleteVerifyNo(VerifyNo existVerifyNo) {
        verifyNoDao.delete(existVerifyNo);
    }

    @Override
    public User findUserById(int userId) {
        return userDao.findById(userId);
    }

    @Override
    public void saveOrUpdateUser(User user) {
        userDao.saveOrUpdate(user);
    }

    @Override
    public User login(String userName, String password) {
        return userDao.login(userName, password);
    }

    @Override
    public boolean checkUserVerifyNoByIp(String phoneNo) {
        return verifyNoDao.checkUserVerifyNoByIp(phoneNo);
    }

    @Override
    public void verifyNoCleanUp(Date time) {
        verifyNoDao.verifyNoCleanUp(time);
    }

    @Override
    public User getUserByUsername(String username) {
        return userDao.getUserByUsername(username);
    }

    @RequiresRoles({"admin"})
    public void test() {
        System.out.println(new Date());
    }

	@Override
	public Map<String, Object> getUserAttentions(SearchCriteria searchCriteria) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<UserAttention> attentions = generalDao.getUserAttentions(searchCriteria);
		int totalCount = generalDao.getUserAttentionTotalCount(searchCriteria);
		map.put("totalCount", totalCount);
		map.put("resultList", attentions);
		return map;
	}


}
