package com.anat.coupons.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.anat.coupons.beans.Customer;
import com.anat.coupons.enums.ErrorType;
import com.anat.coupons.exceptions.ApplicationException;
import com.anat.coupons.utils.DateUtils;
import com.anat.coupons.utils.JdbcUtils;

@Repository
public class CustomerDao {

//	public CustomerDao() {
//		super();
//	}

	// 1. creating a new customer in the customers table
	public long createCustomer(Customer customer) throws ApplicationException {

		java.sql.PreparedStatement preparedStatement = null;
		Connection connection = null;

		try {
			connection = JdbcUtils.getConnection();
			String sql = "insert into customers (customerName, customerPassword) values (?,?)";

			preparedStatement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);

			preparedStatement.setString(1, customer.getCustomerName());
			preparedStatement.setString(2, customer.getCustomerPassword());

			preparedStatement.executeUpdate();

			ResultSet resultSet = preparedStatement.getGeneratedKeys();
			resultSet.first();
			long customerId = resultSet.getLong(1);
			return customerId;

		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR,
					"error in CustomerDao: failed to create a customer" + DateUtils.getCurrentDateAndTime());
		} finally {
			JdbcUtils.closeResources(connection, preparedStatement);
		}
	}

	// 2. deleting a customer from the table
	@Transactional
	public void deleteCustomer(long customerId) throws ApplicationException {

		java.sql.PreparedStatement preparedStatementInsert = null;
		java.sql.PreparedStatement preparedStatementDelete1 = null;
		java.sql.PreparedStatement preparedStatementDelete2 = null;
		java.sql.PreparedStatement preparedStatementUpdate = null;

		Connection connection = null;

		try {
			connection = JdbcUtils.getConnection();

			// applying JDBC transaction make sure all of the statements are executed
			// successfully,
			// if either one of the statements within the block fails, abort and rollback
			// everything in the transaction block.
			//connection.setAutoCommit(false);

			// copying the data of purchases of the soon-to-be deleted customers to the
			// purchase history table.
			// (since the customer_coupon table only contains data of purchases of active
			// customers and active coupons.)
			String sql = "insert into purchase_history (purchaseId, customerId, couponId, companyId, purchasedAmount,purchaseDate) select * from customer_coupon cc where cc.customerId=?";
			// (purchaseId,customerId,couponId,purchasedAmount,purchaseDate)
			preparedStatementInsert = connection.prepareStatement(sql);
			preparedStatementInsert.setLong(1, customerId);
			preparedStatementInsert.executeUpdate();

			// deleting all of the coupon purchases from the customer_coupons table
			// (because it is a foreign key and the coupon cannot be deleted if foreign keys
			// of it exists in another table)
			sql = "delete from customer_coupon where customerId=?";
			preparedStatementDelete1 = connection.prepareStatement(sql);
			preparedStatementDelete1.setLong(1, customerId);
			preparedStatementDelete1.executeUpdate();

			// deleting the customer from the customers table
			sql = "delete from customers where customerId=?";
			preparedStatementDelete2 = connection.prepareStatement(sql);
			preparedStatementDelete2.setLong(1, customerId);
			preparedStatementDelete2.executeUpdate();

			// updating the customerSstatus field in the purchase_history table
			sql = "update purchase_history set customerStatus = ? where customerId=?";
			preparedStatementUpdate = connection.prepareStatement(sql);
			preparedStatementUpdate.setString(1, "deleted");
			preparedStatementUpdate.setLong(2, customerId);
			preparedStatementUpdate.executeUpdate();

			// Committing the statements (executes only if all statements are successful).
			//connection.commit();

		} catch (Exception e) {
//			try {
//				connection.rollback();
//			} catch (SQLException e1) {
//				throw new ApplicationException(e1, ErrorType.DATABASE_ERROR,
//						"error in CustomerDao -delete customer: failed to rollback previous statements "
//								+ DateUtils.getCurrentDateAndTime());
//			}
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR,
					"error in CustomerDao: failed to delete a customer " + DateUtils.getCurrentDateAndTime());
		} finally {
			JdbcUtils.closeResources(connection, preparedStatementInsert);
			JdbcUtils.closeResources(null, preparedStatementDelete1);
			JdbcUtils.closeResources(null, preparedStatementDelete2);
			JdbcUtils.closeResources(null, preparedStatementUpdate);
		}
	}

	// 3. updating a customer
	public void updateCustomer(Customer customer) throws ApplicationException {

		java.sql.PreparedStatement preparedStatement = null;
		Connection connection = null;

		try {
			connection = JdbcUtils.getConnection();
			String sql = "update customers set customerName=?, customerPassword = ? where customerId=?";

			preparedStatement = connection.prepareStatement(sql);

			preparedStatement.setString(1, customer.getCustomerName());
			preparedStatement.setString(2, customer.getCustomerPassword());
			preparedStatement.setLong(3, customer.getCustomerId());

			preparedStatement.executeUpdate();
		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR,
					"error in CustomerDao: failed to update customers information "
							+ DateUtils.getCurrentDateAndTime());
		} finally {
			JdbcUtils.closeResources(connection, preparedStatement);
		}
	}

	// 4. getting a full customer object from the table, based on its ID
	public Customer getCustomerByCustomerId(long customerId) throws ApplicationException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Customer customer = null;

		try {
			connection = JdbcUtils.getConnection();
			String sql = "SELECT * FROM Customers WHERE customerId=?";
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setLong(1, customerId);
			resultSet = preparedStatement.executeQuery();
			if (!resultSet.next()) {
				return null;
			}
			customer = extractCustomerFromResultSet(resultSet);

		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR,
					"error in CustomerDao: failed to get a customer by ID " + DateUtils.getCurrentDateAndTime());
		} finally {
			JdbcUtils.closeResources(connection, preparedStatement);
		}

		return customer;
	}

	// 5. getting a list of all customers
	public List<Customer> getAllCustomers() throws ApplicationException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Customer customer = null;
		List<Customer> list = new ArrayList<Customer>();

		try {
			connection = JdbcUtils.getConnection();

			String sql = "SELECT * FROM customers ";
			preparedStatement = connection.prepareStatement(sql);
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				customer = extractCustomerFromResultSet(resultSet);
				list.add(customer);
			}
		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR,
					"error in CustomerDao: failed to get a list of all customers " + DateUtils.getCurrentDateAndTime());
		} finally {
			JdbcUtils.closeResources(connection, preparedStatement);
		}

		return list;
	}

	// 6. checking if a login of a customer is valid and returning the customer who logged in
	public Long checkLogin(String customerName, String customerPassword) throws ApplicationException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Long customerId = null;
		
		try {
			// establishing a connection to the DB
			connection = JdbcUtils.getConnection();

			// creating the query
			String sql = "SELECT * FROM customers where customerName=? and customerPassword=? ";
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, customerName);
			preparedStatement.setString(2, customerPassword);
			resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				customerId = resultSet.getLong("customerId");
			} 

		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR,
					"error in CustomerDao: failed to activate the login method " + DateUtils.getCurrentDateAndTime());
		} finally {
			JdbcUtils.closeResources(connection, preparedStatement);
		}
	
		return customerId;
	}

	// 7. checking if the customer exists
	public boolean doesCustomerExistById(long customerId) throws ApplicationException {
		java.sql.PreparedStatement preparedStatement = null;
		Connection connection = null;
		ResultSet resultSet = null;

		try {
			connection = JdbcUtils.getConnection();

			String sql = "select * from customers where customerId=? ";
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setLong(1, customerId);
			resultSet = preparedStatement.executeQuery();

			if (!resultSet.next()) {
				return false;
			} else {
				return true;
			}
		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR,
					"error in CustomerDao:doesCustomerExistById Failed. " + DateUtils.getCurrentDateAndTime());
		} finally {
			JdbcUtils.closeResources(connection, preparedStatement);
		}
	}

	// 9. checking if customer's username already exists
	public boolean doesCustomerNameExist(String customerName) throws ApplicationException {
		java.sql.PreparedStatement preparedStatement = null;
		Connection connection = null;
		ResultSet resultSet = null;

		try {
			connection = JdbcUtils.getConnection();

			String sql = "select * from customers where customerName=? ";
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, customerName);
			resultSet = preparedStatement.executeQuery();

			if (!resultSet.next()) {
				return false;
			} else {
				return true;
			}
		} catch (Exception e) {
			throw new ApplicationException(e, ErrorType.GENERAL_ERROR,
					"error in CustomerDao:doesCompanyExistByName Failed. " + DateUtils.getCurrentDateAndTime());
		} finally {
			JdbcUtils.closeResources(connection, preparedStatement);
		}
	}

	// 9. extracting an object from the resultSet
	private Customer extractCustomerFromResultSet(ResultSet resultSet) throws ApplicationException {
		Customer customer = new Customer();
		try {
			customer.setCustomerId(resultSet.getLong("customerId"));
			customer.setCustomerName(resultSet.getString("customerName"));
			customer.setCustomerPassword(resultSet.getString("customerPassword"));
		} catch (SQLException e) {
			throw new ApplicationException(e, ErrorType.DATABASE_ERROR,
					"error in CustomerDao: failed to extract an instance from the resultSet");
		}
		return customer;
	}

}
