package com.yealink.uc.dao;

import com.yealink.uc.entity.UserGroupPermission;
import com.yealink.uc.entity.UserGroupPermissionExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface UserGroupPermissionMapper {
    long countByExample(UserGroupPermissionExample example);

    int deleteByExample(UserGroupPermissionExample example);

    int deleteByPrimaryKey(Long id);

    int insert(UserGroupPermission record);

    int insertSelective(UserGroupPermission record);

    List<UserGroupPermission> selectByExample(UserGroupPermissionExample example);

    UserGroupPermission selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") UserGroupPermission record, @Param("example") UserGroupPermissionExample example);

    int updateByExample(@Param("record") UserGroupPermission record, @Param("example") UserGroupPermissionExample example);

    int updateByPrimaryKeySelective(UserGroupPermission record);

    int updateByPrimaryKey(UserGroupPermission record);
}