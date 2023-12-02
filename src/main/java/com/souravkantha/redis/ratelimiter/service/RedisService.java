package com.souravkantha.redis.ratelimiter.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

public class RedisService {

	private RedisTemplate<String, String> redisTemplate;
	
	public RedisService(final RedisTemplate<String,String> redisTemplate) {
		this.redisTemplate = redisTemplate;
		
	}

	public Boolean isRollingRateExceeded(final String key, final int rate, final int secondsPerWindow,
			final String entropy) {
		ZSetOperations<String, String> ops = redisTemplate.opsForZSet();
		Long currentEpochMili = Instant.now().toEpochMilli();
		
		// key, value, score
		final String value = new StringBuilder(currentEpochMili.toString()).append(entropy).toString();
		ops.add(key, value, currentEpochMili);
		
		//log.info("epoch-> "+currentEpochMili);
		//log.info("Ts-> "+ Instant.ofEpochMilli(currentEpochMili).minus(60, ChronoUnit.SECONDS).toEpochMilli());
		// k,v,t0; v,t1; .., v,tn
		// ------------ti-----x(curr - 1 min)------curr_time
		// ------curr_time < 5
		ops.removeRangeByScore(key, 0,  Instant.ofEpochMilli(currentEpochMili).minus(secondsPerWindow, ChronoUnit.SECONDS).toEpochMilli());
		//log.info("removedKeys:: " + removedKeys);
		//log.info("size:: " + ops.zCard(key));
		return (ops.zCard(key) >= rate);  // no of used rate is greater or equal than rate
	}
	
}
