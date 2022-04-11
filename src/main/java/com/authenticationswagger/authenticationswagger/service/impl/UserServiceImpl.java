package com.authenticationswagger.authenticationswagger.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.authenticationswagger.authenticationswagger.entities.Role;
import com.authenticationswagger.authenticationswagger.entities.User;
import com.authenticationswagger.authenticationswagger.repository.RoleRepository;
import com.authenticationswagger.authenticationswagger.repository.UserRepository;
import com.authenticationswagger.authenticationswagger.service.UserService;
import com.github.javafaker.Faker;

import net.bytebuddy.utility.RandomString;

@Service
public class UserServiceImpl implements UserService {

	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private PasswordEncoder bCryptPasswordEncoder;
	
	@Override
	public User saveUser(String username, String password) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public boolean saveUser(User user, Role role) {
		
		Boolean isDoublant = true;
		if(!userRepository.findByUsername(user.getUsername()).isPresent()) {
			user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
			final Role role1 = roleRepository.findByRoleName(role.getRoleName()).get();
			user.getRoles().clear();
			user.getRoles().add(role1);
			userRepository.saveAndFlush(user);
			isDoublant = false;
		}
		return isDoublant;
	}

	@Override
	public Role saveRole(Role role) {
		// TODO Auto-generated method stub
		return roleRepository.save(role);
	}

	@Override
	public User loadUserByUsername(String username) {
		
		
		return userRepository.findByUsername(username).get();
	}

	@Override
	public void addRoleToUser(String username, String roleName) {
		// TODO Auto-generated method stub
		final User user = userRepository.findByUsername(username).get();
		final Role role = roleRepository.findByRoleName(roleName).get();
		user.getRoles().add(role);
	}

	@Override
	public List<User> getAllUsers() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public List<User> generateUsers(final Integer count){
		
		List<User> users = new ArrayList<User>();
		final Faker faker = new Faker();
		for (int i = 0; i < count; i++)
		{
			final User user = new User();
			try {
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				user.setBirthDate(df.format(faker.date().birthday()));
			}catch(Exception e) {}
			String firstName = faker.name().firstName();
			String lastName = faker.name().lastName();
			user.setFirstName(firstName);
			user.setLastName(lastName);
			user.setCity(faker.address().cityName());
			user.setCountry(faker.address().country());
			user.setAvatar(faker.avatar().image());
			user.setCompany(faker.company().name());
			user.setJobPosition(faker.job().position());
			user.setMobile(faker.phoneNumber().phoneNumber());
			user.setUsername(firstName.toLowerCase() + "." + lastName.toLowerCase());
			user.setEmail(firstName.toLowerCase() + "." + lastName.toLowerCase() + "@gmail.com");
			user.setPassword(RandomString.make(new Random().nextInt(10 - 6) + 6));
			if(i % 2 == 0) {
				user.getRoles().add(new Role(null,"ROLE_ADMIN"));
				
			}else {
				user.getRoles().add(new Role(null,"ROLE_USER"));
			}
			users.add(user);
		}
		return users;
	}
}
