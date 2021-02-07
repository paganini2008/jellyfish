package indi.atlantis.framework.jellyfish.metrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import indi.atlantis.framework.gearless.Handler;
import indi.atlantis.framework.gearless.common.Tuple;
import indi.atlantis.framework.gearless.utils.CustomizedMetric;

/**
 * 
 * CountingSynchronizer
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public class CountingSynchronizer implements Handler {

	public static final String TOPIC_NAME = CountingSynchronizer.class.getName();

	@Qualifier("secondaryCatalogContext")
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
		CatalogMetricsCollector<CustomizedMetric<Counter>> statisticCollector = catalogContext.countingCollector();
		statisticCollector.update(new Catalog(clusterName, applicationName, host, category, path), MetricNames.COUNT, timestamp,
				new CountingMetric(new Counter(count, failedCount, timeoutCount), timestamp, false));
	}

	@Override
	public String getTopic() {
		return TOPIC_NAME;
	}

}
