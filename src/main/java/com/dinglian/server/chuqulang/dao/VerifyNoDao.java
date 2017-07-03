package com.dinglian.server.chuqulang.dao;

import java.util.Date;

import com.dinglian.server.chuqulang.model.VerifyNo;

public interface VerifyNoDao extends GenericDao<VerifyNo> {

	VerifyNo getVerifyNo(String phoneNo, String dataType);

	boolean checkUserVerifyNoByIp(String phoneNo);

	void verifyNoCleanUp(Date time);


}