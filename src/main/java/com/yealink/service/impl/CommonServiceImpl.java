package com.yealink.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.yealink.service.ICommonService;




@Service
public abstract class CommonServiceImpl<T,E> implements ICommonService<T,E>{
	
	public abstract JSONObject findAllByExample(E example);
	public abstract int insert (T record);
    public abstract List<T> selectByExample(E example);
    public abstract long countByExample(E example);
    public abstract T selectByPrimaryKey(Long id);
    public abstract int updateByExampleSelective(T record, Long id);
    public abstract int deleteByPrimaryKey(Long id);

}
