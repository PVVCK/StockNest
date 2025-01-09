package com.ecommerce.stocknest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsersDTO {

	 private Long userId;
	 private String username;
	 private String email;
	 private Long phoneNumber;
}
