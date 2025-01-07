package com.ecommerce.stocknest.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.stocknest.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

	public Optional<Category> findByName(String name);
	
	public boolean existsByName(String name);

}
