package indi.atlantis.framework.jellyfish.metrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;

import indi.atlantis.framework.seafloor.ApplicationInfo;
import indi.atlantis.framework.seafloor.election.ApplicationClusterRefreshedEvent;
import indi.atlantis.framework.seafloor.election.LeaderNotFoundException;
import indi.atlantis.framework.vortex.ApplicationTransportContext;
import indi.atlantis.framework.vortex.ServerInfo;

/**
 * 
 * IncrementalSynchronizationListener
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public class IncrementalSynchronizationListener implements ApplicationListener<ApplicationClusterRefreshedEvent> {

	@Autowired
	private ApplicationTransportContext applicationTransportContext;

	@Autowired
	private Synchronization synchronization;

	@Override
	public void onApplicationEvent(ApplicationClusterRefreshedEvent event) {
		final ApplicationInfo leaderInfo = event.getLeaderInfo();
		ServerInfo serverInfo = applicationTransportContext.getServerInfo(leaderInfo);
		if (serverInfo == null) {
			throw new LeaderNotFoundException();
		}
		synchronization.startIncrementalSynchronization(serverInfo);
	}

}
