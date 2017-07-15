package com.dinglian.server.chuqulang.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dinglian.server.chuqulang.base.ApplicationConfig;
import com.dinglian.server.chuqulang.base.SearchCriteria;
import com.dinglian.server.chuqulang.comparator.TopicCommentComparator;
import com.dinglian.server.chuqulang.comparator.TopicPictureComparator;
import com.dinglian.server.chuqulang.comparator.TopicPraiseComparator;
import com.dinglian.server.chuqulang.model.Coterie;
import com.dinglian.server.chuqulang.model.CoterieGuy;
import com.dinglian.server.chuqulang.model.CoteriePicture;
import com.dinglian.server.chuqulang.model.Tag;
import com.dinglian.server.chuqulang.model.Topic;
import com.dinglian.server.chuqulang.model.TopicComment;
import com.dinglian.server.chuqulang.model.TopicPicture;
import com.dinglian.server.chuqulang.model.TopicPraise;
import com.dinglian.server.chuqulang.model.User;
import com.dinglian.server.chuqulang.service.DiscoverService;
import com.dinglian.server.chuqulang.service.UserService;
import com.dinglian.server.chuqulang.utils.DateUtils;
import com.dinglian.server.chuqulang.utils.RequestHelper;
import com.dinglian.server.chuqulang.utils.ResponseHelper;

@Controller
@RequestMapping("/discover")
public class DiscoverController {
	
	private static final Logger logger = LoggerFactory.getLogger(DiscoverController.class);

	@Autowired
	private DiscoverService discoverService;
	
	@Autowired
    private UserService userService;

	/**
	 * 获取圈子列表
	 * @param typeName	圈子类型
	 * @param tagId		标签ID
	 * @param startRow	开始行
     * @param pageSize	每页记录
     * @param orderBy	排序
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/getCoterieList", method = RequestMethod.POST)
	public Map<String, Object> getCoterieList(
			@RequestParam(name = "typename", required = false) String typeName, 
			@RequestParam(name = "tagid", required = false) Integer tagId,
			@RequestParam(name = "pagesize", required = false) Integer pageSize,
			@RequestParam(name = "start", required = false) Integer startRow,
			@RequestParam(name = "orderby", required = false) String orderBy) {
		Map<String, Object> responseMap = new HashMap<String, Object>();
		try {
			logger.info("=====> Start to get coterie list <=====");
			if (startRow == null) {
				startRow = 0;
			}
			if (pageSize == null) {
				pageSize = ApplicationConfig.getInstance().getDefaultPageSize();
			}
			SearchCriteria searchCriteria = new SearchCriteria();
			searchCriteria.setTypeName(typeName);
			searchCriteria.setTagId(tagId);
			searchCriteria.setStartRow(startRow);
			searchCriteria.setPageSize(pageSize);
			searchCriteria.setOrderBy(orderBy);
			
			Map<String, Object> coterieMap = discoverService.getCoterieList(searchCriteria);
			List<Coterie> coteries = (List<Coterie>) coterieMap.get("resultList");
			int total = (int) coterieMap.get("totalCount");

			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("total", total);
			resultMap.put("start", startRow);
			List<Map> resultList = new ArrayList<Map>();
			if (coteries != null) {
				for (Coterie coterie : coteries) {
					Map<String, Object> result = new HashMap<String, Object>();
					CoteriePicture coteriePicture = coterie.getCoteriePicture();
					Set<CoterieGuy> coterieGuys = coterie.getCoterieGuys();
					
					result.put("coterieId", coterie.getId());
					result.put("picture", coteriePicture != null ? coteriePicture.getUrl() : "");
					result.put("name", coterie.getName());
					result.put("description", coterie.getDescription());
					result.put("fllowers", coterieGuys.size());
					resultList.add(result);
				}
			}
			resultMap.put("cnt", resultList.size());
			resultMap.put("lists", resultList);
			logger.info("=====> Get coterie list end <=====");
			
			ResponseHelper.addResponseData(responseMap, RequestHelper.RESPONSE_STATUS_OK, "", resultMap);
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.addResponseData(responseMap, RequestHelper.RESPONSE_STATUS_FAIL, e.getMessage());
		}

		return responseMap;
	}

	/**
	 * 获取话题列表
	 * @param coterieId	圈子ID
	 * @param pageSize	
	 * @param startRow
	 * @param orderBy
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/getTopicList", method = RequestMethod.POST)
	public Map<String, Object> getTopicList(@RequestParam("coterieId") int coterieId,
			@RequestParam(name = "pagesize", required = false) Integer pageSize,
			@RequestParam(name = "start", required = false) Integer startRow,
			@RequestParam(name = "orderby", required = false) String orderBy) {
		Map<String, Object> responseMap = new HashMap<String, Object>();
		try {
			logger.info("=====> Start to get topic list <=====");
			
			if (startRow == null) {
				startRow = 0;
			}
			if (pageSize == null) {
				pageSize = ApplicationConfig.getInstance().getDefaultPageSize();
			}
			SearchCriteria searchCriteria = new SearchCriteria();
			searchCriteria.setCoterieId(coterieId);
			searchCriteria.setStartRow(startRow);
			searchCriteria.setPageSize(pageSize);
			searchCriteria.setOrderBy(orderBy);
			
			Map<String, Object> topicResultMap = discoverService.getTopicList(searchCriteria);
			List<Topic> topics = (List<Topic>) topicResultMap.get("resultList");
			int total = (int) topicResultMap.get("totalCount");
			
			Coterie coterie = discoverService.findCoterieById(coterieId);
			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("total", total);
			resultMap.put("start", startRow);
			
			// 圈子信息
			resultMap.put("coterieName", coterie.getName());
			resultMap.put("pictures", coterie.getCoteriePicture() != null ? coterie.getCoteriePicture().getUrl() : "");
			resultMap.put("description", coterie.getDescription());
			resultMap.put("fllowers", coterie.getCoterieGuys().size());
			

			List<Map> resultList = new ArrayList<Map>();
			if (topics != null) {
				for (Topic topic : topics) {
					Map<String, Object> result = new HashMap<String, Object>();
					
					Map<String, Object> userMap = new HashMap<String, Object>();
					User creator = topic.getCreator();
					if (creator != null) {
						userMap.put("nickname", creator.getNickName());
						userMap.put("picture", creator.getPicture());
					}
					result.put("user", userMap);
					
					Map<String, Object> topicMap = new HashMap<String, Object>();
					topicMap.put("topicId", topic.getId());
					topicMap.put("description", topic.getDescription());

					List<TopicPicture> topicPictures = new ArrayList<TopicPicture>(topic.getPictures());
					Collections.sort(topicPictures, new TopicPictureComparator());

					List<String> pictures = new ArrayList<String>();
					for (TopicPicture topicPicture : topicPictures) {
						pictures.add(topicPicture.getUrl());
					}

					topicMap.put("pictures", pictures);
					topicMap.put("releaseTime", DateUtils.format(topic.getCreationDate(), DateUtils.yMdHms));
					
					topicMap.put("commentsCount", topic.getComments().size());
					topicMap.put("praisesCount", topic.getPraises().size());
					
					result.put("topics", topicMap);
					
					resultList.add(result);
				}
			}
			
			resultMap.put("cnt", resultList.size());
			resultMap.put("lists", resultList);
			
			logger.info("=====> Get topic list end <=====");
			
			ResponseHelper.addResponseData(responseMap, RequestHelper.RESPONSE_STATUS_OK, "", resultMap);
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.addResponseData(responseMap, RequestHelper.RESPONSE_STATUS_FAIL, e.getMessage());
		}

		return responseMap;
	}

	/**
	 * 发布话题
	 * @param coterieIdStr	圈子ID
	 * @param img 			图片路径
	 * @param description	话题描述
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/editDiscover", method = RequestMethod.POST)
	public Map<String, Object> releaseTopic(@RequestParam(name = "ringId") String coterieIdStr,
			@RequestParam(name = "img", required = false) String img,
			@RequestParam(name = "description", required = false) String description) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			logger.info("=====> Start to release topic <=====");
			
			Subject currentUser = SecurityUtils.getSubject();
			User user = (User) currentUser.getSession().getAttribute(User.CURRENT_USER);
			
//			int userId = user.getId();
//			user = userService.findUserById(userId);
//			if (user == null) {
//				throw new NullPointerException("用户ID：" + userId + " , 用户不存在。");
//			}

			// 当前话题属于哪个圈子
			int coterieId = Integer.parseInt(coterieIdStr);
			Coterie coterie = discoverService.findCoterieById(coterieId);
			
			Topic topic = new Topic(coterie, user, description);
			
			// 话题应该可以上传多个图片, 后期需要修改图片传参类型及保存本地图片
			TopicPicture topicPicture = new TopicPicture(topic, user, img, 1);
			topic.getPictures().add(topicPicture);
			
			discoverService.saveTopic(topic);

			logger.info("=====> Release topic end <=====");
			ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_OK, "");
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
			@RequestParam(name = "comment") String content) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			logger.info("=====> Start to comment topic <=====");
			
			Subject currentUser = SecurityUtils.getSubject();
			User user = (User) currentUser.getSession().getAttribute(User.CURRENT_USER);
			
//			int userId = user.getId();
//			user = userService.findUserById(userId);
//			if (user == null) {
//				throw new NullPointerException("用户ID：" + userId + " , 用户不存在。");
//			}
			
			// 当前话题属于哪个圈子
			int topicId = Integer.parseInt(topicIdStr);
			Topic topic = discoverService.findTopicById(topicId);
			if (topic == null) {
				throw new NullPointerException("话题ID：" + topicId + " , 话题不存在。");
			}
			
			TopicComment topicComment = new TopicComment(topic, user, content);
			topic.getComments().add(topicComment);
			discoverService.saveTopic(topic);

			logger.info("=====> Comment topic end <=====");
			ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_OK, "");
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_FAIL, e.getMessage());
		}

		return resultMap;
	}

	/**
	 * 获取话题评论
	 * @param topicIdStr	话题ID
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/getDiscoverComments", method = RequestMethod.POST)
	public Map<String, Object> getTopicComments(@RequestParam(name = "discoverid") String topicIdStr) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			logger.info("=====> Start to get topic comments <=====");
			
			int topicId = Integer.parseInt(topicIdStr);
			Topic topic = discoverService.findTopicById(topicId);
			if (topic == null) {
				throw new NullPointerException("话题ID：" + topicId + " , 话题不存在。");
			}
			
			Map<String, Object> result = new HashMap<String, Object>();
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
				
			logger.info("=====> Get topic comments end <=====");
			ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_OK, "", result);
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_FAIL, e.getMessage());
		}

		return resultMap;
	}
	
	/**
	 * 搜索圈子/话题
	 * @param keyword	关键字
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/searchActivityOrTopic")
	public Map<String, Object> searchActivityOrTopic(@RequestParam("keyword")String keyword) {
		Map<String, Object> responseMap = new HashMap<String, Object>();
		try{
			logger.info("=====> Start to search activity or topic <=====");
			
			Map<String, Object> resultMap = discoverService.searchActivityOrTopic(keyword);
			List<Coterie> coteries = (List<Coterie>) resultMap.get("coterieList");
			List<Topic> topics = (List<Topic>) resultMap.get("topicList");
			
			Map<String, Object> result = new HashMap<String, Object>();
			if (coteries != null) {
				List<Map> coterieList = new ArrayList<Map>();
				for (Coterie coterie : coteries) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("id", coterie.getId());
					map.put("name", coterie.getName());
					map.put("description", coterie.getDescription());
					map.put("hot", coterie.getHot());
					Tag tag = coterie.getTag();
					if (tag != null) {
						map.put("tagName", tag.getName());
					}
					coterieList.add(map);
				}
				result.put("coteries", coterieList);
				result.put("coterieCnt", coterieList.size());
			}
			if (topics != null) {
				List<Map> topicList = new ArrayList<Map>();
				for (Topic topic : topics) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("id", topic.getId());
					map.put("description", topic.getDescription());
					topicList.add(map);
				}
				result.put("topics", topicList);
				result.put("topicCnt", topicList.size());
			}
			
			logger.info("=====> Search activity or topic type end <=====");
			
			ResponseHelper.addResponseData(responseMap, RequestHelper.RESPONSE_STATUS_OK, "", result);
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.addResponseData(responseMap, RequestHelper.RESPONSE_STATUS_FAIL, e.getMessage());
		}
		return responseMap;
	}
	
	@ResponseBody
	@RequestMapping(value = "/praiseTopic")
	public Map<String, Object> praiseTopic(@RequestParam("topicId")int topicId) {
		Map<String, Object> responseMap = new HashMap<String, Object>();
		try{
			logger.info("=====> Start to praise topic <=====");
			Subject currentUser = SecurityUtils.getSubject();
			User user = (User) currentUser.getSession().getAttribute(User.CURRENT_USER);
			
//			int userId = user.getId();
//			user = userService.findUserById(userId);
//			if (user == null) {
//				throw new NullPointerException("用户信息不存在，请重新登录。");
//			}
			
			Topic topic = discoverService.findTopicById(topicId);
			if (topic == null) {
				throw new NullPointerException("话题ID：" + topicId + " , 话题不存在。");
			}
			
			int nextOrderNo = topic.getNextOrderNo();
			discoverService.saveTopicPraise(new TopicPraise(topic, user, nextOrderNo));
			
			logger.info("=====> Praise topic type end <=====");
			ResponseHelper.addResponseData(responseMap, RequestHelper.RESPONSE_STATUS_OK, "");
		} catch (ConstraintViolationException e) {
			ResponseHelper.addResponseData(responseMap, RequestHelper.RESPONSE_STATUS_FAIL, "不能重复点赞");
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.addResponseData(responseMap, RequestHelper.RESPONSE_STATUS_FAIL, e.getMessage());
		}
		return responseMap;
	}
	
}
