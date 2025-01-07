package com.ecommerce.stocknest.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.stocknest.model.CartItem;
import com.ecommerce.stocknest.response.APIResponse;
import com.ecommerce.stocknest.service.cart.cartitem.CartItemServiceImpl;

@RestController
@RequestMapping("${api.prefix}/cartitem")
public class CartItemController {
	
	@Autowired
	private CartItemServiceImpl cartItemServiceImpl;
	
	@GetMapping("/all")
	public ResponseEntity<APIResponse> getAllCartItems()
	{
		List<CartItem> cartItems = cartItemServiceImpl.getAllCartItems();
		APIResponse apiResponse = new APIResponse();
		apiResponse.setErrorMessage(null);
		apiResponse.setSuccess(true);	
		apiResponse.setTimestamp(LocalDateTime.now());
		apiResponse.setData(cartItems);
		return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
	}
	
	@GetMapping("/cartitems-cartid")
	public ResponseEntity<APIResponse> getAllCartItemsByCartId(@RequestParam(name="cartid")Long cartId)
	{
		List<CartItem> cartItems = cartItemServiceImpl.getAllCartItemsOfCart(cartId);
		APIResponse apiResponse = new APIResponse();
		apiResponse.setErrorMessage(null);
		apiResponse.setSuccess(true);	
		apiResponse.setTimestamp(LocalDateTime.now());
		apiResponse.setData(cartItems);
		return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
	}
	
	@PostMapping("/add-cartitem")
	public ResponseEntity<APIResponse> addCartItem(@RequestParam(name="cartid") Long cartId, 
													@RequestParam(name="productid")Long productId, 
													@RequestParam(name="quantity", defaultValue = "1") Integer quantity) {
		
		try {
				
				CartItem cartItem = cartItemServiceImpl.addCartItem(cartId, productId, quantity);
				
				APIResponse apiResponse = new APIResponse();
				
				apiResponse.setSuccess(true);	
				apiResponse.setTimestamp(LocalDateTime.now());
				apiResponse.setData(cartItem);
				
				
				return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
		}
		catch(Exception e)
		{
			throw e;
		}
		
	}
	
	 @PutMapping("/update/{cartItemId}/quantity")
	    public ResponseEntity<APIResponse> updateCartItemQuantity( @PathVariable(name="cartItemId") Long cartItemId,
	            												@RequestParam(name="quantity") int quantity) {
		 
	        CartItem updatedCartItem = cartItemServiceImpl.updateCartItemQuantity(cartItemId, quantity);
	        APIResponse apiResponse = new APIResponse();
			apiResponse.setSuccess(true);
			apiResponse.setTimestamp(LocalDateTime.now());
			apiResponse.setData(updatedCartItem);
			
	        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
	    }
	 
	 @PutMapping("/update-prices")
	 public ResponseEntity<APIResponse> updateCartItemPrices(@RequestParam Long productId, @RequestParam BigDecimal newPrice) {
	        cartItemServiceImpl.updateCartItemsForProductPriceChange(productId, newPrice);
	        
	        APIResponse apiResponse = new APIResponse();
			apiResponse.setSuccess(true);
			apiResponse.setTimestamp(LocalDateTime.now());
			apiResponse.setData("Updated CartItem Prices");
			return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
	    }
	  @DeleteMapping("/delete/{cartItemId}")
	    public ResponseEntity<APIResponse> deleteCartItem(@PathVariable(name="cartItemId") Long cartItemId) {
	        cartItemServiceImpl.deleteCartItem(cartItemId);
	        APIResponse apiResponse = new APIResponse();
			apiResponse.setSuccess(true);
			apiResponse.setTimestamp(LocalDateTime.now());
			apiResponse.setData("CartItem deleted successfully");
			return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
	    }
	  @DeleteMapping("/delete-by-cart-and-product")
	    public ResponseEntity<APIResponse> deleteCartItemByCart(@RequestParam(name="productId") Long productId,
	    														@RequestParam(name="cartId") Long cartID)
	  	{
	        cartItemServiceImpl.deleteCartItemByCart(productId,cartID);
	        APIResponse apiResponse = new APIResponse();
			apiResponse.setSuccess(true);
			apiResponse.setTimestamp(LocalDateTime.now());
			apiResponse.setData("CartItem deleted successfully");
			return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
	    }

}
