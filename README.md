# redis-sliding-ratelimiter

## Another Rate Limiter?

RollingWindowRateLimiter(RWRL) is a distributed rate limiter written in java. It can be used in a distributed environment where each services or components rate limit is computed using redis storage. This implementation uses **Sliding Window Algorithm** (refer below diagram)

![image](https://github.com/souravkantha/redis-sliding-ratelimiter/assets/32014166/cc9acb12-284e-4506-8d3f-16ae1ddf8f1a)



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


### Using annotation for a rest method
	
	@GetMapping("/v1/ping")
	@RollingWindowRateLimiter(key = "/v1/ping", requestsRatePerWindow = 10,
	timeUnit = WindowTimeUnit.MINUTE, fallbackMethod = "rateLimitResponse")
	public ResponseEntity<?> greetCustomer() {
		
		return  ResponseEntity.status(HttpStatus.OK).body("pong");
	}


### Fallback method which would be called when rate limited

	public ResponseEntity<?> rateLimitResponse() {
		return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("You are rate limited!!");
		
	}
	
