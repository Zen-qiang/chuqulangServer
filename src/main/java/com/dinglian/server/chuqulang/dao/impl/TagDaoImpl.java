package com.dinglian.server.chuqulang.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.dinglian.server.chuqulang.dao.TagDao;
import com.dinglian.server.chuqulang.model.Tag;

@Repository
public class TagDaoImpl extends AbstractHibernateDao<Tag> implements TagDao {

	public TagDaoImpl() {
		super(Tag.class);
	}

	protected TagDaoImpl(Class<Tag> entityClass) {
		super(entityClass);
	}

	@Override
	public List<Tag> getAllTags() {
		String hql = "FROM Tag ORDER BY typeName.id,orderNo ";
		return getCurrentSession().createQuery(hql).list();
	}

	@Override
	public List<Tag> getTagsByTypeNameId(Integer typeNameId) {
		String hql = "FROM Tag WHERE typeName.id = :typeNameId ORDER BY orderNo ";
		return getCurrentSession().createQuery(hql).setInteger("typeNameId", typeNameId).list();
	}

	/*@Override
	public List<Tag> getTagListByTypeName(String typeName) {
		String hql = "FROM Tag WHERE typeName.name = :typeName ORDER BY times DESC ";
		return getCurrentSession().createQuery(hql).setString("typeName", typeName).list();
	}*/
}
