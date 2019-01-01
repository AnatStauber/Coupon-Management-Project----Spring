package com.anat.coupons.beans;

import javax.xml.bind.annotation.XmlRootElement;

public class ErrorBean {

	private int errorCode;
	private String internalMessage;
	private String ErrorMessage;

	public ErrorBean() {
		super();
	}

	public ErrorBean(int errorCode, String internalMessage, String errorMessage) {
		super();
		this.errorCode = errorCode;
		this.internalMessage = internalMessage;
		this.ErrorMessage = errorMessage;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public String getInternalMessage() {
		return internalMessage;
	}

	public void setInternalMessage(String internalMessage) {
		this.internalMessage = internalMessage;
	}

	public String getErrorMessage() {
		return ErrorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.ErrorMessage = errorMessage;
	}

	@Override
	public String toString() {
		return "ErrorBean [errorCode=" + errorCode + ", internalMessage=" + internalMessage + ", ErrorMessage="
				+ ErrorMessage + "]";
	}

}
