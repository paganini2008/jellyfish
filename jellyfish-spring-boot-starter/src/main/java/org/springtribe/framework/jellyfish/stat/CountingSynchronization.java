package org.springtribe.framework.jellyfish.stat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springtribe.framework.gearless.Handler;
import org.springtribe.framework.gearless.common.Tuple;
import org.springtribe.framework.gearless.utils.CustomizedMetric;

/**
 * 
 * CountingSynchronization
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public class CountingSynchronization implements Handler {

	@Autowired
	private CatalogContext catalogContext;

	@Override
	public void onData(Tuple tuple) {
		String clusterName = tuple.getField("clusterName", String.class);
		String applicationName = tuple.getField("applicationName", String.class);
		String host = tuple.getField("host", String.class);
		String category = tuple.getField("category", String.class);
		String path = tuple.getField("path", String.class);

		long count = tuple.getField("count", Long.class);
		long failedCount = tuple.getField("failedCount", Long.class);
		long timeoutCount = tuple.getField("timeoutCount", Long.class);
		long timestamp = tuple.getTimestamp();
		CatalogMetricsCollector<CustomizedMetric<Counter>> statisticCollector = catalogContext.getCountingCollector();
		statisticCollector.update(new Catalog(clusterName, applicationName, host, category, path), "count", timestamp,
				new CountingMetric(new Counter(count, failedCount, timeoutCount)));
	}

	@Override
	public String getTopic() {
	}

}
