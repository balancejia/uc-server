package com.yealink.controller;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;

@RestController
public class UserDemoController {

	@RequestMapping(value = "/api/users/actions/login", method = RequestMethod.POST)
	public JSONObject login(HttpServletRequest request) {
		JSONObject result = new JSONObject();
		JSONObject data = new JSONObject();
		result.put("code",20000);
		data.put("token","admin");
		result.put("data",data);
		return result;
	} 
	@RequestMapping(value = "/api/users/actions/logout", method = RequestMethod.GET)
	public JSONObject logout(HttpServletRequest request) {
		JSONObject result = new JSONObject();
		result.put("code",20000);
		return result;
	} 
	@RequestMapping(value = "/api/users/{token}", method = RequestMethod.GET)
	public JSONObject getInfo(HttpServletRequest request,@PathVariable("token") String token) {
		JSONObject result = new JSONObject();
		JSONObject data = new JSONObject();
		result.put("code",20000);
		data.put("name","admin");
		data.put("avatar","");
		data.put("role",Arrays.asList("admin"));
		data.put("roles",Arrays.asList("admin"));
		result.put("data",data);
		return result;
	} 
	

	
}
