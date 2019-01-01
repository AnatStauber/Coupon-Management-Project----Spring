package com.anat.coupons.servlets;
import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
//import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import com.anat.coupons.utils.CookieUtils;

@Component
public class LoginFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		String pageRequested = httpRequest.getRequestURI().toString();
		
//		if (pageRequested.endsWith("/Login")) {
//			httpResponse =CookieUtils.deleteCookies(httpRequest, httpResponse);
//		}
		// getting the user's session - only if one exists
		HttpSession session = httpRequest.getSession(false);
		
		// if a session does not exist - meaning the user havent logged in yet, then we
		// set the response to error (401)
		if ((session!=null) || (pageRequested.endsWith("/Login")) || (pageRequested.endsWith("/register"))) {
			CookieUtils.setResponseCookies(httpRequest, httpResponse);
			chain.doFilter(httpRequest, httpResponse);
			//return;
		}
			
		else {
			httpResponse.setStatus(401);
		}
//		request.getRequestDispatcher("http://localhost:8080/COUPONS_PROJECT_API's/invalidLogin.html").forward(httpRequest, httpResponse);
		
	}

	//@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub

	}

	//@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}



}