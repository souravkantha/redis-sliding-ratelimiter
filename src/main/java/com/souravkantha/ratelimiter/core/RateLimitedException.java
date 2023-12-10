package com.souravkantha.redis.ratelimiter.core;

public class RateLimitedException extends RuntimeException {
    private static final long serialVersionUID = 1L;
	private String message;
    
    public RateLimitedException(String message) {
        super(message);
        this.message = message;
    }
    
    public String getMessage() {
    	return this.message;
    }
    
    public RateLimitedException() {
    	
    }
}