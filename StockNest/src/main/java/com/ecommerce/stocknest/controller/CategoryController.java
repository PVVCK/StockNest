package com.ecommerce.stocknest.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.stocknest.dto.CategoryDTO;
import com.ecommerce.stocknest.model.Category;
import com.ecommerce.stocknest.response.APIResponse;
import com.ecommerce.stocknest.service.category.CategoryServiceImpl;

@RestController
@RequestMapping("${api.prefix}/category")
public class CategoryController {

	@Autowired
	private CategoryServiceImpl categoryServiceImpl;
	
	
	@GetMapping("/get/categoryid/{id}")
	public ResponseEntity<APIResponse> getCategoryById(@PathVariable(name ="id") Long categoryId)
	{
		try {
			Category category = categoryServiceImpl.getCategoryById(categoryId);
			APIResponse apiResponse = new APIResponse();
			apiResponse.setSuccess(true);
			apiResponse.setTimestamp(LocalDateTime.now());
			apiResponse.setData(category);
			return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
		} catch (Exception e) {
			// TODO: handle exception
			throw e;
		}
	}
	
	@GetMapping("/get/categoryname/{name}")
	public ResponseEntity<APIResponse> getCategoryByCategoryName(@PathVariable(name ="name") String categoryName)
	{
		try {
			Category category = categoryServiceImpl.getCategoryByName(categoryName.toLowerCase());
			APIResponse apiResponse = new APIResponse();
			apiResponse.setSuccess(true);
			apiResponse.setTimestamp(LocalDateTime.now());
			apiResponse.setData(category);
			return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
		} catch (Exception e) {
			// TODO: handle exception
			throw e;
		}
	}
	
	@GetMapping("/get/all")
	public ResponseEntity<APIResponse> getAllCategories()
	{
		try {
			List<Category> categories = categoryServiceImpl.getAllCategories();
			APIResponse apiResponse = new APIResponse();
			apiResponse.setSuccess(true);
			apiResponse.setTimestamp(LocalDateTime.now());
			apiResponse.setData(categories);
			
			return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw e;
		}
	}
	
	@PostMapping("/add")
	public ResponseEntity<APIResponse> addCategory(@RequestBody CategoryDTO name)
	{
		System.out.println(name.toString());
		try {
			Category category = categoryServiceImpl.addCategory(name);
			
			APIResponse apiResponse = new APIResponse();
			apiResponse.setSuccess(true);
			apiResponse.setData(category);
			apiResponse.setTimestamp(LocalDateTime.now());
			return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw e;
		}
		
	}
	
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<APIResponse> deleteCategory(@PathVariable(name = "id")Long categoryId)
	{
		try {
			categoryServiceImpl.deleteCategory(categoryId);
			
			APIResponse apiResponse = new APIResponse();
			apiResponse.setSuccess(true);
			apiResponse.setData(null);
			apiResponse.setTimestamp(LocalDateTime.now());
			return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw e;
		}
	}
	
	@PutMapping("/update/{id}")
	public ResponseEntity<APIResponse> udpdateCategory(@PathVariable(name="id") Long categoryId, @RequestBody CategoryDTO categoryDTO)
	{
		try {
			Category updatedCategory = categoryServiceImpl.updateCategory(categoryDTO, categoryId);
			APIResponse apiResponse = new APIResponse();
			apiResponse.setSuccess(true);
			apiResponse.setTimestamp(LocalDateTime.now());
			apiResponse.setData(updatedCategory);
			
			return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw e;
		}
	}
	
}
