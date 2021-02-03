package org.springtribe.framework.jellyfish.ui;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 
 * JellyfishUIAutoConfiguration
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
@Import({ LogPage.class, StatisticPage.class, LogEntryController.class, StatisticController.class })
@Configuration(proxyBeanMethods = false)
public class JellyfishUIAutoConfiguration {

	@Bean
	public WebMvcConfig webMvcConfig() {
		return new WebMvcConfig();
	}

}
