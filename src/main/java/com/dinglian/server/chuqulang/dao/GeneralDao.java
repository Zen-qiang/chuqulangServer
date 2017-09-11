package com.dinglian.server.chuqulang.dao;

import java.util.List;

import com.dinglian.server.chuqulang.base.SearchCriteria;
import com.dinglian.server.chuqulang.model.Contact;
import com.dinglian.server.chuqulang.model.Coterie;
import com.dinglian.server.chuqulang.model.CoterieCarouselPicture;
import com.dinglian.server.chuqulang.model.CoterieGuy;
import com.dinglian.server.chuqulang.model.Event;
import com.dinglian.server.chuqulang.model.SensitiveWord;
import com.dinglian.server.chuqulang.model.Tag;
import com.dinglian.server.chuqulang.model.TopicPraise;
import com.dinglian.server.chuqulang.model.User;
import com.dinglian.server.chuqulang.model.UserAttention;
import com.dinglian.server.chuqulang.model.WxAccessToken;
import com.dinglian.server.chuqulang.model.WxJsApiTicket;
import com.dinglian.server.chuqulang.model.WxOAuth2AccessToken;

public interface GeneralDao {

	List<UserAttention> getUserAttentions(SearchCriteria searchCriteria);

	int getUserAttentionTotalCount(SearchCriteria searchCriteria);

	void saveUserAttention(UserAttention attention);

	void saveTopicPraise(TopicPraise topicPraise);

	void saveCoterieGuy(CoterieGuy coterieGuy);

	void deleteCoterieGuy(int coterieId, int userId);

	Contact getContact(int userId, int contactUserId);

	void saveContact(Contact contact);

	void deleteContact(int userId, int contactUserId);

	// WX
	WxAccessToken findWxAccessTokenById(int accessTokenId);

	void saveWxAccessToken(WxAccessToken token);

	WxOAuth2AccessToken findWxOAuth2AccessTokenByOpenId(String openid);

	void saveWxOAuth2AccessToken(WxOAuth2AccessToken token);

	WxJsApiTicket findWxJsApiTicketById(int jsapiTicketId);
	
	void saveWxJsApiTicket(WxJsApiTicket jsApiTicket);
	
	List<CoterieCarouselPicture> getCoterieCarouselPictures();

	Tag findTagByName(String tagName);
	
	void changeActivityStatus(int id, String status, String originStatus);

	int getActivityUserCount(int id);

	Event findEventById(int id);

	List<Event> getSingnUpActivitys();

	User getActivityCreator(int id);

	List<User> getActivityMembers(int activityId);

	List<SensitiveWord> loadAllSensitiveWord();

	Coterie getCoterieByActivityId(int activityId);

	void changeCoterieStatus(int coterieId, int status);
}
