package com.example.stockscope;

import com.example.stockscope.controller.AuthController;
import com.example.stockscope.dto.SignupRequest;
import com.example.stockscope.service.AuthService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class StockscopeApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context= SpringApplication.run(StockscopeApplication.class, args);
//		try{
//			AuthController auth = context.getBean(AuthController.class);
//			SignupRequest sign = new SignupRequest();
//			sign.setEmail("admin@gmail.com");
//			sign.setName("Admin");
//			sign.setPassword("Admin@123");
//			auth.signup(sign);
//		}catch(Exception e){
//			System.out.println(e);
//		}
	}
}