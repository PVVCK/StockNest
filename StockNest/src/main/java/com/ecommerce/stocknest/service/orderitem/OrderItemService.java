package com.ecommerce.stocknest.service.orderitem;

import java.util.List;

import com.ecommerce.stocknest.model.OrderItem;

public interface OrderItemService {
	
	public List<OrderItem> getAllOrderItems();
	
	public List<OrderItem> getAllOrderItemsOfAOrder(Long orderId);

}
