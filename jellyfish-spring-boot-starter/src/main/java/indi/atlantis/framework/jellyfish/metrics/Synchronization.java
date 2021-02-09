package indi.atlantis.framework.jellyfish.metrics;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.concurrent.ScheduledFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import indi.atlantis.framework.seafloor.ApplicationInfo;
import indi.atlantis.framework.vortex.ApplicationTransportContext;
import indi.atlantis.framework.vortex.ServerInfo;
import indi.atlantis.framework.vortex.common.NioClient;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * Synchronization
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
@Slf4j
public class Synchronization {

	@Autowired
	private ApplicationTransportContext applicationTransportContext;

	@Autowired
	private ThreadPoolTaskScheduler taskScheduler;

	@Autowired
	private NioClient nioClient;

	@Qualifier("primaryCatalogMetricContext")
	@Autowired
	private CatalogMetricContext primaryCatalogMetricContext;

	@Qualifier("secondaryCatalogMetricContext")
	@Autowired
	private CatalogMetricContext secondaryCatalogMetricContext;

	@Value("${atlantis.framework.jellyfish.metrics.synchronizer.interval:5}")
	private int interval;

	@Value("${atlantis.framework.jellyfish.metrics.synchronizer.incrementalInterval:5}")
	private int incrementalInterval;

	private volatile ScheduledFuture<?> fullFuture;

	private volatile ScheduledFuture<?> incrementalFuture;

	public synchronized void startFullSynchronization(ApplicationInfo leaderInfo) {
		if (fullFuture != null) {
			throw new IllegalStateException("Full synchronization is running now.");
		}
		fullFuture = taskScheduler.scheduleWithFixedDelay(() -> {
			ServerInfo[] serverInfos = applicationTransportContext.getServerInfos(info -> {
				return !info.equals(leaderInfo);
			});
			for (ServerInfo serverInfo : serverInfos) {
				InetSocketAddress remoteAddress = new InetSocketAddress(serverInfo.getHostName(), serverInfo.getPort());
				secondaryCatalogMetricContext.synchronizeSummaryData(nioClient, remoteAddress, false);
				secondaryCatalogMetricContext.synchronizeCountingData(nioClient, remoteAddress, false);
				secondaryCatalogMetricContext.synchronizeHttpStatusCountingData(nioClient, remoteAddress, false);
				secondaryCatalogMetricContext.synchronizeStatisticData(nioClient, remoteAddress, false);
			}
		}, Duration.ofSeconds(interval));
		log.info("Start full synchronization from {} with {} seconds.", leaderInfo, interval);
	}

	public synchronized void startIncrementalSynchronization(ApplicationInfo leaderInfo) {
		if (incrementalFuture != null) {
			incrementalFuture.cancel(false);
		}
		incrementalFuture = taskScheduler.scheduleWithFixedDelay(() -> {
			ServerInfo serverInfo = applicationTransportContext.getServerInfo(leaderInfo);
			if (serverInfo != null) {
				InetSocketAddress remoteAddress = new InetSocketAddress(serverInfo.getHostName(), serverInfo.getPort());
				primaryCatalogMetricContext.synchronizeSummaryData(nioClient, remoteAddress, true);
				primaryCatalogMetricContext.synchronizeCountingData(nioClient, remoteAddress, true);
				primaryCatalogMetricContext.synchronizeHttpStatusCountingData(nioClient, remoteAddress, true);
				primaryCatalogMetricContext.synchronizeStatisticData(nioClient, remoteAddress, true);
			} else {
				log.warn("Leader nioserver is not available now.");
			}
		}, Duration.ofSeconds(incrementalInterval));
		log.info("Start incremental synchronization to {} with {} seconds.", leaderInfo, incrementalInterval);
	}

	public void stopFullSynchronization() {
		if (fullFuture != null) {
			fullFuture.cancel(true);
		}
	}

	public void stopIncrementalSynchronization() {
		if (incrementalFuture != null) {
			incrementalFuture.cancel(true);
		}
	}

}
