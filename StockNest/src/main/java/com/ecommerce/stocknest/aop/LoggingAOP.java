package com.ecommerce.stocknest.aop;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.ecommerce.stocknest.dto.ProductDTO;
import com.ecommerce.stocknest.util.LoggingUtil;
@Component
@Aspect
public class LoggingAOP {

    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final ThreadLocal<Integer> callDepth = ThreadLocal.withInitial(() -> 0);

    // Pointcut for controller and service methods
    @Around("execution(* com.ecommerce.stocknest.controller..*(..)) || execution(* com.ecommerce.stocknest.service..*(..)) && !execution(* com.ecommerce.stocknest.config..*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        // Prevent recursion
        if (callDepth.get() > 5) {
            System.err.printf("Skipping AOP for %s to avoid recursion.%n", joinPoint.getSignature().toShortString());
            return joinPoint.proceed();
        }

        callDepth.set(callDepth.get() + 1);  // Increase call depth
        String methodName = joinPoint.getSignature().toShortString();
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        
        // Get method arguments
        Object[] methodArgs = joinPoint.getArgs();
        StringBuilder methodArgsStr = new StringBuilder();

        
        for (Object arg : methodArgs) {
            if (arg != null) {
                if (arg instanceof ProductDTO) {
                    // Using the utility method to filter out null fields
                    methodArgsStr.append(LoggingUtil.getNonNullFields(arg)).append(", ");
                } else {
                    methodArgsStr.append(arg.toString()).append(", ");
                }
            }
        }

        // Log method entry with arguments
        System.out.printf("[%s] INFO: Entering method: %s with arguments: %s%n", 
                          timestamp, methodName, methodArgsStr);

        // Log method entry without arguments displaying in logs
//        System.out.printf("[%s] INFO: Entering method: %s%n", timestamp, methodName);
        Object result;
        try {
            // Proceed with the method execution
            result = joinPoint.proceed();
        }
        
        catch (Exception e) {
//            System.err.printf("[%s] ERROR: Exception in method: %s -> %s%n", 
//                              timestamp, methodName, t.getMessage());
            throw e;
        }
        finally {
            // Log method exit
            timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
            System.out.printf("[%s] INFO: Exiting method: %s%n", timestamp, methodName);
            callDepth.set(callDepth.get() - 1);  // Decrease call depth
        }
        return result;
    }
}