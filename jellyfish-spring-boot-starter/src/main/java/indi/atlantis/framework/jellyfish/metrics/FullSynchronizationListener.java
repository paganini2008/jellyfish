package indi.atlantis.framework.jellyfish.metrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;

import indi.atlantis.framework.seafloor.InstanceId;
import indi.atlantis.framework.seafloor.LeaderState;
import indi.atlantis.framework.seafloor.election.ApplicationClusterLeaderEvent;

/**
 * 
 * FullSynchronizationListener
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public class FullSynchronizationListener implements ApplicationListener<ApplicationClusterLeaderEvent> {

	@Autowired
	private InstanceId instanceId;

	@Autowired
	private Synchronization synchronization;

	@Override
	public void onApplicationEvent(ApplicationClusterLeaderEvent event) {
		if (event.getLeaderState() == LeaderState.LEADABLE) {
			synchronization.startFullSynchronization(instanceId.getApplicationInfo());
		}
	}

}
