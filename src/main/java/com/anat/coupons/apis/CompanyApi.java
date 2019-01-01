package com.anat.coupons.apis;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.anat.coupons.beans.Company;
import com.anat.coupons.controllers.CompanyController;
import com.anat.coupons.exceptions.ApplicationException;


@RestController
@RequestMapping(value = "/companies")
public class CompanyApi {
	
	@Autowired
	CompanyController companyController;
	
	@PostMapping ("/createNewCompany")
	 public void createCompany (@RequestBody Company company) throws ApplicationException {
		long id = this.companyController.createCompany(company);	
		System.out.println("company #"+ id+ " created");
		//http://localhost:8080/CouponProjectApisV2/rest/companies/createNewCompany
	 }

	@PutMapping ("/update")
	public void updateCompany (@RequestBody Company company)  throws ApplicationException {
		 this.companyController.updateCompany(company);
		 long id = company.getCompanyId();
		 System.out.println("the company with id : " + id + " has updated");
		 //http://localhost:8080/CouponProjectApisV2/companies/update
	 } 
	
	@DeleteMapping("/{companyId}")
	public void deleteCompany (@PathVariable ("companyId") long companyId) throws ApplicationException {
		 this.companyController.deleteCompany(companyId);
		 //http://localhost:8080/CouponProjectApisV2/companies/1
	 }
	
	@GetMapping ("/{companyId}")
	public Company getCompany(@PathVariable ("companyId") long companyId) throws ApplicationException{
		 return (companyController.getCompany(companyId));
		 //http://localhost:8080/CouponProjectApisV2/companies/17/
	 }

	@GetMapping ("/getAll")
	public List<Company> getAllCompanies () throws ApplicationException {
		 return this.companyController.getAllCompanies();
//		 http://localhost:8080/CouponProjectApisV2/companies/getAll
	}
	
	
}
