package com.ecommerce.stocknest.security;

import java.security.Key;
import java.util.Base64;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

public class JWTKeyGenerator {

	public static String keyGenerator() {
	
		Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
		String encodedKey = Base64.getEncoder().encodeToString(key.getEncoded());
		
		return encodedKey;
	}
}
