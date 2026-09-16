package com.example.order.controller;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
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
import com.example.order.confif.JwtUtil;

import jakarta.transaction.Transactional;


@RestController
@RequestMapping("/order/user")
public class UserContr {


	private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    public UserContr(AuthenticationManager authenticationManager,
                     UserDetailsService userDetailsService, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

	
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
    public ResponseEntity<String> authenticateUser(@RequestBody AuthRequest authRequest) {
        try {
            // 1. Authenticate username and encrypted password via Spring Security
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        // 2. Fetch User Details
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
        User user = userSer.findByUsername(authRequest.getUsername());
        // 3. Format the role to map Spring Security expectations (e.g., "ROLE_PATIENT")
        String formattedRole = "ROLE_" + user.getRole();
        // 4. Generate the JWT string containing the role
        final String token = jwtUtil.generateToken(userDetails, formattedRole);

        // 5. Return the raw string directly back to jQuery's success: function (token)
        return ResponseEntity.ok(token);
    }

}

