package com.ecommerce.stocknest.service.cart.cartitem;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.stocknest.exception.ExecutionFailed;
import com.ecommerce.stocknest.model.Cart;
import com.ecommerce.stocknest.model.CartItem;
import com.ecommerce.stocknest.model.Product;
import com.ecommerce.stocknest.repository.CartItemRepository;
import com.ecommerce.stocknest.repository.CartRepository;
import com.ecommerce.stocknest.repository.ProductRepository;

import jakarta.transaction.Transactional;

@Service
public class CartItemServiceImpl implements CartItemService {

	@Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;
    
    @Transactional(rollbackOn = Exception.class)
    @Override
    public CartItem addCartItem(Long cartId, Long productId, int quantity) {
        // Fetch cart and product
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new NoSuchElementException("Cart not found"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("Product not found"));

        // Check if the cart already contains the product
        Optional<CartItem> existingCartItemOpt = cartItemRepository.findByCart_CartIdAndProduct_ProductId(cartId, productId);

        if (existingCartItemOpt.isPresent()) {
            // If product exists in the cart, update the quantity
            CartItem existingCartItem = existingCartItemOpt.get();
            int updatedQuantity = existingCartItem.getQuantity() + quantity;

            // Check if the updated quantity is valid
            if (product.getInventory() < updatedQuantity) {
                throw new ExecutionFailed("Quantity exceeds the available inventory");
            }

            // Update the cart item
            existingCartItem.setQuantity(updatedQuantity);
            existingCartItem.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(updatedQuantity)));
            cartItemRepository.save(existingCartItem);

            // Update the cart total
            cartRepository.updateCartTotal(cartId);

            return existingCartItem;
        } else {
            // If product does not exist in the cart, create a new cart item
            if (product.getInventory() < quantity) {
                throw new ExecutionFailed("Quantity exceeds the available inventory");
            }

            CartItem newCartItem = new CartItem();
            newCartItem.setProduct(product);
            newCartItem.setQuantity(quantity);
            newCartItem.setUnitPrice(product.getPrice());
            newCartItem.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
            newCartItem.setCart(cart);

            cartItemRepository.save(newCartItem);

            // Update the cart total
            cartRepository.updateCartTotal(cartId);

            return newCartItem;
        }
    }

    
    @Transactional(rollbackOn = Exception.class)
    @Override
    public CartItem updateCartItemQuantity(Long cartItemId, int quantity) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new NoSuchElementException("CartItem not found"));

        if(cartItem.getProduct().getInventory()<quantity)
        {
        	throw new ExecutionFailed("Quantity is more than the Available Inventory");
        }
        cartItem.setQuantity(quantity);
        cartItem.setTotalPrice(cartItem.getUnitPrice().multiply(BigDecimal.valueOf(quantity)));

         cartItemRepository.save(cartItem);
        
        cartRepository.updateCartTotal(cartItem.getCart().getCartId());       
        return cartItem;
    }

    @Transactional(rollbackOn = Exception.class)
    @Override
    public void deleteCartItem(Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new NoSuchElementException("CartItem not found"));
        cartItemRepository.delete(cartItem);
        cartRepository.updateCartTotal(cartItem.getCart().getCartId());
    }

    @Transactional(rollbackOn = Exception.class)
	@Override
	public List<CartItem> getAllCartItemsOfCart(Long cartId) {
		  Optional<List<CartItem>> cartItems = cartItemRepository.findByCart_CartId(cartId);

	        // If cartItems is present and not empty, return it, otherwise throw an exception
	        return cartItems.filter(items -> !items.isEmpty())
	                .orElseThrow(() -> new NoSuchElementException("Cart with ID " + cartId + " not found or has no items."));
				
		
	}

    @Transactional(rollbackOn = Exception.class)
	@Override
	public List<CartItem> getAllCartItems() {
		// TODO Auto-generated method stub
		
		return cartItemRepository.findAll();
				
	}

    @Override
    @Transactional(rollbackOn = Exception.class)
	public void updateCartItemsForProductPriceChange(Long productId, BigDecimal newPrice) {
		// TODO Auto-generated method stub
		 List<CartItem> cartItems = cartItemRepository.findByProduct_ProductId(productId);

	        for (CartItem cartItem : cartItems) {
	            // Update the unit price and total price of the CartItem
	            cartItem.setUnitPrice(newPrice);
	            cartItem.setTotalPrice(newPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity())));

	            // Update the Cart's total amount
	            cartRepository.updateCartTotal(cartItem.getCart().getCartId());
	        }

	        // Save all updated CartItems
	        cartItemRepository.saveAll(cartItems);
	}

	@Override
	@Transactional(rollbackOn = Exception.class)
	public void deleteCartItemByCart(Long productId, Long cartID) {
		// TODO Auto-generated method stub
		
		 Optional<CartItem> cartItem = cartItemRepository.findByCart_CartIdAndProduct_ProductId(cartID, productId);

	        cartItem.ifPresentOrElse(
	            // If the CartItem exists, delete it and update the cart total
	            item -> {
	                cartItemRepository.deleteByCart_CartIdAndProduct_ProductId(cartID, productId);
	                // After deletion, update the total amount of the associated cart
	                cartRepository.updateCartTotal(cartID);
	            },
	            // If the CartItem doesn't exist, throw an exception
	            () -> { throw new RuntimeException("CartItem with cartId " + cartID + " and productId " + productId + " not found."); }
	        );
	}
	
	
	
}
