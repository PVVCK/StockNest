package com.ecommerce.stocknest.dto;

import java.util.ArrayList;
import java.util.List;

import com.ecommerce.stocknest.model.Cart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FetchUsersDTO {

	private Long userId;
	 private String username;
	 private String email;
	 private Long phoneNumber;
	 private List<Cart> carts = new ArrayList<>();
}
