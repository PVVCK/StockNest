package com.ecommerce.stocknest.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class APIResponse {
    private boolean success;       // Indicates success or failure
    private LocalDateTime timestamp; // Time of the response
    private Object data;                // Response data (generic to support any type)
    private String errorMessage;   // Error message, if any
//    private String path;           // Endpoint path, optional
}

