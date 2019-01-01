package com.anat.coupons.beans;

import javax.xml.bind.annotation.XmlRootElement;

public class Purchase {

	private long couponId;
	private long customerId;
	private int amount;

	public Purchase() {
	}

	public Purchase(long couponId, long customerId, int amount) {
		super();
		this.couponId = couponId;
		this.customerId = customerId;
		this.amount = amount;
	}

	public long getCouponId() {
		return couponId;
	}

	public void setCouponId(long couponId) {
		this.couponId = couponId;
	}

	public long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	@Override
	public String toString() {
		return "Purchase [couponId=" + couponId + ", customerId=" + customerId + ", amount=" + amount + "]";
	}

}
