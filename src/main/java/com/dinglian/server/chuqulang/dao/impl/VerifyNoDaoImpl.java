package com.dinglian.server.chuqulang.dao.impl;

import java.util.Calendar;
import java.util.Date;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.dinglian.server.chuqulang.dao.VerifyNoDao;
import com.dinglian.server.chuqulang.model.VerifyNo;

@Repository("verifyNoDao")
public class VerifyNoDaoImpl extends AbstractHibernateDao<VerifyNo> implements VerifyNoDao{

	public VerifyNoDaoImpl() {
		super(VerifyNo.class);
	}
	
	protected VerifyNoDaoImpl(Class<VerifyNo> entityClass) {
		super(entityClass);
	}

	@Override
	public VerifyNo getVerifyNo(String phoneNo, String dataType) {
		String hql = "FROM VerifyNo vn WHERE vn.phoneNo = :phoneNo AND vn.dataType = :dataType AND vn.valid = true ";
		return (VerifyNo) this.getCurrentSession().createQuery(hql).setString("phoneNo", phoneNo).setString("dataType", dataType).uniqueResult();
	}

	@Override
	public boolean checkUserVerifyNoByIp(String phoneNo) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR_OF_DAY, -1);
		
		String sql = "select count(1) from verify_no where phone_no = :phoneNo and creation_time >= :createTime ";
		Query query = this.getCurrentSession().createSQLQuery(sql).setString("phoneNo", phoneNo).setTimestamp("createTime", calendar.getTime());
		
		Object result = query.uniqueResult();
		if (result != null) {
			int count = Integer.parseInt(result.toString());
			if (count >= 3) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void verifyNoCleanUp(Date time) {
		String hql = "DELETE VerifyNo vn WHERE vn.creationTime < :time ";
		getCurrentSession().createQuery(hql).setTimestamp("time", time).executeUpdate();
	}


}
