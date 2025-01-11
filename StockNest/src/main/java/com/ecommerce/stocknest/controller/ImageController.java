package com.ecommerce.stocknest.controller;


import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import org.springframework.web.multipart.MultipartFile;

import com.ecommerce.stocknest.dto.ImageDTO;
import com.ecommerce.stocknest.model.Image;
import com.ecommerce.stocknest.response.APIResponse;
import com.ecommerce.stocknest.service.image.ImageServiceImpl;

@RestController
@RequestMapping("${api.prefix}/image")
public class ImageController {

	@Autowired
	private ImageServiceImpl imageServiceImpl;
	
	@PostMapping("/add-images")
	public ResponseEntity<APIResponse> saveImages(@RequestBody List<MultipartFile> images,  @RequestParam(name="productid") Long productId) throws Exception
	{
		try
		{
			List<ImageDTO> savedImageDTOs = imageServiceImpl.saveImages(images, productId);
			
			 APIResponse apiResponse = new APIResponse(
		                true,
		                LocalDateTime.now(),
		                savedImageDTOs,
		                null
		        );
			return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
		}
		catch(Exception e)
		{
			throw e;
		}
	}
	
	
	@GetMapping("/download/{productType}/{id}")
//	@GetMapping("/api/v1/images/image/download/{imageId}")
	public ResponseEntity<byte[]> downloadImage(@PathVariable(name="productType") String productType,
												@PathVariable(name="id") Long imageId) {
	    try {
	        // Fetch the image
	        Image image = imageServiceImpl.getImageById(imageId);

	        // Return the image bytes with appropriate headers
	        return ResponseEntity.ok()
	                .contentType(MediaType.parseMediaType(image.getFileType()))
	                .header("Content-Disposition", "inline; filename=\"" + image.getFileName() + "\"")
	                .body(image.getImage()); // Directly use the byte[] field
	    } catch (Exception e) {
	        throw new RuntimeException("Error fetching image", e);
	    }
	}
	
	@PutMapping("/update/{imageId}")
	public ResponseEntity<APIResponse> updateImage(@PathVariable Long imageId, @RequestBody MultipartFile file) throws Exception
	{
		 try {
			 imageServiceImpl.updateImage(file, imageId);
				
		         APIResponse apiResponse = new APIResponse(
			                true,
			                LocalDateTime.now(),
			                "Image Updated successfully",
			                null
			        );
		         return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
		} catch (Exception e) {
			
			// TODO: handle exception
			throw e;
		}
	}
	
	@DeleteMapping("/delete/{imageId}")
	public ResponseEntity<APIResponse> deleteImage(@PathVariable Long imageId) throws Exception
	{
		
		try {
            
			imageServiceImpl.deleteImageById(imageId);
            APIResponse apiResponse = new APIResponse();
    		apiResponse.setSuccess(true);	
    		apiResponse.setTimestamp(LocalDateTime.now());
    		apiResponse.setData("Image Deleted Successfully");
    		apiResponse.setErrorMessage(null);
            return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
        } 
		
		catch (Exception e) {
			// TODO: handle exception
			throw e;
		}
		
	}
	
	
	@GetMapping("/all-product-images/{productId}")
	public ResponseEntity<APIResponse> getAllImagesOfProduct(@PathVariable Long productId)
	{
		List<Image> images = imageServiceImpl.getAllImagesOfProduct(productId);
		return ResponseEntity.status(HttpStatus.OK).body( new APIResponse(
                true,
                LocalDateTime.now(),
                images,
                null
        ));
		
	}
	
	@GetMapping("/all-images")
	public ResponseEntity<APIResponse> getAllImages()
	{
		List<Image> images = imageServiceImpl.getAllImages();
		return ResponseEntity.status(HttpStatus.OK).body( new APIResponse(
                true,
                LocalDateTime.now(),
                images,
                null
        ));
		
	}
}
