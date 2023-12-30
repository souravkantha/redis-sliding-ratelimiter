# Distributed Rolling Window Rate Limiter

![image](https://github.com/souravkantha/redis-sliding-ratelimiter/assets/32014166/bf38fa93-b1ec-4ff4-9732-aea4bfce9eb5)

 <br /> <br /> <br />
## Another Rate Limiter, huh 🤨

RollingWindowRateLimiter(RWRL) is a distributed rate limiter written in java. It can be used in a distributed environment where each services or components rate limiting is computed using redis storage. This implementation uses **Sliding Window Algorithm** (refer below diagram)

![image](https://github.com/souravkantha/redis-sliding-ratelimiter/assets/32014166/8591d1dc-7adf-48a2-8b4e-891fc5276ee6)


## How to import in your project

### Maven
	<dependency>
	    <groupId>io.github.souravkantha</groupId>
	    <artifactId>redis-sliding-ratelimiter</artifactId>
	    <version>1.0.1</version>
	</dependency>

### Gradle
	implementation 'io.github.souravkantha:redis-sliding-ratelimiter:1.0.1'

 ### Gradle (Kotlin)
 	implementation("io.github.souravkantha:redis-sliding-ratelimiter:1.0.1")

  


## Configs to remember

* Redis Client Config
  * **Default** - This will use local redis instance running in localhost:6379

		RedissonClientConfig redisClientConfig = new RedissonClientConfig();
  * **Custom IP and port** - We can pass custom IP and port using this config

		RedissonClientConfig redisClientConfig = new RedissonClientConfig("192.168.120.12", 7766);
  * **Sentinel or Cluster** - We can pass java.io.File object to use config from redisson yaml config (https://github.com/redisson/redisson/wiki/2.-Configuration#22-declarative-configuration)

  		// config yaml file path as String
		RedissonClientConfig redisClientConfig = new RedissonClientConfig(configFilePath);
		// config yaml loaded as File Object
		RedissonClientConfig redisClientConfig = new RedissonClientConfig(new File("config-file.yaml")));


* RWRL Window Config - WindowTimeUnit.class
  * **SECOND**
  * **MINUTE**
  * **HOUR**
  * **DAY** 

* @RollingWindowRateLimite - Attributes
  * **key** (Mandatory) - String
  * **requestsRatePerWindow** (Mandatory) - Integer
  * **timeUnit** (Optional) - WindowTimeUnit enum. Default is WindowTimeUnit.SECOND
  * **fallbackMethod** (Optional) - Any Method from same class where annotation is applied. Default is ""

## How to use RWRL

We can use **@RollingWindowRateLimiter** annotation before the method which we want to rate limit.

	@RollingWindowRateLimiter(key = "any-string-as-key", requestsRatePerWindow = <integer>,timeUnit = WindowTimeUnit.MINUTE, fallbackMethod = "any-method-in-same-class")

If we don't want any fallback method to be used. In case of rate limit is reached, default string "You are rate limited!!" will be returned.

### Ways to use Fallback method when rate limit is reached

Return type for both target and fallback method should be same.

#### Target method with no-args

	
	@RollingWindowRateLimiter(key = "/test/v1/foo", requestsRatePerWindow = 2, timeUnit = WindowTimeUnit.MINUTE, fallbackMethod = "rateLimitResponse")
	public ResponseEntity<?> foo() {
		
		return  ResponseEntity.status(HttpStatus.OK).body("bar");
	}
	
	
	public ResponseEntity<?> rateLimitResponse() {
		return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("You are rate limited!");
		
	}
 
**To-Remember: **In the above code, method foo() does not have any args. Hence, rateLimitResponse() method should also not have any args.


#### Target method with args

	
	@RollingWindowRateLimiter(key = "/test/v1/ping", requestsRatePerWindow = 10,
	timeUnit = WindowTimeUnit.MINUTE, fallbackMethod = "rateLimitResponse")
	public ResponseEntity<?> ping(@PathVariable("name") String name, @PathVariable("id") String id) {
		
		return  ResponseEntity.status(HttpStatus.OK).body("pong -> " + name);
	}
	
	public ResponseEntity<?> rateLimitResponse(Object [] args) {
		// name -> args[0]  -- This is same "name" arg from above ping method
		// id -> args[1]  -- This is same "id" arg from above ping method
  
  		String id = args[1].toString();
		return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("You are rate limited ID :" + id );
		
	}
 
**To-Remember: **In the above code, method ping(@PathVariable("name") String name, @PathVariable("id") have two args. Hence, rateLimitResponse(Object []) method should have Object [] as args. 

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
	
	@GetMapping("/test/v1/ping/{name}/{id}")
	@RollingWindowRateLimiter(key = "/test/v1/ping", requestsRatePerWindow = 10,
	timeUnit = WindowTimeUnit.MINUTE, fallbackMethod = "rateLimitResponse")
	public ResponseEntity<?> ping(@PathVariable("name") String name, @PathVariable("id") String id) {
		
		return  ResponseEntity.status(HttpStatus.OK).body("pong -> " + name);
	}
	


### Fallback method which would be called when rate limited

	public ResponseEntity<?> rateLimitResponse(Object [] args) {
		// name -> args[0]  -- This is same "name" arg from above ping method
		// id -> args[1]  -- This is same "id" arg from above ping method
  
  		String id = args[1].toString();
		return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("You are rate limited ID :" + id );
		
	}

### How to use with Kafka Consumer to throttle requests

	@KafkaListener(topics = "test")
 	@RollingWindowRateLimiter(key = "test-consumer", requestsRatePerWindow = 10,
	timeUnit = WindowTimeUnit.MINUTE, fallbackMethod = "rateLimitResponse")
  	public void consumeMessage(String message, Acknowledgment acknowledgment) throws InterruptedException {
	  
	  msgProcessor.processMessage(message);
	  
	  acknowledgment.acknowledge();
	 
  	}

  	 public void rateLimitResponse(Object [] args) {
		// message -> args[0]  
		// acknowledgment -> args[1]  
  
  		Acknowledgment acknowledgment = (Acknowledgment) args[1];
		acknowledgment.nack(Duration.ofMillis(1000*5)); // send NACK back to broker
		
	}
