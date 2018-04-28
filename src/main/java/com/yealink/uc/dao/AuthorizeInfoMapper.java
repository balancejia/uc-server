package com.yealink.uc.dao;

import com.yealink.uc.entity.AuthorizeInfo;
import com.yealink.uc.entity.AuthorizeInfoExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface AuthorizeInfoMapper {
    long countByExample(AuthorizeInfoExample example);

    int deleteByExample(AuthorizeInfoExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(AuthorizeInfo record);

    int insertSelective(AuthorizeInfo record);

    List<AuthorizeInfo> selectByExample(AuthorizeInfoExample example);

    AuthorizeInfo selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") AuthorizeInfo record, @Param("example") AuthorizeInfoExample example);

    int updateByExample(@Param("record") AuthorizeInfo record, @Param("example") AuthorizeInfoExample example);

    int updateByPrimaryKeySelective(AuthorizeInfo record);

    int updateByPrimaryKey(AuthorizeInfo record);
}