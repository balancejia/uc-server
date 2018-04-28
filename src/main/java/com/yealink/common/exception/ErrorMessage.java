package com.yealink.common.exception;

import java.io.Serializable;
import java.util.Date;

/**
 * 错误信息封装类
 *
 */
public class ErrorMessage implements Serializable {
	private static final long serialVersionUID = -5401402542472113075L;

	private String code;
	private String message;
	private String detail;
	private Date serverTime=new Date();

	public ErrorMessage() {
	}

	public ErrorMessage(String code) {
		this(code, null, null);
	}

	public ErrorMessage(String code, String message) {
		this(code, message, null);
	}
	public ErrorMessage(String code, String message, String detail) {
		this.message = message;
		this.code = code;
		this.detail = detail;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public Date getServerTime() {
		return serverTime;
	}

	public void setServerTime(Date serverTime) {
		this.serverTime = serverTime;
	}
	
	
}
