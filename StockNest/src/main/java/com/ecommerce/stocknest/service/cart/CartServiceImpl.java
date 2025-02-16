package com.ecommerce.stocknest.service.cart;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.ecommerce.stocknest.cache.CachingSetup;
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
	
	@Autowired
	private CachingSetup cachingSetup;

	
	@Override
	@Transactional(rollbackOn = Exception.class)
	@CacheEvict(value = "Cache_Cart_All", allEntries = true)
    public Cart createCart() {
        Cart cart = new Cart();
        return cartRepository.save(cart);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    @Cacheable(value = "Cache_Cart", key = "#cartId")
    public Cart getCartById(Long cartId) {
        return cartRepository.findById(cartId)
                .orElseThrow(() -> new NoSuchElementException("Cart not found"));
    }
    
    
    @Override
    @Transactional(rollbackOn = Exception.class)
//	@CachePut(value = "Cache_Cart", key = "#cartId")
//	@CacheEvict(value = "Cache_Product_All", allEntries = true)
    public Cart updateCart(Cart cart) {
        return cartRepository.save(cart);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    @CacheEvict(value = "Cache_Cart", key = "#cartId")
    public void deleteCart(Long cartId) {
    	
//        Cart cart = getCartById(cartId); 
//        cartRepository.delete(cart);
//        
        cartRepository.findById(cartId)
        .ifPresentOrElse(user -> {
        	cartRepository.delete(user);
            cachingSetup.clearCacheByNames(Arrays.asList("Cache_Cart_All"));
        }, 
        () -> {
            throw new NoSuchElementException("Cart with Id:- " + cartId + " is not Present");
        });
    }
    
    @Override
    @Transactional(rollbackOn = Exception.class)
    @CacheEvict(value = "Cache_Cart", key = "#cartId")
    public void clearCart(Long cartId) {
    	 Cart cart = cartRepository.findById(cartId)
                 .orElseThrow(() -> new RuntimeException("Cart with ID " + cartId + " not found."));

         // Clear all associated CartItems
         cart.getCartItems().clear();

         // Reset the total amount in the cart
         cart.setTotalAmount(BigDecimal.ZERO);
         

         // Save the updated cart
         cartRepository.save(cart);
         cachingSetup.clearCacheByNames(Arrays.asList("Cache_Cart_All"));
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    @CachePut(value = "Cache_Cart", key = "#cartId")
    public Cart addItemToCart(Long cartId, CartItem cartItem) {
        Cart cart = getCartById(cartId);

        cartItem.setCart(cart); // Link cartItem to cart
        cart.getCartItems().add(cartItem);

        // Update total amount
        cart.setTotalAmount(calculateTotalAmount(cart));
        cachingSetup.clearCacheByNames(Arrays.asList("Cache_Cart_All"));
        return cartRepository.save(cart);
    }
    
    @Override
    @Transactional(rollbackOn = Exception.class)
    @CachePut(value = "Cache_Cart", key = "#cartId")
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
    @Transactional(rollbackOn = Exception.class)
    public BigDecimal calculateTotalAmount(Cart cart) {
        return cart.getCartItems()
                .stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

	@Override
	@Transactional(rollbackOn = Exception.class)
	@Cacheable(value = "Cache_Cart_All", key = "'allCarts'")
	public List<Cart> getAllCarts() {
		// TODO Auto-generated method stub
		
		List<Cart> carts = cartRepository.findAll();
		if (carts.isEmpty()) {
	        throw new NoSuchElementException("No Carts are available in the database.");
	    }
		return carts;
	}
    
    

}
