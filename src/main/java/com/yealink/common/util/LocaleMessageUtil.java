package com.yealink.common.util;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.yealink.common.exception.ErrorMessage;
import com.yealink.common.exception.UCException;

@Component
public class LocaleMessageUtil {

	@Autowired
	private MessageSource messageSource;

	private String getLocalErrorMessage(String errorCode) {
		Locale locale = LocaleContextHolder.getLocale();
		try {
			return messageSource.getMessage(errorCode, null, locale);
		} catch (Exception e) {
			return errorCode;
		}

	}

	public ResponseEntity<ErrorMessage> getErrorMessage(UCException error) {
		ErrorMessage errorMessage = error.getResponseEntity().getBody();
		errorMessage.setMessage(getLocalErrorMessage(errorMessage.getMessage()));
		return error.getResponseEntity();
	}

	public ResponseEntity<ErrorMessage> getErrorMessage(ErrorMessage errorMessage, HttpStatus status) {
		errorMessage.setMessage(getLocalErrorMessage(errorMessage.getMessage()));
		return new ResponseEntity<ErrorMessage>(errorMessage, status);
	}
}
