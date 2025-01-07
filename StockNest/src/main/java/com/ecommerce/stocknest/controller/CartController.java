package com.ecommerce.stocknest.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.stocknest.model.Cart;
import com.ecommerce.stocknest.response.APIResponse;
import com.ecommerce.stocknest.service.cart.CartServiceImpl;

@RestController
@RequestMapping("${api.prefix}/cart")
public class CartController {

	
	@Autowired
	private CartServiceImpl cartServiceImpl;
	
	@PostMapping("/create-cart")
    public ResponseEntity<APIResponse> createCart() {
        Cart cart = cartServiceImpl.createCart();
        
        APIResponse apiResponse =new APIResponse();
        apiResponse.setSuccess(true);
        apiResponse.setTimestamp(LocalDateTime.now());
        apiResponse.setData(cart);
        
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }
	
	@DeleteMapping("/delete-cart")
	public ResponseEntity<APIResponse> deleteCart(@RequestParam Long cartID)
	{
		cartServiceImpl.deleteCart(cartID);
		APIResponse apiResponse =new APIResponse();
        apiResponse.setSuccess(true);
        apiResponse.setTimestamp(LocalDateTime.now());
        apiResponse.setData("Cart :-" +cartID +" deleted successfully");
        
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}
	
	@GetMapping("/get-cart/{cartId}")
	public ResponseEntity<APIResponse> getCartById(@PathVariable(name="cartId") Long cartId)
	{
		Cart cart = cartServiceImpl.getCartById(cartId);
		APIResponse apiResponse =new APIResponse();
        apiResponse.setSuccess(true);
        apiResponse.setTimestamp(LocalDateTime.now());
        apiResponse.setData(cart);
        
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}
	
	@GetMapping("/get/all")
	public ResponseEntity<APIResponse> getAllCarts()
	{
		APIResponse apiResponse =new APIResponse();
        apiResponse.setSuccess(true);
        apiResponse.setTimestamp(LocalDateTime.now());
        apiResponse.setData(cartServiceImpl.getAllCarts());
        
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
        
        
	}
	
	@DeleteMapping("/clear-cart")
	public ResponseEntity<APIResponse> clearCart(@RequestParam(name="cartId") Long cartID)
	{
		cartServiceImpl.clearCart(cartID);
		APIResponse apiResponse =new APIResponse();
        apiResponse.setSuccess(true);
        apiResponse.setTimestamp(LocalDateTime.now());
        apiResponse.setData("Cart :-" +cartID +" cleared successfully");
        
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}
}
