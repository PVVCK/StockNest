package com.ecommerce.stocknest.service.cart;

import java.math.BigDecimal;
import java.util.List;

import com.ecommerce.stocknest.model.Cart;
import com.ecommerce.stocknest.model.CartItem;

public interface CartService {

	public Cart createCart();
    public Cart getCartById(Long cartId);
    public List<Cart> getAllCarts();
    public Cart updateCart(Cart cart);
    public void deleteCart(Long cartId);
    public void clearCart(Long cartId);
    public Cart addItemToCart(Long cartId, CartItem cartItem);
    public Cart removeItemFromCart(Long cartId, Long cartItemId);
    public BigDecimal calculateTotalAmount(Cart cart);
}
