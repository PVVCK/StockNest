package com.ecommerce.stocknest.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Category {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long categoryId;
	private String name;
	
	 // Use JsonIgnoreProperties to avoid infinite recursion
//    @JsonIgnoreProperties("category")  // This will prevent the 'products' field in Category from being serialized
//	@JsonManagedReference
	@JsonIgnore
	@OneToMany(mappedBy = "category")
	private List<Product> products;
	
}