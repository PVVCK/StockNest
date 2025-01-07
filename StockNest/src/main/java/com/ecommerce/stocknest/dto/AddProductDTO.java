package com.ecommerce.stocknest.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddProductDTO {

	 	private Long id;
	    private String name;
	    private String brand;
	    private BigDecimal price;
	    private Integer inventory;
	    private String description;
	    private String category; // Only store the ID of the category
//	    private List<ImageDTO> images; // Represent images as a list of DTOs
//	    private List<CartItem> cartItems;
}
