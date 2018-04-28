package com.yealink.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.yealink.common.exception.ErrorCode;
import com.yealink.common.exception.UCException;
import com.yealink.common.util.GroupSourceEnum;
import com.yealink.model.GroupModel;
import com.yealink.service.IGroupService;
import com.yealink.uc.dao.UserGroupMapper;
import com.yealink.uc.dao.UserGroupPermissionMapper;
import com.yealink.uc.entity.UserGroup;
import com.yealink.uc.entity.UserGroupExample;
import com.yealink.uc.entity.UserGroupPermissionExample;


@Service
public class GroupServiceImpl extends CommonServiceImpl<UserGroup, UserGroupExample> implements IGroupService{
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private UserGroupMapper userGroupMapper;

	@Autowired
	private UserGroupPermissionMapper userGroupPermissionMapper;

	@Override
	public JSONObject findAllByExample(UserGroupExample example) {
		List<UserGroup> userGroups = selectByExample(example);
	/*	if(userGroups == null){
			aDServiceImpl.getAllGroupFromAD();
			userGroups = userGroupMapper.selectByExample(example);
		}*/
		Long total = countByExample(example);
		List<GroupModel> groupModels = new ArrayList<>();
		for(UserGroup userGroup : userGroups){
			groupModels.add(new GroupModel(userGroup.getId(), userGroup.getRealm(), userGroup.getCode(), userGroup.getName(), userGroup.getSource()));
		}
		JSONObject result = new JSONObject();
		result.put("total", total);
		result.put("items", groupModels);
		return result;
	}

	@Override
	public int insert(UserGroup record) {
		record.setSource(GroupSourceEnum.INNER.getSource());
		try {
			return userGroupMapper.insertSelective(record);
		} catch (DuplicateKeyException e) {
			throw new UCException(ErrorCode.AD_CODE_EXISTED);
		}
		
	}

	@Override
	public List<UserGroup> selectByExample(UserGroupExample example) {
		return userGroupMapper.selectByExample(example);
	}

	@Override
	public long countByExample(UserGroupExample example) {
		return userGroupMapper.countByExample(example);
	}

	@Override
	public UserGroup selectByPrimaryKey(Long id) {
		UserGroup userGroup = userGroupMapper.selectByPrimaryKey(id);
		if(userGroup != null){
			return userGroup;
		}
		throw new UCException(ErrorCode.AD_GROUP_NOT_EXIST);
	}

	@Override
	public int updateByExampleSelective(UserGroup record, Long id) {
		record.setSource(null);
		UserGroupExample example = new UserGroupExample();
		example.createCriteria().andIdEqualTo(id);
		try {
			return userGroupMapper.updateByExampleSelective(record, example);
		} catch (DuplicateKeyException e) {
			throw new UCException(ErrorCode.AD_CODE_EXISTED);
		}
		
	}

	@Override
	public int deleteByPrimaryKey(Long id) {
		int num = userGroupMapper.deleteByPrimaryKey(id);
		UserGroupPermissionExample userGroupPermissionExample = new UserGroupPermissionExample();
		userGroupPermissionExample.createCriteria().andGroupIdEqualTo(id);
		userGroupPermissionMapper.deleteByExample(userGroupPermissionExample);
		return num;
	}
	 
}
