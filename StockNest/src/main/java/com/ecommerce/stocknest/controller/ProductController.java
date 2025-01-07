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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.stocknest.dto.AddProductDTO;
import com.ecommerce.stocknest.dto.ProductDTO;
import com.ecommerce.stocknest.model.Product;
import com.ecommerce.stocknest.response.APIResponse;
import com.ecommerce.stocknest.service.product.ProductServiceImpl;
import com.ecommerce.stocknest.util.ExecutionTimer;

@RestController
@RequestMapping("${api.prefix}/product")
public class ProductController {

	@Autowired
	private ProductServiceImpl productServiceImpl;
	
	@GetMapping("/get/all")
	public ResponseEntity<APIResponse> getAllProducts()
	{
		try {
			ExecutionTimer executionTimer = new ExecutionTimer();
			System.out.println(executionTimer.getEnteringMethodMessage());
			List<Product> allProducts = productServiceImpl.getAllProducts();
			APIResponse apiResponse = new APIResponse();
			apiResponse.setSuccess(true);
			apiResponse.setTimestamp(LocalDateTime.now());
			apiResponse.setData(allProducts);
			
			System.out.println(executionTimer.getLeavingMethodMessage());
			return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw e;
		}
		
	}
	
	@GetMapping("/get/productid/{id}")
	public ResponseEntity<APIResponse> getCategoryById(@PathVariable(name ="id") Long productId)
	{
		try {
			Product product = productServiceImpl.getProductById(productId);
			APIResponse apiResponse = new APIResponse();
			apiResponse.setSuccess(true);
			apiResponse.setTimestamp(LocalDateTime.now());
			apiResponse.setData(product);
			
			return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw e;
		}
		
	}
	
	@PostMapping("/add")
	public ResponseEntity<APIResponse> addProduct(@RequestBody AddProductDTO productdto)
	{
		
		try {
			ProductDTO productDTO = productServiceImpl.addProduct(productdto);
			
			APIResponse apiResponse = new APIResponse();
			apiResponse.setSuccess(true);
			apiResponse.setData(productDTO);
			apiResponse.setTimestamp(LocalDateTime.now());
			return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw e;
		}
		
	}
	
	@PutMapping("/update/{id}")
	public ResponseEntity<APIResponse> updateProduct(@RequestBody ProductDTO productdto, 
													 @PathVariable(name="id")Long  productId)
	{
		
		try {
			ProductDTO updatedProductDTO = productServiceImpl.updateProductById(productdto, productId);
			
			APIResponse apiResponse = new APIResponse();
			apiResponse.setSuccess(true);
			apiResponse.setData(updatedProductDTO);
			apiResponse.setTimestamp(LocalDateTime.now());
			return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw e;
		}
		
	}
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<APIResponse> deleteProduct(@PathVariable(name = "id")Long productId)
	{
		try {
			productServiceImpl.deleteProductById(productId);
			
			APIResponse apiResponse = new APIResponse();
			apiResponse.setSuccess(true);
			apiResponse.setTimestamp(LocalDateTime.now());
			apiResponse.setData(null);
			return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw e;
		}
	}
	
	@GetMapping("/by/brand-and-name") //?brand=Nike&name=AirMax
	public ResponseEntity<APIResponse> getProductByBrandAndName(@RequestParam(name = "brand") String brandName, 
																@RequestParam(name = "name") String productName)
	{
		try {
				List<Product> products = productServiceImpl.getProductsByBrandAndName(brandName,productName);
				APIResponse apiResponse = new APIResponse();
				apiResponse.setSuccess(true);
				apiResponse.setTimestamp(LocalDateTime.now());
				apiResponse.setData(products);
				return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			throw e;
		}
	
	}
	
	@GetMapping("/by/category-and-brand")//?category=Shoes&brand=Puma
	public ResponseEntity<APIResponse> getProductByCategoryAndBrand(@RequestParam(name = "category") String category,
																	@RequestParam(name = "brand") String brandName)
	{
		try
		{
			List<Product> products = productServiceImpl.getProductsByCategoryAndBrand(category, brandName);
			APIResponse apiResponse = new APIResponse();
			apiResponse.setSuccess(true);
			apiResponse.setTimestamp(LocalDateTime.now());
			apiResponse.setData(products);
			return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
		}
		catch(Exception e)
		{
			throw e;
		}
		
	}
	
	@GetMapping("/get/by-name")//?name=Airmax
	public ResponseEntity<APIResponse> getProductByName(@RequestParam(name = "name") String productName)
	{
		try
		{
			List<Product> products = productServiceImpl.getProductsByName(productName);
			APIResponse apiResponse = new APIResponse();
			apiResponse.setSuccess(true);
			apiResponse.setTimestamp(LocalDateTime.now());
			apiResponse.setData(products);
			return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
		}
		catch(Exception e)
		{
			throw e;
		}
		
	}
	
	
	@GetMapping("/get/by-brand")//?brand=puma
	public ResponseEntity<APIResponse> getProductByBrand(@RequestParam(name = "brand") String brandName)
	{
		try
		{
			List<Product> products = productServiceImpl.getProductsByBrand(brandName);
			APIResponse apiResponse = new APIResponse();
			apiResponse.setSuccess(true);
			apiResponse.setTimestamp(LocalDateTime.now());
			apiResponse.setData(products);
			return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
		}
		catch(Exception e)
		{
			throw e;
		}
		
	}
	
	@GetMapping("/get/by-category")//?category=shoes
	public ResponseEntity<APIResponse> getProductByCategory(@RequestParam(name = "category") String category)
	{
		try
		{
			List<Product> products = productServiceImpl.getProductsByCategory(category);
			APIResponse apiResponse = new APIResponse();
			apiResponse.setSuccess(true);
			apiResponse.setTimestamp(LocalDateTime.now());
			apiResponse.setData(products);
			return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
		}
		catch(Exception e)
		{
			throw e;
		}
		
	}
	
	@GetMapping("/count/by-brand-and-name")//?brand=Nike&name=AirMax
	public ResponseEntity<APIResponse> countProductByBrandAndName(@RequestParam(name = "brand") String brand,
																  @RequestParam(name = "name") String name)
	{
		try
		{
			Long count = productServiceImpl.countProductsByBrandAndName(brand,name);
			APIResponse apiResponse = new APIResponse();
			apiResponse.setSuccess(true);
			apiResponse.setTimestamp(LocalDateTime.now());
			apiResponse.setData(count);
			return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
		}
		catch(Exception e)
		{
			throw e;
		}
		
	}
}

