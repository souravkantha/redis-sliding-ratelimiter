# Distributed Rolling Window Rate Limiter

## Another Rate Limiter ? 🤨

RollingWindowRateLimiter(RWRL) is a distributed rate limiter written in java. It can be used in a distributed environment where each services or components rate limiting is computed using redis storage. This implementation uses **Sliding Window Algorithm** (refer below diagram)

![image](https://github.com/souravkantha/redis-sliding-ratelimiter/assets/32014166/8591d1dc-7adf-48a2-8b4e-891fc5276ee6)

## Configs to remember


## How to use RWRL

## Using with spring boot


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
	
