package indi.atlantis.framework.jellyfish.agent;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.github.paganini2008.devtools.collection.CollectionUtils;
import com.github.paganini2008.devtools.multithreads.PooledThreadFactory;

import indi.atlantis.framework.vortex.common.HttpTransportClient;
import indi.atlantis.framework.vortex.common.TransportClient;
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
public class AgentAutoConfiguration implements WebMvcConfigurer {

	@Value("#{'${spring.application.cluster.jellyfish.excludedUrlPatterns:}'.split(',')}")
	private List<String> excludedUrlPatterns = new ArrayList<String>();

	@ConditionalOnMissingBean
	@Bean
	public PathMatcher pathMatcher() {
		return new PathMatcher();
	}

	@Bean("realtimeStatisticalWriter")
	public StatisticalWriter realtimeStatisticalWriter() {
		log.info("Load RealtimeStatisticalWriter");
		return new RealtimeMetricsWriter();
	}

	@Bean("qpsWriter")
	public StatisticalWriter qpsWriter() {
		log.info("Load QpsWriter");
		return new QpsWriter();
	}

	@ConditionalOnMissingBean
	@Bean
	public TransportClient transportClient(@Value("${atlantis.jellyfish.brokerUrl}") String brokerUrl) {
		return new HttpTransportClient(brokerUrl);
	}

	@ConditionalOnMissingBean
	@Bean(destroyMethod = "shutdown")
	public ThreadPoolTaskScheduler jellyfishMonitorTaskScheduler(
			@Value("${spring.application.cluster.jellyfish.threadPool.maxSize:8}") int maxSize) {
		ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
		threadPoolTaskScheduler.setPoolSize(maxSize);
		threadPoolTaskScheduler.setThreadFactory(new PooledThreadFactory("jellyfish-agent-task-scheduler-"));
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
		interceptorRegistration = registry.addInterceptor(qpsWriter()).addPathPatterns("/**");
		if (CollectionUtils.isNotEmpty(excludedUrlPatterns)) {
			interceptorRegistration.excludePathPatterns(excludedUrlPatterns);
		}
	}

}
