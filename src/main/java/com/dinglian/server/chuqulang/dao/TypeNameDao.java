package com.dinglian.server.chuqulang.dao;

import java.util.List;

import com.dinglian.server.chuqulang.model.TypeName;

public interface TypeNameDao extends GenericDao<TypeName> {

	TypeName getTypeNameByName(String typeNameStr);

	List<TypeName> getActivityTypes(String type);
}