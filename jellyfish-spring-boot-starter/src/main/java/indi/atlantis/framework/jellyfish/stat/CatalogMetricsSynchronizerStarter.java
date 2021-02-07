package indi.atlantis.framework.jellyfish.stat;

import java.time.Duration;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springtribe.framework.gearless.common.NioClient;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * CatalogMetricSynchronizerStarter
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
@Slf4j
public class CatalogMetricsSynchronizerStarter implements Runnable, InitializingBean {

	@Value("${atlantis.framework.jellyfish.metrics.synchronizer.interval:3}")
	private int interval;

	@Autowired
	private ThreadPoolTaskScheduler taskScheduler;

	@Qualifier("primaryCatalogContext")
	@Autowired
	private CatalogContext catalogContext;

	@Autowired
	private NioClient nioClient;

	@Override
	public void afterPropertiesSet() throws Exception {
		taskScheduler.scheduleWithFixedDelay(this, Duration.ofSeconds(interval));
		log.info("Start CatalogMetricsSynchronizerStarter.");
	}

	@Override
	public void run() {
		log.info("Synchronize all catalogs metrics ...");
		catalogContext.synchronizeSummaryData(nioClient);
		catalogContext.synchronizeCountingData(nioClient);
		catalogContext.synchronizeHttpStatusCountingData(nioClient);
		catalogContext.synchronizeStatisticData(nioClient);
		log.info("Synchronize all catalogs completedly");
	}

}
