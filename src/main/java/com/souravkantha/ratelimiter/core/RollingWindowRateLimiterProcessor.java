package com.souravkantha.ratelimiter.core;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import com.souravkantha.redis.ratelimiter.service.RollingWindowThrottleService;

@Aspect
public class RollingWindowRateLimiterProcessor {

	private RollingWindowThrottleService throttlerService;

	public RollingWindowRateLimiterProcessor(final RollingWindowThrottleService throttlerService) {
		this.throttlerService = throttlerService;
	}

	@Around("execution(* *.*(..)) && @annotation(RollingWindowRateLimiter)")
	public Object throttleRequests(ProceedingJoinPoint jp, RollingWindowRateLimiter rateLimiter) throws Throwable {
		System.out.println("Throttle Request Aspect for joinpoint - " + jp);
		//RollingWindowRateLimiter rateLimiter = ((MethodSignature) jp.getSignature()).getMethod().getAnnotation(RollingWindowRateLimiter.class);
		try {
			throttlerService.acquire(rateLimiter.key(), rateLimiter.requestsRatePerWindow(), rateLimiter.timeUnit());
			Object res = jp.proceed(); // let the target method execute successfully
			return res; 

		} catch (RateLimitedException e) { // Rate Limited

			if (!"".equals(rateLimiter.fallbackMethod())) {
				
				try {
				Class<?> targetObject  = (Class<?>) ((MethodSignature) jp.getSignature()).getMethod().getDeclaringClass();
				Method m = targetObject.getMethod(rateLimiter.fallbackMethod());
				return m.invoke(jp.getThis());
				
				} catch (Exception ex) {
					
					// let's send null as the fallback method is invalid
				}
				
			}
		
		return null;
			
		}

	}
}
