/**
* Copyright 2017-2022 Fred Feng (paganini.fy@gmail.com)

* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package io.atlantisframework.jellyfish.logging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import com.github.paganini2008.springdessert.reditools.common.IdGenerator;
import com.github.paganini2008.springdessert.reditools.common.TimeBasedIdGenerator;

import io.atlantisframework.tridenter.InstanceId;
import io.atlantisframework.vortex.Handler;
import io.atlantisframework.vortex.buffer.BufferZone;
import io.atlantisframework.vortex.common.HashPartitioner;
import io.atlantisframework.vortex.common.MultipleChoicePartitioner;
import io.atlantisframework.vortex.common.Partitioner;

/**
 * 
 * JellyfishAutoConfiguration
 *
 * @author Fred Feng
 * @since 2.0.1
 */
@ConditionalOnProperty(name = "atlantis.framework.jellyfish.logging.enabled", havingValue = "true", matchIfMissing = true)
@Configuration(proxyBeanMethods = false)
@Import({ LogEntryController.class })
@EnableElasticsearchRepositories("io.atlantisframework.jellyfish.logging")
public class JellyfishLoggingAutoConfiguration {

	private static final String idKeyPattern = "atlantis:framework:jellyfish:id:%s";

	@Value("${spring.application.cluster.name:default}")
	private String clusterName;

	@Autowired
	public void addPartitioner(Partitioner partitioner) {
		MultipleChoicePartitioner multipleChoicePartitioner = (MultipleChoicePartitioner) partitioner;
		final String[] fieldNames = { "clusterName", "applicationName", "host", "category", "path" };
		HashPartitioner hashPartitioner = new HashPartitioner(fieldNames);
		multipleChoicePartitioner.addPartitioner(hashPartitioner);
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
		return new TimeBasedIdGenerator(keyPrefix, redisConnectionFactory);
	}

}
