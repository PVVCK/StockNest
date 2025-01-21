package com.ecommerce.stocknest.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class LoggingUtil {

    // Method to filter out null fields from any object
    public static String getNonNullFields(Object obj) {
        StringBuilder result = new StringBuilder();
        
        // Handle null objects to prevent NullPointerException
        if (obj == null) {
            return "null";
        }

        // Get the class of the object
        Class<?> objClass = obj.getClass();

        // Get all fields of the object
        Field[] fields = objClass.getDeclaredFields();

        // Map to hold non-null fields
        Map<String, Object> nonNullFields = new HashMap<>();

        // Iterate over the fields and check for non-null values
        for (Field field : fields) {
            field.setAccessible(true); // make private fields accessible
            try {
                Object value = field.get(obj);
                if (value != null) {
                    nonNullFields.put(field.getName(), value);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        // Build the result string for non-null fields
        nonNullFields.forEach((key, value) -> 
            result.append(key).append("=").append(value).append(", ")
        );

        // Remove the last comma and space if any
        if (result.length() > 0) {
            result.setLength(result.length() - 2); // trim last comma
        }
        
        return result.toString();
    }
}
