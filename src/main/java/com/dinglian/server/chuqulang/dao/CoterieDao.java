package com.dinglian.server.chuqulang.dao;

import java.util.List;

import com.dinglian.server.chuqulang.model.Coterie;

public interface CoterieDao extends GenericDao<Coterie> {

	List<Coterie> getCoterieList(int tagId, String type);

}