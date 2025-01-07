package com.ecommerce.stocknest.service.category;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.ecommerce.stocknest.dto.CategoryDTO;
import com.ecommerce.stocknest.model.Category;
import com.ecommerce.stocknest.repository.CategoryRepository;

@Service
public class CategoryServiceImpl implements CategoryService {
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Override
	public Category getCategoryById(Long id) {
		// TODO Auto-generated method stub
		System.out.println("inside");
		return categoryRepository.findById(id)
				.orElseThrow(()->new NoSuchElementException("Category with Id:- " +id +" is not Present"));
	}

	@Override
	public Category getCategoryByName(String name) {
		return categoryRepository.findByName(name)
		        .orElseThrow(() -> new NoSuchElementException("Category with Name: " + name + " is not Present"));

	}

	@Override
	public List<Category> getAllCategories() {
		// TODO Auto-generated method stub
		return categoryRepository.findAll();
	}

	@Override
	public Category addCategory(CategoryDTO categoryDTO) {
		// TODO Auto-generated method stub
		Category category = modelMapper.map(categoryDTO, Category.class);
		
		return Optional.ofNullable(category)
				.filter(input -> !categoryRepository.existsByName(category.getName()))
				.map(categoryRepository::save)
				.orElseThrow(() -> new DataIntegrityViolationException("Category with Name: " + category.getName() + " is already Present"));
	}

	@Override
	public Category updateCategory(CategoryDTO categoryDTO, Long categoryId) {
		// TODO Auto-generated method stub
		    		    
		return Optional.ofNullable(getCategoryById(categoryId))
				.map(oldCategory -> {
					oldCategory.setName(categoryDTO.getName());
					return categoryRepository.save(oldCategory);
				})
				.orElseThrow(() -> new NoSuchElementException("Category with Name :- "+categoryDTO.getName()+" is not present"));
	}

	@Override
	public void deleteCategory(Long id) {
		// TODO Auto-generated method stub
		categoryRepository.findById(id)
		.ifPresentOrElse(categoryRepository::delete, 
				() -> {throw new NoSuchElementException("Category with Id:- " +id +" is not Present");});
	}

}
