package indi.atlantis.framework.jellyfish.metrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;

import indi.atlantis.framework.seafloor.ApplicationInfo;
import indi.atlantis.framework.seafloor.election.ApplicationClusterRefreshedEvent;

/**
 * 
 * IncrementalSynchronizationListener
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public class IncrementalSynchronizationListener implements ApplicationListener<ApplicationClusterRefreshedEvent> {

	@Autowired
	private Synchronization synchronization;

	@Override
	public void onApplicationEvent(ApplicationClusterRefreshedEvent event) {
		final ApplicationInfo leaderInfo = event.getLeaderInfo();
		synchronization.startIncrementalSynchronization(leaderInfo);
	}

}
