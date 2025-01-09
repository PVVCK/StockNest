package com.ecommerce.stocknest.aop;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class LoggingAOP {

	 private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	 @Around("execution(* com.ecommerce.stocknest..*(..)) && !execution(* com.ecommerce.stocknest.config..*(..))")
	    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
	        String methodName = joinPoint.getSignature().toShortString();
	        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
	        Object[] methodArgs = joinPoint.getArgs();

	        // Log method entry
	        System.out.printf("[%s] INFO: Entering method: %s with arguments: %s%n", 
	                          timestamp, methodName, Arrays.toString(methodArgs));

	        long start = System.nanoTime();

	        Object result;
	        try {
	            result = joinPoint.proceed();
	        } catch (Throwable t) {
	            System.err.printf("[%s] ERROR: Exception in method: %s -> %s%n", 
	                              timestamp, methodName, t.getMessage());
	            throw t;
	        }

	        long end = System.nanoTime();
	        double executionTime = (end - start) / 1_000_000_000.0; // Convert to seconds

	        // Log method exit
	        timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
	        System.out.printf("[%s] INFO: Exiting method: %s; Execution time: %.2fs%n", 
	                          timestamp, methodName, executionTime);

	        return result;
	    }
	}

