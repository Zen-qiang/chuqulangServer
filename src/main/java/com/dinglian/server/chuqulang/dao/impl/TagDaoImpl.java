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
	public List<Tag> getTags(String type) {
		String hql = "FROM Tag WHERE type = :type ORDER BY times DESC";
		return getCurrentSession().createQuery(hql).setString("type", type).list();
	}

	@Override
	public List<Tag> getChildTags(Integer parentId) {
		String sql = "SELECT ctag.* FROM tag_associate_map map JOIN tag ptag ON ptag.id = map.parent_tag_id ";
		sql += "JOIN tag ctag ON ctag.id = map.child_tag_id WHERE map.parent_tag_id = :parentId ORDER BY ctag.times DESC";
		return getCurrentSession().createSQLQuery(sql).addEntity(Tag.class).setInteger("parentId", parentId).list();
	}

}
