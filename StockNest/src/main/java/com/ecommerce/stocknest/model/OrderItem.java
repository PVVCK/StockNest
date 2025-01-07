package com.ecommerce.stocknest.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long orderItemId;
	private Integer quantity;
	private BigDecimal unitPrice;
	private BigDecimal totalPrice;
	@ManyToOne
	@JoinColumn(name="orders_id")
	@JsonBackReference
	private Orders orders;
	@ManyToOne
	@JoinColumn(name="product_id")
	@JsonManagedReference
	private Product product;
	
	
}
