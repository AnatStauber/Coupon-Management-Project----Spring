package com.anat.coupons.exceptions;


import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.Response;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

	@ControllerAdvice
	public class ExceptionsHandler { //implements ExceptionMapper<Throwable>{
	
		@ExceptionHandler ({ApplicationException.class, Throwable.class})
		public void ExceptionHandling (HttpServletResponse response , Throwable exception) {
		
			exception.getStackTrace();
			
			if (exception instanceof ApplicationException) {
				ApplicationException appException = (ApplicationException) exception;
				String errorMessage = appException.getErrorType().getErrorDefinition();
				String internalMessage = exception.getMessage();
				int errorCode = appException.getErrorType().getErrorCode();
				System.out.println(errorCode);
				response.setStatus(errorCode);
				response.setHeader(errorMessage, internalMessage);
			}
			else {
	
			//here we handle an exception that we didn't catch and wrapped 
				String internalMessage = exception.getMessage();
				response.setStatus(601);
				response.setHeader("general exception", internalMessage);
			}
		}
}
