package com.ecommerce.stocknest.service.orderitem;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.ecommerce.stocknest.model.OrderItem;
import com.ecommerce.stocknest.repository.OrderItemRepository;

import jakarta.transaction.Transactional;

@Service
public class OrderItemServiceImpl implements OrderItemService {

	@Autowired
	private OrderItemRepository orderItemRepository;
	
	
	@Override
	@Transactional(rollbackOn = Exception.class)
	@Cacheable(value = "Cache_OrderItems_All", key = "'allOrderItems'")
	public List<OrderItem> getAllOrderItems() {
		// TODO Auto-generated method stub
			List<OrderItem> orderItems = orderItemRepository.findAll();
			if (orderItems.isEmpty()) {
		        throw new NoSuchElementException("No OrderItems.");
		    }
			return orderItems;
	}


	@Override
	@Transactional(rollbackOn = Exception.class)
	@Cacheable(value = "Cache_AllOrderItemsOfOrder", key = "#orderId")
	public List<OrderItem> getAllOrderItemsOfAOrder(Long orderId) {
		// TODO Auto-generated method stub
		return orderItemRepository.findByOrders_OrderId(orderId)
				.orElseThrow(() -> new NoSuchElementException("Order not found"));
	}
}


