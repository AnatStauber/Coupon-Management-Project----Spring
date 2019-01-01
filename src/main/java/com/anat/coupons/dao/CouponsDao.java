package com.anat.coupons.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.anat.coupons.beans.Coupon;
import com.anat.coupons.enums.CouponType;
import com.anat.coupons.enums.ErrorType;
import com.anat.coupons.exceptions.ApplicationException;
import com.anat.coupons.utils.DateUtils;
import com.anat.coupons.utils.JdbcUtils;

@Repository
public class CouponsDao {

	// 1. creating a new coupon in the DB , that will hold the information from a
	// coupon instance
	public long createCoupon(Coupon coupon) throws ApplicationException {

		java.sql.PreparedStatement preparedStatement = null;
		Connection connection = null;

		try {
			connection = JdbcUtils.getConnection();
			String sql = "insert into coupon (couponTitle,couponStartDate, couponEndDate, couponAmount, couponType, couponMessage, couponPrice, couponImage, companyId) values (?,?,?,?,?,?,?,?,?)";

			preparedStatement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);

			preparedStatement.setString(1, coupon.getCouponTitle());
			preparedStatement.setString(2, coupon.getCouponStartDate());
			preparedStatement.setString(3, coupon.getCouponEndDate());
			preparedStatement.setLong(4, coupon.getCouponAmount());
			preparedStatement.setString(5, coupon.getType().name());
			preparedStatement.setString(6, coupon.getCouponMessage());
			preparedStatement.setDouble(7, coupon.getCouponPrice());
			preparedStatement.setString(8, coupon.getCouponImage());
			preparedStatement.setLong(9, coupon.getComapnyId());

			preparedStatement.executeUpdate();

			ResultSet resultSet = preparedStatement.getGeneratedKeys();
			resultSet.first();
			long couponId = resultSet.getLong(1);
			return couponId;
		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR,
					"failed to create coupon " + DateUtils.getCurrentDateAndTime());
		} finally {
			JdbcUtils.closeResources(connection, preparedStatement);
		}
	}

	// 2. deleting a coupon from the DB based on it's ID
	@Transactional
	public void deleteCoupon(long couponId) throws ApplicationException {

		java.sql.PreparedStatement preparedStatementDelete1 = null;
		java.sql.PreparedStatement preparedStatementDelete2 = null;
		Connection connection = null;
		String couponEndDate = this.getCouponByCouponId(couponId).getCouponEndDate();

		try {

			connection = JdbcUtils.getConnection();

			// applying JDBC transaction make sure all of the statements are executed
			// successfully,
			// if either one of the statements within the block fails, abort and rollback
			// everything in the transaction block.
			//connection.setAutoCommit(false);

			// copying the data of the soon-to-be-deleted coupons and purchases to the
			// coupons history table and the purchase history table.
			if (DateUtils.isCurrentDateAfterEndDate(couponEndDate)) {
				updatePurchaseHistory(connection, couponId, "expired");
				updateCouponsHistory(connection, couponId, "expired");
			} else {
				updatePurchaseHistory(connection, couponId, "deleted");
				updateCouponsHistory(connection, couponId, "deleted");
			}

			// deleting all of the coupon purchases from the customer_coupons table
			// (because it is a foreign key and the coupon cannot be deleted if foreign keys
			// of it exists in another table)
			String sql = "delete from customer_coupon where couponId=?";
			preparedStatementDelete1 = connection.prepareStatement(sql);
			preparedStatementDelete1.setLong(1, couponId);
			preparedStatementDelete1.executeUpdate();

			// deleting the coupon from the coupons table
			sql = "delete from coupon where couponId=?";
			preparedStatementDelete2 = connection.prepareStatement(sql);
			preparedStatementDelete2.setLong(1, couponId);
			preparedStatementDelete2.executeUpdate();

			// Committing the statements (executes only if all statements are successful).
			//connection.commit();
		} catch (Exception e) {
//			try {
//				connection.rollback();
//			} catch (SQLException e1) {
//				throw new ApplicationException(e1, ErrorType.DATABASE_ERROR,
//						"delete coupon: failed to rollback previous statements " + DateUtils.getCurrentDateAndTime());
//			}
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR,
					"failed to delete coupon " + DateUtils.getCurrentDateAndTime());

		} finally {
			JdbcUtils.closeResources(connection, preparedStatementDelete1);
			JdbcUtils.closeResources(null, preparedStatementDelete2);
		}
	}

	// 2b. delete expired coupons (in the coupons table and in the customer_coupons
	// table.
	// there is no transaction needed because the 'deleteCoupons' method is applying
	// it's own trnsction, and if there will be a problem with the
	public void deleteExpiredCoupons() throws ApplicationException {

		PreparedStatement preparedStatementSelect = null;
		Connection connection = null;
		ResultSet resultSet = null;
		// List<Coupon> expiredList = null;
		String currentDate = DateUtils.getCurrentDate();
		long couponId = 0;

		try {
			connection = JdbcUtils.getConnection();

			// putting all expired coupons in the resultSet in order to delete
			String sql = "select * from coupon where couponEndDate < ?";
			preparedStatementSelect = connection.prepareStatement(sql);
			preparedStatementSelect.setString(1, currentDate);
			resultSet = preparedStatementSelect.executeQuery();

			if (!resultSet.next()) {
				return;
			}
			// inserting the 1st
			couponId = resultSet.getLong("couponId");
			deleteCoupon(couponId);

			// inserting starting from the 2nd
			while (resultSet.next()) {
				couponId = resultSet.getLong("couponId");
				deleteCoupon(couponId);
			}

		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR,
					"couponsDao:deleteExpiredCoupons failed. " + DateUtils.getCurrentDateAndTime());
		} finally {
			JdbcUtils.closeResources(connection, preparedStatementSelect);
		}
	}

	// 3. updating coupon's info
	public void updateCoupon(Coupon coupon) throws ApplicationException {

		java.sql.PreparedStatement preparedStatement = null;
		Connection connection = null;

		try {
			connection = JdbcUtils.getConnection();
			String sql = "update coupon set couponTitle=?, couponEndDate=?, couponAmount=?, couponType=?, couponMessage=?, couponPrice=?, couponImage=?, companyId=?  where couponId=?";

			preparedStatement = connection.prepareStatement(sql);

			preparedStatement.setString(1, coupon.getCouponTitle());
			preparedStatement.setString(2, coupon.getCouponEndDate());
			preparedStatement.setInt(3, coupon.getCouponAmount());
			preparedStatement.setString(4, coupon.getType().name());
			preparedStatement.setString(5, coupon.getCouponMessage());
			preparedStatement.setDouble(6, coupon.getCouponPrice());
			preparedStatement.setString(7, coupon.getCouponImage());
			preparedStatement.setLong(8, coupon.getComapnyId());
			preparedStatement.setLong(9, coupon.getCouponId());

			preparedStatement.executeUpdate();
		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR,
					"failed to update coupon " + DateUtils.getCurrentDateAndTime());
		} finally {
			JdbcUtils.closeResources(connection, preparedStatement);
		}
	}

	// 9. get coupon: returning a full coupon object, based on a given ID
	public Coupon getCouponByCouponId(long couponId) throws ApplicationException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Coupon coupon = null;

		try {
			connection = JdbcUtils.getConnection();
			String sql = "SELECT * FROM Coupon WHERE couponId=?";
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setLong(1, couponId);
			resultSet = preparedStatement.executeQuery();
			if (!resultSet.next()) {
				return null;
			}
			coupon = extractCouponFromResultSet(resultSet);

		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR,
					"failed to get coupon by ID " + DateUtils.getCurrentDateAndTime());
		} finally {
			JdbcUtils.closeResources(connection, preparedStatement);
		}

		return coupon;
	}

	// 10. returning all active coupons of a certain type
	public List<Coupon> getCouponsByType(CouponType type) throws ApplicationException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Coupon coupon = null;
		List<Coupon> couponsList = new ArrayList<Coupon>();
		String couponType = type.name();

		try {
			connection = JdbcUtils.getConnection();
			String sql = "SELECT * FROM Coupon WHERE couponType=?";
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, couponType);
			resultSet = preparedStatement.executeQuery();

			if (!resultSet.next()) {
				return null;
			}
			// inserting the 1st:
			coupon = extractCouponFromResultSet(resultSet);
			couponsList.add(coupon);
			// inserting starting from the 2nd:
			while (resultSet.next()) {
				coupon = extractCouponFromResultSet(resultSet);
				couponsList.add(coupon);
			}

		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR,
					"failed to get coupon by type " + DateUtils.getCurrentDateAndTime());
		} finally {
			JdbcUtils.closeResources(connection, preparedStatement);
		}

		return couponsList;
	}

	// 10b. get all active coupons by endDate
	public List<Coupon> getCouponsByEndDate(String endDate) throws ApplicationException {

		PreparedStatement preparedStatement = null;
		Connection connection = null;
		ResultSet resultSet = null;
		List<Coupon> couponsList = new ArrayList<Coupon>();
		Coupon coupon = null;

		try {
			connection = JdbcUtils.getConnection();

			String sql = "select * from coupon where couponEndDate <= ?";
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, endDate);
			resultSet = preparedStatement.executeQuery();

			if (!resultSet.next()) {
				return null;
			}
			// inserting the 1st
			coupon = extractCouponFromResultSet(resultSet);
			couponsList.add(coupon);
			// inserting starting from the 2nd
			while (resultSet.next()) {
				coupon = extractCouponFromResultSet(resultSet);
				couponsList.add(coupon);
			}
		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR,
					"couponsDao:getCouponsByEndDate failed. " + DateUtils.getCurrentDateAndTime());
		} finally {
			JdbcUtils.closeResources(connection, preparedStatement);
		}

		return couponsList;
	}

	// 10c. get list ofall active coupons between a range of prices
	public List<Coupon> getCouponsByPrices(double minPrice, double maxPrice) throws ApplicationException {

		PreparedStatement preparedStatement = null;
		Connection connection = null;
		ResultSet resultSet = null;
		List<Coupon> couponsList = new ArrayList<Coupon>();
		Coupon coupon = null;

		try {
			connection = JdbcUtils.getConnection();

			String sql = "select * from coupon where couponPrice >= ? and couponPrice<=?";
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setDouble(1, minPrice);
			preparedStatement.setDouble(2, maxPrice);
			resultSet = preparedStatement.executeQuery();

			if (!resultSet.next()) {
				return null;
			}
			// inserting the 1st
			coupon = extractCouponFromResultSet(resultSet);
			couponsList.add(coupon);
			// inserting starting from the 2nd
			while (resultSet.next()) {
				coupon = extractCouponFromResultSet(resultSet);
				couponsList.add(coupon);
			}
		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR,
					"couponsDao:getCouponsByPrices failed. " + DateUtils.getCurrentDateAndTime());
		} finally {
			JdbcUtils.closeResources(connection, preparedStatement);
		}

		return couponsList;
	}

	// 11. returning a list of all active coupons in the system
	public List<Coupon> getAllActiveCoupons() throws ApplicationException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Coupon coupon = null;
		List<Coupon> couponsList = new ArrayList<Coupon>();

		try {
			connection = JdbcUtils.getConnection();

			String sql = "SELECT * FROM Coupon ";
			preparedStatement = connection.prepareStatement(sql);
			resultSet = preparedStatement.executeQuery();
			if (!resultSet.next()) {
				return null;
			}
			// inserting the 1st coupon to the list
			coupon = extractCouponFromResultSet(resultSet);
			couponsList.add(coupon);
			// inserting all coupons, starting from the second.
			while (resultSet.next()) {
				coupon = extractCouponFromResultSet(resultSet);
				couponsList.add(coupon);
			}

		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR,
					"failed to creat a list of all coupons " + DateUtils.getCurrentDateAndTime());
		} finally {
			JdbcUtils.closeResources(connection, preparedStatement);
		}

		return couponsList;
	}

	// 11b. returning a list of all coupons in the system - both active and inactive
	// (from history)
	public List<Coupon> getAllCoupons() throws ApplicationException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Coupon coupon = null;
		List<Coupon> couponsList = new ArrayList<Coupon>();

		try {
			connection = JdbcUtils.getConnection();

			String sql = "SELECT c.*, null AS dateOfRemoval FROM coupon c UNION SELECT ch.* FROM coupon_history ch ";
			preparedStatement = connection.prepareStatement(sql);
			resultSet = preparedStatement.executeQuery();

			if (!resultSet.next()) {
				return null;
			}

			// inserting the 1st coupon to the list
			coupon = extractCouponFromResultSet(resultSet);
			couponsList.add(coupon);

			// inserting all coupons, starting from the second.
			while (resultSet.next()) {
				coupon = extractCouponFromResultSet(resultSet);
				couponsList.add(coupon);
			}

		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR,
					"failed to creat a list of all coupons " + DateUtils.getCurrentDateAndTime());
		} finally {
			JdbcUtils.closeResources(connection, preparedStatement);
		}

		return couponsList;
	}

	// 12. returning a list of all active coupons of a certain company
	public List<Coupon> getActiveCouponsByCompany(long companyId) throws ApplicationException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Coupon coupon = null;
		List<Coupon> couponsList = new ArrayList<Coupon>();

		try {
			connection = JdbcUtils.getConnection();

			String sql = "SELECT * FROM Coupon where companyId = ? ";
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setLong(1, companyId);
			resultSet = preparedStatement.executeQuery();

			if (!resultSet.next()) {
				return null;
			}

			// inserting the 1st coupon to the list
			coupon = extractCouponFromResultSet(resultSet);
			couponsList.add(coupon);
//						
			// inserting all coupons, //starting from the second.
			while (resultSet.next()) {
				coupon = extractCouponFromResultSet(resultSet);
				couponsList.add(coupon);
			}

		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR,
					"failed to create a list of all coupons of a company " + DateUtils.getCurrentDateAndTime());
		} finally {
			JdbcUtils.closeResources(connection, preparedStatement);
		}
		return couponsList;
	}

	// 12b. returning a list of all coupons of a certain company - both active and
	// inactive(from history)
	public List<Coupon> getAllCouponsByCompany(long companyId) throws ApplicationException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Coupon coupon = null;
		List<Coupon> couponsList = new ArrayList<Coupon>();

		try {
			connection = JdbcUtils.getConnection();

			String sql = " SELECT c.*,null AS dateOfRemoval FROM coupon c WHERE companyId=?  UNION SELECT ch.* FROM coupon_history ch WHERE companyId=? ";

			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setLong(1, companyId);
			preparedStatement.setLong(2, companyId);
			resultSet = preparedStatement.executeQuery();

			if (!resultSet.next()) {
				return null;
			}

			// inserting the 1st coupon to the list
			coupon = extractCouponFromResultSet(resultSet);
			couponsList.add(coupon);

			// inserting all coupons
			while (resultSet.next()) {
				coupon = extractCouponFromResultSet(resultSet);
				couponsList.add(coupon);
			}

		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR,
					"failed to create a list of all coupons of a customer " + DateUtils.getCurrentDateAndTime());
		} finally {
			JdbcUtils.closeResources(connection, preparedStatement);
		}

		return couponsList;
	}

	// 13. returning a list of all active coupons of a certain customer
	public List<Coupon> getActiveCouponsByCustomer(long customerId) throws ApplicationException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Coupon coupon = null;
		List<Coupon> couponsList = new ArrayList<Coupon>();

		try {
			connection = JdbcUtils.getConnection();

			String sql = "SELECT * from coupon c inner join customer_coupon cc on c.couponId=cc.couponId where cc.customerId = ?";
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setLong(1, customerId);
			resultSet = preparedStatement.executeQuery();

			if (!resultSet.next()) {
				return null;
			}

			// inserting the 1st coupon to the list
			coupon = extractCouponFromResultSet(resultSet);
			couponsList.add(coupon);
//						
			// inserting all coupons
			while (resultSet.next()) {
				coupon = extractCouponFromResultSet(resultSet);
				couponsList.add(coupon);
			}

		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR,
					"failed to create a list of all active coupons of a customer " + DateUtils.getCurrentDateAndTime());
		} finally {
			JdbcUtils.closeResources(connection, preparedStatement);
		}

		return couponsList;
	}

	// 13b. returning a list of all coupons of a certain customer - both active and
	// inactive(from history)
	public List<Coupon> getAllCouponsByCustomer(long customerId) throws ApplicationException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Coupon coupon = null;
		List<Coupon> couponsList = new ArrayList<Coupon>();

		try {
			connection = JdbcUtils.getConnection();

			String sql = " (SELECT c.*, null as dateOfRemoval FROM coupon c JOIN customer_coupon cc ON c.couponId = cc.couponId WHERE cc.customerId = ? ) UNION (SELECT ch.* FROM coupon_history ch JOIN purchase_history ph ON ch.couponId = ph.couponId WHERE ph.customerId =? )";

			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setLong(1, customerId);
			preparedStatement.setLong(2, customerId);
			resultSet = preparedStatement.executeQuery();

			if (!resultSet.next()) {
				return null;
			}

			// inserting the 1st coupon to the list
			coupon = extractCouponFromResultSet(resultSet);
			couponsList.add(coupon);

			// inserting all coupons
			while (resultSet.next()) {
				coupon = extractCouponFromResultSet(resultSet);
				couponsList.add(coupon);
			}

		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR,
					"failed to create a list of all coupons of a customer " + DateUtils.getCurrentDateAndTime());
		} finally {
			JdbcUtils.closeResources(connection, preparedStatement);
		}

		return couponsList;
	}

	// 14. updating the customer_coupon table when a customer purchased a new
	// coupon.
	public void purchaseCoupon(long customerId, long couponId, int purchasedAmount) throws ApplicationException {

		Coupon coupon = this.getCouponByCouponId(couponId);
		int PreviousCouponAmount = coupon.getCouponAmount(); // instead of transaction

		long companyId = coupon.getComapnyId();
		String currentDate = DateUtils.getCurrentDate();

		java.sql.PreparedStatement preparedStatement = null;
		Connection connection = null;

		try {
			connection = JdbcUtils.getConnection();

			// subtracting the purchased coupons from the remaining amount in the coupons
			// table
			coupon.setCouponAmount(PreviousCouponAmount - purchasedAmount);
			updateCoupon(coupon);

			// inserting a new row to the customer_coupon table , when a new purchase has
			// been made
			String sql = "insert into customer_coupon (customerId, couponId,companyId, purchasedAmount,purchaseDate) values (?,?,?,?,?)";

			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setLong(1, customerId);
			preparedStatement.setLong(2, couponId);
			preparedStatement.setLong(3, companyId);
			preparedStatement.setInt(4, purchasedAmount);
			preparedStatement.setString(5, currentDate);
			preparedStatement.executeUpdate();

		} catch (Exception e) {
			coupon.setCouponAmount(PreviousCouponAmount); //
			updateCoupon(coupon); // we chose not to use a transaction because we invoked a diff method, so we
									// made it reassign the last value in case the 2nd action wouldnt work.
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR,
					"failed to activate the purchase method " + DateUtils.getCurrentDateAndTime());
		} finally {
			JdbcUtils.closeResources(connection, preparedStatement);
		}
	}

	// 15. checking if coupon exists by its name and doesn't repeat itself in the
	// rest of the coupons of the company
	public boolean doesCouponTitleExist(String title, long companyId) throws ApplicationException {
		java.sql.PreparedStatement preparedStatement = null;
		Connection connection = null;
		ResultSet resultSet = null;

		try {
			connection = JdbcUtils.getConnection();

			String sql = "select * from coupon where couponTitle=? and companyId=? ";
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, title);
			preparedStatement.setLong(2, companyId);
			resultSet = preparedStatement.executeQuery();

			if (!resultSet.next()) {
				return false;
			} else {
				return true;
			}
		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR,
					"error in CouponsDao:doesCouponExistByName Failed.");
		} finally {
			JdbcUtils.closeResources(connection, preparedStatement);
		}
	}

	// 16. checking if a coupon exists by its id
	public boolean doesCouponExistById(long couponId) throws ApplicationException {
		java.sql.PreparedStatement preparedStatement = null;
		Connection connection = null;
		ResultSet resultSet = null;

		try {
			connection = JdbcUtils.getConnection();

			String sql = "select * from coupon where couponId=? ";
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setLong(1, couponId);
			resultSet = preparedStatement.executeQuery();

			if (!resultSet.next()) {
				return false;
			} else {
				return true;
			}
		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR,
					"error in CouponsDao:doesCouponExistById Failed.");
		} finally {
			JdbcUtils.closeResources(connection, preparedStatement);
		}
	}

	// ??. pulling a coupon from the resultSet and turning it into a coupon object.
	private Coupon extractCouponFromResultSet(ResultSet resultSet) throws ApplicationException {
		Coupon coupon = new Coupon();
		try {
			coupon.setCouponId(resultSet.getLong("couponId"));
			coupon.setCouponAmount(resultSet.getInt("couponAmount"));
			coupon.setCouponEndDate(resultSet.getString("couponEndDate"));
			coupon.setCouponImage(resultSet.getString("couponImage"));
			coupon.setCouponMessage(resultSet.getString("couponMessage"));
			coupon.setCouponPrice(resultSet.getDouble("couponPrice"));
			coupon.setCouponStartDate(resultSet.getString("couponStartDate"));
			coupon.setCouponTitle(resultSet.getString("couponTitle"));
			coupon.setType(CouponType.valueOf(resultSet.getString("couponType")));
			coupon.setComapnyId(resultSet.getLong("companyId"));
			coupon.setCouponStatus(resultSet.getString("couponStatus"));
		} catch (SQLException e) {
			throw new ApplicationException(e, ErrorType.DATABASE_ERROR,
					"couponsDao:failed to extract an instance from the resultSet " + DateUtils.getCurrentDateAndTime());
		}
		return coupon;
	}

	// utility method: assigning the reason of removal for a coupon
	// ('deleted'/'expired') for any coupon inserted to the coupon_history table.
	private void updateCouponsHistory(Connection connection, long couponId, String reason) throws ApplicationException {

		String date = DateUtils.getCurrentDate();

		java.sql.PreparedStatement preparedStatementInsert = null;
		java.sql.PreparedStatement preparedStatementUpdate = null;

		try {
			String sql = "insert into coupon_history (couponId, couponTitle,couponStartDate, couponEndDate, couponAmount, couponType, couponMessage, couponPrice, couponImage, companyId, couponStatus) select * from coupon c where c.couponId=?;";
			preparedStatementInsert = connection.prepareStatement(sql);
			preparedStatementInsert.setLong(1, couponId);
			preparedStatementInsert.executeUpdate();

			sql = "update coupon_history set couponStatus = ?, dateOfRemoval=? where couponId=?";
			preparedStatementUpdate = connection.prepareStatement(sql);
			preparedStatementUpdate.setString(1, reason);
			preparedStatementUpdate.setString(2, date);
			preparedStatementUpdate.setLong(3, couponId);
			preparedStatementUpdate.executeUpdate();

		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR,
					"failed to update coupon's detail of removal. " + DateUtils.getCurrentDateAndTime());
		} finally {
			JdbcUtils.closeResources(null, preparedStatementInsert);
			JdbcUtils.closeResources(null, preparedStatementUpdate);
		}
	}

	// utility method: assigning the details of the soon-to-be-deleted purchases to
	// the purchase_history table.
	private void updatePurchaseHistory(Connection connection, long couponId, String reason)
			throws ApplicationException {

		java.sql.PreparedStatement preparedStatementInsert = null;
		java.sql.PreparedStatement preparedStatementUpdate = null;

		try {
			String sql = "insert into purchase_history (purchaseId, customerId, couponId, companyId, purchasedAmount,purchaseDate) select * from customer_coupon cc where cc.couponId=?;";
			preparedStatementInsert = connection.prepareStatement(sql);
			preparedStatementInsert.setLong(1, couponId);
			preparedStatementInsert.executeUpdate();

			sql = "update purchase_history set couponStatus = ? where couponId=?";
			preparedStatementUpdate = connection.prepareStatement(sql);
			preparedStatementUpdate.setString(1, reason);
			preparedStatementUpdate.setLong(2, couponId);
			preparedStatementUpdate.executeUpdate();

		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR,
					"failed to update details in purchase history. " + DateUtils.getCurrentDateAndTime());
		} finally {
			//// testttttttt
			JdbcUtils.closeResources(null, preparedStatementInsert);
			JdbcUtils.closeResources(null, preparedStatementUpdate);

		}
	}

}
