package org.springdessert.framework.jellyfish.stat;

import java.util.Set;

import org.springframework.data.redis.core.RedisOperations;

import com.github.paganini2008.devtools.cache.AbstractCache;
import com.github.paganini2008.devtools.cache.Cache;

/**
 * 
 * MetricCache
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public class MetricCache extends AbstractCache implements Cache {

	private final String key;
	private final RedisOperations<String, Object> redisOperations;

	public MetricCache(String key, RedisOperations<String, Object> redisOperations) {
		this.key = key;
		this.redisOperations = redisOperations;
	}

	@Override
	public void putObject(Object hashKey, Object value, boolean ifAbsent) {
		redisOperations.opsForHash().put(key, hashKey, value);
	}

	@Override
	public boolean hasKey(Object hashKey) {
		return redisOperations.opsForHash().hasKey(key, hashKey);
	}

	@Override
	public Object getObject(Object hashKey) {
		return redisOperations.opsForHash().get(key, hashKey);
	}

	@Override
	public Object removeObject(Object hashKey) {
		return redisOperations.opsForHash().delete(key, hashKey);
	}

	@Override
	public Set<Object> keys() {
		return redisOperations.opsForHash().keys(key);
	}

	@Override
	public void clear() {
		redisOperations.delete(key);
	}

	@Override
	public int getSize() {
		Long value = redisOperations.opsForHash().size(key);
		return value != null ? value.intValue() : 0;
	}

}
