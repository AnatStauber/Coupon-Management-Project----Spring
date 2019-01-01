package com.anat.coupons.enums;

public enum ErrorType{
	
	GENERAL_ERROR(800), INVALID_LOGIN(810), INVALID_PARAMETER(820), MISSING_PARAMETERS(830), NULL_VALUE(831),
	DATABASE_ERROR(840), SESSION_TIMEOUT(850), SERVER_NOT_RESPONDING(880), SYSTEM_ERROR(870), PAGE_NOT_FOUND(860),
	COUPON_TITLE_ALREADY_EXISTS_FOR_COMPANY(8770), ACTION_CANNOT_BE_COMPLETED(881), EMPTY_LIST(802);

	private int errorCode;

	private ErrorType(int errorCode) {
		this.errorCode = errorCode;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public String getErrorDefinition() {
		return this.name();
	}

	public static ErrorType fromString (final String s) {
		return ErrorType.valueOf(s);
	}
}