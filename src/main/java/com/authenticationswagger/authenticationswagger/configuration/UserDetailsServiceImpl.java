package com.authenticationswagger.authenticationswagger.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException.Forbidden;

import com.authenticationswagger.authenticationswagger.entities.User;
import com.authenticationswagger.authenticationswagger.repository.UserRepository;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {
  @Autowired
  UserRepository userRepository;

  @Override
  @Transactional
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	  User user = null;
	  if(username.contains("@")) {
		  user = userRepository.findByEmail(username)
			        .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
	  }else {
		   user = userRepository.findByUsername(username)
			        .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username)); 
	  }
    

    return UserDetailsImpl.build(user);
  }

}
