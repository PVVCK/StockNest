package com.ecommerce.stocknest.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.stocknest.dto.AddUsersDTO;
import com.ecommerce.stocknest.dto.FetchUsersDTO;
import com.ecommerce.stocknest.exception.ExecutionFailed;
import com.ecommerce.stocknest.response.APIResponse;
import com.ecommerce.stocknest.service.user.UsersServiceImpl;

import jakarta.validation.Valid;

@RestController
@RequestMapping("${api.prefix}/user")
public class UsersController {
	
	@Autowired
	private UsersServiceImpl userServiceImpl;

	@PostMapping("/create-user")
	public ResponseEntity<APIResponse> createUser(@Valid @RequestBody AddUsersDTO addUserDTO, BindingResult result)
	{
		if(result.hasErrors())
		{
			StringBuilder errorMessage = new StringBuilder("Validation errors Occured: ");
			result.getAllErrors().forEach(error -> errorMessage.append(error.getDefaultMessage()).append(", "));
			
			throw new ExecutionFailed(errorMessage.toString());
		}
		try {
		            
            APIResponse apiResponse = new APIResponse();
    		apiResponse.setSuccess(true);	
    		apiResponse.setTimestamp(LocalDateTime.now());
    		apiResponse.setData(userServiceImpl.createUser(addUserDTO));
    		apiResponse.setErrorMessage(null);
            return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
        } 
		
		catch (Exception e) {
			// TODO: handle exception
			throw e;
		}
		
	}
	
	@PutMapping("/update-user/{userId}")
	public ResponseEntity<APIResponse> updateUser(@RequestBody AddUsersDTO addUserDTO, @PathVariable(name="userId") Long usersId , BindingResult result)
	{
		if(result.hasErrors())
		{
			StringBuilder errorMessage = new StringBuilder("Validation errors Occured: ");
			result.getAllErrors().forEach(error -> errorMessage.append(error.getDefaultMessage()).append(", "));
			
			throw new ExecutionFailed(errorMessage.toString());
		}
		try {
		            
            APIResponse apiResponse = new APIResponse();
    		apiResponse.setSuccess(true);	
    		apiResponse.setTimestamp(LocalDateTime.now());
    		apiResponse.setData(userServiceImpl.updateUser(addUserDTO,usersId));
    		apiResponse.setErrorMessage(null);
            return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
        } 
		
		catch (Exception e) {
			// TODO: handle exception
			throw e;
		}
		
	}
	
	@GetMapping("/get-all")
	public ResponseEntity<APIResponse> getAllUsers()
	{
		try {
			
			
			List<FetchUsersDTO> allUsers = userServiceImpl.getAllUsers();
			APIResponse apiResponse = new APIResponse();
			apiResponse.setSuccess(true);
			apiResponse.setTimestamp(LocalDateTime.now());
			apiResponse.setData(allUsers);
			
			
			return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw e;
		}
		
	}
	
	@GetMapping("/get-by-id/{userId}")
	public ResponseEntity<APIResponse> getUserById(@PathVariable(name="userId") Long usersId)
	{
		try {
			APIResponse apiResponse = new APIResponse();
			apiResponse.setSuccess(true);
			apiResponse.setTimestamp(LocalDateTime.now());
			apiResponse.setData(userServiceImpl.getUserByID(usersId));
			
			
			return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw e;
		}
		
	}
	
	@DeleteMapping("/delete-user/{userId}")
	public ResponseEntity<APIResponse> deleteUser(@PathVariable(name="userId") Long usersId)
	{
		try {
			userServiceImpl.deleteUser(usersId);
			APIResponse apiResponse = new APIResponse();
			apiResponse.setSuccess(true);
			apiResponse.setTimestamp(LocalDateTime.now());
			apiResponse.setData("User Deleted Successfully");
			
			
			return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw e;
		}
		
	}
	
	@PostMapping("/create-user-cart")
	public ResponseEntity<APIResponse> assignCartToUser(@RequestParam(name="userId") Long usersId)
	{
		try {
			
			APIResponse apiResponse = new APIResponse();
			apiResponse.setSuccess(true);
			apiResponse.setTimestamp(LocalDateTime.now());
			apiResponse.setData(userServiceImpl.createCartForUser(usersId));
			
			
			return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw e;
		}
	}
	
	@PostMapping("/add-user-cartItems")
	public ResponseEntity<APIResponse> addCartItemsToCartFromUser(@RequestParam(name="userId") Long usersId, 
																  @RequestParam(name="productId")Long productId, 
																  @RequestParam(name="quantity", defaultValue = "1") Integer quantity)
	{
		try {
			APIResponse apiResponse = new APIResponse();
			apiResponse.setSuccess(true);
			apiResponse.setTimestamp(LocalDateTime.now());
			apiResponse.setData(userServiceImpl.addCartItemFromUser(usersId,productId,quantity));
			
			
			return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw e;
		}
	}
	
	@PostMapping("/user-place-order")
	public ResponseEntity<APIResponse> userPlacingOrder(@RequestParam(name="cartId") Long cartId,
														@RequestParam(name="userId")Long userId)
	{
		try {
			APIResponse apiResponse = new APIResponse();
			apiResponse.setSuccess(true);
			apiResponse.setTimestamp(LocalDateTime.now());
			apiResponse.setData(userServiceImpl.userPlacingOrder(userId, cartId));
			
			
			return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw e;
		}
	}
}




