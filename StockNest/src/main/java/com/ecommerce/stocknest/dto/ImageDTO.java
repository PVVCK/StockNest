package com.ecommerce.stocknest.dto;

import com.ecommerce.stocknest.model.Product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageDTO {

	private Long id;
    private String imageName;
    private String downloadUrl;
    private Product product;

  
}
