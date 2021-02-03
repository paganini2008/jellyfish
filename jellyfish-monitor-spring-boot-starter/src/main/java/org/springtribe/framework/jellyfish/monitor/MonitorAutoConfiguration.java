package org.springtribe.framework.jellyfish.monitor;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springtribe.framework.gearless.common.HttpTransportClient;
import org.springtribe.framework.gearless.common.TransportClient;

import com.github.paganini2008.devtools.collection.CollectionUtils;
import com.github.paganini2008.devtools.multithreads.PooledThreadFactory;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * MonitorAutoConfiguration
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
@Slf4j
@ConditionalOnWebApplication
@Configuration
public class MonitorAutoConfiguration implements WebMvcConfigurer {

	@Value("#{'${spring.application.cluster.jellyfish.excludedUrlPatterns:}'.split(',')}")
	private List<String> excludedUrlPatterns = new ArrayList<String>();

	@ConditionalOnMissingBean
	@Bean
	public PathMatchedMap pathMatchedMap() {
		PathMatchedMap map = new PathMatchedMap();
		map.put("/**", 3000L);
		return map;
	}

	@Bean("realtimeStatisticalWriter")
	public StatisticalWriter realtimeStatisticalWriter() {
		log.info("Load RealtimeStatisticalWriter");
		return new RealtimeStatisticalWriter();
	}

	@Bean("bulkStatisticalWriter")
	public StatisticalWriter bulkStatisticalWriter() {
		log.info("Load BulkStatisticalWriter");
		return new BulkStatisticalWriter();
	}

	@ConditionalOnMissingBean
	@Bean
	public TransportClient transportClient(@Value("${spring.application.cluster.jellyfish.brokerUrl}") String brokerUrl) {
		return new HttpTransportClient(brokerUrl);
	}

	@ConditionalOnMissingBean
	@Bean(destroyMethod = "shutdown")
	public ThreadPoolTaskExecutor jellyfishMonitorTaskExecutor(@Value("${spring.application.cluster.jellyfish.threadPool.maxSize:8}") int maxSize) {
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setCorePoolSize(maxSize);
		taskExecutor.setMaxPoolSize(maxSize);
		taskExecutor.setThreadFactory(new PooledThreadFactory("jellyfish-monitor-task-executor-"));
		return taskExecutor;
	}

	@ConditionalOnMissingBean
	@Bean(destroyMethod = "shutdown")
	public ThreadPoolTaskScheduler jellyfishMonitorTaskScheduler(@Value("${spring.application.cluster.jellyfish.threadPool.maxSize:8}") int maxSize) {
		ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
		threadPoolTaskScheduler.setPoolSize(maxSize);
		threadPoolTaskScheduler.setThreadFactory(new PooledThreadFactory("jellyfish-monitor-task-scheduler-"));
		threadPoolTaskScheduler.setWaitForTasksToCompleteOnShutdown(true);
		threadPoolTaskScheduler.setAwaitTerminationSeconds(60);
		return threadPoolTaskScheduler;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		InterceptorRegistration interceptorRegistration = registry.addInterceptor(realtimeStatisticalWriter()).addPathPatterns("/**");
		if (CollectionUtils.isNotEmpty(excludedUrlPatterns)) {
			interceptorRegistration.excludePathPatterns(excludedUrlPatterns);
		}
		interceptorRegistration = registry.addInterceptor(bulkStatisticalWriter()).addPathPatterns("/**");
		if (CollectionUtils.isNotEmpty(excludedUrlPatterns)) {
			interceptorRegistration.excludePathPatterns(excludedUrlPatterns);
		}
	}

}
