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
	