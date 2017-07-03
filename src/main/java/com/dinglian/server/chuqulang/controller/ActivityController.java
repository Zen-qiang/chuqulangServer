package com.dinglian.server.chuqulang.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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

    @Autowired
    private ActivityService activityService;

    @Autowired
    private UserService userService;

    @Autowired
    private HttpServletRequest request;

    /**
     * 活动报名
     *
     * @param eventIdStr
     * @param userIdStr
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/signUp", method = RequestMethod.POST)
    public Map<String, Object> signUp(@RequestParam(name = "eventId") String eventIdStr, @RequestParam(name = "userId") String userIdStr) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
        	int eventId = Integer.parseInt(eventIdStr);
        	int userId = Integer.parseInt(userIdStr);
//            Subject currentUser = SecurityUtils.getSubject();
//            User user = (User) currentUser.getSession().getAttribute(User.CURRENT_USER);
            Event event = activityService.getEventById(eventId);
            User user = userService.findUserById(userId);

            if (/*currentUser.isAuthenticated() && */user != null && event != null) {
            	// 检查满员
            	Set<EventUser> eventUsers = event.getEventUsers();
            	if (eventUsers.size() == event.getUserCount()) {
            		ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_FAIL, "活动成员已满");
                    return resultMap;
				}
            	// 检查重复报名
            	boolean haveSignUp = event.haveSignUp(userId);
                if (haveSignUp){
                    ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_FAIL, "您已经报名");
                    return resultMap;
                }

                int maxOrderNo = event.getEventUserMaxOrderNo();
                EventUser eventUser = new EventUser(event, user, maxOrderNo + 1);
                activityService.saveEventUser(eventUser);

                Map<String, Object> result = new HashMap<String, Object>();
                result.put("userid", user.getId());
                result.put("nickname", user.getNickName());
                result.put("picture", user.getPicture());
                resultMap.put("result", result);
                
                ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_OK, "");
            } else {
            	ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_FAIL, "用户或活动不存在。");
            }
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
     * @param gps			活动位置
     * @param description	活动介绍
     * @param limiter		限定条件
     * @param pictures		活动封面
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/launchActivity", method = RequestMethod.POST)
    public Map<String, Object> launchActivity(@RequestParam("typename") String typeNameStr
            , @RequestParam("isOpen") boolean isOpen
            , @RequestParam("tags") int[] tags
            , @RequestParam("shortname") String shortName
            , @RequestParam(name = "retime", required = false) Date retime
            , @RequestParam("number") int userCount
            , @RequestParam("charge") String charge
            , @RequestParam(name = "cost") double cost
            , @RequestParam("gps") String gps
            , @RequestParam(name = "description", required = false) String description
            , @RequestParam(name = "limiter", required = false) String limiter
            , @RequestParam(name = "pictures", required = false) String picture
            , @RequestParam(name = "friends[]", required = false) int[] friends
            , @RequestParam(name = "phoneNo", required = false) String phoneNo) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        Subject currentUser = SecurityUtils.getSubject();
        User user = (User) currentUser.getSession().getAttribute(User.CURRENT_USER);
        try {
        	if (user != null) {
        		user = userService.findUserById(user.getId());
                Event event = new Event();
                TypeName typeName = activityService.getTypeNameByName(typeNameStr);
                event.setTypeName(typeName);
                event.setOpen(isOpen);
                
                for (int tagId : tags) {
                	Tag tag = activityService.findTagById(tagId);
                	if (tag != null) {
						EventTag eventTag = new EventTag(event, tag, 0, 0);
						event.getTags().add(eventTag);
					}
				}
                
                event.setShortName(shortName);
                event.setReTime(retime);
                event.setUserCount(userCount);
                
                event.setCharge(charge);
				event.setCost(cost);
				
                event.setGps(gps);
                event.setDescription(description);
                event.setLimiter(limiter);
                event.setCreator(user);

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

                resultMap.put("result", new HashMap<>());
                ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_OK, "");
            }
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
	public Map<String, Object> changeFavStatus(@RequestParam("eventId")String eventIdStr, @RequestParam("userId")String userIdStr, @RequestParam("isFav")String isFavStr) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try{
			int eventId = Integer.parseInt(eventIdStr);
			int userId = Integer.parseInt(userIdStr);
			boolean isFav = Boolean.parseBoolean(isFavStr);
			
			UserCollect userCollect = activityService.getUserCollectByEventAndUser(eventId, userId);
			if (isFav) {
				if (userCollect != null) {
					ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_FAIL, "您已经收藏");
					return resultMap;
				}
				
				Event event = activityService.findEventById(eventId);
				User user = userService.findUserById(userId);
				
				int orderNo = user.getUserCollectMaxOrderNo();
				userCollect = new UserCollect(event, orderNo + 1, user);
				
				activityService.saveUserCollect(userCollect);
			} else {
				if (userCollect != null) {
					activityService.deleteUserCollect(userCollect);
				}
			}
			
			ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_OK, "");
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_FAIL, e.getMessage());
		}
		return resultMap;
	}

    /*获取所有活动*/
    @ResponseBody
	@RequestMapping(value = "/getAllActivity")
	public Map<String, Object> getAllActivity() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try{
			List<Event> events = activityService.getAllActivity();
			List<Map> resultList = new ArrayList<Map>();
			for (Event event : events) {
				Map<String, Object> result = new HashMap<String, Object>();
				
				EventPicture cover = event.getCover();
				result.put("picture", cover != null ? cover.getUrl() : "");
				result.put("shortname", event.getShortName());
				result.put("rstime", event.getRsTime());
				result.put("publishtime", event.getStartTime());
				result.put("status", event.getNowStatus());
				result.put("charge", event.getCharge());
				result.put("gps", event.getGps());
				
				TypeName typeName = event.getTypeName();
				result.put("typename", typeName.getName());
				
				List<Map> tagList = new ArrayList<>();
				Set<EventTag> eventTags = event.getTags();
				for (EventTag eventTag : eventTags) {
					Tag tag = eventTag.getTag();
					Map<String, Object> tagsMap = new HashMap<String, Object>();
					tagsMap.put("tagid", tag.getId());
					tagsMap.put("tagname", tag.getName());
					tagList.add(tagsMap);
				}
				result.put("tag", tagList);
				
				Set<EventUser> eventUsers = event.getEventUsers();
				Map<String, Object> numbersMap = new HashMap<String, Object>();
				numbersMap.put("num", event.getUserCount());
				numbersMap.put("enteringNum", eventUsers.size());
				result.put("numbers", numbersMap);
				
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
     * 获取标签列表
     * @param typeName	标签类型
     * @return
     */
    @ResponseBody
	@RequestMapping(value = "/getTagList")
	public Map<String, Object> getTagList(@RequestParam("typeName")String typeName) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try{
			List<Tag> tags = activityService.getTagListByTypeName(typeName);
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
			
			ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_OK, "", resultList);
		} catch (Exception e) {
			e.printStackTrace();
			ResponseHelper.addResponseData(resultMap, RequestHelper.RESPONSE_STATUS_FAIL, e.getMessage());
		}
		return resultMap;
	}

}
