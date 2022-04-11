package com.authenticationswagger.authenticationswagger.service;

import java.util.List;

import com.authenticationswagger.authenticationswagger.entities.Role;
import com.authenticationswagger.authenticationswagger.entities.User;


public interface UserService {

	public User saveUser(String username, String password);

	public boolean saveUser(User user, Role role);

	public Role saveRole(Role role);

	public User loadUserByUsername(String username);

	public void addRoleToUser(String username, String roleName);

	public List<User> getAllUsers();
	
	public List<User> generateUsers(final Integer count);
}
