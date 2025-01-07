package com.ecommerce.stocknest.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.stocknest.response.APIResponse;
import com.ecommerce.stocknest.service.orderitem.OrderItemServiceImpl;

@RestController
@RequestMapping("${api.prefix}/orderitem")
public class OrderItemController {
	
	@Autowired
	private OrderItemServiceImpl orderItemServiceImpl;
	
	@GetMapping("/get-all")
    public ResponseEntity<APIResponse> getAllOrders()
    {
    	try {
            
            APIResponse apiResponse = new APIResponse();
    		apiResponse.setSuccess(true);	
    		apiResponse.setTimestamp(LocalDateTime.now());
    		apiResponse.setData(orderItemServiceImpl.getAllOrderItems());
    		apiResponse.setErrorMessage(null);
            return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
        } catch (Exception e) {
			// TODO: handle exception
			throw e;
		}
    }
	
	@GetMapping("/get-all-orderitems-by-order/{orderId}")
    public ResponseEntity<APIResponse> getOrderByID(@PathVariable(name="orderId") Long orderId)
    {
    	try {
    		
//    		orderServiceImpl.getAllOrders();
            
            APIResponse apiResponse = new APIResponse();
    		apiResponse.setSuccess(true);	
    		apiResponse.setTimestamp(LocalDateTime.now());
    		apiResponse.setData(orderItemServiceImpl.getAllOrderItemsOfAOrder(orderId));
    		apiResponse.setErrorMessage(null);
            return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
        } catch (Exception e) {
			// TODO: handle exception
			throw e;
		}
    }

}
