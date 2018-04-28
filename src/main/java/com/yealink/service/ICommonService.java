package com.yealink.service;

import java.util.List;

import com.alibaba.fastjson.JSONObject;


public interface ICommonService<T,E> {
	
	JSONObject findAllByExample(E example);
	int insert (T record);
    List<T> selectByExample(E example);
    long countByExample(E example);
    T selectByPrimaryKey(Long id);
    int updateByExampleSelective(T record, Long id);
    int deleteByPrimaryKey(Long id);
}
