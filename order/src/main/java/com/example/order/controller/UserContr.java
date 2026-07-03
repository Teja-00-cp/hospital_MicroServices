package com.example.order.controller;


import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.order.Dto.AuthRequest;
import com.example.order.Dto.DoctorDao;
import com.example.order.Dto.PatientDao;
import com.example.order.Model.User;
import com.example.order.Service.UserSer;

import jakarta.transaction.Transactional;


@RestController
@RequestMapping("/order/user")
public class UserContr {


	
	@Autowired
	private UserSer userSer;

	@Transactional
	@PostMapping("/addPatient")
	public void addUser(@RequestBody PatientDao patientDao){
		userSer.addPatient(patientDao);
		
	}
	@Transactional
	@PostMapping("/addDoctor")
	public void addDoctor(@RequestBody DoctorDao doctorDao){
		userSer.addDoctor(doctorDao);
		
	}
//	@GetMapping("/get/{id}/{pass}")
//	public String getUser(@PathVariable long id, @PathVariable String pass) {
//		
//		return userSer.geDta(id, pass)?"Login-success":"Login-Fail";
//	}
	@GetMapping("/getall")
//    @PreAuthorize("hasAuthority('ADMIN')")
	public Iterable<User> getallData() {
		return userSer.getallData();
	}
	@PostMapping("/forgot/forgotreq/{name}")
	public void forGotrer(@PathVariable String name) {
		
		 userSer.forGot(name);
	}
	@GetMapping("/loadtest")
	public String loadTest() {
		try {
            // This pauses the current thread for exactly 5 seconds
            TimeUnit.SECONDS.sleep(5); 
            // Alternative: Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            // System.err.println("❌ Wait was interrupted: " + e.getMessage());
        }
		// System.out.println("Load test completed successfully.");
		return "Load test completed successfully -- User Service";
	}
	@PostMapping("/forgot/forgotreqotp/{num}/{pass}/{userName}")
	public String veOtp(@PathVariable int num, @PathVariable String pass, @PathVariable String userName) {
		System.out.println("clickeg");
		return userSer.veOtp(num,pass,userName);
	 }
	@GetMapping("/forall")
	public String forAll() {
		return "All can view";
	}
	@GetMapping("/alluservrr")
	public String forAllUsers() {
		return "All Users can vieew";
	}
	@PostMapping("/authenticate")
    public String authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
        return userSer.authenticateAndGetToken(authRequest);
    }

}
