package indi.atlantis.framework.jellyfish.metrics;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.ScheduledFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import indi.atlantis.framework.vortex.ServerInfo;
import indi.atlantis.framework.vortex.common.NioClient;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * CatalogMetricSynchronization
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
@Slf4j
public class CatalogMetricSynchronization {

	@Autowired
	private ThreadPoolTaskScheduler taskScheduler;

	@Autowired
	private NioClient nioClient;

	@Qualifier("primaryCatalogContext")
	@Autowired
	private CatalogContext primaryCatalogContext;

	@Qualifier("secondCatalogContext")
	@Autowired
	private CatalogContext secondCatalogContext;

	@Value("${atlantis.framework.jellyfish.metrics.synchronizer.interval:3}")
	private int interval;

	@Value("${atlantis.framework.jellyfish.metrics.synchronizer.incrementalInterval:3}")
	private int incrementalInterval;

	private volatile ScheduledFuture<?> future;

	public synchronized void startSynchronization(ServerInfo[] serverInfos) {
		if (future != null) {
			future.cancel(true);
		}
		future = taskScheduler.scheduleWithFixedDelay(() -> {
			for (ServerInfo serverInfo : serverInfos) {
				InetSocketAddress remoteAddress = new InetSocketAddress(serverInfo.getHostName(), serverInfo.getPort());
				secondCatalogContext.synchronizeSummaryData(nioClient, remoteAddress, false);
				secondCatalogContext.synchronizeCountingData(nioClient, remoteAddress, false);
				secondCatalogContext.synchronizeHttpStatusCountingData(nioClient, remoteAddress, false);
				secondCatalogContext.synchronizeStatisticData(nioClient, remoteAddress, false);
			}
		}, Duration.ofSeconds(interval));
		log.info("Start full synchronization to {} with {} seconds.", Arrays.toString(serverInfos), interval);
	}

	public synchronized void startIncrementalSynchronization(ServerInfo serverInfo) {
		if (future != null) {
			future.cancel(true);
		}
		taskScheduler.scheduleWithFixedDelay(() -> {
			InetSocketAddress remoteAddress = new InetSocketAddress(serverInfo.getHostName(), serverInfo.getPort());
			primaryCatalogContext.synchronizeSummaryData(nioClient, remoteAddress, true);
			primaryCatalogContext.synchronizeCountingData(nioClient, remoteAddress, true);
			primaryCatalogContext.synchronizeHttpStatusCountingData(nioClient, remoteAddress, true);
			primaryCatalogContext.synchronizeStatisticData(nioClient, remoteAddress, true);

		}, Duration.ofSeconds(incrementalInterval));
		log.info("Start incremental synchronization to {} with {} seconds.", serverInfo, incrementalInterval);
	}

}
