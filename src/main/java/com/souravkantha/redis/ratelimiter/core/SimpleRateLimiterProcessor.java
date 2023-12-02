package com.souravkantha.redis.ratelimiter.core;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

@Aspect
public class SimpleRateLimiterProcessor {
	
	private RollingWindowThrottleService throttlerService;
	
	public SimpleRateLimiterProcessor(final RollingWindowThrottleService throttlerService) {
		this.throttlerService = throttlerService;
	}
	
    @Before(value = "@annotation(SimpleRateLimiter)", argNames = "SimpleRateLimiter")
    public void throttleRequest(SimpleRateLimiter request) throws RateLimitedException {

    	throttlerService.acquire(request.key(), request.requestsRatePerWindow(), request.timeUnit());
    }
}
