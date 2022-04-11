package com.authenticationswagger.authenticationswagger.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.authenticationswagger.authenticationswagger.entities.Role;
import com.authenticationswagger.authenticationswagger.entities.User;

public interface RoleRepository extends JpaRepository<Role, Long>{

	public Optional<Role> findByRoleName(String roleName); 
}
