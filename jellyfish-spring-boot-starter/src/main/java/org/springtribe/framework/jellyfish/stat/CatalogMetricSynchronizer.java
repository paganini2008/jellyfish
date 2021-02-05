package org.springtribe.framework.jellyfish.stat;

import java.time.Duration;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springtribe.framework.gearless.common.NioClient;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * CatalogMetricSynchronizer
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
@Slf4j
public class CatalogMetricSynchronizer implements Runnable, InitializingBean {

	@Autowired
	private ThreadPoolTaskScheduler taskScheduler;

	@Qualifier("primaryCatalogContext")
	@Autowired
	private CatalogContext catalogContext;

	@Autowired
	private NioClient nioClient;

	@Override
	public void afterPropertiesSet() throws Exception {
		taskScheduler.scheduleWithFixedDelay(this, Duration.ofSeconds(3));
		log.info("Start TransientStatisticSynchronizer.");
	}

	@Override
	public void run() {
		catalogContext.synchronizeSummaryData(nioClient);
		catalogContext.synchronizeCountingData(nioClient);
		catalogContext.synchronizeHttpStatusCountinData(nioClient);
		catalogContext.synchronizeStatisticData(nioClient);
	}

}
