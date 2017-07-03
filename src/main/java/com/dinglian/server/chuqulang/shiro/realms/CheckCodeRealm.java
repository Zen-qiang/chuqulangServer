package com.dinglian.server.chuqulang.shiro.realms;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.realm.AuthenticatingRealm;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;

import com.dinglian.server.chuqulang.base.SearchCriteria;
import com.dinglian.server.chuqulang.model.User;
import com.dinglian.server.chuqulang.model.VerifyNo;
import com.dinglian.server.chuqulang.service.UserService;

public class CheckCodeRealm extends AuthenticatingRealm {

	@Autowired
	private UserService userService;
	
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws UnknownAccountException,AuthenticationException {
		
		CustomizedToken token = (CustomizedToken) authenticationToken;

		String phoneNo = token.getUsername();
		String verifyNo = String.valueOf(token.getPassword());
		
		SearchCriteria searchCriteria = new SearchCriteria();
		searchCriteria.setPhoneNo(phoneNo);
		
		User user = userService.getUser(searchCriteria);
		if (user == null) {
			throw new UnknownAccountException();
		}
		
		VerifyNo vn = userService.getVerifyNo(phoneNo, VerifyNo.VERIFY_NO_TYPE_LOGIN);
		if (vn != null && !vn.getVerifyNo().equalsIgnoreCase(verifyNo)) {
			throw new AuthenticationException();
		}
		
		Object principal = user.getPhoneNo();
		Object credentials = user.getPassword();
		
		token.setUsername(principal.toString());
		token.setPassword(credentials.toString().toCharArray());
		
		SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(principal, credentials, getName());
		
		Session session = SecurityUtils.getSubject().getSession();
		session.setAttribute(User.CURRENT_USER, user);

		return info;
	}

}
