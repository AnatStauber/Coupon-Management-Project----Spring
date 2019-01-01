package com.anat.coupons.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.anat.coupons.beans.Coupon;
import com.anat.coupons.dao.CompanyDao;
import com.anat.coupons.dao.CouponsDao;
import com.anat.coupons.dao.CustomerDao;
import com.anat.coupons.enums.CouponType;
import com.anat.coupons.enums.ErrorType;
import com.anat.coupons.exceptions.ApplicationException;
import com.anat.coupons.utils.DateUtils;

@Controller
public class CouponController {

	// Assigning a local variable for each one of the 'dao' objects,
	// in order to gain access to the methods communicating with the DB.
	@Autowired
	private CouponsDao couponDao;
	@Autowired
	private CompanyDao companyDao;
	@Autowired
	private CustomerDao customerDao;

//	public CouponController() {
//		this.couponDao = new CouponsDao();
//		this.companyDao = new CompanyDao();
//		this.customerDao = new CustomerDao();
//	}

	// 1. calling the createCoupon method
	public long createCoupon(Coupon coupon) throws ApplicationException {

		validateCreateCoupon(coupon);

		// If we didn't catch any exception, we call the 'createCoupon' method.
		return this.couponDao.createCoupon(coupon);
	}

	// 2. calling the deleteCoupon method
	public void deleteCoupon(long couponId) throws ApplicationException {

		validateCouponId(couponId);

		// if valid - (no exceptions are thrown) - activate the delete method
		this.couponDao.deleteCoupon(couponId);
	}

	// 3. calling the updateCoupon method
	public void updateCoupon(Coupon coupon) throws ApplicationException {

		validateUpdateCoupon(coupon);

		this.couponDao.updateCoupon(coupon);
	}

	// 4. calling the getCouponsById method - confirming that coupon exists
	public Coupon getCoupon(long couponId) throws ApplicationException {

		validateCouponId(couponId);

		return (this.couponDao.getCouponByCouponId(couponId));
	}

	// 5. calling the getCouponsByType method
	public List<Coupon> getCouponsByType(CouponType type) throws ApplicationException {

		validateGetCouponsByType(type);

		return this.couponDao.getCouponsByType(type);
	}

	// 6. calling the getByEndDate
	public List<Coupon> getCouponsByEndDate(String endDate) throws ApplicationException {

		validateGetCouponByEndDate(endDate);

		return this.couponDao.getCouponsByEndDate(endDate);
	}

	// 7. calling the getCouponsByPrices method
	public List<Coupon> getCouponsByPrices(double minPrice, double maxPrice) throws ApplicationException {

		validateGetCouponsByPrices(minPrice, maxPrice);

		return this.couponDao.getCouponsByPrices(minPrice, maxPrice);
	}

	// 8. calling the getAllActiveCoupons
	public List<Coupon> getAllActiveCoupons() throws ApplicationException {

		validateGetAllActiveCoupons();

		return this.couponDao.getAllActiveCoupons();
	}

	// 9. calling the get all coupons:
	public List<Coupon> getAllCoupons() throws ApplicationException {

		validateGetAllCoupons();

		return this.couponDao.getAllCoupons();
	}

	// 10. calling the getActiveCouponsByCompany method
	public List<Coupon> getActiveCouponsByCompany(long companyId) throws ApplicationException {

		validateGetActiveCouponsByCompany(companyId);

		return this.couponDao.getActiveCouponsByCompany(companyId);
	}

	// 11. calling the getAllCouponsByCompany
	public List<Coupon> getAllCouponsByCompany(long companyId) throws ApplicationException {

		validateGetAllCouponsByCompany(companyId);

		return this.couponDao.getAllCouponsByCompany(companyId);
	}

	// 12. calling the getActiveCouponsByCustomer method
	public List<Coupon> getActiveCouponsByCustomer(long customerId) throws ApplicationException {

		validateGetActiveCouponsByCustomer(customerId);

		return this.couponDao.getActiveCouponsByCustomer(customerId);
	}

	// 13. calling the getAllCOuponsByCustomer method
	public List<Coupon> getAllCouponsByCustomer(long customerId) throws ApplicationException {

		validateGetAllCouponsByCustomer(customerId);

		return this.couponDao.getAllCouponsByCustomer(customerId);
	}

	// 14. calling the purchaseCoupon method:
	public void purchaseCoupon(long customerId, long couponId, int purchasedAmount) throws ApplicationException {

		validatePurchaseCoupon(customerId, couponId, purchasedAmount);

		// if everything is valid - activate the purchasing method
		this.couponDao.purchaseCoupon(customerId, couponId, purchasedAmount);
	}

	/*-----------------------------------------------validations-------------------------------------------------------*/

	// # 1 validating the create coupon: if there are no other coupons by that name
	// for the company, if the name is valid
	private void validateCreateCoupon(Coupon coupon) throws ApplicationException {

		String title = coupon.getCouponTitle();
		String couponStartDate = coupon.getCouponStartDate();
		String couponEndDate = coupon.getCouponEndDate();
		int couponAmount = coupon.getCouponAmount();
//			CouponType type = coupon.getType();
		String couponMessage = coupon.getCouponMessage();
		double couponPrice = coupon.getCouponPrice();
		//String couponImage = coupon.getCouponImage();
		long companyId = coupon.getComapnyId();

		validateCouponTitle(title, companyId);
		validateCouponDates(couponStartDate, couponEndDate);
		validateCouponAmount(couponAmount);
		validateCouponMessage(couponMessage);
		validateCouponPrice(couponPrice);

		// assuring that the company exists in the DB
		if (!this.companyDao.doesCompanyExistById(companyId)) {
			throw new ApplicationException(ErrorType.INVALID_PARAMETER,
					"companyId doesnt exist. " + DateUtils.getCurrentDateAndTime());
		}
	}

	// # 3 validating the update method
	private void validateUpdateCoupon(Coupon coupon) throws ApplicationException {

		long couponId = coupon.getCouponId();
		String title = coupon.getCouponTitle();
		String couponStartDate = coupon.getCouponStartDate();
		String couponEndDate = coupon.getCouponEndDate();
		int couponAmount = coupon.getCouponAmount();
//			CouponType type = coupon.getType();
		String couponMessage = coupon.getCouponMessage();
		double couponPrice = coupon.getCouponPrice();
//			String couponImage = coupon.getCouponImage();
		long companyId = coupon.getComapnyId();

		// validating the id
		validateCouponId(couponId);

		Coupon oldCoupon = this.couponDao.getCouponByCouponId(couponId);

		// if the user is trying to update the title - then validate the new title
		String oldTitle = oldCoupon.getCouponTitle();
		if (!oldTitle.equals(title)) {
			validateCouponTitle(title, companyId);
		}

		// validating the amount, message and price
		validateCouponAmount(couponAmount);
		validateCouponMessage(couponMessage);
		validateCouponPrice(couponPrice);

		String oldStartDate = oldCoupon.getCouponStartDate();
		String oldEndDate = oldCoupon.getCouponEndDate();

		// checking if the startDate is being modified:
		if (!oldStartDate.equals(couponStartDate)) {
			throw new ApplicationException(ErrorType.ACTION_CANNOT_BE_COMPLETED,
					"forbidden action, you are not allowed to change the coupon start Date. "
							+ DateUtils.getCurrentDateAndTime());
		}

		// checking that the user didnt try to extend the coupon's end date when there
		// are no more coupons in stock
		if ((DateUtils.isDate1AfterDate2(couponEndDate, oldEndDate)) && (couponAmount == 0)) {
			throw new ApplicationException(ErrorType.INVALID_PARAMETER,
					"forbidden action: you are not allowed to postpone the coupon's expiry date when there are no coupons left in stock.");
		}

		// validating the dates
		validateCouponDates(couponStartDate, couponEndDate);

	}

	// #5 validating the getCouponsByType method : that the returned list is not
	// empty
	private void validateGetCouponsByType(CouponType type) throws ApplicationException {

		if (couponDao.getCouponsByType(type) == null) {
			throw new ApplicationException(ErrorType.EMPTY_LIST,
					"a null value was returned. seems like the list is empty." + DateUtils.getCurrentDateAndTime());
		}

	}

	// #6 validating the getCouponsByEndDates - that the inserted date is valid and
	// that list isn't empty
	private void validateGetCouponByEndDate(String endDate) throws ApplicationException {

		// we check if the input dates fit the format "yyyy-mm-dd"
		if (!(DateUtils.isDateInFormat(endDate))) {
			throw new ApplicationException(ErrorType.INVALID_PARAMETER,
					"getting coupons by end date failed. the given endDate is not in the right format: 'yyyy-mm-dd'"
							+ DateUtils.getCurrentDateAndTime());
		}

		if (couponDao.getCouponsByEndDate(endDate) == null) {
			throw new ApplicationException(ErrorType.EMPTY_LIST,
					"a null value was returned. seems like the list is empty." + DateUtils.getCurrentDateAndTime());
		}
	}

	// #7 validating the get by prices: cheking that the prices entered are
	// logically valid and that list is not empty
	private void validateGetCouponsByPrices(double minPrice, double maxPrice) throws ApplicationException {

		// checking if the maxPrice is larger than the minPrice
		if (minPrice >= maxPrice) {
			throw new ApplicationException(ErrorType.INVALID_PARAMETER,
					"invalid parameter- maximum price must be larger than minimum price."
							+ DateUtils.getCurrentDateAndTime());
		}
		// checking that the prices are in the right range
		if ((minPrice < 0) || (maxPrice < 0)) {
			throw new ApplicationException(ErrorType.INVALID_PARAMETER,
					"prices cannot be negative." + DateUtils.getCurrentDateAndTime());
		}

		// checking if the list is not empty
		if (couponDao.getCouponsByPrices(minPrice, maxPrice) == null) {
			throw new ApplicationException(ErrorType.EMPTY_LIST,
					"a null value was returned. seems like the list is empty." + DateUtils.getCurrentDateAndTime());
		}
	}

	// #8 validating the get all active coupons: that list is not empty
	private void validateGetAllActiveCoupons() throws ApplicationException {

		if (couponDao.getAllActiveCoupons() == null) {
			throw new ApplicationException(ErrorType.EMPTY_LIST,
					"a null value was returned. seems like the list is empty." + DateUtils.getCurrentDateAndTime());
		}

	}

	// #9 validating the get all coupons (active&history) - that list is not empty
	private void validateGetAllCoupons() throws ApplicationException {

		if (this.couponDao.getAllCoupons() == null) {
			throw new ApplicationException(ErrorType.EMPTY_LIST,
					"a null value was returned. seems like the list is empty." + DateUtils.getCurrentDateAndTime());
		}
	}

	// #10 validating the get active coupons of company : that company exists and
	// the list is not empty
	private void validateGetActiveCouponsByCompany(long companyId) throws ApplicationException {

		if (!this.companyDao.doesCompanyExistById(companyId)) {
			throw new ApplicationException(ErrorType.INVALID_PARAMETER,
					"companyId doesnt Exist." + DateUtils.getCurrentDateAndTime());
		}

		if (couponDao.getActiveCouponsByCompany(companyId) == null) {
			throw new ApplicationException(ErrorType.EMPTY_LIST,
					"a null value was returned. seems like the list is empty." + DateUtils.getCurrentDateAndTime());
		}
	}

	// #11 validating the get all coupons of company (active&history): that company
	// exists and list is not empty
	private void validateGetAllCouponsByCompany(long companyId) throws ApplicationException {

		if (!this.companyDao.doesCompanyExistById(companyId)) {
			throw new ApplicationException(ErrorType.INVALID_PARAMETER,
					"companyId doesnt Exist." + DateUtils.getCurrentDateAndTime());
		}

		if (couponDao.getAllCouponsByCompany(companyId) == null) {
			throw new ApplicationException(ErrorType.EMPTY_LIST,
					"a null value was returned. seems like the list is empty." + DateUtils.getCurrentDateAndTime());
		}

	}

	// #12 validating the get active coupons of a customer: that customer exists and
	// list is not empty.
	private void validateGetActiveCouponsByCustomer(long customerId) throws ApplicationException {

		if (!this.customerDao.doesCustomerExistById(customerId)) {
			throw new ApplicationException(ErrorType.INVALID_PARAMETER,
					"customerId doesnt exist." + DateUtils.getCurrentDateAndTime());
		}
		if (couponDao.getActiveCouponsByCustomer(customerId) == null) {
			throw new ApplicationException(ErrorType.EMPTY_LIST,
					"a null value was returned. seems like the list is empty." + DateUtils.getCurrentDateAndTime());
		}
	}

	// #13 validating the get all coupons of a customer: that customer exists and
	// that list is not empty
	private void validateGetAllCouponsByCustomer(long customerId) throws ApplicationException {

		if (!this.customerDao.doesCustomerExistById(customerId)) {
			throw new ApplicationException(ErrorType.INVALID_PARAMETER,
					"customerId doesnt exist." + DateUtils.getCurrentDateAndTime());
		}
		if (couponDao.getAllCouponsByCustomer(customerId) == null) {
			throw new ApplicationException(ErrorType.EMPTY_LIST,
					"a null value was returned. seems like the list is empty." + DateUtils.getCurrentDateAndTime());
		}
	}

	// #14 validating the purchasing of a coupon by a customer: that customer
	// exists, that coupon exists, that amount enterd is valid ant there are enough
	// coupons left in stock
	private void validatePurchaseCoupon(long customerId, long couponId, int purchasedAmount)
			throws ApplicationException {

		// checking if the couponId is valid:
		validateCouponId(couponId);

		// checking if the customerId is valid:
		if (!this.customerDao.doesCustomerExistById(customerId)) {
			throw new ApplicationException(ErrorType.INVALID_PARAMETER,
					"customerId doesnt exist." + DateUtils.getCurrentDateAndTime());
		}

		// checking if the amount inserted was valid:
		if (purchasedAmount <= 0) {
			throw new ApplicationException(ErrorType.INVALID_PARAMETER,
					"illegal amount inserted. amount must be above 0." + DateUtils.getCurrentDateAndTime());
		}

		Coupon coupon = this.couponDao.getCouponByCouponId(couponId);

		// checking if the purchase is valid;
		if (coupon.getCouponAmount() < purchasedAmount) {
			throw new ApplicationException(ErrorType.INVALID_PARAMETER, "there are not enough coupons.there are "
					+ coupon.getCouponAmount() + "  left. " + DateUtils.getCurrentDateAndTime());
		}

	}

	/*------------------------------------------------------------------------- general utility validation methods---------------------------------------------------------------------*/

	// general utility method #1: validating that coupon Id exists in db
	private void validateCouponId(long couponId) throws ApplicationException {

		if (!this.couponDao.doesCouponExistById(couponId)) {
			throw new ApplicationException(ErrorType.INVALID_PARAMETER,
					"couponId doesnt exist. " + DateUtils.getCurrentDateAndTime());
		}
	}

	// general utility method #2: validating the title: checking that the new title
	// is valid and there are no other coupons by that title in the company's
	// coupons.
	private void validateCouponTitle(String title, long companyId) throws ApplicationException {

		// checking if the title is valid:
		if (title.length() < 2 || title.length() > 45) {
			throw new ApplicationException(ErrorType.INVALID_PARAMETER,
					"coupon's name is either too short or too long. coupon title must be between 2 and 45 characters."
							+ DateUtils.getCurrentDateAndTime());
		}

		// checking if there is a coupon of the same name in the company's coupons;
		if (this.couponDao.doesCouponTitleExist(title, companyId)) {
			throw new ApplicationException(ErrorType.INVALID_PARAMETER,
					"coupon's title already exists for this company." + DateUtils.getCurrentDateAndTime());
		}
	}

	// general utility method #3: validating the Coupon dates: checking that the
	// dates are in the right format and are chronologically logical
	private void validateCouponDates(String startDate, String endDate) throws ApplicationException {

		// validating that the input dates fits the format "yyyy-mm-dd"
		if ((!DateUtils.isDateInFormat(endDate) || (!DateUtils.isDateInFormat(startDate)))) {
			throw new ApplicationException(ErrorType.INVALID_PARAMETER,
					"validating the date failed. the given Date/s is not in the right format: 'yyyy-mm-dd'"
							+ DateUtils.getCurrentDateAndTime());
		}

//				//validating that startDate hasn't passed
//				if (DateUtils.isCurrentDateAfterEndDate(startDate)) {
//					throw new ApplicationException(ErrorType.INVALID_PARAMETER, " the coupon start date has already passed enter a new end date." +DateUtils.getCurrentDateAndTime());
//				}
		// validating that endDate hasn't passed
		if (DateUtils.isCurrentDateAfterEndDate(endDate)) {
			throw new ApplicationException(ErrorType.INVALID_PARAMETER,
					"the coupon end date has already passed. enter a new end date "
							+ DateUtils.getCurrentDateAndTime());
		}
		// validating that the end date is later than the start date
		if (DateUtils.isDate1AfterDate2(startDate, endDate)) {
			throw new ApplicationException(ErrorType.INVALID_PARAMETER,
					"start date is later than end date " + DateUtils.getCurrentDateAndTime());
		}

	}

	// general utility method #4: validating the update Amount method: that coupon
	// exists and that the new amount inserted isn't negative
	private void validateCouponAmount(int amount) throws ApplicationException {

		// checking that the amount is not negative
		if (amount < 0) {
			throw new ApplicationException(ErrorType.INVALID_PARAMETER,
					"illegal amount entered.amount must be 0 or above." + DateUtils.getCurrentDateAndTime());
		}
	}

	// general utility method #5: validating the message: confirming that the
	// message is valid lenghtwise
	private void validateCouponMessage(String message) throws ApplicationException {

		// checking if the message is valid:
		if (message.length() < 2 || message.length() > 200) {
			throw new ApplicationException(ErrorType.INVALID_PARAMETER,
					"coupon's message is either too short or too long. coupon message must be between 2 and 200 characters."
							+ DateUtils.getCurrentDateAndTime());
		}
	}

	// general utility method #6: validating the price: checking that the price
	// inserted is higher than 0.
	private void validateCouponPrice(double price) throws ApplicationException {

		// check that the price is higher than 0
		if (price <= 0) {
			throw new ApplicationException(ErrorType.INVALID_PARAMETER,
					"the price is not valid. must be higher than 0." + DateUtils.getCurrentDateAndTime());
		}
	}

}
