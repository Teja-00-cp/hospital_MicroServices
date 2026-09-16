package com.example.order;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.order.Model.User;
import com.example.order.Model.User.Role;
import com.example.order.Repository.UserRepo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@SpringBootApplication
@RestController
@RequestMapping(value = "/order")

@EnableDiscoveryClient
public class OrderApplication  implements CommandLineRunner {
	@Autowired
	private Environment environment;
	@Autowired
	private UserRepo userRepo;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@GetMapping("/welcome")
	public String ss(){
		return "Welcome iam from order"+environment.getProperty("server.port");
	}
	@GetMapping("/order")
	public String getMethodName() {
		return "I am from Order";
	}
	
	
	

	public static void main(String[] args) {
		SpringApplication.run(OrderApplication.class, args);
	}
	@Override
	public void run(String... args) throws Exception {
		long count = userRepo.count();
		if (count == 0) {
			User user = new User();
			user.setUsername("admin");
			user.setPassword(passwordEncoder.encode("admin"));
			user.setRole(Role.ADMIN);
			userRepo.save(user);
		}
	}

}
