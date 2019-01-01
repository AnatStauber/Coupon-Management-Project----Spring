package com.anat.coupons.beans;

import javax.xml.bind.annotation.XmlRootElement;

public class UserLoginDetails {

	private String userType;
	private String userName;
	private String password;
	
	public UserLoginDetails () {}
	
	public UserLoginDetails(String userType, String userName, String password) {
		super();
		this.userType = userType;
		this.userName = userName;
		this.password = password;
	}

//	public UserLoginDetails( String userName, String password) {
//		super();
//		this.userName = userName;
//		this.password = password;
//	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "UserLoginDetails [userType=" + userType + ", userName=" + userName + ", password=" + password + "]";
	}
	
	
	
	
	
	
}
