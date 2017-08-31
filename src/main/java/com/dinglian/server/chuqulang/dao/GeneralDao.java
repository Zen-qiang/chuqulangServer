package com.dinglian.server.chuqulang.dao;

import java.util.List;

import com.dinglian.server.chuqulang.base.SearchCriteria;
import com.dinglian.server.chuqulang.model.Contact;
import com.dinglian.server.chuqulang.model.CoterieCarouselPicture;
import com.dinglian.server.chuqulang.model.CoterieGuy;
import com.dinglian.server.chuqulang.model.Tag;
import com.dinglian.server.chuqulang.model.TopicPraise;
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

	List<CoterieCarouselPicture> getCoterieCarouselPictures();

	Tag findTagByName(String tagName);

	WxJsApiTicket findWxJsApiTicketById(int jsapiTicketId);

	void saveWxJsApiTicket(WxJsApiTicket jsApiTicket);

}
