package com.ecommerce.stocknest.service.order;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.ecommerce.stocknest.enums.OrderStatus;
import com.ecommerce.stocknest.model.Cart;
import com.ecommerce.stocknest.model.CartItem;
import com.ecommerce.stocknest.model.OrderItem;
import com.ecommerce.stocknest.model.Orders;
import com.ecommerce.stocknest.model.Users;
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
	public String placeOrder(Long cartId, Users user) {
	    // Fetch the cart
	    Cart cart = cartRepository.findById(cartId)
	            .orElseThrow(() -> new RuntimeException("Cart not found"));
//	    System.out.println(cart);
	    if (cart.getCartItems().isEmpty()) {
	        throw new RuntimeException("Cart is empty. Cannot place an order.");
	    }
	    System.out.println("in1");
	    // Create a new order
	    Orders orders = new Orders();
	    orders.setOrderDate(LocalDate.now());
	    orders.setTotalAmount(cart.getTotalAmount());
	    orders.setOrderStatus(OrderStatus.PLACED);
	    orders.setUsers(user);

	  
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
	    
	    return "Order placed successfully";
	}

	@Override
	@Transactional(rollbackOn = Exception.class)
	@Cacheable(value = "Cache_Order_All", key = "'allOrders'")
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
	@Cacheable(value = "Cache_Order", key = "#orderId")
	public Orders getOrderByID(Long orderId) {
		// TODO Auto-generated method stub
		
		return orderRepository.findById(orderId)
				.orElseThrow(() -> new NoSuchElementException("Order not found"));
	}

}
