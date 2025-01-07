package com.ecommerce.stocknest.service.order;

import java.util.List;

import com.ecommerce.stocknest.model.Orders;

public interface OrderService {
	
	public void placeOrder(Long cartId);
	public List<Orders> getAllOrders();

	public Orders getOrderByID(Long orderId);
}
