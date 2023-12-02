package com.souravkantha.redis.ratelimiter.core;

public class RateLimitedException extends RuntimeException {
    private String message;
    
    public RateLimitedException(String message) {
        super(message);
        this.message = message;
    }
    public RateLimitedException() {
    	
    }
}