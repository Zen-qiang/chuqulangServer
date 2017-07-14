package com.dinglian.server.chuqulang.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
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
import com.dinglian.server.chuqulang.comparator.EventUserComparator;
import com.dinglian.server.chuqulang.model.ChatRoom;
import com.dinglian.server.chuqulang.model.Event;
import com.dinglian.server.chuqulang.model.EventPicture;
import com.dinglian.server.chuqulang.model.EventTag;
import com.dinglian.server.chuqulang.model.EventUser;
import com.dinglian.server.chuqulang.model.Tag;
import com.dinglian.server.chuqulang.model.TypeName;
import com.dinglian.server.chuqulang.model.User;
import com.dinglian.server.chuqulang.model.UserCollect;
import com.dinglian.server.chuqulang.service.ActivityService;
import com.dinglian.server.chuqulang.service.UserService;
import com.dinglian.server.chuqulang.utils.RequestHelper;
import com.dinglian.server.chuqulang.utils.ResponseHelper;

@Controller
@RequestMapping("/activity")
public class ActivityController {
	
	private static final Logger logger = LoggerFactory.getLogger(ActivityController.class);

    @Autowired
    private ActivityService activityService;

    @Autowired
    private UserService userService;

    @Autowired
    private HttpServletRequest request;

    /**
     * 活动报名
     * @param eventIdStr	活动ID
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/signUp", method = RequestMethod.POST)
    public Map<String, Object> signUp(@RequestParam(name = "eventId") String eventIdStr) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
        	logger.info("=====> Start to event signup <=====");
        	
            Subject currentUser = SecurityUtils.getSubject();
            User user = (User) currentUser.getSession().getAttribute(User.CURRENT_USER);
            int userId = user.getId();
            int eventId = Integer.parseInt(eventIdStr);

            user = userService.findUserById(userId);
            if (user == null) {
				throw new NullPointerException("用户ID：" + userId + " , 用户不存在。");
			}
            Event event = activityService.getEventById(eventId);
            if (event == null) {
            	throw new NullPointerException("活动ID：" + eventId + " , 活动不存在。");
			}
            // 检查满员
        	Set<EventUser> eventUsers = event.getEventUsers();
        	if (eventUsers.size() == event.getUserCount()) {
        		ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_FAIL, "活动成员已满");
                return resultMap;
			}
        	// 检查重复报名
        	boolean haveSignUp = event.haveSignUp(user.getId());
            if (haveSignUp){
                ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_FAIL, "您已经报名");
                return resultMap;
            }

            int maxOrderNo = event.getEventUserMaxOrderNo();
            EventUser eventUser = new EventUser(event, user, maxOrderNo + 1);
            activityService.saveEventUser(eventUser);
            
            activityService.refresh(event);
            
            List<Map> resultList = new ArrayList<Map>();
            for (EventUser eu : event.getEventUsers()) {
            	if (eu.getUser() != null) {
            		Map<String, Object> result = new HashMap<String, Object>();
            		result.put("userid", eu.getUser().getId());
            		result.put("nickname", eu.getUser().getNickName());
            		result.put("picture", eu.getUser().getPicture());
            		resultList.add(result);
				}
			}
            
            logger.info("=====> Event signup end <=====");
            ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_OK, "", resultList);
        } catch (Exception e) {
            e.printStackTrace();
            ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_FAIL, e.getMessage());
        }

        return resultMap;
    }

    /**
     * 发起活动
     * @param typeNameStr	活动类型
     * @param isOpen		是否公开
     * @param tags			标签
     * @param shortName		活动简称
     * @param retime		活动开始时间
     * @param userCount		活动人数	
     * @param charge		费用类型
     * @param cost			费用费用
     * @param gps			活动位置
     * @param description	活动介绍
     * @param limiter		限定条件
     * @param pictures		活动封面
     * @param friends		邀请好友
     * @param phoneNo		联系方式
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/launchActivity", method = RequestMethod.POST)
    public Map<String, Object> launchActivity(@RequestParam("typename") String typeNameStr
            , @RequestParam("isOpen") boolean isOpen
            , @RequestParam(name = "password",required = false) String password
            , @RequestParam("tags") int[] tags
            , @RequestParam("name") String name
            , @RequestParam("startTime") long retime
            , @RequestParam("userCount") int userCount
            , @RequestParam("charge") String charge
            , @RequestParam("cost") double cost
            , @RequestParam("gps") String gps
            , @RequestParam("address") String address
            , @RequestParam(name = "description", required = false) String description
            , @RequestParam(name = "limiter", required = false) String limiter
            , @RequestParam(name = "pictures", required = false) String picture
            , @RequestParam(name = "friends", required = false) int[] friends
            , @RequestParam(name = "phoneNo", required = false) String phoneNo) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
        	logger.info("=====> Start to launch activity <=====");
        	
        	Subject currentUser = SecurityUtils.getSubject();
        	User user = (User) currentUser.getSession().getAttribute(User.CURRENT_USER);
        	
        	int userId = user.getId();
			user = userService.findUserById(userId);
			if (user == null) {
				throw new NullPointerException("用户ID：" + userId + " , 用户不存在。");
			}
        	
            Event event = new Event();
            TypeName typeName = activityService.getTypeNameByName(typeNameStr);
            event.setTypeName(typeName);
            event.setOpen(isOpen);
            if (isOpen) {
				event.setPassword(password);
			}
            
            int i = 1;
            for (int tagId : tags) {
            	Tag tag = activityService.findTagById(tagId);
            	if (tag != null) {
					EventTag eventTag = new EventTag(event, tag, 1, i);
					event.getTags().add(eventTag);
				}
            	i++;
			}
            
            event.setName(name);
            
            Date startTime = new Date(retime);
            event.setStartTime(startTime);
            event.setUserCount(userCount);
            event.setCharge(charge);
			event.setCost(cost);
            event.setGps(gps);
            event.setAddress(address);
            event.setDescription(description);
            event.setLimiter(limiter);
            event.setCreator(user);
            event.setCreationDate(new Date());
            event.setStatus(Event.STATUS_SIGNUP);

            EventPicture eventPicture = new EventPicture(event, picture, 1, user);
            event.getEventPictures().add(eventPicture);
            
            event.getEventUsers().add(new EventUser(event, user, 1));
            if (friends != null) {
            	int orderNo = 2;
            	for (int id : friends) {
            		User friend = userService.findUserById(id);
            		if (friend != null) {
            			event.getEventUsers().add(new EventUser(event, friend, orderNo));
            			orderNo++;
            		}
				}
			}
            
            activityService.saveEvent(event);
            
            Map<String, Object> result = new HashMap<String, Object>();
            EventPicture cover = event.getCover();
			result.put("eventId", event.getId());
			result.put("picture", cover != null ? cover.getUrl() : "");
			result.put("name", event.getName());
			result.put("releaseTime", event.getCreationDate());
			result.put("startTime", event.getStartTime());
			result.put("status", event.getStatus());
			result.put("charge", event.getCharge());
			result.put("cost", event.getCost());
			result.put("gps", event.getGps());
			result.put("address", event.getAddress());
			result.put("isOpen", event.isOpen());
			result.put("typename", typeName != null ? typeName.getName() : "");
			
			List<Map> tagList = new ArrayList<>();
			Set<EventTag> eventTags = event.getTags();
			for (EventTag eventTag : eventTags) {
				Tag tag = eventTag.getTag();
				Map<String, Object> tagsMap = new HashMap<String, Object>();
				tagsMap.put("tagId", tag.getId());
				tagsMap.put("tagName", tag.getName());
				tagList.add(tagsMap);
			}
			result.put("tags", tagList);
			
			Map<String, Object> numbersMap = new HashMap<String, Object>();
			numbersMap.put("num", event.getUserCount());
			numbersMap.put("enteringNum", event.getEventUsers().size());
			result.put("numbers", numbersMap);
            
            logger.info("=====> Launch activity end <=====");
            ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_OK, "", result);
        } catch (Exception e) {
            e.printStackTrace();
            ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_FAIL, e.getMessage());
        }
        return resultMap;
    }

    /**
     * 收藏/取消收藏活动
     * @param eventIdStr
     * @param userIdStr
     * @param isFavStr
     * @return
     */
    @ResponseBody
	@RequestMapping(value = "/changeFavStatus", method = RequestMethod.POST)
	public Map<String, Object> changeCollectStatus(@RequestParam("eventId")String eventIdStr, @RequestParam("isFav")String isFavStr) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try{
			logger.info("=====> Start to change collect status <=====");
			
			Subject currentUser = SecurityUtils.getSubject();
        	User user = (User) currentUser.getSession().getAttribute(User.CURRENT_USER);
        	
			int userId = user.getId();
			user = userService.findUserById(userId);
			if (user == null) {
				throw new NullPointerException("用户ID：" + userId + " , 用户不存在。");
			}
			
			int eventId = Integer.parseInt(eventIdStr);
			Event event = activityService.findEventById(eventId);
			if (event == null) {
            	throw new NullPointerException("活动ID：" + eventId + " , 活动不存在。");
			}
			
			boolean needCollect = Boolean.parseBoolean(isFavStr);
			
			UserCollect userCollect = activityService.getUserCollectByEventAndUser(eventId, userId);
			if (needCollect && userCollect != null) {
				ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_FAIL, "您已经收藏");
				return resultMap;
			}
			if (needCollect) {
				int orderNo = user.getUserCollectMaxOrderNo();
				userCollect = new UserCollect(event, orderNo + 1, user);
				
				activityService.saveUserCollect(userCollect);
			} else {
				if (userCollect != null) {
					activityService.deleteUserCollect(userCollect);
				}
			}
			logger.info("=====> Change collect status end <=====");
			ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_OK, "");
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_FAIL, e.getMessage());
		}
		return resultMap;
	}
    
    /**
     * 获取我的活动列表 (category、status不传值获取全部，暂未加入距离排序)
     * @param orderBy	排序
     * @param category	分类
     * @param status	状态
     * @param keyword	关键字
     * @param startRow	开始行
     * @param pageSize	每页记录
     * @param isOwnList	是否个人列表
     * @return
     */
	@ResponseBody
	@RequestMapping(value = "/getActivityList", method = RequestMethod.POST)
	public Map<String, Object> getActivityList(
			@RequestParam(name = "orderby", required = false) String orderBy, 
			@RequestParam(name = "category", required = false) Integer category, 
			@RequestParam(name = "status", required = false) String status,
			@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(name = "start", required = false) Integer startRow,
			@RequestParam(name = "pagesize", required = false) Integer pageSize,
			@RequestParam(name = "isOwnList", required = false) Boolean isOwnList) {
		Map<String, Object> responseMap = new HashMap<String, Object>();
		try {
			logger.info("=====> Start to get activity list <=====");
			
			Subject currentUser = SecurityUtils.getSubject();
			User user = (User) currentUser.getSession().getAttribute(User.CURRENT_USER);
			
			int userId = user.getId();
			user = userService.findUserById(userId);
			if (user == null) {
				throw new NullPointerException("用户ID：" + userId + " , 用户不存在。");
			}
			
			// default value
			if (startRow == null) {
				startRow = 0;
			}
			if (pageSize == null) {
				pageSize = ApplicationConfig.getInstance().getDefaultPageSize();
			}
			if (isOwnList == null) {
				isOwnList = false;
			}
			
			SearchCriteria searchCriteria = new SearchCriteria();
			searchCriteria.setOrderBy(orderBy);
			searchCriteria.setCategory(category);
			searchCriteria.setStatus(status);
			searchCriteria.setStartRow(startRow);
			searchCriteria.setPageSize(pageSize);
			searchCriteria.setOwnList(isOwnList);
			searchCriteria.setKeyword(keyword);
			if (isOwnList) {
				searchCriteria.setUserId(userId);
			}
			Map<String, Object> eventListMap = activityService.getActivityList(searchCriteria);
			List<Event> events = (List<Event>) eventListMap.get("resultList");
			int total = (int) eventListMap.get("totalCount");
			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("total", total);
			resultMap.put("start", startRow);
			
			List<Map> resultList = new ArrayList<Map>();
			if (events != null) {
				for (Event event : events) {
//					System.out.println(event.getId());
					Map<String, Object> result = new HashMap<String, Object>();
					
					EventPicture cover = event.getCover();
					result.put("eventId", event.getId());
					result.put("picture", cover != null ? cover.getUrl() : "");
					result.put("name", event.getName());
					result.put("releaseTime", event.getCreationDate());
					result.put("startTime", event.getStartTime());
					
					// 判断好友参与
					boolean friendJoin = activityService.checkFriendJoin(event.getId(), userId);
					result.put("status", friendJoin ? Event.STATUS_FRIENDS : event.getStatus());
					
					result.put("charge", event.getCharge());
					result.put("cost", event.getCost());
					result.put("gps", event.getGps());
					result.put("address", event.getAddress());
					result.put("isOpen", event.isOpen());
					
					TypeName typeName = event.getTypeName();
					result.put("typename", typeName != null ? typeName.getName() : "");
					
					List<Map> tagList = new ArrayList<>();
					Set<EventTag> eventTags = event.getTags();
					for (EventTag eventTag : eventTags) {
						Tag tag = eventTag.getTag();
						Map<String, Object> tagsMap = new HashMap<String, Object>();
						tagsMap.put("tagId", tag.getId());
						tagsMap.put("tagName", tag.getName());
						tagList.add(tagsMap);
					}
					result.put("tags", tagList);
					
					Map<String, Object> numbersMap = new HashMap<String, Object>();
					numbersMap.put("num", event.getUserCount());
					numbersMap.put("enteringNum", event.getEventUsers().size());
					result.put("numbers", numbersMap);
					
					resultList.add(result);
				}
			}
			
			if (StringUtils.isNotBlank(status) && status.equals(Event.STATUS_FRIENDS)) {
				List<Map> finalList = new ArrayList<Map>();
				for (int i = 0; i < resultList.size(); i++) {
					Map result = resultList.get(i);
					if (result.get("status").equals(Event.STATUS_FRIENDS)) {
						finalList.add(result);
					}
				}
				resultList = finalList;
			}
			
			resultMap.put("cnt", resultList.size());
			resultMap.put("lists", resultList);
			
			logger.info("=====> Get my activity list end <=====");
			
			ResponseHelper.addResponseData(responseMap, RequestHelper.RESPONSE_STATUS_OK, "", resultMap);
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.addResponseData(responseMap, RequestHelper.RESPONSE_STATUS_FAIL, e.getMessage());
		}

		return responseMap;
	}
	
	/**
	 * 获取活动详情
	 * @param eventId	活动ID
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/getActivityInfo", method = RequestMethod.POST)
	public Map<String, Object> getActivityInfo(@RequestParam(name = "eventId") String eventId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			logger.info("=====> Start to get activity info <=====");
			
			int id = Integer.parseInt(eventId);
			Event event = activityService.findEventById(id);
			Map<String, Object> result = new HashMap<String, Object>();
			
			EventPicture cover = event.getCover();
			result.put("picture", cover != null ? cover.getUrl() : "");
			result.put("name", event.getName());
			
			ChatRoom chatRoom = event.getChatRoom();
			result.put("chatroomId", chatRoom != null ? chatRoom.getId() : "");

			result.put("status", event.getStatus());
			
			User organizer = event.getCreator();
			if (organizer != null) {
				Map<String, Object> organizerMap = new HashMap<String, Object>();
				organizerMap.put("organizerId", organizer.getId());
				organizerMap.put("organizerNickName", organizer.getNickName());
				organizerMap.put("organizerPicture", organizer.getPicture());
				result.put("organizer", organizerMap);
			}
			
			TypeName typeName = event.getTypeName();
			result.put("typename", typeName!= null ? typeName.getName() : "");
			
			result.put("startTime", event.getStartTime());
			result.put("gps", event.getGps());
			result.put("address", event.getAddress());
			result.put("isOpen", event.isOpen());
			result.put("charge", event.getCharge());
			result.put("cost", event.getCost());
			result.put("description", event.getDescription());
			
			List<Map> tagList = new ArrayList<>();
			Set<EventTag> eventTags = event.getTags();
			for (EventTag eventTag : eventTags) {
				Tag tag = eventTag.getTag();
				if (tag != null) {
					Map<String, Object> tagsMap = new HashMap<String, Object>();
					tagsMap.put("tagId", tag.getId());
					tagsMap.put("tagName", tag.getName());
					tagList.add(tagsMap);
				}
			}
			result.put("tags", tagList);
			
			// 参与活动人员
			List<EventUser> eventUsers = new ArrayList<EventUser>(event.getEventUsers());
			Collections.sort(eventUsers, new EventUserComparator());
			
			List<Map> eventUserList = new ArrayList<>();
			for (EventUser eventUser : eventUsers) {
				if (eventUser.getUser() != null) {
					Map<String, Object> userMap = new HashMap<String, Object>();
					userMap.put("userId", eventUser.getUser().getId());
					userMap.put("nickName", eventUser.getUser().getNickName());
					userMap.put("picture", eventUser.getUser().getPicture());
					eventUserList.add(userMap);
				}
			}
			result.put("eventUserList", eventUserList);
			
			Map<String, Object> numbersMap = new HashMap<String, Object>();
			numbersMap.put("num", event.getUserCount());
			numbersMap.put("enteringNum", eventUsers.size());
			result.put("numbers", numbersMap);
			
			logger.info("=====> Get activity info end <=====");
			ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_OK, "", result);
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_FAIL, e.getMessage());
		}

		return resultMap;
	}
    
    /**
     * 获取标签列表
     * @param typeName	标签类型
     * @return
     */
    @ResponseBody
	@RequestMapping(value = "/getTagList")
	public Map<String, Object> getTagList(@RequestParam(name = "typeNameId", required = false) Integer typeNameId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try{
			logger.info("=====> Start to get tag list <=====");
			
			List<Tag> tags = activityService.getTagListByTypeNameId(typeNameId);
			List<Map> resultList = new ArrayList<Map>();
			Map<String, Object> map = null;
			if (tags != null) {
				for (Tag tag : tags) {
					map = new HashMap<String, Object>();
					map.put("tagId", tag.getId());
					map.put("tagName", tag.getName());
					map.put("tagTimes", tag.getTimes());
					resultList.add(map);
				}
			}
			
			logger.info("=====> Get tag list end <=====");
			
			ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_OK, "", resultList);
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_FAIL, e.getMessage());
		}
		return resultMap;
	}
    
    /**
     * 获取活动类型,根据description字段获取所有typeName
     * @param type	description
     * @return
     */
    @ResponseBody
	@RequestMapping(value = "/getActivityType")
	public Map<String, Object> getActivityType(@RequestParam("type")String type) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try{
			logger.info("=====> Start to get activity type <=====");
			
			List<TypeName> activityTypes = activityService.getActivityTypes(type);
			List<Map> resultList = new ArrayList<Map>();
			Map<String, Object> map = null;
			if (activityTypes != null) {
				for (TypeName typeName : activityTypes) {
					map = new HashMap<String, Object>();
					map.put("typeNameId", typeName.getId());
					map.put("typeName", typeName.getName());
					map.put("typeNameDescription", typeName.getDescription());
					resultList.add(map);
				}
			}
			
			logger.info("=====> Get activity type end <=====");
			
			ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_OK, "", resultList);
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_FAIL, e.getMessage());
		}
		return resultMap;
	}
    
}
