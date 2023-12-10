package com.souravkantha.ratelimiter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import com.souravkantha.ratelimiter.core.RollingWindowRateLimiterProcessor;
import com.souravkantha.redis.ratelimiter.service.RedisService;
import com.souravkantha.redis.ratelimiter.service.RollingWindowThrottleService;

@Configuration
@EnableAspectJAutoProxy
public class AspectConfig {

    @Bean
    public RollingWindowRateLimiterProcessor notifyAspect() {
    	RollingWindowRateLimiterProcessor rlp = new RollingWindowRateLimiterProcessor(
				new RollingWindowThrottleService(new RedisService( new RedissonClientConfig())));
    	
    	return rlp;
    }
}
