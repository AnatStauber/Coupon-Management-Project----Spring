package com.anat.coupons.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.anat.coupons.beans.Company;
import com.anat.coupons.enums.ErrorType;
import com.anat.coupons.utils.DateUtils;
import com.anat.coupons.utils.JdbcUtils;
import com.anat.coupons.exceptions.ApplicationException;

@Repository
public class CompanyDao {

//	// constructor
//	public CompanyDao() {
//		super();
//	}

	// 1. creating a new company in the BD
	public long createCompany(Company company) throws ApplicationException {

		java.sql.PreparedStatement preparedStatement = null;
		Connection connection = null;

		try {

			// establishing a connection
			connection = JdbcUtils.getConnection();

			// SQL query
			String sql = "insert into company (companyName, companyPassword, companyEmail) values (?,?,?)";

			preparedStatement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);

			// inserting values to the query (prevents SQL injection)
			preparedStatement.setString(1, company.getCompanyName());
			preparedStatement.setString(2, company.getCompanyPassword());
			preparedStatement.setString(3, company.getCompanyEmail());

			// executing the query
			preparedStatement.executeUpdate();

			ResultSet resultSet = preparedStatement.getGeneratedKeys();
			resultSet.first();
			long comapnyId = resultSet.getLong(1);
			return comapnyId;
		}

		catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR,
					"error in CompanyDao: failed to create company. " + DateUtils.getCurrentDateAndTime());
		} finally {
			JdbcUtils.closeResources(connection, preparedStatement);
		}
	}

	// 2. delete a company from the DB (and it's coupons and active purchases.)
	@Transactional
	public void deleteCompany(long companyId) throws ApplicationException {

		java.sql.PreparedStatement preparedStatementDelete1 = null;
		java.sql.PreparedStatement preparedStatementDelete2 = null;
		java.sql.PreparedStatement preparedStatementDelete3 = null;
		Connection connection = null;

		try {
			
			connection = JdbcUtils.getConnection();

			// applying JDBC transaction make sure all of the statements are executed
			// successfully,
			// if either one of the statements within the block fails, abort and rollback
			// everything in the transaction block.
			//connection.setAutoCommit(false);

			// copying the data of the soon-to-be-deleted coupons and purchases to the
			// coupons history table and the purchase history table.
			updatePurchaseHistory(connection, companyId, "company deleted");
			updateCouponsHistory(connection, companyId, "company deleted");

			// deleting all of the company's coupon purchases from the customer_coupons
			// table
			// (because it is a foreign key and the company cannot be deleted if foreign
			// keys of it exists in another tables)
			String sql = "delete from customer_coupon where companyId=?";
			preparedStatementDelete1 = connection.prepareStatement(sql);
			preparedStatementDelete1.setLong(1, companyId);
			preparedStatementDelete1.executeUpdate();

			// deleting the company's coupon from the coupons table
			sql = "delete from coupon where companyId=?";
			preparedStatementDelete2 = connection.prepareStatement(sql);
			preparedStatementDelete2.setLong(1, companyId);
			preparedStatementDelete2.executeUpdate();

			// deleting the company from the companies table
			sql = "delete from company where companyId=?";
			preparedStatementDelete3 = connection.prepareStatement(sql);
			preparedStatementDelete3.setLong(1, companyId);
			preparedStatementDelete3.executeUpdate();

			// Committing the statements (executes only if all statements are successful).
			//connection.commit();
		} catch (Exception e) {
//			try {
//				connection.rollback();
//			} catch (SQLException e1) {
//				throw new ApplicationException(e1, ErrorType.DATABASE_ERROR,
//						"delete company: failed to rollback previous statements " + DateUtils.getCurrentDateAndTime());
//			}
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR,
					"failed to delete company " + DateUtils.getCurrentDateAndTime());
		} finally {
			JdbcUtils.closeResources(connection, preparedStatementDelete1);
			JdbcUtils.closeResources(null, preparedStatementDelete2);
			JdbcUtils.closeResources(null, preparedStatementDelete3);
		}
	}

	// 3. updating the company's info
	public void updateCompany(Company company) throws ApplicationException {

		java.sql.PreparedStatement preparedStatement = null;
		Connection connection = null;

		try {
			connection = JdbcUtils.getConnection();
			String sql = "update company set companyName=?, companyPassword=?, companyEmail=? where companyId=?";

			preparedStatement = connection.prepareStatement(sql);

			preparedStatement.setString(1, company.getCompanyName());
			preparedStatement.setString(2, company.getCompanyPassword());
			preparedStatement.setString(3, company.getCompanyEmail());
			preparedStatement.setLong(4, company.getCompanyId());

			preparedStatement.executeUpdate();

		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR,
					"error in CompanyDao: failed to update company" + DateUtils.getCurrentDateAndTime());
		} finally {
			JdbcUtils.closeResources(connection, preparedStatement);
		}
	}

	// 4.getting a full company object out of the DB based on it's Id
	public Company getCompanyByCompanyId(long companyId) throws ApplicationException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Company company = null;

		try {
			connection = JdbcUtils.getConnection();
			String sql = "SELECT * FROM COMPANY WHERE companyId=?";
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setLong(1, companyId);
			resultSet = preparedStatement.executeQuery();

			if (!resultSet.next()) {
				return null;
			}
			company = extractCompanyFromResultSet(resultSet);

		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR,
					"error in CompanyDao: failed to get a company by ID " + DateUtils.getCurrentDateAndTime());
		} finally {
			JdbcUtils.closeResources(connection, preparedStatement);
		}
		return company;
	}

	// 5. producing a list of all companies in the DB
	public List<Company> getAllCompanies() throws ApplicationException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Company company = null;
		List<Company> list = new ArrayList<Company>();

		try {
			connection = JdbcUtils.getConnection();

			String sql = "SELECT * FROM COMPANY ";
			preparedStatement = connection.prepareStatement(sql);
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				company = extractCompanyFromResultSet(resultSet);
				list.add(company);
			}
		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR,
					"error in CompanyDao: failed to gat list of all companies " + DateUtils.getCurrentDateAndTime());
		} finally {
			JdbcUtils.closeResources(connection, preparedStatement);
		}

		return list;
	}

	// 6. checking login and getting the company who logged in
	public Long checkLogin(String companyName, String companyPassword) throws ApplicationException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Long companyId = null;
		try {
			connection = JdbcUtils.getConnection();

			String sql = "SELECT * FROM COMPANY where companyName=? and companyPassword=?";
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, companyName);
			preparedStatement.setString(2, companyPassword);
			resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				companyId = resultSet.getLong("companyId");
			}
			
			} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR,
					"error in CompanyDao: failed to check login " + DateUtils.getCurrentDateAndTime());
		} finally {
			JdbcUtils.closeResources(connection, preparedStatement);
		}
		
		return companyId;
	}

	// 7. checking if the company exists
	public boolean doesCompanyExistById(long companyId) throws ApplicationException {
		java.sql.PreparedStatement preparedStatement = null;
		Connection connection = null;
		ResultSet resultSet = null;

		try {
			connection = JdbcUtils.getConnection();

			String sql = "select * from company where companyId=? ";
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setLong(1, companyId);
			resultSet = preparedStatement.executeQuery();

			if (!resultSet.next()) {
				return false;
			} else {
				return true;
			}
		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR,
					"error in CompanyDao:doesCompanyExistById Failed. " + DateUtils.getCurrentDateAndTime());
		} finally {
			JdbcUtils.closeResources(connection, preparedStatement);
		}
	}

	// 8.
	public boolean doesCompanyNameExist(String name) throws ApplicationException {
		java.sql.PreparedStatement preparedStatement = null;
		Connection connection = null;
		ResultSet resultSet = null;

		try {
			connection = JdbcUtils.getConnection();

			String sql = "select * from company where companyName=? ";
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, name);
			resultSet = preparedStatement.executeQuery();

			if (!resultSet.next()) {
				return false;
			} else {
				return true;
			}
		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR,
					"error in CompanyDao:doesCompanyExistByName Failed. " + DateUtils.getCurrentDateAndTime());
		} finally {
			JdbcUtils.closeResources(connection, preparedStatement);
		}
	}

	// 9. extracting an instance from the resultSet
	private Company extractCompanyFromResultSet(ResultSet resultSet) throws ApplicationException {

		Company company = new Company();
		try {
			company.setCompanyId(resultSet.getLong("companyId"));
			company.setCompanyName(resultSet.getString("companyName"));
			company.setCompanyPassword(resultSet.getString("companyPassword"));
			company.setCompanyEmail(resultSet.getString("companyEmail"));
		} catch (SQLException e) {
			throw new ApplicationException(e, ErrorType.DATABASE_ERROR,
					"error in CompanyDao: failed to extract an instance from the resultSet");
		}
		return company;
	}

	// utility method: assigning the reason of removal for a coupon
	// ('deleted'/'expired') for any coupon inserted to the coupon_history table.
	private void updateCouponsHistory(Connection connection, long companyId, String reason)
			throws ApplicationException {

		String date = DateUtils.getCurrentDate();

		java.sql.PreparedStatement preparedStatementInsert = null;
		java.sql.PreparedStatement preparedStatementUpdate = null;

		try {
			String sql = "insert into coupon_history (couponId, couponTitle,couponStartDate, couponEndDate, couponAmount, couponType, couponMessage, couponPrice, couponImage, companyId, couponStatus) select * from coupon c where c.companyId=?;";
			preparedStatementInsert = connection.prepareStatement(sql);
			preparedStatementInsert.setLong(1, companyId);
			preparedStatementInsert.executeUpdate();

			sql = "update coupon_history set couponStatus = ?, dateOfRemoval=? where companyId=?";
			preparedStatementUpdate = connection.prepareStatement(sql);
			preparedStatementUpdate.setString(1, reason);
			preparedStatementUpdate.setString(2, date);
			preparedStatementUpdate.setLong(3, companyId);
			preparedStatementUpdate.executeUpdate();

		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR,
					"error on companyDao: failed to update coupon's detail of removal. "
							+ DateUtils.getCurrentDateAndTime());
		} finally {
			JdbcUtils.closeResources(null, preparedStatementInsert);
			JdbcUtils.closeResources(null, preparedStatementUpdate);
		}
	}

	// utility method: assigning the details of the soon-to-be-deleted purchases to
	// the purchase_history table.
	private void updatePurchaseHistory(Connection connection, long companyId, String reason)
			throws ApplicationException {

		java.sql.PreparedStatement preparedStatementInsert = null;
		java.sql.PreparedStatement preparedStatementUpdate = null;

		//////////////////////////////////
		try {
			String sql = "insert into purchase_history (purchaseId, customerId, couponId, companyId, purchasedAmount,purchaseDate) select * from customer_coupon cc where cc.companyId=?;";
			preparedStatementInsert = connection.prepareStatement(sql);
			preparedStatementInsert.setLong(1, companyId);
			preparedStatementInsert.executeUpdate();

			sql = "update purchase_history set couponStatus = ?, companyStatus=? where companyId=?";
			preparedStatementUpdate = connection.prepareStatement(sql);
			preparedStatementUpdate.setString(1, reason);
			preparedStatementUpdate.setString(2, "deleted");
			preparedStatementUpdate.setLong(3, companyId);
			preparedStatementUpdate.executeUpdate();

		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR,
					"error on companyDao: failed to update details in purchase history. "
							+ DateUtils.getCurrentDateAndTime());
		} finally {
			JdbcUtils.closeResources(null, preparedStatementInsert);
			JdbcUtils.closeResources(null, preparedStatementUpdate);

		}
	}

}
