package com.anat.coupons.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

	public static String getCurrentDateAndTime() {
		// Creating a format for the date
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		// By calling the date constructor, we get the current date
		Date today = new Date();
		// formatting the date to string
		String currentDateAndTime = dateFormat.format(today);

		return currentDateAndTime;
	}

	public static String getCurrentDate() {
		// creating a matching format for the date as it appears on the Database
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		// By calling the date constructor, we get the current date
		Date today = new Date();
		// formatting the date to string
		String currentDate = dateFormat.format(today);

		return currentDate;
	}

	public static boolean isDate1AfterDate2(String strDate1, String strDate2) {
		try {
			// creating a matching format for the date as it appears on the Database
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			// Parsing the dates from string to date type.
			Date date1 = dateFormat.parse(strDate1);
			Date date2 = dateFormat.parse(strDate2);
			// We check if date1 is before date2
			if (date1.after(date2)) {
				return true;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean isCurrentDateAfterEndDate(String strEndDate) {
		// We check if the coupon's end date is expired
		return isDate1AfterDate2(getCurrentDate(), strEndDate);
		/*
		 * this is the return line if we want that the company wont be able to enter an
		 * end date that is earlier than tomorrow 9because the system is updated every
		 * night or so.? return isDate1AfterDate2(getCurrentDate()+1, strEndDate);
		 */

	}

	// enforcing the date format on any date inserted by the user:
	public static boolean isDateInFormat(String date) {

		boolean checkFormat;
		checkFormat = date.matches("^((2000|2400|2800|(2[0-9](0[48]|[2468][048]|[13579][26])))-02-29)$"
				+ "|^(((2[0-9])[0-9]{2})-02-(0[1-9]|1[0-9]|2[0-8]))$"
				+ "|^(((2[0-9])[0-9]{2})-(0[13578]|10|12)-(0[1-9]|[12][0-9]|3[01]))$"
				+ "|^(((2[0-9])[0-9]{2})-(0[469]|11)-(0[1-9]|[12][0-9]|30))$");

		return checkFormat;

	}
}
