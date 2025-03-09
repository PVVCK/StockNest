package com.ecommerce.stocknest.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.stocknest.dto.AddUsersDTO;
import com.ecommerce.stocknest.model.Users;
import com.ecommerce.stocknest.response.APIResponse;
import com.ecommerce.stocknest.security.JWTUtil;
import com.ecommerce.stocknest.service.user.UsersServiceImpl;

@RestController
@RequestMapping("${api.prefix}/auth")
public class AuthController {
	
	@Autowired
	private JWTUtil jwtUtil;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private UsersServiceImpl usersServiceImpl;
	
	
	 @PostMapping("/register-user")
	    public ResponseEntity<APIResponse> registerUser(@RequestBody AddUsersDTO addUsersDTO) {
	        // Hash the password before saving
		 
		 String role = (addUsersDTO.getRole() == null || addUsersDTO.getRole().isEmpty()) 
                 ? "ROLE_USER" 
                 : "ROLE_" + addUsersDTO.getRole().toUpperCase();

		 addUsersDTO.setRole(role); // Ensure role is stored as ROLE_USER or ROLE_ADMIN
		 
		 addUsersDTO.setPassword(passwordEncoder.encode(addUsersDTO.getPassword()));

//	        usersServiceImpl.createUser(addUsersDTO);
//	        
	        
	        try {
	            
	            APIResponse apiResponse = new APIResponse();
	    		apiResponse.setSuccess(true);	
	    		apiResponse.setTimestamp(LocalDateTime.now());
	    		apiResponse.setData(usersServiceImpl.createUser(addUsersDTO));
	    		apiResponse.setErrorMessage(null);
	    		
	            return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
	        } 
			
			catch (Exception e) {
				// TODO: handle exception
				throw e;
			}
	    }

	 @PostMapping("/login")
	 public ResponseEntity<APIResponse> login(@RequestParam String username, @RequestParam String password) {
	    
	     
	     try {
	            
	    	 APIResponse apiResponse = new APIResponse();
	    	 Users user = usersServiceImpl.findUserByUserName(username);

		     // Compare hashed password
		     if (!passwordEncoder.matches(password, user.getPassword())) {
		    	 apiResponse.setSuccess(false);
		    	 apiResponse.setTimestamp(LocalDateTime.now());
		    	 apiResponse.setErrorMessage("Invalid Credentials");
		         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
		     }

		     String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
		     
	           
	    		apiResponse.setSuccess(true);	
	    		apiResponse.setTimestamp(LocalDateTime.now());
	    		apiResponse.setData(token);
	            return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
	        } 
			
			catch (Exception e) {
				// TODO: handle exception
				throw e;
			}
	 }
}
