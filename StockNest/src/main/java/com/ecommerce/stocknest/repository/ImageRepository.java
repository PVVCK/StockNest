package com.ecommerce.stocknest.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.stocknest.model.Image;

public interface ImageRepository extends JpaRepository<Image, Long>{
	
	public Optional<List<Image>> findByProduct_ProductId(Long productId);

}
