package com.anat.coupons.apis;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.anat.coupons.beans.UserLoginDetails;
import com.anat.coupons.controllers.CompanyController;
import com.anat.coupons.controllers.CustomerController;
import com.anat.coupons.exceptions.ApplicationException;
import com.anat.coupons.utils.CookieUtils;


@RestController
@RequestMapping(value = "/Login")
public class LoginApi {

	@Autowired
	CompanyController companyController;
	@Autowired
	CustomerController customerController;

	@PostMapping
	public void login(HttpServletRequest request, HttpServletResponse response, @RequestBody UserLoginDetails userLoginDetails) throws ApplicationException {
		
		String userType = userLoginDetails.getUserType();
		String userName = userLoginDetails.getUserName();
		String password = userLoginDetails.getPassword();
		
		
		//if the user is a company 
		if(userType.equals("Company")){
			
			
			Long companyId = this.companyController.login(userName, password);//getting the company that just logged in
			System.out.println(companyId);
		
			if (!companyId.equals(null)){
				request.getSession();
				request.getSession().setMaxInactiveInterval(30*60);
//				HttpSession session = request.getSession(); // if the credentials are correct, get a session.
				
				Cookie userTypeCookie = new Cookie("userType", userType);
				response.addCookie(userTypeCookie);
				
				String userId =String.valueOf(companyId);
				Cookie idCookie = new Cookie("userId",userId);
				response.addCookie(idCookie);	
				
				Cookie loginCookie = new Cookie("loginStatus","success");
				response.addCookie(loginCookie);
				
				CookieUtils.printCookies(request, response);
//				request.getRequestDispatcher("http://localhost:8080/COUPONS_PROJECT_API's/companyEntrance.html").forward(request, response);
				response.setStatus(200);
				//return Response.status(200).entity(companyId).build();
			}
			else {
				response.setStatus(401);
				//return Response.status(401).entity(null).build(); 
			}
		}
		else if (userType.equals("Customer")){
			
			
			Long customerId = this.customerController.login(userName, password);
			System.out.println(customerId);
			
			if (!customerId.equals(null)){
				
				HttpSession session =request.getSession(); // if the credentials are correct, get a session.
				session.setMaxInactiveInterval(60*30);
				
				Cookie userTypeCookie = new Cookie("userType", userType);
				response.addCookie(userTypeCookie);
				
				String userId =String.valueOf(customerId);
				Cookie idCookie = new Cookie("userId",userId);
				response.addCookie(idCookie);	
				
				Cookie loginCookie = new Cookie("loginStatus","success");
				response.addCookie(loginCookie);
				
				//request.getRequestDispatcher("http://localhost:8080/COUPONS_PROJECT_API's/customerEntrance.html").forward(request, response);
				response.setStatus(200);
				//return Response.status(200).entity(customerId).build();
			} 
			else {
				response.setStatus(401);
			}
				//return Response.status(401).entity(null).build();
			}
		else {
		response.setStatus(401);
		}
	}
}
	

	
	
	
	
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
