package com.ecommerce.stocknest.service.product;

import java.util.List;

import com.ecommerce.stocknest.dto.AddProductDTO;
import com.ecommerce.stocknest.dto.ProductDTO;
import com.ecommerce.stocknest.model.Product;

public interface ProductService {

	public ProductDTO addProduct(AddProductDTO productDTO);
	public ProductDTO getProductById(Long productId);
	public void deleteProductById(Long productId);
	public ProductDTO updateProductById(ProductDTO productDTO, Long productId);
	public List<Product> getAllProducts();
	public List<Product> getProductsByCategory(String category);
	public List<Product> getProductsByBrand(String brand);
	public List<Product> getProductsByCategoryAndBrand(String category, String brand);
	public List<Product> getProductsByName(String name);
	public List<Product> getProductsByBrandAndName(String brand, String name);
	public Long countProductsByBrandAndName(String brand, String name);
}
