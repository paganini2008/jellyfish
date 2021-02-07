package org.springtribe.framework.jellyfish;

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
import org.springtribe.framework.gearless.common.HashPartitioner;
import org.springtribe.framework.gearless.common.NamedSelectionPartitioner;
import org.springtribe.framework.jellyfish.log.LogEntrySearchService;
import org.springtribe.framework.jellyfish.log.LogEntryService;
import org.springtribe.framework.jellyfish.log.Slf4jHandler;
import org.springtribe.framework.jellyfish.stat.CatalogContext;
import org.springtribe.framework.jellyfish.stat.CatalogMetricsSynchronizerStarter;
import org.springtribe.framework.jellyfish.stat.CatalogSummarySynchronizer;
import org.springtribe.framework.jellyfish.stat.CountingSynchronizer;
import org.springtribe.framework.jellyfish.stat.HttpStatusCountingSynchronizer;
import org.springtribe.framework.jellyfish.stat.QpsHandler;
import org.springtribe.framework.jellyfish.stat.RealtimeStatisticHandler;
import org.springtribe.framework.jellyfish.stat.StatisticSynchronizer;
import org.springtribe.framework.reditools.common.IdGenerator;
import org.springtribe.framework.reditools.common.TimestampIdGenerator;

import lombok.Setter;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 
 * JellyfishAutoConfiguration
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
@EnableElasticsearchRepositories("org.springtribe.framework.jellyfish.log")
@Configuration(proxyBeanMethods = false)
public class JellyfishAutoConfiguration {

	private static final String keyPattern = "spring:application:cluster:jellyfish:%s:id";

	@Value("${spring.application.cluster.name:default}")
	private String clusterName;

	@Autowired
	public void addHashPartitioner(NamedSelectionPartitioner partitioner) {
		final String[] fieldNames = { "clusterName", "applicationName", "host", "category", "path" };
		HashPartitioner hashPartitioner = new HashPartitioner(fieldNames);
		partitioner.addPartitioner(hashPartitioner);
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
	public CatalogSummarySynchronizer catalogSummarySynchronizer() {
		return new CatalogSummarySynchronizer();
	}

	@Bean
	public CountingSynchronizer countingSynchronizer() {
		return new CountingSynchronizer();
	}

	@Bean
	public HttpStatusCountingSynchronizer httpStatusCountingSynchronizer() {
		return new HttpStatusCountingSynchronizer();
	}

	@Bean
	public StatisticSynchronizer statisticSynchronizer() {
		return new StatisticSynchronizer();
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
	public CatalogMetricsSynchronizerStarter catalogMetricsSynchronizerStarter() {
		return new CatalogMetricsSynchronizerStarter();
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
