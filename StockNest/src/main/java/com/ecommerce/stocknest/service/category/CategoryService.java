package com.ecommerce.stocknest.service.category;

import java.util.List;

import com.ecommerce.stocknest.dto.CategoryDTO;
import com.ecommerce.stocknest.model.Category;

public interface CategoryService {

	public Category getCategoryById(Long categoryId);
	public Category getCategoryByName(String name);
	public List<Category> getAllCategories();
	public Category addCategory(CategoryDTO categoryDTO);
	public Category updateCategory(CategoryDTO categoryDTO, Long categoryId);
	public void deleteCategory(Long categoryId);
	
}
