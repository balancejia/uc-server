package com.yealink.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.yealink.service.IADService;

@RestController
@RequestMapping("/api/v1")
public class ADSyncController {

	@Autowired
	private IADService aDService;
	
	@RequestMapping(value = "/user_groups/actions/adSync", method = RequestMethod.GET)
	public void syncUserGroups(HttpServletRequest request){
		aDService.getAllGroupFromAD();
	
	}
	
	@RequestMapping(value = "/users/actions/adSync", method = RequestMethod.GET)
	public void syncUsers(HttpServletRequest request){
		aDService.getAllUserFromAD();
	
	}
	
}
