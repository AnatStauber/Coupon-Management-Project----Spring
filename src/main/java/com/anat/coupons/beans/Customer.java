package com.anat.coupons.beans;

import javax.xml.bind.annotation.XmlRootElement;

public class Customer {

	private long customerId;
	private String customerName;
	private String customerPassword;

	public Customer() {
	}

	public Customer(String customername, String customerPassword) {
		super();
		this.customerName = customername;
		this.customerPassword = customerPassword;
	}

	public long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customername) {
		this.customerName = customername;
	}

	public String getCustomerPassword() {
		return customerPassword;
	}

	public void setCustomerPassword(String customerPassword) {
		this.customerPassword = customerPassword;
	}

	@Override
	public String toString() {
		return "\n Customer [customerId=" + customerId + ", customerName=" + customerName + ", customerPassword="
				+ customerPassword + "]";
	}

}
