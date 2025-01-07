package com.ecommerce.stocknest.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.stocknest.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>{

	Optional<List<Product>> findByCategoryNameIgnoreCase(String category);

	Optional<List<Product>> findByBrandIgnoreCase(String brand);

	Optional<List<Product>> findByCategoryNameIgnoreCaseAndBrandIgnoreCase(String category, String brand);

	Optional<List<Product>> findByNameIgnoreCase(String name);

	Optional<List<Product>> findByBrandIgnoreCaseAndNameIgnoreCase(String brand, String name);

	Long countByBrandIgnoreCaseAndNameIgnoreCase(String brand, String name);

	
}
