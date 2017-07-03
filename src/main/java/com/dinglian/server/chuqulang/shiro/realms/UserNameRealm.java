package com.dinglian.server.chuqulang.shiro.realms;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.realm.AuthenticatingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import com.dinglian.server.chuqulang.base.SearchCriteria;
import com.dinglian.server.chuqulang.model.User;
import com.dinglian.server.chuqulang.service.UserService;

public class UserNameRealm extends AuthenticatingRealm {

	@Autowired
	private UserService userService;
	
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws UnknownAccountException,AuthenticationException {

		CustomizedToken token = (CustomizedToken) authenticationToken;
		
		String phoneNo = token.getUsername();
		SearchCriteria searchCriteria = new SearchCriteria();
		searchCriteria.setPhoneNo(phoneNo);
		User user = userService.getUser(searchCriteria);
		if (user == null) {
			throw new UnknownAccountException();
		}
		
		Object principal = user.getPhoneNo();
		Object credentials = user.getPassword();
		
		ByteSource credentialsSalt = ByteSource.Util.bytes(phoneNo);
		
		SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(principal, credentials, credentialsSalt, getName());
		
		Session session = SecurityUtils.getSubject().getSession();
		session.setAttribute(User.CURRENT_USER, user);
		
		return info;
	}

}
