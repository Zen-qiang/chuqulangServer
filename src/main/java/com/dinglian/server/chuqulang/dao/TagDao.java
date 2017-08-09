package com.dinglian.server.chuqulang.dao;

import java.util.List;

import com.dinglian.server.chuqulang.model.Tag;

public interface TagDao extends GenericDao<Tag> {

	List<Tag> getTags(String tagType);

	List<Tag> getChildTags(Integer parentId);
	
}