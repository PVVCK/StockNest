package com.ecommerce.stocknest.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.stocknest.model.CartItem;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

	void deleteAllByCart_CartId(Long id);

	Optional<List<CartItem>> findByCart_CartId(Long cartId);

	List<CartItem> findByProduct_ProductId(Long productId);
	
	// Find a CartItem by cartId and productId using JPA method naming convention
    Optional<CartItem> findByCart_CartIdAndProduct_ProductId(Long cartId, Long productId);

    // Delete CartItem by cartId and productId using JPA method naming convention
    void deleteByCart_CartIdAndProduct_ProductId(Long cartId, Long productId);

}
