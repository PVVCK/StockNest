package com.ecommerce.stocknest.service.image;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ecommerce.stocknest.cache.CachingSetup;
import com.ecommerce.stocknest.dto.AddProductDTO;
import com.ecommerce.stocknest.dto.ImageDTO;
import com.ecommerce.stocknest.model.Image;
import com.ecommerce.stocknest.model.Product;
import com.ecommerce.stocknest.repository.ImageRepository;
import com.ecommerce.stocknest.repository.ProductRepository;

import jakarta.transaction.Transactional;

@Service
public class ImageServiceImpl implements ImageService {
	
	@Autowired
	private ImageRepository imageRepository;
	
	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private CachingSetup cachingSetup;
	
	@Value("${imageDownloadUrl}")
	private String imageURL;
	
	
	@Override
	@Transactional(rollbackOn = Exception.class)
	@Cacheable(value = "Cache_Image", key = "#imageId" )
	public Image getImageById(Long imageId) {
		// TODO Auto-generated method stub
		
		return imageRepository.findById(imageId)
				.orElseThrow(() -> new NoSuchElementException("Image with Id :- "+imageId+" is not found"));
	}
	
	@Override
	@Transactional(rollbackOn = Exception.class)
	@Cacheable(value = "Cache_Image_All", key = "'allImages'")
	public List<Image> getAllImages() {
		// TODO Auto-generated method stub
		List<Image> images = imageRepository.findAll();
		if (images.isEmpty()) {
	        throw new NoSuchElementException("No Images are available in the database.");
	    }
		return images;
				
	}
	
	@Override
	@Transactional(rollbackOn = Exception.class)
	@Cacheable(value = "Cache_Image_Product", key = "#productId" )
	public List<Image> getAllImagesOfProduct(Long productId) {
		// TODO Auto-generated method stub
		
		Product product = productRepository.findById(productId)
	            .orElseThrow(() -> new NoSuchElementException("Product not found with Product Id: " + productId));
	    
	    // Check if images exist and return them
	    if (product.getImages() != null && !product.getImages().isEmpty()) {
	        return product.getImages();
	    } else {
	        throw new NoSuchElementException("No images attached to Product Id: " + productId);
	    }
	}
	public byte[] getImageBytesById(Long imageId) throws Exception {
		 Image image = imageRepository.findById(imageId)
		            .orElseThrow(() -> new NoSuchElementException("Image not found with ID: " + imageId));

		    // Return the image data as byte[] (from the image field)
		    return image.getImage(); // The 'image' field is already a byte[]
	}
	
	@Override
	@Transactional(rollbackOn = Exception.class)
	@CacheEvict(value = "Cache_Image", key = "#imageId" )
	public void deleteImageById(Long imageId) {
		// TODO Auto-generated method stub
		Optional<Image> imageOptional = imageRepository.findById(imageId);

		if (imageOptional.isPresent()) {
		    Image image = imageOptional.get();
		    imageRepository.delete(image);
		    cachingSetup.clearCacheByNames(Arrays.asList("Cache_Image_All","Cache_Image","Cache_Image_Product","Cache_Product_All"));
		} else {
		    throw new NoSuchElementException("Image with Id:- " + imageId + " is not Present");
		}
	}

	@Override
	@Transactional(rollbackOn = Exception.class)
	@CacheEvict(value = "Cache_Product", key = "#productId" )
	public List<ImageDTO> saveImages(List<MultipartFile> files, Long productId) throws Exception {
		// TODO Auto-generated method stub
		Product product = productRepository.findById(productId)
	            .orElseThrow(() -> new NoSuchElementException("Product with Id:- " + productId + " is not Present"));
		
		AddProductDTO addProductDTO = new AddProductDTO();
		addProductDTO.setBrand(product.getBrand());
		addProductDTO.setName(product.getName());
		addProductDTO.setPrice(product.getPrice());
		addProductDTO.setInventory(product.getInventory());
		addProductDTO.setDescription(product.getDescription());
		addProductDTO.setId(product.getProductId());
		addProductDTO.setCategory(product.getCategory().getName());
		List<ImageDTO> savedImageDto = new ArrayList<>();
		for(MultipartFile file : files)
		{
			try {
				    Image image = new Image();
				    image.setFileName(file.getOriginalFilename());
				    image.setFileType(file.getContentType());
				    image.setImage(file.getBytes()); 
				    image.setProduct(product);
	
				    // Save the image and get the assigned ID
				    Image savedImage = imageRepository.save(image);
	
				    // Update and persist the download URL
				    String brandAndName = product.getBrand().toLowerCase();
				    brandAndName+="-";
				    brandAndName+=product.getName().replaceAll("\\s+", "").trim().toLowerCase();
				    savedImage.setDownloadUrl(imageURL + brandAndName+"/"+ savedImage.getId());
				    imageRepository.save(savedImage); // Save again to persist the updated URL
				    
				    ImageDTO imageDTO = new ImageDTO();
				    imageDTO.setId(savedImage.getId());
				    imageDTO.setImageName(savedImage.getFileName());
				    imageDTO.setDownloadUrl(savedImage.getDownloadUrl());
				    imageDTO.setProductDTO(addProductDTO);
				    savedImageDto.add(imageDTO);
				    
				    cachingSetup.clearCacheByNames(Arrays.asList("Cache_Image_All","Cache_Image","Cache_Image_Product","Cache_Product_All"));
			    }
			catch(Exception e)
			{
				throw e;
			}
		}
		
		return savedImageDto;
	}

	@Override
	@Transactional(rollbackOn = Exception.class)
	@CacheEvict(value = "Cache_Image", key = "#imageId" )
	public Image updateImage(MultipartFile file, Long imageId) throws Exception {
	    // Fetch the image by ID
	    Image image = getImageById(imageId);
	    try {
	    	
	        // Update image details
	        image.setFileName(file.getOriginalFilename()); // Set file name
	        image.setFileType(file.getContentType());      // Set file type (corrected)
	        image.setImage(file.getBytes()); // Set file as Blob
	        

	        // Save the updated image
	        cachingSetup.clearCacheByNames(Arrays.asList("Cache_Image_All","Cache_Image_Product"));
	         return imageRepository.save(image);
	    } catch (Exception e) {
	        // Log and rethrow the exception for clarity
	        throw new Exception("Failed to update image: " + e.getMessage(), e);
	    }
	
	}

	
	

}
