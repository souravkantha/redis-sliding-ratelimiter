package com.souravkantha.redis.clientconfig;

import java.io.File;
import java.io.IOException;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

public class RedissonClientConfig {
	
	private RedissonClient redisson;
	
	public RedissonClientConfig() {
		
		// connects to 127.0.0.1:6379 by default
		this.redisson = Redisson.create();
	}
	
	public RedissonClientConfig(final String fileName) throws IOException {
		
		Config config = Config.fromYAML(new File(fileName));  
		this.redisson = Redisson.create(config);
		
	}
	
	public RedissonClientConfig(final String ip, final int port) throws IOException {
		
		Config config = new Config();
		config.useSingleServer().setAddress("redis://".concat(ip).concat(":").concat(String.valueOf(port)));
		this.redisson = Redisson.create(config);
		
	}
	
	public RedissonClient getClient() {
		
		return this.redisson;
		
	}

}
