package com.ecommerce.stocknest.util;

import java.text.DecimalFormat;
public class ExecutionTimer {
	public static final String ENTERING_METHOD = "Entering method";
	public static final String LEAVING_METHOD = "Leaving method";
	private long date;
 	public ExecutionTimer() {
		reset();
	}
	
	public long getElapsedMills() {
	    long mil0 = this.date;
	    long mil1 = System.currentTimeMillis();
	    return mil1 - mil0;
	}
	public String getElapsedTime() {
	    long mil0 = this.date;
	    long mil1 = System.currentTimeMillis();
	    long sec = (mil1 - mil0) / 1000;
	    long rest = mil1 - mil0 - (sec * 1000);
		DecimalFormat formater = new DecimalFormat("000");
	    return "" + sec + "." + formater.format(rest);
	}
	
	public String getElapsedTimeString() {
		return "ELAPSED TIME = [" + getElapsedTime() + " seconds]";
	}
	
	public String getEnteringMethodMessage() {
		return ENTERING_METHOD;
	}
	
	public String getEnteringMethodMessage(String methodName) {
		if (methodName == null) {
			methodName = "";
		} else {
			methodName += " - ";
		}
		return methodName + "" + ENTERING_METHOD;
	}
	
	public String getLeavingMethodMessage(String methodName) {
		if (methodName == null) {
			methodName = "";
		} else {
			methodName += " - ";
		}
		return methodName + "" + LEAVING_METHOD + " - " + getElapsedTimeString();
	}
	
	public String getLeavingMethodMessage() {
		return LEAVING_METHOD + " - " + getElapsedTimeString();
	}
	public long getStartDate() {
		return this.date;
	}
	
	public void reset() {
		this.date = System.currentTimeMillis();
	}
}

