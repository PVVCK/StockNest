package com.ecommerce.stocknest.service.cart;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.stocknest.model.Cart;
import com.ecommerce.stocknest.model.CartItem;
import com.ecommerce.stocknest.repository.CartItemRepository;
import com.ecommerce.stocknest.repository.CartRepository;

import jakarta.transaction.Transactional;

@Service
public class CartServiceImpl implements CartService {
	
	@Autowired
	private CartRepository cartRepository;
	
	@Autowired
	private CartItemRepository cartItemRepository;

//	@Override
//	public Cart getCart(Long id) {
//		// TODO Auto-generated method stub
//		 Cart cart = cartRepository.findById(id)
//				.orElseThrow(()->new NoSuchElementException("Cart with Id:- " +id +" is not Present"));
//		 
//		 
//		BigDecimal totalAmt = cart.getTotalAmout();
//		cart.setTotalAmout(totalAmt);
//		return cartRepository.save(cart);
//	}

//	@Override
//	public void clearCart(Long id) {
//		// TODO Auto-generated method stub
//		
//		Cart cart = getCart(id);
//		cartItemRepository.deleteAllByCart_CartId(id);
//		cart.getCartItems().clear();
//		cartRepository.deleteById(id);
//		
//	}

//	@Override
//	public BigDecimal getTotalPrice(Long id) {
//		// TODO Auto-generated method stub
//		return null;
//	}
	
	@Override
    public Cart createCart() {
        Cart cart = new Cart();
        return cartRepository.save(cart);
    }

    @Override
    public Cart getCartById(Long cartId) {
        return cartRepository.findById(cartId)
                .orElseThrow(() -> new NoSuchElementException("Cart not found"));
    }
    
    
    @Override
    public Cart updateCart(Cart cart) {
        return cartRepository.save(cart);
    }

    @Override
    @Transactional
    public void deleteCart(Long cartId) {
    	
        Cart cart = getCartById(cartId); 
        cartRepository.delete(cart);
    }
    
    @Override
    public void clearCart(Long cartId) {
    	 Cart cart = cartRepository.findById(cartId)
                 .orElseThrow(() -> new RuntimeException("Cart with ID " + cartId + " not found."));

         // Clear all associated CartItems
         cart.getCartItems().clear();

         // Reset the total amount in the cart
         cart.setTotalAmount(BigDecimal.ZERO);

         // Save the updated cart
         cartRepository.save(cart);
    }

    @Override
    public Cart addItemToCart(Long cartId, CartItem cartItem) {
        Cart cart = getCartById(cartId);

        cartItem.setCart(cart); // Link cartItem to cart
        cart.getCartItems().add(cartItem);

        // Update total amount
        cart.setTotalAmount(calculateTotalAmount(cart));

        return cartRepository.save(cart);
    }
    
    @Override
    public Cart removeItemFromCart(Long cartId, Long cartItemId) {
        Cart cart = getCartById(cartId);
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new NoSuchElementException("CartItem not found with Id:- " + cartItemId));

        cart.getCartItems().remove(cartItem); // Remove item
        cartItemRepository.delete(cartItem); // Remove from database

        // Update total amount
        cart.setTotalAmount(calculateTotalAmount(cart));

        return cartRepository.save(cart);
    }
    
    @Override
    public BigDecimal calculateTotalAmount(Cart cart) {
        return cart.getCartItems()
                .stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

	@Override
	@Transactional(rollbackOn = Exception.class)
	public List<Cart> getAllCarts() {
		// TODO Auto-generated method stub
		return cartRepository.findAll();
	}
    
    

}
