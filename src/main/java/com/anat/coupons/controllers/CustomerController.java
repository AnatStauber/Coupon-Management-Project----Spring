package com.anat.coupons.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.anat.coupons.beans.Customer;
import com.anat.coupons.dao.CustomerDao;
import com.anat.coupons.enums.ErrorType;
import com.anat.coupons.exceptions.ApplicationException;
import com.anat.coupons.utils.DateUtils;
import com.anat.coupons.utils.LoginValidationUtils;

@Controller
public class CustomerController {

	@Autowired
	private CustomerDao customerDao;

//	public CustomerController() {
//		this.couponDao = new CouponsDao();
//		this.companyDao = new CompanyDao();
//		this.customerDao = new CustomerDao();
//	}

	// 1. calling the create customer validation
	public Long createCustomer(Customer customer) throws ApplicationException {

		validateCreateCustomer(customer);

		return this.customerDao.createCustomer(customer);
	}

	// 2. calling the deleteCustomer method
	public void deleteCustomer(long customerId) throws ApplicationException {

		validateCustomerId(customerId);

		// if valid - (no exceptions are thrown) - activate the delete method
		this.customerDao.deleteCustomer(customerId);
	}

	// 4. calling the updateCustomerName method
	public void updateCustomer(Customer customer) throws ApplicationException {
		
		validateUpdateCustomer(customer);

		this.customerDao.updateCustomer(customer);
	}

	// 5. calling the getCustomerByCustomerId method
	public Customer getCustomer(long customerId) throws ApplicationException {

		validateGetCustomerByCustomerId(customerId);

		return this.customerDao.getCustomerByCustomerId(customerId);

	}

	// 6. calling the getAllCustomers method:
	public List<Customer> getAllCustomers() throws ApplicationException {

		validateGetAllCustomers();

		// If we didn't catch any exception, we call the 'getAllCustomers' method.
		return this.customerDao.getAllCustomers();
	}

	// 7. calling the login method
	public Long login(String customerName, String customerPassword) throws ApplicationException {

		validateLogin(customerName, customerPassword);

		return this.customerDao.checkLogin(customerName, customerPassword);
	}

//--------------------------------------------------------------------validations-------------------------------------------------------------------------

	// #1 validating the createCustomer method
	private void validateCreateCustomer(Customer customer) throws ApplicationException {

		validateCustomerName(customer.getCustomerName());

		validateCustomerPassword(customer.getCustomerPassword());
		
	}

	// #3 validating the updateCustomerPassword method
	private void validateUpdateCustomer(Customer customer) throws ApplicationException {

		// validating the customer ID
		validateCustomerId(customer.getCustomerId());
		
		//validating the customer password
		validateCustomerPassword(customer.getCustomerPassword());

		//checking if the user is trying to update the customer's name. if so - we redirect the system to the 'validateCustomerName' method.
		long custId = customer.getCustomerId();
		Customer oldCustomer = this.customerDao.getCustomerByCustomerId(custId);
		String oldCustName = oldCustomer.getCustomerName();
		String newCustName = customer.getCustomerName();
		if (!oldCustName.equals(newCustName)) {
			System.out.println(oldCustName);
			System.out.println(newCustName);
			validateCustomerName(newCustName);
		}
		
	}

	// #4 validating the getCOmpanyByCustomerId method
	private void validateGetCustomerByCustomerId(long customerId) throws ApplicationException {

		validateCustomerId(customerId);

	}

	// #5 validating the getAllCpmpanies method
	private void validateGetAllCustomers() throws ApplicationException {
		// checking if the list is empty
		if (this.customerDao.getAllCustomers() == null) {
			throw new ApplicationException(ErrorType.EMPTY_LIST,
					"getAllCustomers failed: The list of customers is empty " + DateUtils.getCurrentDateAndTime());
		}
	}

	// #6 validating the login of the customer
	private void validateLogin(String customerName, String customerPassword) throws ApplicationException {
		Long customerId = this.customerDao.checkLogin(customerName, customerPassword);
		if (customerId == null) {
		throw new ApplicationException(ErrorType.INVALID_LOGIN, "unsuccessful login. customer's userName or password are incorrect. ");
		}
	}
	// #1 general utility method: validating that customer Id exists in the db
	private void validateCustomerId(long customerId) throws ApplicationException {

		if (!this.customerDao.doesCustomerExistById(customerId)) {
			throw new ApplicationException(ErrorType.INVALID_PARAMETER, "customerId doesnt exist.");
		}
	}

	// #2 general utility method: validating that customer name doesnt already exist in the db
	private void validateCustomerName(String name) throws ApplicationException {

		// We check if the customer's name is already exist in the DB
		if (this.customerDao.doesCustomerNameExist(name)) {
			throw new ApplicationException(ErrorType.INVALID_PARAMETER,
					"validation of name failed: the customer's name you entered already exists. "
							+ DateUtils.getCurrentDateAndTime());
		}
		// we check if the name is valid
		if (name.length() < 2 || name.length() > 50) {
			throw new ApplicationException(ErrorType.INVALID_PARAMETER,
					"validation of name failed: customer's name is either too short or too long. customers name must be between 2 and 50 characters.");
		}
	}
	
	// #3 utility method: validating the password (via regEx)
	private void validateCustomerPassword (String password) throws ApplicationException {
		
		if (!LoginValidationUtils.passwordValidation(password)){
				throw new ApplicationException(ErrorType.INVALID_PARAMETER,"creation of customer failed: password is not valid. password must contain 8-10 characters, and at least 1 upperCase, 1 lowerCase, and 1 digit. it cannot contain special chars");
			}
	}

}
