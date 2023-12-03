package com.souravkantha.redis.ratelimiter.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.redisson.api.RLock;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;

import com.souravkantha.redis.clientconfig.RedissonClientConfig;
import com.souravkantha.redis.ratelimiter.core.WindowTimeUnit;

public class RedisService {

	private RedissonClientConfig redissonClientConfig;
	
	public RedisService(final RedissonClientConfig redissonClientConfig) {
		
		this.redissonClientConfig = redissonClientConfig;
		
	}

	public Boolean isRollingRateExceeded(final String key, final int rate, final WindowTimeUnit timeUnit,
			final String entropy) {
		
		RedissonClient client = this.redissonClientConfig.getClient();
		
		RLock lock = client.getLock("rateLock");
		
		lock.lock();
		try {
			RScoredSortedSet<String> rateLimiterSet = client.getScoredSortedSet(key);
			Long currentEpochMili = Instant.now().toEpochMilli();
			
			// k,v,t0; v,t1; .., v,tn
			// ------------ti-----x(curr - 1 min)------curr_time
			// ------curr_time < 5
			switch (timeUnit) {
			case SECOND:
				rateLimiterSet.removeRangeByScore(0, false, 
						Instant.ofEpochMilli(currentEpochMili).minus(1000, ChronoUnit.MILLIS).toEpochMilli(), false);
				break;
				
			case MINUTE:
				rateLimiterSet.removeRangeByScore(0, false, 
						Instant.ofEpochMilli(currentEpochMili).minus(60, ChronoUnit.SECONDS).toEpochMilli(), false);
				break;
				
			case HOUR:
				rateLimiterSet.removeRangeByScore(0, false, 
						Instant.ofEpochMilli(currentEpochMili).minus(60, ChronoUnit.MINUTES).toEpochMilli(), false);
				break;
				
			}
			// score, value
			final String value = new StringBuilder(currentEpochMili.toString()).append(entropy).toString();
			rateLimiterSet.add(currentEpochMili, value);
			
			return rateLimiterSet.size() >= rate;
			
			
		} finally {
			
		    lock.unlock();
		}
	}
	
}
