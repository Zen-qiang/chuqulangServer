package com.dinglian.server.chuqulang.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dinglian.server.chuqulang.comparator.TopicCommentComparator;
import com.dinglian.server.chuqulang.comparator.TopicPictureComparator;
import com.dinglian.server.chuqulang.comparator.TopicPraiseComparator;
import com.dinglian.server.chuqulang.model.Coterie;
import com.dinglian.server.chuqulang.model.CoterieGuy;
import com.dinglian.server.chuqulang.model.CoteriePicture;
import com.dinglian.server.chuqulang.model.Topic;
import com.dinglian.server.chuqulang.model.TopicComment;
import com.dinglian.server.chuqulang.model.TopicPicture;
import com.dinglian.server.chuqulang.model.TopicPraise;
import com.dinglian.server.chuqulang.model.User;
import com.dinglian.server.chuqulang.service.DiscoverService;
import com.dinglian.server.chuqulang.utils.DateUtils;
import com.dinglian.server.chuqulang.utils.RequestHelper;
import com.dinglian.server.chuqulang.utils.ResponseHelper;

@Controller
@RequestMapping("/discover")
public class DiscoverController {

	@Autowired
	private DiscoverService discoverService;

	/**
	 * 获取圈子列表
	 * 
	 * @param tagId 标签ID
	 * @param type 排序类型
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/getRingList", method = RequestMethod.POST)
	public Map<String, Object> getCoterieList(@RequestParam(name = "tagid", required = false) String tagIdStr,
			@RequestParam("type") String type) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			int tagId = 0;
			if (StringUtils.isNotBlank(tagIdStr)) {
				tagId = Integer.parseInt(tagIdStr);
			}
			List<Coterie> coterieList = discoverService.getCoterieList(tagId, type);

			List<Map> resultList = new ArrayList<Map>();
			Map<String, Object> result = null;
			for (Coterie coterie : coterieList) {
				result = new HashMap<String, Object>();
				CoteriePicture coteriePicture = coterie.getCoteriePicture();
				Set<CoterieGuy> coterieGuys = coterie.getCoterieGuys();

				result.put("ringId", coterie.getId());
				result.put("picture", coteriePicture != null ? coteriePicture.getUrl() : "");
				result.put("shortname", coterie.getName());
				result.put("description", coterie.getDescription());
				result.put("fllowers", coterieGuys.size());
				resultList.add(result);
			}
			ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_OK, "", resultList);
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_FAIL, e.getMessage());
		}

		return resultMap;
	}

	/**
	 * 获取话题列表
	 * 
	 * @param coterieIdStr 圈子ID
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/getDiscoverList", method = RequestMethod.POST)
	public Map<String, Object> getTopicList(@RequestParam(name = "ringId", required = false) String coterieIdStr) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			int coterieId = 0;
			if (StringUtils.isNotBlank(coterieIdStr)) {
				coterieId = Integer.parseInt(coterieIdStr);
			}

			List<Coterie> coterieList = new ArrayList<Coterie>();
			if (coterieId == 0) {
				// 获取所有圈子里的话题
				coterieList = discoverService.getCoterieList(0, Coterie.TYPE_HOT);
			} else {
				// 获取指定圈子里的话题
				Coterie coterie = discoverService.findCoterieById(coterieId);
				if (coterie != null) {
					coterieList.add(coterie);
				}
			}

			List<Map> resultList = new ArrayList<Map>();
			Map<String, Object> result = null;
			for (Coterie coterie : coterieList) {
				result = new HashMap<String, Object>();
				CoteriePicture coteriePicture = coterie.getCoteriePicture();
				Set<CoterieGuy> coterieGuys = coterie.getCoterieGuys();

				result.put("picture", coteriePicture != null ? coteriePicture.getUrl() : "");
				result.put("shortname", coterie.getName());
				result.put("description", coterie.getDescription());
				result.put("fllowers", coterieGuys.size());

				Set<Topic> topics = coterie.getTopics();
				List<Map> discovers = new ArrayList<Map>();

				Map<String, Object> discoverMap = null;
				Map<String, Object> userMap = null;
				Map<String, Object> topicMap = null;
				for (Topic topic : topics) {
					discoverMap = new HashMap<String, Object>();

					User creator = topic.getCreator();
					userMap = new HashMap<String, Object>();
					if (creator != null) {
						userMap.put("nickname", creator.getNickName());
						userMap.put("picture", creator.getPicture());
					}
					System.out.println(creator);

					topicMap = new HashMap<String, Object>();
					topicMap.put("discoverId", topic.getId());
					topicMap.put("description", topic.getDescription());

					List<TopicPicture> topicPictures = new ArrayList<TopicPicture>(topic.getPictures());
					Collections.sort(topicPictures, new Comparator<TopicPicture>() {
						@Override
						public int compare(TopicPicture o1, TopicPicture o2) {
							return o1.getOrderNo() - o2.getOrderNo();
						}
					});

					List<String> pictures = new ArrayList<String>();
					for (TopicPicture topicPicture : topicPictures) {
						pictures.add(topicPicture.getUrl());
					}

					topicMap.put("pictures", pictures);
					topicMap.put("publishTime", DateUtils.format(topic.getCreationDate(), DateUtils.yMdHms));

					discoverMap.put("user", userMap);
					discoverMap.put("discover", topicMap);
					discoverMap.put("commentNum", topic.getComments().size());
					discoverMap.put("getFavNum", topic.getPraises().size());
					discovers.add(discoverMap);
				}
				result.put("discovers", discovers);

				resultList.add(result);

			}
			ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_OK, "", resultList);
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_FAIL, e.getMessage());
		}

		return resultMap;
	}

	/**
	 * 发布话题
	 * @param coterieIdStr 圈子ID
	 * @param img 图片路径
	 * @param description 话题描述
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/editDiscover", method = RequestMethod.POST)
	public Map<String, Object> releaseTopic(@RequestParam(name = "ringId") String coterieIdStr,
			@RequestParam(name = "img", required = false) String img,
			@RequestParam(name = "description", required = false) String description) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			Subject currentUser = SecurityUtils.getSubject();
			User user = (User) currentUser.getSession().getAttribute(User.CURRENT_USER);

			// 当前话题属于哪个圈子
			int coterieId = Integer.parseInt(coterieIdStr);
			Coterie coterie = discoverService.findCoterieById(coterieId);
			
			Topic topic = new Topic(coterie, user, description);
			
			// 话题应该可以上传多个图片, 后期需要修改图片传参类型及保存本地图片
			TopicPicture topicPicture = new TopicPicture(topic, user, img, 1);

			topic.getPictures().add(topicPicture);
			
			discoverService.saveTopic(topic);

			ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_OK, "", new HashMap());
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_FAIL, e.getMessage());
		}

		return resultMap;
	}
	
	/**
	 * 评论话题
	 * @param topicIdStr	话题ID
	 * @param userIdStr		评论人ID
	 * @param comment		评论
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/commentDiscover", method = RequestMethod.POST)
	public Map<String, Object> commentTopic(@RequestParam(name = "discoverid") String topicIdStr,
			/*@RequestParam(name = "userid") String userIdStr,*/
			@RequestParam(name = "comment") String content) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			Subject currentUser = SecurityUtils.getSubject();
			User user = (User) currentUser.getSession().getAttribute(User.CURRENT_USER);

			// 当前话题属于哪个圈子
			int topicId = Integer.parseInt(topicIdStr);
			Topic topic = discoverService.findTopicById(topicId);
			if (topic != null) {
				TopicComment topicComment = new TopicComment(topic, user, content);
				topic.getComments().add(topicComment);
				discoverService.saveTopic(topic);
			}

			ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_OK, "", new HashMap());
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_FAIL, e.getMessage());
		}

		return resultMap;
	}

	@ResponseBody
	@RequestMapping(value = "/getDiscoverComments", method = RequestMethod.POST)
	public Map<String, Object> getTopicComments(@RequestParam(name = "discoverid") String topicIdStr) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			Subject currentUser = SecurityUtils.getSubject();
			User user = (User) currentUser.getSession().getAttribute(User.CURRENT_USER);

			int topicId = Integer.parseInt(topicIdStr);
			Topic topic = discoverService.findTopicById(topicId);
			Map<String, Object> result = new HashMap<String, Object>();
			if (topic != null) {
				Map<String, Object> discover = new HashMap<String, Object>();
				discover.put("discoverId", topic.getId());
				discover.put("description", topic.getDescription());
				discover.put("publishTime", DateUtils.format(topic.getCreationDate(), DateUtils.yMdHms));
				discover.put("commentNum", topic.getComments().size());
				discover.put("getFavNum", topic.getPraises().size());
				
				// 图片
				List<TopicPicture> topicPictures = new ArrayList<TopicPicture>(topic.getPictures());
				Collections.sort(topicPictures, new TopicPictureComparator());
				
				List<String> pictures = new ArrayList<String>();
				for (TopicPicture topicPicture : topicPictures) {
					pictures.add(topicPicture.getUrl());
				}
				discover.put("pictures", pictures);
				
				// 评论
				List<TopicComment> topicComments = new ArrayList<TopicComment>(topic.getComments());
				Collections.sort(topicComments, new TopicCommentComparator());
				
				List<Map> comments = new ArrayList<Map>();
				Map<String, Object> comment = null;
				for (TopicComment topicComment : topicComments) {
					comment = new HashMap<String, Object>();
					User commentUser = topicComment.getUser();
					Map<String, Object> userMap = new HashMap<String, Object>();
					if (commentUser != null) {
						userMap.put("userid", commentUser.getId());
						userMap.put("nickname", commentUser.getNickName());
						userMap.put("picture", commentUser.getPicture());
					}
					comment.put("user", userMap);
					comment.put("comment", topicComment.getContent());
					comment.put("publishTime", DateUtils.format(topicComment.getCreationDate(), DateUtils.yMdHms));
					comments.add(comment);
				}
				
				// 点赞
				List<TopicPraise> topicPraises = new ArrayList<TopicPraise>(topic.getPraises());
				Collections.sort(topicPraises, new TopicPraiseComparator());
				List<Map> praises = new ArrayList<Map>();
				Map<String, Object> praise = null;
				for (TopicPraise topicPraise : topicPraises) {
					praise = new HashMap<String, Object>();
					User praiseUser = topicPraise.getUser();
					Map<String, Object> userMap = new HashMap<String, Object>();
					if (praiseUser != null) {
						userMap.put("userid", praiseUser.getId());
						userMap.put("nickname", praiseUser.getNickName());
						userMap.put("picture", praiseUser.getPicture());
					}
					praise.put("user", userMap);
					praises.add(praise);
				}
				
				result.put("discover", discover);
				result.put("comments", comments);
				result.put("favs", praises);
			}

			ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_OK, "", result);
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_FAIL, e.getMessage());
		}

		return resultMap;
	}
	
}
