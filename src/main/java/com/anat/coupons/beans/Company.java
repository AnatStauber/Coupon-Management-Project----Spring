package com.anat.coupons.beans;

public class Company {

	private long companyId;
	private String companyName;
	private String companyPassword;
	private String companyEmail;

	public Company() {
		super();
	}

	public Company(String companyName, String companyPassword, String companyEmail) {
		super();
		this.companyName = companyName;
		this.companyPassword = companyPassword;
		this.companyEmail = companyEmail;
	}

	public Company(long companiId, String companyName, String companyPassword, String companyEmail) {
		super();
		this.companyId = companiId;
		this.companyName = companyName;
		this.companyPassword = companyPassword;
		this.companyEmail = companyEmail;
	}

	public long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(long companiId) {
		this.companyId = companiId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getCompanyPassword() {
		return companyPassword;
	}

	public void setCompanyPassword(String companyPassword) {
		this.companyPassword = companyPassword;
	}

	public String getCompanyEmail() {
		return companyEmail;
	}

	public void setCompanyEmail(String companyEmail) {
		this.companyEmail = companyEmail;
	}

	@Override
	public String toString() {
		return "\n Company [companyId=" + companyId + ", companyName=" + companyName + ", companyPassword="
				+ companyPassword + ", companyEmail=" + companyEmail + "]";
	}

}
