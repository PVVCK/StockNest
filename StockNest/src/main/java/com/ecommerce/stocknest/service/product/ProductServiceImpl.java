package com.ecommerce.stocknest.service.product;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ecommerce.stocknest.dto.AddProductDTO;
import com.ecommerce.stocknest.dto.ProductDTO;
import com.ecommerce.stocknest.exception.ExecutionFailed;
import com.ecommerce.stocknest.model.CartItem;
import com.ecommerce.stocknest.model.Category;
import com.ecommerce.stocknest.model.Product;
import com.ecommerce.stocknest.repository.CartItemRepository;
import com.ecommerce.stocknest.repository.CartRepository;
import com.ecommerce.stocknest.repository.CategoryRepository;
import com.ecommerce.stocknest.repository.ProductRepository;

import jakarta.transaction.Transactional;

@Service
public class ProductServiceImpl implements ProductService{

	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Autowired
	private CartItemRepository cartItemRepository;
	
	@Autowired
	private CartRepository cartRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Value("${api.prefix}")
	private String apiPrefix; 
	
	@Value("${baseURLStockNest}")
	private String baseURL;
	
	/*
	 * @Override public ProductDTO addProduct(ProductDTO productDTO) { // TODO
	 * Auto-generated method stub System.out.println("add"); Product product =
	 * modelMapper.map(productDTO, Product.class);
	 * 
	 * if (productDTO.getCategory() != null && productDTO.getCategory().getName() !=
	 * null) { String categoryName = productDTO.getCategory().getName();
	 * 
	 * // Find the category by name Category category =
	 * categoryRepository.findByName(categoryName).get();
	 * 
	 * if (category == null) { // If category doesn't exist, create and save a new
	 * one category = new Category(); category.setName(categoryName); category =
	 * categoryRepository.save(category); }
	 * 
	 * // Set the category to the product product.setCategory(category); }
	 * 
	 * // Save the product and return the ProductDTO return
	 * modelMapper.map(productRepository.save(product), ProductDTO.class); }
	 * 
	 */	
	public ProductDTO addProduct(AddProductDTO productDTO) {
	    Product product = modelMapper.map(productDTO, Product.class);

//	    if (productDTO.getCategory() != null && productDTO.getCategory().getName() != null) {
	    if(productDTO.getCategory()!=null && !productDTO.getCategory().trim().isEmpty()) {
	        String categoryName = productDTO.getCategory();

	        // Find the category by name
	        Category category = categoryRepository.findByName(categoryName)
	                .orElseGet(() -> {
	                    // If category doesn't exist, create and save a new one
	                    Category newCategory = new Category();
	                    newCategory.setName(categoryName);
	                    return categoryRepository.save(newCategory);
	                });

	        // Set the category to the product
	        product.setCategory(category);
	    }
	    else
	    {
	    	throw new ExecutionFailed("Product category is not Provided");
	    }

	    // Save the product and return the ProductDTO
	    return modelMapper.map(productRepository.save(product), ProductDTO.class);
	}

	@Override
	public Product getProductById(Long id) {
		// TODO Auto-generated method stub
		
		return productRepository.findById(id)
				.orElseThrow(()-> new NoSuchElementException("Product with Id:- " +id +" is not Present"));
	}

	@Transactional(rollbackOn = Exception.class)
	@Override
	public void deleteProductById(Long productId) {
		// TODO Auto-generated method stub
		
		Product product = productRepository.findById(productId)
	            .orElseThrow(() -> new NoSuchElementException("Product with Id:- " + productId + " is not Present"));
		
		// Remove all related CartItems
	    List<CartItem> cartItems = cartItemRepository.findByProduct_ProductId(productId);
	    if (!cartItems.isEmpty()) {
	        // Extract unique cart IDs from cart items
	        Set<Long> cartIds = cartItems.stream()
	                .map(cartItem -> cartItem.getCart().getCartId())
	                .collect(Collectors.toSet());

	        // Delete all related CartItems
	        cartItemRepository.deleteAll(cartItems);

	        // Update total for each affected cart
	        for (Long cartId : cartIds) {
	            cartRepository.updateCartTotal(cartId);
	        }
	    }

	    // Flush the session to synchronize with the database
	    productRepository.flush();

	    // Delete the product
	    productRepository.delete(product);
		
	}

	@Override
	public ProductDTO updateProductById(ProductDTO productDTO, Long productId) {
		// TODO Auto-generated method stub
		// Find existing product, else throws error
        Product existingProduct = getProductById(productId);
        
        // Check if the product price is being updated
        boolean isPriceUpdated = productDTO.getPrice() != null 
            && productDTO.getPrice().compareTo(existingProduct.getPrice()) != 0;

        // Update fields if present
        if (productDTO.getName() != null) existingProduct.setName(productDTO.getName());
        if (productDTO.getBrand() != null) existingProduct.setBrand(productDTO.getBrand());
        if (productDTO.getPrice() != null) existingProduct.setPrice(productDTO.getPrice());
        if (productDTO.getInventory() != null) existingProduct.setInventory(productDTO.getInventory());
        if (productDTO.getDescription() != null) existingProduct.setDescription(productDTO.getDescription());
        
        if (productDTO.getCategory() != null && productDTO.getCategory().getName() != null) {
            String categoryName = productDTO.getCategory().getName();

            // Find the category by name
            Category category = categoryRepository.findByName(categoryName).get();

            if (category == null) {
                // If category doesn't exist, create and save a new one
                category = new Category();
                category.setName(categoryName);
                category = categoryRepository.save(category);
            }

            // Set the category to the product
            existingProduct.setCategory(category);
        }

        if (isPriceUpdated) {
            String url = baseURL+"/cartitem/update-prices?productId=" + productId + "&newPrice=" + productDTO.getPrice();
            restTemplate.put(url, null);
        }
        
		return modelMapper.map(productRepository.save(existingProduct), ProductDTO.class);	
	}

	@Override
	public List<Product> getAllProducts() {
	    List<Product> products = productRepository.findAll();
	    if (products.isEmpty()) {
	        throw new NoSuchElementException("No products are available in the database.");
	    }
	    return products;
	}

	@Override
	public List<Product> getProductsByCategory(String category) {
		// TODO Auto-generated method stub
		return productRepository.findByCategoryNameIgnoreCase(category)
				.filter(products -> !products.isEmpty()) // Check if the list is non-empty
				.orElseThrow( () -> new NoSuchElementException("Products with category :- " +category + " is not present"))  ;
	}

	@Override
	public List<Product> getProductsByBrand(String brand) {
		// TODO Auto-generated method stub
		
		return productRepository.findByBrandIgnoreCase(brand)
				.filter(products -> !products.isEmpty()) // Check if the list is non-empty
				.orElseThrow( ()-> new NoSuchElementException("Products with brand :- " + brand +" is not present"))  ;
	}

	@Override
	public List<Product> getProductsByCategoryAndBrand(String category, String brand) {
		// TODO Auto-generated method stub
		return productRepository.findByCategoryNameIgnoreCaseAndBrandIgnoreCase(category, brand)
				.filter(products -> !products.isEmpty()) // Check if the list is non-empty
				.orElseThrow( () -> new NoSuchElementException("Products with category :- " + category + " and brand :- "+ brand +" is not present"));
	}

	@Override
	public List<Product> getProductsByName(String name) {
		// TODO Auto-generated method stub
		return productRepository.findByNameIgnoreCase(name)
				.orElseThrow(()-> new NoSuchElementException("Products with Name:-" +name +" is not Present"));
	}

	@Override
	public List<Product> getProductsByBrandAndName(String brand, String name) {
		// TODO Auto-generated method stub
		return productRepository.findByBrandIgnoreCaseAndNameIgnoreCase(brand, name)
				.orElseThrow( () -> new NoSuchElementException("Products with name :- " +name+" and brand :- " + brand +" is not present") );
				
	}

	@Override
	public Long countProductsByBrandAndName(String brand, String name) {
		// TODO Auto-generated method stub
		 Long count = productRepository.countByBrandIgnoreCaseAndNameIgnoreCase(brand, name);
		    if (count > 0) {
		        return count;
		    } else {
		        throw new NoSuchElementException("No Products with brand:- " + brand + " and name:- " + name);
		    }
	}

}
