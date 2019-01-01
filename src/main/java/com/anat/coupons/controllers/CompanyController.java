package com.anat.coupons.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.anat.coupons.beans.Company;
import com.anat.coupons.dao.CompanyDao;
import com.anat.coupons.enums.ErrorType;
import com.anat.coupons.exceptions.ApplicationException;
import com.anat.coupons.utils.DateUtils;
import com.anat.coupons.utils.LoginValidationUtils;

@Controller
public class CompanyController {

	@Autowired
	private CompanyDao companyDao;

	public CompanyController() {
		this.companyDao = new CompanyDao();
	}

	// 1. calling the create company validation
	public long createCompany(Company company) throws ApplicationException {

		validateCreateCompany(company);

		return this.companyDao.createCompany(company);

	}

	// 2. calling the deleteCompany method
	public void deleteCompany(long companyId) throws ApplicationException {

		validateCompanyId(companyId);

		// if valid - (no exceptions are thrown) - activate the delete method
		this.companyDao.deleteCompany(companyId);
	}

	// 3.calling the updateCompanysPassword method:
	public void updateCompany(Company company) throws ApplicationException {

		validateUpdateCompany(company);

		this.companyDao.updateCompany(company);

	}

	// 4. calling the getCompanyByCompanyId method
	public Company getCompany(long companyId) throws ApplicationException {

		validateGetCompanyByCompanyId(companyId);

		return this.companyDao.getCompanyByCompanyId(companyId);

	}

	// 5. calling the getAllCompanies method:
	public List<Company> getAllCompanies() throws ApplicationException {

		validateGetAllCompanies();

		// If we didn't catch any exception, we call the 'getAllCompanies' method.
		return this.companyDao.getAllCompanies();
	}

	// 6. calling the login method
	public Long login(String companyName, String companyPassword) throws ApplicationException {

		validateLogin(companyName, companyPassword);

		return this.companyDao.checkLogin(companyName, companyPassword);

	}

//--------------------------------------------------------------------validations-------------------------------------------------------------------------

	// #1 validating the createCompany method
	private void validateCreateCompany(Company company) throws ApplicationException {

		validateCompanyName(company.getCompanyName());

		validateCompanyDetails(company);
	}

	// #3 validating the updateCompany method
	private void validateUpdateCompany(Company company) throws ApplicationException {

		// validating the company ID
		long companyId = company.getCompanyId();
		validateCompanyId(companyId);

		// validating password & email:
		validateCompanyDetails(company);

		// extracting the company befor the update
		Company oldCompany = this.companyDao.getCompanyByCompanyId(companyId);

		// extracting the old companys name and the new company sane
		String OldName = oldCompany.getCompanyName();
		String newName = company.getCompanyName();

		// if theyre not equals - meaning the user is trying to update the company name
		// - we validate the name
		if (!OldName.equals(newName)) {
			validateCompanyName(newName);
		}

	}

	// #4 validating the getCOmpanyByCompanyId method
	private void validateGetCompanyByCompanyId(long companyId) throws ApplicationException {

		validateCompanyId(companyId);

	}

	// #5 validating the getAllCpmpanies method
	private void validateGetAllCompanies() throws ApplicationException {
		// checking if the list is empty
		if (this.companyDao.getAllCompanies() == null) {
			throw new ApplicationException(ErrorType.EMPTY_LIST,
					"getAllCOmpanies failed: The list of companies is empty " + DateUtils.getCurrentDateAndTime());
		}
	}

	// #6 validating the login of the company
	private void validateLogin(String companyName, String companyPassword) throws ApplicationException {

		if (this.companyDao.checkLogin(companyName, companyPassword) == null) {
			throw new ApplicationException(ErrorType.INVALID_LOGIN, "invalid login of company. username & password do not match.");
		}
	}

	// general utility method: validating that company Id exists in the db
	private void validateCompanyId(long companyId) throws ApplicationException {

		if (!this.companyDao.doesCompanyExistById(companyId)) {
			throw new ApplicationException(ErrorType.INVALID_PARAMETER, "companyId doesnt exist.");
		}
	}

	// ** utility method: validating that company name doesnt already exist in the
	// db
	private void validateCompanyName(String name) throws ApplicationException {

		// this means the user is trying to update the name of an existing company, so
		// we check if the company's name is already exist in the DB
		if (this.companyDao.doesCompanyNameExist(name)) {
			throw new ApplicationException(ErrorType.INVALID_PARAMETER,
					"validation of updateName failed: the comapny name you entered already exists. "
							+ DateUtils.getCurrentDateAndTime());
		}
		// we check if the new name is valid
		if (name.length() < 2 || name.length() > 50) {
			throw new ApplicationException(ErrorType.INVALID_PARAMETER,
					"validation of updatName failed: company's name is either too short or too long. companys name must be between 2 and 50 characters.");
		}
	}

	// *** utility method: validating that email & password are valid
	private void validateCompanyDetails(Company company) throws ApplicationException {

		// we check if the email is valid (via RegExp)
		if (!LoginValidationUtils.emailValidation(company.getCompanyEmail())) {
			throw new ApplicationException(ErrorType.INVALID_PARAMETER,
					"creation/update of company failed: email address is not valid.");
		}
		// we check if the password is valid (via RegExp)
		if (!LoginValidationUtils.passwordValidation(company.getCompanyPassword())) {
			throw new ApplicationException(ErrorType.INVALID_PARAMETER,
					"creation/update of company failed: password is not valid. password must contain 8-10 characters, and at least 1 upperCase, 1 lowerCase, and 1 digit. it cannot contain special chars");
		}

	}

}
