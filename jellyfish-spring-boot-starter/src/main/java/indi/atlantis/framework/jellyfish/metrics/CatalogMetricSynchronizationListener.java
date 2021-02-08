package indi.atlantis.framework.jellyfish.metrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;

import indi.atlantis.framework.seafloor.ApplicationInfo;
import indi.atlantis.framework.seafloor.InstanceId;
import indi.atlantis.framework.seafloor.election.ApplicationClusterLeaderEvent;
import indi.atlantis.framework.seafloor.election.LeaderNotFoundException;
import indi.atlantis.framework.vortex.ApplicationTransportContext;
import indi.atlantis.framework.vortex.ServerInfo;

/**
 * 
 * CatalogMetricSynchronizationListener
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public class CatalogMetricSynchronizationListener implements ApplicationListener<ApplicationClusterLeaderEvent> {

	@Autowired
	private InstanceId instanceId;

	@Autowired
	private ApplicationTransportContext applicationTransportContext;

	@Autowired
	private CatalogMetricSynchronization catalogMetricsSynchronization;

	@Override
	public void onApplicationEvent(ApplicationClusterLeaderEvent event) {
		final ApplicationInfo leaderInfo = instanceId.getLeaderInfo();
		if (leaderInfo == null) {
			throw new LeaderNotFoundException();
		}
		ServerInfo[] serverInfos = applicationTransportContext.getServerInfos(info -> {
			return !info.equals(leaderInfo);
		});
		catalogMetricsSynchronization.startSynchronization(serverInfos);
	}

}
