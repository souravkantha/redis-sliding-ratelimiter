package com.souravkantha.redis.ratelimiter.core;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

@Aspect
public class RollingWindowRateLimiterProcessor {
	
	private RollingWindowThrottleService throttlerService;
	
	public RollingWindowRateLimiterProcessor(final RollingWindowThrottleService throttlerService) {
		this.throttlerService = throttlerService;
	}
	
    @Before(value = "@annotation(RollingWindowRateLimiter)", argNames = "RollingWindowRateLimiter")
    public void throttleRequest(RollingWindowRateLimiter request) throws RateLimitedException {

    	throttlerService.acquire(request.key(), request.requestsRatePerWindow(), request.timeUnit());
    }
}
