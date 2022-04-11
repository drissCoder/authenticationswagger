package com.authenticationswagger.authenticationswagger.controller;

import java.io.File;
import java.io.InputStream;
import java.net.http.HttpRequest;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.authenticationswagger.authenticationswagger.configuration.JwtUtils;
import com.authenticationswagger.authenticationswagger.configuration.UserDetailsImpl;
import com.authenticationswagger.authenticationswagger.entities.JwtRequest;
import com.authenticationswagger.authenticationswagger.entities.JwtResponse;
import com.authenticationswagger.authenticationswagger.entities.User;
import com.authenticationswagger.authenticationswagger.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@CrossOrigin("*")
@RequestMapping("/api")
@RestController
public class UserController {

	@Autowired
	private UserService userService;
	@Autowired
	private UserDetailsService userDetailsService;
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private JwtUtils jwtUtils;
	@Value("${authenticationswagger.app.uploads}")
	private String uploads;
	
	@RequestMapping(value = "/users/generate", method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public ResponseEntity<byte[]> generate(@RequestParam("count") final Integer count) throws Exception
	{
		
		List<User> users = userService.generateUsers(count);
		ObjectMapper objectMapper = new ObjectMapper();
		String json = objectMapper.writeValueAsString(users);
		byte[] jsonBytes = json.getBytes();
		String fileName = "users.json";
		HttpHeaders respHeaders = new HttpHeaders();
		respHeaders.setContentLength(jsonBytes.length);
		respHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		respHeaders.setCacheControl("must-revalidate, post-check=0, pre-check=0");
		respHeaders.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
		return new ResponseEntity<byte[]>(jsonBytes, respHeaders, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/users/batch", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> usersBatch(@RequestParam("file") MultipartFile file) throws Exception
	{
		String message = "";
		Path path = Paths.get(uploads);
		if(!Files.exists(path)) {
			Files.createDirectory(path);
		}
		InputStream inputStream = file.getInputStream();
		Files.copy(inputStream, path.resolve(file.getOriginalFilename()), StandardCopyOption.REPLACE_EXISTING);
		ObjectMapper mapper = new ObjectMapper();
		Object[] objects = mapper.readValue(new File(uploads + File.separator + file.getOriginalFilename()), Object[].class);
		List<User> users = Arrays.stream(objects).map(object -> mapper.convertValue(object, User.class)).collect(Collectors.toList());
		int nombreLigne = 0;
		for(User user: users) {
			if(!userService.saveUser(user, user.getRoles().get(0))) {
				nombreLigne++;
			}
		}
		
	    message = "Le fichier importé avec succès : " + file.getOriginalFilename() + " avec nombre de lignes importés " + nombreLigne
	    		+ " et non importés " + (users.size()-nombreLigne);
	    
	    return ResponseEntity.status(HttpStatus.OK).body(message);
	    
	  }
	
	
	@RequestMapping(value = "/auth", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> generateAuthenticationToken(@RequestBody final JwtRequest loginRequest) throws Exception
	{
		Authentication authentication = authenticationManager.authenticate(
		        new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

		    SecurityContextHolder.getContext().setAuthentication(authentication);
		    String jwt = jwtUtils.generateJwtToken(authentication);
		    System.out.println(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		    System.out.println(authentication.getPrincipal());
		    
		    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();  
		    System.out.println(userDetails.getUsername());
		    List<String> roles = userDetails.getAuthorities().stream()
		        .map(item -> item.getAuthority())
		        .collect(Collectors.toList());
		    System.out.println(roles);
		    return ResponseEntity.ok(new JwtResponse(jwt));
	}
	
	@SecurityRequirement(name = "Authorization")
	@RequestMapping(value = "/users/me", method = RequestMethod.GET)
	public ResponseEntity<?> consulterMonPofil()
	{
		return ResponseEntity.ok(userDetailsService.loadUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName()));
	}
	
	@SecurityRequirement(name = "Authorization")
	@RequestMapping(value = "/users/{username}", method = RequestMethod.GET)
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> consulterProfil(@PathVariable("username") final String username) throws Exception
	{	
		 return ResponseEntity.ok(userDetailsService.loadUserByUsername(username)); 		
	}
}
