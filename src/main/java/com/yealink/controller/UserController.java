package com.yealink.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.yealink.common.exception.ErrorCode;
import com.yealink.common.exception.UCException;
import com.yealink.common.util.CheckParameter;
import com.yealink.common.util.EncrypUtils;
import com.yealink.common.util.GroupSourceEnum;
import com.yealink.model.PasswordForm;
import com.yealink.model.UpdateUserForm;
import com.yealink.model.UserForm;
import com.yealink.model.UserInfoModel;
import com.yealink.security.authentication.model.UserInfo;
import com.yealink.service.ICommonService;
import com.yealink.service.IUserService;
import com.yealink.uc.entity.User;
import com.yealink.uc.entity.UserExample;


@RestController
@RequestMapping("/api/v1")
public class UserController {
	
	@Autowired
	private IUserService userService;
	
	@Autowired
	private ICommonService<User,UserExample> commonService;
	

	/**
	 * 获取用户信息全量
	 * 
	 * @param request
	 * @param limit 返回条数,最大限制100条，默认10
	 * @param offset 偏移量，默认0
	 * @return 用户的JSONObject数据
	 */
	@RequestMapping(value = "/users", method = RequestMethod.GET)
	public JSONObject getAllUser(HttpServletRequest request,
			@RequestParam(value = "$limit", defaultValue = "10")Integer limit,
			@RequestParam(value = "$offset", defaultValue = "0")Integer offset){
		CheckParameter.checkLimitAndOffset(limit, offset);
		UserExample example = new UserExample();
		UserExample.Criteria criteria = example.createCriteria();
		example.setLimit(limit);
		example.setOffset(offset);

		String keyword = request.getParameter("keyword");
		String status = request.getParameter("status");
		String email = request.getParameter("email");
		UserExample.Criteria nicknameCriteria = example.createCriteria();
		if(!StringUtils.isEmpty(keyword)){
			criteria.andUsernameLike("%" + CheckParameter.checkSql(keyword) + "%");
			nicknameCriteria.andNicknameLike("%" + CheckParameter.checkSql(keyword) + "%");
		}
		if(!StringUtils.isEmpty(status)){
			if(!(status.equals("0")||status.equals("1"))){
				throw new UCException(ErrorCode.UC_USER_STATUS_INVALID);
			}
			criteria.andStatusEqualTo(status);
			nicknameCriteria.andStatusEqualTo(status);
		}
		if(!StringUtils.isEmpty(email)){
			criteria.andEmailLike(CheckParameter.checkSql(email) + "%");
			nicknameCriteria.andEmailLike(CheckParameter.checkSql(email) + "%");
		}
		example.or(nicknameCriteria);
		JSONObject result = commonService.findAllByExample(example);
		return result;
	}
	
	/**
	 * 添加用户
	 * 
	 * @param request
	 * @param userForm 添加用户表单
	 * @param bindingResult
	 * @return 添加用户的详细信息
	 */
	@RequestMapping(value = "/users", method = RequestMethod.POST)
	public UserInfoModel insertUser(HttpServletRequest request,@Valid@RequestBody UserForm userForm, BindingResult bindingResult){
		if(bindingResult.hasErrors()){
			throw new UCException("INVALID_REQUEST",bindingResult.getAllErrors().get(0).getDefaultMessage(),HttpStatus.BAD_REQUEST);
		}
		User user = new User();
		user = UserForm.toUser(userForm, user);
		commonService.insert(user);
		return selectById(user.getId());
	}
	
	/**
	 * 获取某个用户的信息
	 * 
	 * @param userId
	 * @return 该用户信息
	 */
	@RequestMapping(value = "/users/{userId}", method = RequestMethod.GET)	
	public UserInfoModel selectById(@PathVariable Long userId){
		return userService.selectByUserId(userId);
	}
	
	/**
	 * 根据用户id修改用户资料
	 * 
	 * @param userId 用户的id
	 * @param updateUserForm 用户基本资料的表单
	 * @param bindingResult
	 * @return 修改后的用户
	 */
	@RequestMapping(value = "/users/{userId}", method = RequestMethod.PUT)
	public UserInfoModel updateById(@PathVariable Long userId, @Valid@RequestBody UpdateUserForm updateUserForm, BindingResult bindingResult){
		if(bindingResult.hasErrors()){
			throw new UCException("INVALID_REQUEST",bindingResult.getAllErrors().get(0).getDefaultMessage(),HttpStatus.BAD_REQUEST);
		}

		User user = commonService.selectByPrimaryKey(userId);
		if(user.getSource().equals(GroupSourceEnum.THIRD.getSource())){
			if(StringUtils.isEmpty(updateUserForm.getStatus())){
				throw new UCException(ErrorCode.UC_UPDATE_NOT_ALLOWED);
			}else {
				updateUserForm.setAvatar(null);
				updateUserForm.setEmail(null);
				updateUserForm.setNickname(null);
			}

		}
		User record = new User();
		record = UpdateUserForm.toUser(updateUserForm, record);
		commonService.updateByExampleSelective(record, userId);
		return selectById(userId);
	}
	
	/**
	 * 根据用户id重设密码
	 * 
	 * @param userId 要修改的用户id
	 * @param userInfo 当前登录的用户的详细信息
	 * @param passwordForm 密码表单
	 * @param bindingResult
	 * @return 重设密码的用户id
	 */
	@RequestMapping(value = "/users/resetpwd/{userId}", method = RequestMethod.PUT)
	public JSONObject resetPassword(@PathVariable Long userId,@AuthenticationPrincipal UserInfo userInfo,
			@Valid@RequestBody PasswordForm passwordForm, BindingResult bindingResult){
		if(!userInfo.getUserId().equals(String.valueOf(userId))){
			throw new UCException(ErrorCode.UC_UNAUTHORIZED);
		}
		if(!userInfo.getSource().equals(GroupSourceEnum.INNER.getSource())){
			throw new UCException(ErrorCode.UC_UPDATE_NOT_ALLOWED);
		}
		if(!EncrypUtils.validPassword(passwordForm.getOldPassword(), commonService.selectByPrimaryKey(userId).getPassword())){
			throw new UCException(ErrorCode.UC_UNAUTHORIZED);
		}
		
		if(bindingResult.hasErrors()){
			throw new UCException("INVALID_REQUEST",bindingResult.getAllErrors().get(0).getDefaultMessage(),HttpStatus.BAD_REQUEST);
		}
		if(!passwordForm.getNewPassword().equals(passwordForm.getVerifyPassword())){
			throw new UCException(ErrorCode.UC_INCONSISTENT_PASSWORD);
		}
		
		userService.resetPassword(userId, passwordForm.getNewPassword());
		JSONObject result = new JSONObject();
		result.put("id", userId);
		return result;
	}
	
	/**
	 * 根据用户id删除
	 * 
	 * @param userId 用户id
	 * @return 该用户的id
	 */
	@RequestMapping(value = "/users/{userId}", method = RequestMethod.DELETE)
	public JSONObject deleteById(@PathVariable Long userId){
		commonService.deleteByPrimaryKey(userId);
		JSONObject result = new JSONObject();
		result.put("id", userId);
		return result;
	}
	
	/**
	 * 根据用户id获取用户组角色权限
	 * 
	 * @param userId 用户id
	 * @return 该用户的角色权限
	 */
	@RequestMapping(value = "/users/{userId}/role_permissions", method = RequestMethod.GET)	
	public JSONObject getRolePermission(@PathVariable Long userId){
		JSONObject result = userService.getRolePermission(userId);
		JSONObject resultGroup = userService.getUserGroup(userId);
		if(resultGroup != null){
			result.putAll(resultGroup);
		}
		return result;
	}
	
	/**
	 * 根据userId获取用户用户组
	 * @param userId
	 * @return
	 */
	@RequestMapping(value = "/users/{userId}/user_groups", method = RequestMethod.GET)
	public JSONObject getUserGroup(@PathVariable Long userId){
		return userService.getUserGroup(userId);

	}
	
}
