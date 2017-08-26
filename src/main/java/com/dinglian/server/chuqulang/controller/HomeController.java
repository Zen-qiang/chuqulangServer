package com.dinglian.server.chuqulang.controller;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.dinglian.server.chuqulang.model.User;
import com.dinglian.server.chuqulang.service.UserService;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	@RequestMapping(value = "/test", method = RequestMethod.GET)
	public String test(Locale locale, Model model) {
		logger.info("Welcome home! The client locale is {}.", locale);
		
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		
		String formattedDate = dateFormat.format(date);
		
		model.addAttribute("serverTime", formattedDate );
		
		return "home";
	}
	
	@RequestMapping("/")
	public String home(Locale locale, Model model) {
		return "index";
	}
	
	@RequestMapping("/findactivity")
	public String findactivity(Locale locale, Model model) {
		return "findactivity";
	}
	
	@RequestMapping("/index")
	public String index(Locale locale, Model model) {
		return "index";
	}
	@RequestMapping("/loginactivity")
	public String loginactivity(Locale locale, Model model) {
		return "loginactivity";
	}
	
	@RequestMapping("/activitydetails")
	public String activitydetails(Locale locale, Model model) {
		return "activitydetails";
	}
	
	@RequestMapping("/communitytalk")
	public String communitytalk(Locale locale, Model model) {
		return "communitytalk";
	}
	
	@RequestMapping("/community")
	public String community(Locale locale, Model model) {
		return "community";
	}
}
