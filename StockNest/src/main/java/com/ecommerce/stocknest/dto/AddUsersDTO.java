package com.ecommerce.stocknest.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddUsersDTO {

	 @NotBlank(message = "UserName")
	 private String username;
	 @NotBlank(message = "Email")
	 @Pattern(
		        regexp = "^[A-Za-z0-9._%+-]+@(gmail\\.com|yahoo\\.com|outlook\\.com)$",
		        message = "Email must be a valid Gmail, Yahoo, or Outlook address"
			    )
	 private String email;
	  @Min(value = 1000000000, message = "Phone number must be exactly 10 digits")
	    @Max(value = 9999999999L, message = "Phone number must be exactly 10 digits")
	  @NotNull(message = "Phone number must not be null")
	 private Integer phoneNumber;
}
