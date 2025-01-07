package com.ecommerce.stocknest.exception;

public class ExecutionFailed extends RuntimeException {

	public ExecutionFailed(String message, Throwable cause)
	{
		super(message, cause);
	}
	public ExecutionFailed(String message)
	{
		super(message);
	}
}
