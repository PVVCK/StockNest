package com.ecommerce.stocknest.service.order;

import java.util.List;

import com.ecommerce.stocknest.model.Orders;
import com.ecommerce.stocknest.model.Users;

public interface OrderService {
	
	public String placeOrder(Long cartId, Users user);
	public List<Orders> getAllOrders();

	public Orders getOrderByID(Long orderId);
}
