package com.ecommerce.stocknest.service.order;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.stocknest.enums.OrderStatus;
import com.ecommerce.stocknest.model.Cart;
import com.ecommerce.stocknest.model.CartItem;
import com.ecommerce.stocknest.model.OrderItem;
import com.ecommerce.stocknest.model.Orders;
import com.ecommerce.stocknest.repository.CartRepository;
import com.ecommerce.stocknest.repository.OrderRepository;

import jakarta.transaction.Transactional;

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private CartRepository cartRepository;
	
	@Autowired
	private OrderRepository orderRepository;
	
	@Override
	@Transactional(rollbackOn = Exception.class)
	public void placeOrder(Long cartId) {
	    // Fetch the cart
	    Cart cart = cartRepository.findById(cartId)
	            .orElseThrow(() -> new RuntimeException("Cart not found"));

	    if (cart.getCartItems().isEmpty()) {
	        throw new RuntimeException("Cart is empty. Cannot place an order.");
	    }

	    // Create a new order
	    Orders orders = new Orders();
	    orders.setOrderDate(LocalDate.now());
	    orders.setTotalAmount(cart.getTotalAmount());
	    orders.setOrderStatus(OrderStatus.PLACED);

	  
	    List<CartItem> cartItemsCopy = new ArrayList<>(cart.getCartItems());

	    for (CartItem cartItem : cartItemsCopy) {
	        OrderItem orderItem = new OrderItem();
	        orderItem.setProduct(cartItem.getProduct());
	        orderItem.setQuantity(cartItem.getQuantity());
	        orderItem.setUnitPrice(cartItem.getUnitPrice());
	        orderItem.setTotalPrice(cartItem.getTotalPrice());
	        orderItem.setOrders(orders);

	        orders.getOrderItems().add(orderItem);
	    }

	    // Save the order
	    orderRepository.save(orders);

	    // Clear the cart after processing
	    cart.getCartItems().clear();
	    cart.setTotalAmount(BigDecimal.ZERO);
	    cartRepository.save(cart);
	}

	@Override
	@Transactional(rollbackOn = Exception.class)
	public List<Orders> getAllOrders() {
		// TODO Auto-generated method stub
		List<Orders> orders = orderRepository.findAll();
		if (orders.isEmpty()) {
	        throw new NoSuchElementException("No Orders are placed.");
	    }
		return orders;
	}
	
	@Override
	@Transactional(rollbackOn = Exception.class)
	public Orders getOrderByID(Long orderId) {
		// TODO Auto-generated method stub
		
		return orderRepository.findById(orderId)
				.orElseThrow(() -> new NoSuchElementException("Order not found"));
	}

}
