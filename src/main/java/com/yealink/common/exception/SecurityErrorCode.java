package com.yealink.common.exception;

import org.springframework.http.HttpStatus;


/**
 * security 相关的异常信息
 *
 */
public enum SecurityErrorCode implements IErrorCode {
	
	DEBUG_ENABLED(HttpStatus.UNAUTHORIZED, "DEBUG_ENABLED", "debug.enabled"),
    UC_REALM_EMPTY(HttpStatus.UNAUTHORIZED, "UC_REALM_EMPTY", "uc.realm_empty"),
    USER_NOT_EXIST(HttpStatus.NOT_FOUND, "USER_NOT_EXIST", "user.not.exist"),
    USER_NOT_LOGIN(HttpStatus.UNAUTHORIZED, "USER_NOT_LOGIN", "user.not.login"),
    AUTH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "AUTH_TOKEN_EXPIRED", "auth.token.expired"),
    AUTH_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "AUTH_TOKEN_INVALID", "auth.token.invalid"),
    MAC_SIGN_INVALID(HttpStatus.UNAUTHORIZED, "MAC_SIGN_INVALID", "auth.mac.sign.invalid"),
    NONCE_INVALID(HttpStatus.UNAUTHORIZED, "NONCE_INVALID", "auth.nonce.invalid"),
	FORBIDDEN(HttpStatus.FORBIDDEN, "FORBIDDEN", "auth.forbidden"),
	SAF_ER_AUTHORIZED_EXCEPTION(HttpStatus.FORBIDDEN, "SAF_ER_AUTHORIZED_EXCEPTION", "saf.er.authorized.exception");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
    SecurityErrorCode(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }

    public String getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }
}
