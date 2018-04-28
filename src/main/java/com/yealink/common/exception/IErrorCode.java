package com.yealink.common.exception;

import org.springframework.http.HttpStatus;

/**
 *
 */
public interface IErrorCode {
	
    HttpStatus getHttpStatus();

    String getCode();

    String getMessage();
}
