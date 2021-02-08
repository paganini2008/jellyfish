package indi.atlantis.framework.jellyfish;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
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

import indi.atlantis.framework.jellyfish.log.LogEntrySearchService;
import indi.atlantis.framework.jellyfish.log.LogEntryService;
import indi.atlantis.framework.jellyfish.log.Slf4jHandler;
import indi.atlantis.framework.jellyfish.metrics.CatalogContext;
import indi.atlantis.framework.jellyfish.metrics.FullCountingSynchronizer;
import indi.atlantis.framework.jellyfish.metrics.FullSynchronizationListener;
import indi.atlantis.framework.jellyfish.metrics.FullHttpStatusCountingSynchronizer;
import indi.atlantis.framework.jellyfish.metrics.IncrementalCountingSynchronizer;
import indi.atlantis.framework.jellyfish.metrics.IncrementalHttpStatusCountingSynchronizer;
import indi.atlantis.framework.jellyfish.metrics.IncrementalStatisticSynchronizer;
import indi.atlantis.framework.jellyfish.metrics.IncrementalSummarySynchronizer;
import indi.atlantis.framework.jellyfish.metrics.IncrementalSynchronizationListener;
import indi.atlantis.framework.jellyfish.metrics.QpsHandler;
import indi.atlantis.framework.jellyfish.metrics.RealtimeStatisticHandler;
import indi.atlantis.framework.jellyfish.metrics.FullStatisticSynchronizer;
import indi.atlantis.framework.jellyfish.metrics.FullSummarySynchronizer;
import indi.atlantis.framework.reditools.common.IdGenerator;
import indi.atlantis.framework.reditools.common.TimestampIdGenerator;
import indi.atlantis.framework.seafloor.InstanceId;
import indi.atlantis.framework.vortex.buffer.BufferZone;
import indi.atlantis.framework.vortex.common.HashPartitioner;
import indi.atlantis.framework.vortex.common.NamedSelectionPartitioner;
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

	private static final String keyPattern = "atlantis:framework:jellyfish:id:%s";

	@Value("${spring.application.cluster.name:default}")
	private String clusterName;

	@Autowired
	public void addHashPartitioner(NamedSelectionPartitioner partitioner) {
		final String[] fieldNames = { "clusterName", "applicationName", "host", "category", "path" };
		HashPartitioner hashPartitioner = new HashPartitioner(fieldNames);
		partitioner.addPartitioner(hashPartitioner);
	}

	@Autowired
	public void configureBufferZone(BufferZone bufferZone, InstanceId instanceId) {
		String subNamePrefix = clusterName + ":" + instanceId.get();
		bufferZone.setCollectionNamePrefix(BufferZone.DEFAULT_COLLECTION_NAME_PREFIX, subNamePrefix);
	}

	@Bean
	public Slf4jHandler slf4jHandler() {
		return new Slf4jHandler();
	}

	@Bean
	public RealtimeStatisticHandler realtimeStatisticalHandler() {
		return new RealtimeStatisticHandler();
	}

	@Bean
	public QpsHandler qpsHandler() {
		return new QpsHandler();
	}

	@Bean
	public IncrementalSummarySynchronizer incrementalSummarySynchronizer() {
		return new IncrementalSummarySynchronizer();
	}

	@Bean
	public IncrementalCountingSynchronizer incrementalCountingSynchronizer() {
		return new IncrementalCountingSynchronizer();
	}

	@Bean
	public IncrementalHttpStatusCountingSynchronizer incrementalHttpStatusCountingSynchronizer() {
		return new IncrementalHttpStatusCountingSynchronizer();
	}

	@Bean
	public IncrementalStatisticSynchronizer incrementalStatisticSynchronizer() {
		return new IncrementalStatisticSynchronizer();
	}

	@Bean
	public FullSummarySynchronizer summarySynchronizer() {
		return new FullSummarySynchronizer();
	}

	@Bean
	public FullCountingSynchronizer countingSynchronizer() {
		return new FullCountingSynchronizer();
	}

	@Bean
	public FullHttpStatusCountingSynchronizer httpStatusCountingSynchronizer() {
		return new FullHttpStatusCountingSynchronizer();
	}

	@Bean
	public FullStatisticSynchronizer statisticSynchronizer() {
		return new FullStatisticSynchronizer();
	}

	@Bean
	public CatalogContext primaryCatalogContext() {
		return new CatalogContext();
	}

	@Bean
	public CatalogContext secondaryCatalogContext() {
		return new CatalogContext();
	}

	@Bean
	public IncrementalSynchronizationListener incrementalSynchronizationListener() {
		return new IncrementalSynchronizationListener();
	}

	@Bean
	public FullSynchronizationListener fullSynchronizationListener() {
		return new FullSynchronizationListener();
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
		final String keyPrefix = String.format(keyPattern, clusterName);
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
