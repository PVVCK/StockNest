package com.ecommerce.stocknest.service.cart.cartitem;

import java.math.BigDecimal;
import java.util.List;

import com.ecommerce.stocknest.model.CartItem;

public interface CartItemService {

	public void updateCartItemsForProductPriceChange(Long productId, BigDecimal newPrice);
	public List<CartItem> getAllCartItems();
	public List<CartItem> getAllCartItemsOfCart(Long cartId);
	public CartItem addCartItem(Long cartId, Long productId, int quantity);
    public CartItem updateCartItemQuantity(Long cartItemId, int quantity);
    public void deleteCartItem(Long cartItemId);
    public void deleteCartItemByCart(Long cartItemId,Long cartID);
}
