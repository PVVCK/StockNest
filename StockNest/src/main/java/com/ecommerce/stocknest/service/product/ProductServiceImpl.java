package com.ecommerce.stocknest.service.product;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.ecommerce.stocknest.cache.CachingSetup;
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
import com.ecommerce.stocknest.service.cart.cartitem.CartItemService;

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
	
//	@Autowired
//	private RestTemplate restTemplate;
	
	@Autowired
	private CachingSetup cachingSetup;
	
	@Autowired
	private CartItemService cartItemServiceImpl;
	
	@Value("${api.prefix}")
	private String apiPrefix; 
	
	@Value("${baseURLStockNest}")
	private String baseURL;
	
	@Override
	@Transactional(rollbackOn = Exception.class)
//	@CacheEvict(value = "Cache_Product_All", allEntries = true)
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
	    
//	    cachingSetup.clearCacheByNames(Arrays.asList("Cache_Product_All","Cache_Product"));

	    // Save the product and return the ProductDTO
	    		product = productRepository.save(product);
	    		cachingSetup.clearCacheByNames(getProductCacheKeys(product));
	    	    return modelMapper.map(product, ProductDTO.class);
	}

	@Override
	@Transactional(rollbackOn = Exception.class)
	@Cacheable(value = "Cache_Product_Id", key = "#productId")
	public ProductDTO getProductById(Long productId) {
		// TODO Auto-generated method stub
		
		Product product =  productRepository.findById(productId)
				.orElseThrow(()-> new NoSuchElementException("Product with Id:- " +productId +" is not Present"));
		
		return modelMapper.map(product, ProductDTO.class);
	}

	@Override
	@Transactional(rollbackOn = Exception.class)
//	@CacheEvict(value = { "Cache_Product_All", "Cache_Product" }, allEntries = true, key = "#productId")
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
	    cachingSetup.clearCacheByNames(getProductCacheKeys(product));
//	    cachingSetup.clearCacheByNames(Arrays.asList("Cache_Product_All","Cache_Product"));
	    
		
	}

	@Override
	@Transactional(rollbackOn = Exception.class)
//	@CachePut(value = "Cache_Product_Id", key = "#productId")
//	@CacheEvict(value = "Cache_Product_All", allEntries = true)
	public ProductDTO updateProductById(ProductDTO productDTO, Long productId) {
		// TODO Auto-generated method stub
		// Find existing product, else throws error
        Product existingProduct = productRepository.findById(productId)
	            .orElseThrow(() -> new NoSuchElementException("Product with Id:- " + productId + " is not Present"));
        
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
        	cartItemServiceImpl.updateCartItemsForProductPriceChange(productId, productDTO.getPrice());
        	
//            String url = baseURL+"/cartitem/update-prices?productId=" + productId + "&newPrice=" + productDTO.getPrice();
//            restTemplate.put(url, null);
        }
//        cachingSetup.clearCacheByNames(Arrays.asList("Cache_Product"));
      
//		return modelMapper.map(productRepository.save(existingProduct), ProductDTO.class);
        existingProduct = productRepository.save(existingProduct);
        cachingSetup.clearCacheByNames(getProductCacheKeys(existingProduct));
		return modelMapper.map(existingProduct, ProductDTO.class);	
	}
	
	
	@Override
	@Transactional(rollbackOn = Exception.class)
	@Cacheable(value = "Cache_Product_All", key = "'allProducts'")
	public List<Product> getAllProducts() {
	    List<Product> products = productRepository.findAll();
	    if (products.isEmpty()) {
	        throw new NoSuchElementException("No products are available in the database.");
	    }
	    return products;
	}

	@Override
	@Transactional(rollbackOn = Exception.class)
	@Cacheable(value = "Cache_Product_Category", key = "#category")
	public List<Product> getProductsByCategory(String category) {
		// TODO Auto-generated method stub
		return productRepository.findByCategoryNameIgnoreCase(category)
				.filter(products -> !products.isEmpty()) // Check if the list is non-empty
				.orElseThrow( () -> new NoSuchElementException("Products with category :- " +category + " is not present"))  ;
	}

	@Override
	@Transactional(rollbackOn = Exception.class)
	@Cacheable(value = "Cache_Product_Brand", key = "#brand")
	public List<Product> getProductsByBrand(String brand) {
		// TODO Auto-generated method stub
		
		return productRepository.findByBrandIgnoreCase(brand)
				.filter(products -> !products.isEmpty()) // Check if the list is non-empty
				.orElseThrow( ()-> new NoSuchElementException("Products with brand :- " + brand +" is not present"))  ;
	}

	@Override
	@Transactional(rollbackOn = Exception.class)
	@Cacheable(value = "Cache_Product_CategoryAndBrand", key = "#category + '_' + #brand")
	public List<Product> getProductsByCategoryAndBrand(String category, String brand) {
		// TODO Auto-generated method stub
		return productRepository.findByCategoryNameIgnoreCaseAndBrandIgnoreCase(category, brand)
				.filter(products -> !products.isEmpty()) // Check if the list is non-empty
				.orElseThrow( () -> new NoSuchElementException("Products with category :- " + category + " and brand :- "+ brand +" is not present"));
	}

	@Override
	@Transactional(rollbackOn = Exception.class)
	@Cacheable(value = "Cache_Product_Name", key = "#name")
	public List<Product> getProductsByName(String name) {
		// TODO Auto-generated method stub
		return productRepository.findByNameIgnoreCase(name)
				.filter(products -> !products.isEmpty())// Ensure the list is not empty
				.orElseThrow(()-> new NoSuchElementException("Products with Name:-" +name +" is not Present"));
	}

	@Override
	@Transactional(rollbackOn = Exception.class)
	@Cacheable(value = "Cache_Product_BrandAndName", key = "#brand + '_' + #name")
	public List<Product> getProductsByBrandAndName(String brand, String name) {
		// TODO Auto-generated method stub
		return productRepository.findByBrandIgnoreCaseAndNameIgnoreCase(brand, name)
		        .filter(products -> !products.isEmpty()) // Ensure the list is not empty
		        .orElseThrow(() -> new NoSuchElementException("Products with name: " + name + " and brand: " + brand + " are not present"));

				
	}

	@Override
	@Transactional(rollbackOn = Exception.class)
	@Cacheable(value = "Cache_Product_CountBrandAndName", key = "#count + '_' +#brand + '_' + #name")
	public Long countProductsByBrandAndName(String brand, String name) {
		// TODO Auto-generated method stub
		 Long count = productRepository.countByBrandIgnoreCaseAndNameIgnoreCase(brand, name);
		    if (count > 0) {
		        return count;
		    } else {
		        throw new NoSuchElementException("No Products with brand:- " + brand + " and name:- " + name);
		    }
	}
	
	private Map<String, String> getProductCacheKeys(Product product) {
	    Map<String, String> cacheKeys = new HashMap<>();
	    cacheKeys.put("Cache_Product_Category", product.getCategory().getName());
	    cacheKeys.put("Cache_Product_Brand", product.getBrand());
	    cacheKeys.put("Cache_Product_Name", product.getName());
	    cacheKeys.put("Cache_Product_CategoryAndBrand", product.getCategory().getName() + "_" + product.getBrand());
	    cacheKeys.put("Cache_Product_BrandAndName", product.getBrand() + "_" + product.getName());
	    return cacheKeys;
	
	}
}
