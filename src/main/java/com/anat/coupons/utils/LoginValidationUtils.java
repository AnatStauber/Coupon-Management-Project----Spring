package com.anat.coupons.utils;

import java.util.regex.Pattern;

public class LoginValidationUtils {

	// pattern of input mail address. the rules are:
	// the local part can contain letters/numbers/scores and dots. after the local
	// part there must be an '@' sign and the domain can contain only letters, dots,
	// scores.domain last part has to be 2-5 letters.
	final static Pattern EMAIL_PATTERN = Pattern
			.compile("^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$");

	// pattern of input password.
	// the rules are : password must contain 8-10 characters, can be made of only
	// uppercase, lowercase and digits. it must contain at least on of each.
	final static Pattern PASSWORD_PATTERN = Pattern
			.compile("^(?=[A-Za-z0-9]*[a-z])(?=[A-Za-z0-9]*[A-Z])(?=[A-Za-z0-9]*\\d)[A-Za-z0-9]{8,10}$");

	// validating email addresses
	public static boolean emailValidation(String email) {
		return (EMAIL_PATTERN.matcher(email).matches());
	}

	// validating passwords
	public static boolean passwordValidation(String password) {
		return (PASSWORD_PATTERN.matcher(password).matches());
	}

}
