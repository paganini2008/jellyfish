package indi.atlantis.framework.jellyfish;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

import indi.atlantis.framework.jellyfish.http.ApiQpsHandler;
import indi.atlantis.framework.jellyfish.http.ApiStatisticHandler;
import indi.atlantis.framework.jellyfish.http.ApiStatisticSynchronizationHandler;
import indi.atlantis.framework.jellyfish.http.ApiStatisticSynchronizer;
import indi.atlantis.framework.jellyfish.http.ApiSummarySynchronizationHandler;
import indi.atlantis.framework.jellyfish.http.ApiSummarySynchronizer;
import indi.atlantis.framework.jellyfish.http.Environment;
import indi.atlantis.framework.jellyfish.log.LogEntrySearchService;
import indi.atlantis.framework.jellyfish.log.LogEntryService;
import indi.atlantis.framework.jellyfish.log.Slf4jHandler;
import indi.atlantis.framework.reditools.common.IdGenerator;
import indi.atlantis.framework.reditools.common.TimestampIdGenerator;
import indi.atlantis.framework.tridenter.InstanceId;
import indi.atlantis.framework.vortex.Handler;
import indi.atlantis.framework.vortex.buffer.BufferZone;
import indi.atlantis.framework.vortex.common.HashPartitioner;
import indi.atlantis.framework.vortex.common.NamedSelectionPartitioner;
import indi.atlantis.framework.vortex.common.Partitioner;
import indi.atlantis.framework.vortex.metric.FullSynchronizationExecutor;
import indi.atlantis.framework.vortex.metric.IncrementalSynchronizationExecutor;
import indi.atlantis.framework.vortex.metric.SynchronizationExecutor;
import indi.atlantis.framework.vortex.metric.Synchronizer;
import lombok.Setter;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 
 * JellyfishAutoConfiguration
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
@EnableElasticsearchRepositories("indi.atlantis.framework.jellyfish.log")
@Configuration(proxyBeanMethods = false)
public class JellyfishAutoConfiguration {

	private static final String idKeyPattern = "atlantis:framework:jellyfish:id:%s";

	@Value("${spring.application.cluster.name:default}")
	private String clusterName;

	@Autowired
	public void addPartitioner(Partitioner partitioner) {
		NamedSelectionPartitioner namedSelectionPartitioner = (NamedSelectionPartitioner) partitioner;
		final String[] fieldNames = { "clusterName", "applicationName", "host", "category", "path" };
		HashPartitioner hashPartitioner = new HashPartitioner(fieldNames);
		namedSelectionPartitioner.addPartitioner(hashPartitioner);
	}

	@Autowired
	public void configureBufferZone(BufferZone bufferZone, InstanceId instanceId) {
		final String subNamePrefix = clusterName + ":" + instanceId.get();
		bufferZone.setCollectionNamePrefix(BufferZone.DEFAULT_COLLECTION_NAME_PREFIX, subNamePrefix);
	}
	
	@Bean
	public Handler slf4jHandler() {
		return new Slf4jHandler();
	}

	@Bean
	public LogEntryService logEntryService() {
		return new LogEntryService();
	}

	@Bean
	public LogEntrySearchService logEntrySearchService() {
		return new LogEntrySearchService();
	}

	@ConditionalOnMissingBean(name = "logIdGenerator")
	@Bean
	public IdGenerator logIdGenerator(RedisConnectionFactory redisConnectionFactory) {
		final String keyPrefix = String.format(idKeyPattern, clusterName);
		return new TimestampIdGenerator(keyPrefix, redisConnectionFactory);
	}

	@Setter
	@Configuration
	@ConfigurationProperties(prefix = "spring.redis")
	public class RedisConfig {

		private String host = "localhost";
		private String password;
		private int port = 6379;
		private int dbIndex = 0;

		@ConditionalOnMissingBean
		@Bean
		public RedisConnectionFactory redisConnectionFactory() {
			RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
			redisStandaloneConfiguration.setHostName(host);
			redisStandaloneConfiguration.setPort(port);
			redisStandaloneConfiguration.setDatabase(dbIndex);
			redisStandaloneConfiguration.setPassword(RedisPassword.of(password));
			JedisClientConfiguration.JedisClientConfigurationBuilder jedisClientConfiguration = JedisClientConfiguration.builder();
			jedisClientConfiguration.connectTimeout(Duration.ofMillis(60000)).readTimeout(Duration.ofMillis(60000)).usePooling()
					.poolConfig(jedisPoolConfig());
			JedisConnectionFactory factory = new JedisConnectionFactory(redisStandaloneConfiguration, jedisClientConfiguration.build());
			return factory;
		}

		@ConditionalOnMissingBean
		@Bean
		public JedisPoolConfig jedisPoolConfig() {
			JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
			jedisPoolConfig.setMinIdle(1);
			jedisPoolConfig.setMaxIdle(10);
			jedisPoolConfig.setMaxTotal(200);
			jedisPoolConfig.setMaxWaitMillis(-1);
			jedisPoolConfig.setTestWhileIdle(true);
			return jedisPoolConfig;
		}

	}

}
