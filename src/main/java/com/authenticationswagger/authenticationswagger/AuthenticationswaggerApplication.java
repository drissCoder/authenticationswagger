package com.authenticationswagger.authenticationswagger;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.authenticationswagger.authenticationswagger.entities.Role;
import com.authenticationswagger.authenticationswagger.service.UserService;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@SpringBootApplication
@SecurityScheme(name = "Authorization", scheme = "basic", type = SecuritySchemeType.APIKEY, in = SecuritySchemeIn.HEADER)
@OpenAPIDefinition(info = @Info(title = "Users API", version = "2.0", description = "Users Information"))
public class AuthenticationswaggerApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthenticationswaggerApplication.class, args);
	}
	
	
	
	@Bean
	CommandLineRunner start(final UserService userService)
	{
		return args -> {
			userService.saveRole(new Role(null, "ROLE_USER"));
			userService.saveRole(new Role(null, "ROLE_ADMIN"));
		};



	}
}
