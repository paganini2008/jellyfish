package indi.atlantis.framework.jellyfish.metrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;

import indi.atlantis.framework.seafloor.ApplicationInfo;
import indi.atlantis.framework.seafloor.election.ApplicationClusterRefreshedEvent;
import indi.atlantis.framework.vortex.ApplicationTransportContext;
import indi.atlantis.framework.vortex.ServerInfo;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * IncrementalSynchronizationListener
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
@Slf4j
public class IncrementalSynchronizationListener implements ApplicationListener<ApplicationClusterRefreshedEvent> {

	@Autowired
	private ApplicationTransportContext applicationTransportContext;

	@Autowired
	private CatalogMetricSynchronization synchronization;

	@Override
	public void onApplicationEvent(ApplicationClusterRefreshedEvent event) {
		final ApplicationInfo leaderInfo = event.getLeaderInfo();
		ServerInfo serverInfo = applicationTransportContext.getServerInfo(leaderInfo);
		synchronization.startIncrementalSynchronization(serverInfo);
	}

}
