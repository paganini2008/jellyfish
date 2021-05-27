package indi.atlantis.framework.jellyfish.http;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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
 * JellyfishApiWatcherAutoConfiguration
 *
 * @author Fred Feng
 * @version 1.0
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class JellyfishApiWatcherAutoConfiguration {

	@ConditionalOnMissingBean
	@Bean
	public PathMatcher pathMatcher() {
		return new PathMatcher();
	}

	@Bean
	public ApiStatisticWatcher apiStatisticWatcher() {
		log.info("Load {}", ApiStatisticWatcher.class.getName());
		return new ApiStatisticWatcher();
	}

	@Bean
	public ApiQpsWatcher apiQpsWatcher() {
		log.info("Load {}", ApiQpsWatcher.class.getName());
		return new ApiQpsWatcher();
	}

	@ConditionalOnMissingBean
	@Bean
	public TransportClient transportClient(@Value("${atlantis.framework.jellyfish.brokerUrl}") String brokerUrl) {
		return new HttpTransportClient(brokerUrl);
	}

	@ConditionalOnMissingBean
	@Bean(destroyMethod = "shutdown")
	public ThreadPoolTaskScheduler taskScheduler(@Value("${atlantis.framework.jellyfish.apiwatcher.scheduler.maxSize:8}") int maxSize) {
		ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
		threadPoolTaskScheduler.setPoolSize(maxSize);
		threadPoolTaskScheduler.setThreadFactory(new PooledThreadFactory("jellyfish-apiwatcher-task-scheduler-"));
		threadPoolTaskScheduler.setWaitForTasksToCompleteOnShutdown(true);
		threadPoolTaskScheduler.setAwaitTerminationSeconds(60);
		return threadPoolTaskScheduler;
	}

	/**
	 * 
	 * JellyfishHttpWebMvcConfig
	 * 
	 * @author Fred Feng
	 *
	 * @version 1.0
	 */
	@Configuration
	static class JellyfishHttpWebMvcConfig implements WebMvcConfigurer {

		@Value("#{'${atlantis.framework.jellyfish.apiwatcher.excludedUrlPatterns:}'.split(',')}")
		private List<String> excludedUrlPatterns = new ArrayList<String>();

		@Autowired
		private ApiStatisticWatcher apiStatisticWriter;

		@Autowired
		private ApiQpsWatcher apiQpsWriter;

		@Override
		public void addInterceptors(InterceptorRegistry registry) {
			InterceptorRegistration interceptorRegistration = registry.addInterceptor(apiStatisticWriter).addPathPatterns("/**");
			if (CollectionUtils.isNotEmpty(excludedUrlPatterns)) {
				interceptorRegistration.excludePathPatterns(excludedUrlPatterns);
			}
			interceptorRegistration = registry.addInterceptor(apiQpsWriter).addPathPatterns("/**");
			if (CollectionUtils.isNotEmpty(excludedUrlPatterns)) {
				interceptorRegistration.excludePathPatterns(excludedUrlPatterns);
			}
		}
	}

}
