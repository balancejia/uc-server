package com.yealink.controller;

import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.yealink.common.exception.ErrorCode;
import com.yealink.common.exception.ErrorMessage;
import com.yealink.common.exception.SecurityErrorCode;
import com.yealink.common.exception.UCException;
import com.yealink.common.util.LocaleMessageUtil;

/**
 * 全局错误异常处理
 * 
 * @author yl1454
 *
 */
@RestController
public class RestErrorController implements ErrorController {

	private static final String PATH = "/error";

	@Autowired
	private ErrorAttributes errorAttributes;

	@Autowired
	LocaleMessageUtil localeMessageUtil;

	@RequestMapping(value = PATH, produces = { MediaType.APPLICATION_JSON_VALUE })
	ResponseEntity<ErrorMessage> error(HttpServletRequest request, HttpServletResponse response) {

		// timestamp,error,exception,message,trace,path
		Map<String, Object> body = getErrorAttributes(request, true);
		HttpStatus status = getStatus(request);
		ErrorMessage errorMessage = new ErrorMessage();
		errorMessage.setServerTime(new Date());
		errorMessage.setCode("INTERNAL_SERVER_ERROR");
		errorMessage.setDetail(body.get("trace").toString());
		errorMessage.setMessage(body.get("message").toString());
		Throwable error = getError(request);
		if (error instanceof UCException) {
			// 自定义异常处理
			return localeMessageUtil.getErrorMessage((UCException) error);
		} else if (error instanceof AccessDeniedException || error instanceof IllegalArgumentException
				|| error instanceof AuthenticationCredentialsNotFoundException) {
			return localeMessageUtil.getErrorMessage(UCException.of(SecurityErrorCode.FORBIDDEN));
		} else if (error instanceof BadSqlGrammarException) {
			return localeMessageUtil.getErrorMessage(new UCException(ErrorCode.DATABASE_ERROR));
		}
		return localeMessageUtil.getErrorMessage(errorMessage,status);
	}

	protected HttpStatus getStatus(HttpServletRequest request) {
		Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
		if (statusCode == null) {
			return HttpStatus.INTERNAL_SERVER_ERROR;
		}
		try {
			return HttpStatus.valueOf(statusCode);
		} catch (Exception ex) {
			return HttpStatus.INTERNAL_SERVER_ERROR;
		}
	}

	@Override
	public String getErrorPath() {
		return PATH;
	}

	private Throwable getError(HttpServletRequest request) {
		RequestAttributes requestAttributes = new ServletRequestAttributes(request);
		return errorAttributes.getError(requestAttributes);
	}

	private Map<String, Object> getErrorAttributes(HttpServletRequest request, boolean includeStackTrace) {
		RequestAttributes requestAttributes = new ServletRequestAttributes(request);
		return errorAttributes.getErrorAttributes(requestAttributes, includeStackTrace);
	}
}