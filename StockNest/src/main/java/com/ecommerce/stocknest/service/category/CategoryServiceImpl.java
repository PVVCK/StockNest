package com.ecommerce.stocknest.service.category;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.ecommerce.stocknest.cache.CachingSetup;
import com.ecommerce.stocknest.dto.CategoryDTO;
import com.ecommerce.stocknest.model.Category;
import com.ecommerce.stocknest.repository.CategoryRepository;

import jakarta.transaction.Transactional;

@Service
public class CategoryServiceImpl implements CategoryService {
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private CachingSetup cachingSetup;
	
	@Override
	@Transactional(rollbackOn = Exception.class)
	@Cacheable(value = "Cache_Category", key = "#categoryId")
	public Category getCategoryById(Long categoryId) {
		// TODO Auto-generated method stub
		System.out.println("inside");
		return categoryRepository.findById(categoryId)
				.orElseThrow(()->new NoSuchElementException("Category with Id:- " +categoryId +" is not Present"));
	}

	@Override
	@Transactional(rollbackOn = Exception.class)
	@Cacheable(value = "Cache_Category", key = "#name")
	public Category getCategoryByName(String name) {
		return categoryRepository.findByName(name)
		        .orElseThrow(() -> new NoSuchElementException("Category with Name: " + name + " is not Present"));

	}

	@Override
	@Transactional(rollbackOn = Exception.class)
	@Cacheable(value = "Cache_Category_All", key = "'allCategories'")
	public List<Category> getAllCategories() {
		// TODO Auto-generated method stub
		return categoryRepository.findAll();
	}

	@Override
	@Transactional(rollbackOn = Exception.class)
	@CacheEvict(value = "Cache_Category_All", allEntries = true)
	public Category addCategory(CategoryDTO categoryDTO) {
		// TODO Auto-generated method stub
		Category category = modelMapper.map(categoryDTO, Category.class);
		
		return Optional.ofNullable(category)
				.filter(input -> !categoryRepository.existsByName(category.getName()))
				.map(categoryRepository::save)
				.orElseThrow(() -> new DataIntegrityViolationException("Category with Name: " + category.getName() + " is already Present"));
	}

	@Override
	@Transactional(rollbackOn = Exception.class)
	@CachePut(value = "Cache_Category", key = "#categoryId")
	@CacheEvict(value = "Cache_Category_All", allEntries = true)
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
	@Transactional(rollbackOn = Exception.class)
	@CacheEvict(value = "Cache_Category", key = "#categoryId")
	public void deleteCategory(Long categoryId) {
		// TODO Auto-generated method stub
		categoryRepository.findById(categoryId)
		.ifPresentOrElse(
			    input -> {
			        categoryRepository.delete(input);
			        cachingSetup.clearCacheByNames(Arrays.asList("Cache_Category_All"));
			    },
			    () -> {
			        throw new NoSuchElementException("Category with Id: " + categoryId + " is not Present");
			    }
			);

	}

}
