package com.anat.coupons.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieUtils {

	
		public static HttpServletResponse setResponseCookies (HttpServletRequest request, HttpServletResponse response) {
		
			Cookie [] cookies = request.getCookies();
			if (request.getCookies()!=null) {
				for (Cookie cookie : cookies) {
					response.addCookie(cookie);
				}
			}
		return response;
		
		}
		public static void printCookies (HttpServletRequest request, HttpServletResponse response){
			Cookie[] cookies = request.getCookies();
			if(cookies!=null) {
			for (Cookie cookie : cookies) {
				System.out.println(cookie.getName()+ " : "+ cookie.getValue() + " , " + cookie.getPath() );
			}
		}
	}
		public static Cookie getCookie(HttpServletRequest request, String name) {
		    Cookie[] cookies = request.getCookies();
		    if (cookies != null) {
		        for (Cookie cookie : cookies) {
		            if (cookie.getName().equals(name)) {
		                return cookie;
		            }
		        }
		    }
		    return null;
		}
		public static HttpServletResponse deleteCookies(HttpServletRequest request,HttpServletResponse response) {
			 Cookie[] cookies = request.getCookies();
			 if (cookies!=null) {
				for (Cookie cookie : cookies) {
					cookie.setMaxAge(0);
				    cookie.setPath("/");
				    response.addCookie(cookie);
				 }
			 }
			 return response;
		 }
}		

	
	
	
	
	
	
