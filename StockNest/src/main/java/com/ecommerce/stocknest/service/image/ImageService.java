package com.ecommerce.stocknest.service.image;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.ecommerce.stocknest.dto.ImageDTO;
import com.ecommerce.stocknest.model.Image;

public interface ImageService {

	public Image getImageById(Long imageId);
	public void deleteImageById(Long imageId);
	public List<ImageDTO> saveImages(List<MultipartFile> files, Long productId)throws Exception;
	public Image updateImage(MultipartFile file, Long imageId) throws Exception;
	public List<Image> getAllImages();
	public List<Image> getAllImagesOfProduct(Long productId);
}
