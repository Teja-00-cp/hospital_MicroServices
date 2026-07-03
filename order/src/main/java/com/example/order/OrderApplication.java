package com.example.order;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@SpringBootApplication
@RestController
@RequestMapping(value = "/order")

@EnableDiscoveryClient
public class OrderApplication {
	@Autowired
	private Environment environment;
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

}
