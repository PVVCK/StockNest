package com.ecommerce.stocknest.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.ecommerce.stocknest.model.Cart;

import jakarta.transaction.Transactional;

public interface CartRepository extends JpaRepository<Cart, Long>{

	@Modifying
    @Transactional
    @Query("UPDATE Cart c SET c.totalAmount = (SELECT COALESCE(SUM(ci.totalPrice), 0) FROM CartItem ci WHERE ci.cart.cartId = :cartId) WHERE c.cartId = :cartId")
    void updateCartTotal(Long cartId);
	
	 public Optional<Cart> findByUsers_UserId(Long userId);
	 
	 boolean existsByCartIdAndUsers_UserId(Long cartId, Long userId);
}
