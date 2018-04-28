package com.yealink.common.exception;

import org.springframework.http.HttpStatus;

/**
 */
public enum ErrorCode implements IErrorCode {

	// 租户和用户相关
	MISSING_ORG_ID(HttpStatus.BAD_REQUEST, "MISSING_ORG_ID", "error.code.missing.org.id"), VO_ACCESS_NG(
			HttpStatus.BAD_REQUEST, "VO_ACCESS_NG", "error.code.vo.access.ng"), NO_PERMISSION(HttpStatus.FORBIDDEN,
					"NO_PERMISSION", "error.code.no.permission"),

	// 请求相关
	INVALID_ARGUMENT(HttpStatus.BAD_REQUEST, "INVALID_ARGUMENT", "error.code.invalid.argument"), INVALID_QUERY(
			HttpStatus.BAD_REQUEST, "INVALID_QUERY", "error.code.invalid.query"),
	PARAMETER_ERROR(HttpStatus.BAD_REQUEST,"PARAMETER_ERROR","erroe.code.parameter.error"),

	// 没有数据
	DATA_NOT_FOUND(HttpStatus.NOT_FOUND, "DATA_NOT_FOUND", "error.code.data.not.found"),

	// 配置相关
	CONFIG_LOADING_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "CONFIG_LOADING_FAIL",
			"error.code.config.loading.fail"), CONFIG_MISSING(HttpStatus.INTERNAL_SERVER_ERROR, "CONFIG_MISSING",
					"error.code.config.missing"), CONFIG_MISSING_ITEM(HttpStatus.INTERNAL_SERVER_ERROR,
							"CONFIG_MISSING_ITEM", "error.code.config.missing.item"),

	// 内容服务相关
	CS_SESSION_NG(HttpStatus.BAD_REQUEST, "CS_SESSION_NG", "error.code.cs.session.ng"), CS_DISABLE(HttpStatus.NOT_FOUND,
			"CS_DISABLE", "error.code.cs.disable"),

	// 程序错误
	FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "FAIL", "error.code.fail"),
	
	//数据库相关
	DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"DATABASE_ERROR", "error.database"),
	
	//token相关
	UC_TOKEN_REFRESHTOKEN_FAIL(HttpStatus.FORBIDDEN,"REFRESHTOKEN_FAIL","uc.token.refreshToken.fail"),
	
	//uc用户相关
	UC_UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"UNAUTHORIZED","uc.token.unauthorized"),
	UC_USERNAME_INVALID(HttpStatus.BAD_REQUEST,"INVALID_REQUEST","uc.user.username.invalid"),
	UC_PASSWORD_INVALID(HttpStatus.BAD_REQUEST,"INVALID_REQUEST","uc.user.password.invalid"),
	UC_NICKNAME_INVALID(HttpStatus.BAD_REQUEST,"INVALID_REQUEST","uc.user.nickname.invalid"),
	UC_EMAIL_INVALID(HttpStatus.BAD_REQUEST,"INVALID_REQUEST","uc.user.email.invalid"),
	UC_USERNAME_EXISTED(HttpStatus.BAD_REQUEST,"USERNAME_EXISTED","uc.user.username.existed"),
	UC_USERNAME_NOT_EXIST(HttpStatus.BAD_REQUEST,"USERNAME_NOT_EXIST","uc.user.username.not.exist"),
	UC_USER_STATUS_INVALID(HttpStatus.BAD_REQUEST,"USER_STATUS_INVALID","uc.user.status.invalid"),
	UC_USER_ROLE_NOT_EXIST(HttpStatus.BAD_REQUEST,"USER_ROLE_NOT_EXIT","uc.user.role.not.exist"),
	UC_INCONSISTENT_PASSWORD(HttpStatus.BAD_REQUEST,"INCONSISTENT_PASSWORD","uc.inconsistent.password"),
	UC_UPDATE_NOT_ALLOWED(HttpStatus.BAD_REQUEST,"UPDATE_NOT_ALLOWED","uc.update.are.not.allowed"),
	UC_USER_DISABLE(HttpStatus.UNAUTHORIZED,"UC_USER_DISABLE","uc.user.status.disable"),
	
	//uc角色相关
	UC_ROLENAME_INVALID(HttpStatus.BAD_REQUEST,"INVALID_REQUEST","uc.role.name.invalid"),
	UC_ROLECODE_INVALID(HttpStatus.BAD_REQUEST,"INVALID_REQUEST","uc.role.code.invalid"),
	UC_ROLE_EXISTED(HttpStatus.BAD_REQUEST,"ROLE_EXISTED","uc.role.code.existed"),
	UC_ROLE_NOT_EXIST(HttpStatus.BAD_REQUEST,"ROLE_NOT_EXIST","uc.role.code.not.exist"),
	UC_ROLE_PERMISSION_NOT_EXIST(HttpStatus.BAD_REQUEST,"ROLE_PERMISSION_NOT_EXIST","uc.role.permission.not.exist"),
	
	//uc权限相关
	UC_PERMISSIONNAME_INVALID(HttpStatus.BAD_REQUEST,"INVALID_REQUEST","uc.permission.name.invalid"),
	UC_PERMSSIONCODE_INVALID(HttpStatus.BAD_REQUEST,"INVALID_REQUEST","uc.permission.code.invalid"),
	UC_PERMISSION_EXISTED(HttpStatus.BAD_REQUEST,"PERMISSION_EXISTED","uc.permission.code.existed"),
	UC_PERMISSION_NOT_EXIST(HttpStatus.BAD_REQUEST,"PERMISSION_NOT_EXIST","uc.permission.code.not.exist"),

	UC_EXPRESS_INVALID(HttpStatus.BAD_REQUEST,"EXPRESS_INVALID","uc.express.invalid"),
	UC_ROLE_PERMISSION_INVALID(HttpStatus.BAD_REQUEST,"INVALID_REQUEST","uc.role.permission.code.invalid"), 
	
	//AD相关
	AD_NOT_EXIST(HttpStatus.BAD_REQUEST, "AD_NOT_EXIST","active.directory.not.exist"),
	AD_GROUP_NOT_EXIST(HttpStatus.BAD_REQUEST, "AD_GROUP_NOT_EXIST", "active.directory.usergroup.not.exist"),
	AD_GROUP_PERMISSION_NOT_EXIST(HttpStatus.BAD_REQUEST, "AD_GROUP_PERMISSION_NOT_EXIST", "active.directory.usergroup.permission.not.exist"),
	AD_CODE_EXISTED(HttpStatus.BAD_REQUEST,"AD_CODE_EXISTED","active.directory.code.existed"),
	AD_SYNC_ERROR(HttpStatus.BAD_REQUEST,"AD_SYNC_ERROR","active.directory.sync.error"),
	AD_SEARCH_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"AD_SEARCH_ERROR","active.directory.search.error");
	

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;

	ErrorCode(HttpStatus httpStatus, String code, String message) {
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