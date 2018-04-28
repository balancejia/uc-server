package com.yealink.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class UCException extends RuntimeException {

	private static final long serialVersionUID = -6471095491050850818L;
	ResponseEntity<ErrorMessage> responseEntity;

	public UCException(ResponseEntity<ErrorMessage> responseEntity, Throwable cause) {
		super(responseEntity.getBody().getMessage(), cause);
		this.responseEntity = responseEntity;
	}

	public UCException(ResponseEntity<ErrorMessage> responseEntity) {
		this(responseEntity, null);
	}

	public UCException(ErrorCode errorCode){
		this(errorCode.getCode(), errorCode.getMessage(), errorCode.getHttpStatus());
	}
	
	public UCException(ErrorMessage errorMessage, HttpStatus status, Throwable cause) {
		this(new ResponseEntity<>(errorMessage, status), cause);
	}

	public UCException(ErrorMessage errorMessage, HttpStatus status) {
		this(new ResponseEntity<>(errorMessage, status));
	}

	public UCException(String code, String message, String detail, HttpStatus status, Throwable cause) {
		this(new ErrorMessage(code, message, detail), status, cause);
	}

	public UCException(String code, String message, String detail, HttpStatus status) {
		this(new ErrorMessage(code, message, detail), status, null);
	}

	public UCException(String code, String message, HttpStatus status, Throwable cause) {
		this(new ErrorMessage(code, message), status, cause);
	}

	public UCException(String code, String message, HttpStatus status) {
		this(code, message, status, null);
	}

	public UCException(String code, String message, Throwable cause) {
		this(new ErrorMessage(code, message), HttpStatus.INTERNAL_SERVER_ERROR, cause);
	}

	public UCException(String code, String message) {
		this(code, message, (Throwable) null);
	}

	public ResponseEntity<ErrorMessage> getResponseEntity() {
		return responseEntity;
	}

	public ErrorMessage getError() {
		return responseEntity.getBody();
	}

	public static UCException of(IErrorCode errorCode) {
		return new UCException(errorCode.getCode(), errorCode.getMessage(), errorCode.getHttpStatus());
	}

	public static UCException of(String code, String message, HttpStatus status) {
		return new UCException(code, message, status);
	}

	public static UCException of(String code, String message, HttpStatus status, Throwable cause) {
		return new UCException(code, message, status, cause);
	}

	public static UCException of(IErrorCode errorCode,Throwable cause) {
		return new UCException(errorCode.getCode(), errorCode.getMessage(), errorCode.getHttpStatus(), cause);
	}
}
