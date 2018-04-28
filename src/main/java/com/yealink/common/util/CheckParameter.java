package com.yealink.common.util;

import org.springframework.http.HttpStatus;

import com.yealink.common.exception.UCException;

public class CheckParameter {
	public static void checkLimitAndOffset(Integer limit, Integer offset){
		if(limit < 0 ||  limit > 100){
			throw new UCException("INVALID_REQUEST", "uc.limit.invalid", HttpStatus.BAD_REQUEST);
		}
		if(offset < 0){
			throw new UCException("INVALID_REQUEST", "uc.offset.invalid", HttpStatus.BAD_REQUEST);
		}
	}
	
	public static String checkSql(String sql){
		sql = sql.replace("[", "\\["); // 这句话一定要在下面两个语句之前，否则作为转义符的方括号会被当作数据被再次处理 
		sql = sql.replace("_", "\\_"); 
		sql = sql.replace("%", "\\%"); 
		sql = sql.replace("^", "\\^");
		return sql; 
	}
}
