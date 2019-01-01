package com.anat.coupons.beans;

import com.anat.coupons.enums.CouponType;

public class Coupon {

	private long couponId;
	private String couponTitle;
	private String couponStartDate;
	private String couponEndDate;
	private int couponAmount;
	private CouponType type; 
	private String couponMessage;
	private double couponPrice;
	private String couponImage;
	private long comapnyId;
	private String couponStatus;

	// constructors
	public Coupon() {
		super();
	}

	public Coupon(String couponTitle, String couponStartDate, String couponEndDate, int couponAmount,
			String couponMessage, double couponPrice, String couponImage, CouponType type, long companyId) {

		super();

		this.couponTitle = couponTitle;
		this.couponStartDate = couponStartDate;
		this.couponEndDate = couponEndDate;
		this.couponAmount = couponAmount;
		this.couponMessage = couponMessage;
		this.couponPrice = couponPrice;
		this.couponImage = couponImage;
		this.type = type;
		this.comapnyId = companyId;
	}

	// getters&setters
	public long getCouponId() {
		return couponId;
	}

	public void setCouponId(long couponId) {
		this.couponId = couponId;
	}

	public String getCouponTitle() {
		return couponTitle;
	}

	public void setCouponTitle(String couponTitle) {
		this.couponTitle = couponTitle;
	}

	public String getCouponStartDate() {
		return couponStartDate;
	}

	public void setCouponStartDate(String couponStartDate) {
		this.couponStartDate = couponStartDate;
	}

	public String getCouponEndDate() {
		return couponEndDate;
	}

	public void setCouponEndDate(String couponEndDate) {
		this.couponEndDate = couponEndDate;
	}

	public int getCouponAmount() {
		return couponAmount;
	}

	public void setCouponAmount(int couponAmount) {
		this.couponAmount = couponAmount;
	}

	public String getCouponMessage() {
		return couponMessage;
	}

	public void setCouponMessage(String couponMessage) {
		this.couponMessage = couponMessage;
	}

	public double getCouponPrice() {
		return couponPrice;
	}

	public void setCouponPrice(double couponPrice) {
		this.couponPrice = couponPrice;
	}

	public String getCouponImage() {
		return couponImage;
	}

	public void setCouponImage(String couponImage) {
		this.couponImage = couponImage;
	}

	public CouponType getType() {
		return this.type;
	}

	public void setType(CouponType type) {
		this.type = type;
	}

	public long getComapnyId() {
		return comapnyId;
	}

	public void setComapnyId(long comapnyId) {
		this.comapnyId = comapnyId;
	}

//	public long getCustomerId() {
//		return customerId;
//	}
//
//
//	public void setCustomerId(long customerId) {
//		this.customerId = customerId;
//	}

	public String getCouponStatus() {
		return couponStatus;
	}

	public void setCouponStatus(String couponStatus) {
		this.couponStatus = couponStatus;
	}

	@Override
	public String toString() {
		return "Coupon [couponId=" + couponId + ", couponTitle=" + couponTitle + ", couponStartDate=" + couponStartDate
				+ ", couponEndDate=" + couponEndDate + ", couponAmount=" + couponAmount + ", type=" + type
				+ ", couponMessage=" + couponMessage + ", couponPrice=" + couponPrice + ", couponImage=" + couponImage
				+ ", comapnyId=" + comapnyId + ", couponStatus=" + couponStatus + "]\n";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + couponAmount;
		result = prime * result + ((couponEndDate == null) ? 0 : couponEndDate.hashCode());
		result = prime * result + (int) (couponId ^ (couponId >>> 32));
		result = prime * result + ((couponImage == null) ? 0 : couponImage.hashCode());
		result = prime * result + ((couponMessage == null) ? 0 : couponMessage.hashCode());
		long temp;
		temp = Double.doubleToLongBits(couponPrice);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((couponStartDate == null) ? 0 : couponStartDate.hashCode());
		result = prime * result + ((couponStatus == null) ? 0 : couponStatus.hashCode());
		result = prime * result + ((couponTitle == null) ? 0 : couponTitle.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Coupon other = (Coupon) obj;
		if (couponAmount != other.couponAmount)
			return false;
		if (couponEndDate == null) {
			if (other.couponEndDate != null)
				return false;
		} else if (!couponEndDate.equals(other.couponEndDate))
			return false;
		if (couponId != other.couponId)
			return false;
		if (couponImage == null) {
			if (other.couponImage != null)
				return false;
		} else if (!couponImage.equals(other.couponImage))
			return false;
		if (couponMessage == null) {
			if (other.couponMessage != null)
				return false;
		} else if (!couponMessage.equals(other.couponMessage))
			return false;
		if (Double.doubleToLongBits(couponPrice) != Double.doubleToLongBits(other.couponPrice))
			return false;
		if (couponStartDate == null) {
			if (other.couponStartDate != null)
				return false;
		} else if (!couponStartDate.equals(other.couponStartDate))
			return false;
		if (couponStatus == null) {
			if (other.couponStatus != null)
				return false;
		} else if (!couponStatus.equals(other.couponStatus))
			return false;
		if (couponTitle == null) {
			if (other.couponTitle != null)
				return false;
		} else if (!couponTitle.equals(other.couponTitle))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

}