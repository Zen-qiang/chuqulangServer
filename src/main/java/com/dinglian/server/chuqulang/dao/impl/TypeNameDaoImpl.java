package com.dinglian.server.chuqulang.dao.impl;

import org.springframework.stereotype.Repository;

import com.dinglian.server.chuqulang.dao.TypeNameDao;
import com.dinglian.server.chuqulang.model.TypeName;

@Repository("typeNameDao")
public class TypeNameDaoImpl extends AbstractHibernateDao<TypeName> implements TypeNameDao {

	public TypeNameDaoImpl() {
		super(TypeName.class);
	}

	protected TypeNameDaoImpl(Class<TypeName> entityClass) {
		super(entityClass);
	}

	@Override
	public TypeName getTypeNameByName(String typeNameStr) {
		String hql = "FROM TypeName WHERE name = :name ";
		return (TypeName) getCurrentSession().createQuery(hql).setString("name", typeNameStr).uniqueResult();
	}
}
