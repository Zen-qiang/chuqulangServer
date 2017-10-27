package com.dinglian.server.chuqulang.dao;

import java.util.List;

import com.dinglian.server.chuqulang.base.SearchCriteria;
import com.dinglian.server.chuqulang.model.Coterie;
import com.dinglian.server.chuqulang.model.CoterieGuy;
import com.dinglian.server.chuqulang.model.Topic;

public interface CoterieDao extends GenericDao<Coterie> {

//	List<Coterie> getCoterieList(int tagId, String type);

	int getCoterieTotalCount();

	List<Coterie> getCoterieList(SearchCriteria searchCriteria);

	int getTopicTotalCount(Integer coterieId);

	List<Topic> getTopicList(SearchCriteria searchCriteria);

	List<Coterie> getCoteriesByName(String keyword);

	List<Coterie> getMyCoteries(String dataType, int userId, String keyword);

	Coterie getLastCoterie(int userId);

	boolean hasActivityProcess(int coterieId);

	List<CoterieGuy> getCoterieMembers(int coterieId, int start, int limit);

	CoterieGuy getCoterieGuy(int coterieId, int userId);

}