package com.souravkantha.ratelimiter.core;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import com.souravkantha.redis.ratelimiter.service.RollingWindowThrottleService;

@Aspect
public class RollingWindowRateLimiterProcessor {
	
	private static final String ERR_MSG = "You are rate limited!!";

	private RollingWindowThrottleService throttlerService;

	public RollingWindowRateLimiterProcessor(final RollingWindowThrottleService throttlerService) {
		this.throttlerService = throttlerService;
	}

	@Around("execution(* *.*(..)) && @annotation(RollingWindowRateLimiter)")
	public Object throttleRequests(ProceedingJoinPoint jp, RollingWindowRateLimiter rateLimiter) throws Throwable {
		System.out.println("Throttle Request Aspect for joinpoint :: " + jp);
		// RollingWindowRateLimiter rateLimiter = ((MethodSignature)
		// jp.getSignature()).getMethod().getAnnotation(RollingWindowRateLimiter.class);
		try {
			throttlerService.acquire(rateLimiter.key(), rateLimiter.requestsRatePerWindow(), rateLimiter.timeUnit());
			Object res = jp.proceed(); // let the target method execute successfully
			return res;

		} catch (RateLimitedException e) { // Rate Limited

			if (!"".equals(rateLimiter.fallbackMethod())) {
				try {
					 
					Class<?> targetObject = (Class<?>) ((MethodSignature) jp.getSignature()).getMethod()
							.getDeclaringClass();
					Method m = null;
					if (jp.getArgs().length == 0) {
						m = targetObject.getMethod(rateLimiter.fallbackMethod(), new Class[] {});
						return m.invoke(jp.getThis(), jp.getArgs());
					} else {
						m = targetObject.getMethod(rateLimiter.fallbackMethod(), Object[].class);
						return m.invoke(jp.getThis(), (Object) jp.getArgs());
					}
				} catch (Exception ex) {

					// let's send null as the fallback method is invalid
					System.out.println(ex.getLocalizedMessage());
					return null;
				}

			}

			return ERR_MSG;

		}

	}
}
