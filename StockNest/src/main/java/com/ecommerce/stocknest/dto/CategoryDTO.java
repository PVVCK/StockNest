package com.ecommerce.stocknest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDTO {

    private Long categoryId;         // ID of the category (for updates)
    private String name;     // Name of the category
//    private List<ProductDTO> products; // List of products associated with the category



}

