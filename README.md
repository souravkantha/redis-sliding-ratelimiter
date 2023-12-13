# redis-sliding-ratelimiter


## How to use in spring boot



	@SpringBootApplication
	@Configuration
	@EnableAspectJAutoProxy
	public class SpringMainApplication {

	@Bean RollingWindowRateLimiterProcessor notifyAspect() {
	    	RollingWindowRateLimiterProcessor rlp = new RollingWindowRateLimiterProcessor(
					new RollingWindowThrottleService(new RedisService( new RedissonClientConfig())));
	    	
	    	return rlp;
	}

....