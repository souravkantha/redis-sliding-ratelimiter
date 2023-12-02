package com.souravkantha.redis.ratelimiter.core;

import com.souravkantha.redis.ratelimiter.service.RedisService;


public class RollingWindowThrottleService {
	
	
	private RedisService redisService;
	
	public RollingWindowThrottleService(RedisService redisService) {
		this.redisService = redisService;
	}
	
	public boolean acquire(final String key,
			final int rate, final WindowTimeUnit timeUnit) throws RateLimitedException {
		
		String entropy =  Long.toHexString(Thread.currentThread().getId());
		if(redisService.isRollingRateExceeded(key, rate, timeUnit, entropy)) {
				throw new RateLimitedException("Rate Limited");
		} else {
					return Boolean.TRUE; // Not rate limited
		}
	}

}
