package com.authenticationswagger.authenticationswagger.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtRequest {

	
	private String username;
	private String password;
}
