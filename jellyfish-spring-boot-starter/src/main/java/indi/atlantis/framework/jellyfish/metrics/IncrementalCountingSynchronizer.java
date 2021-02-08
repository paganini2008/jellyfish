package indi.atlantis.framework.jellyfish.metrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import indi.atlantis.framework.gearless.Handler;
import indi.atlantis.framework.gearless.common.Tuple;
import indi.atlantis.framework.gearless.utils.CustomizedMetric;

/**
 * 
 * IncrementalCountingSynchronizer
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public class IncrementalCountingSynchronizer implements Handler {

	public static final String TOPIC_NAME = IncrementalCountingSynchronizer.class.getName();

	@Qualifier("secondaryCatalogContext")
	@Autowired
	private CatalogContext catalogContext;

	@Override
	public void onData(Tuple tuple) {
		Catalog category = Catalog.of(tuple);
		long count = tuple.getField("count", Long.class);
		long failedCount = tuple.getField("failedCount", Long.class);
		long timeoutCount = tuple.getField("timeoutCount", Long.class);
		long timestamp = tuple.getTimestamp();
		CatalogMetricsCollector<CustomizedMetric<Counter>> collector = catalogContext.countingCollector();
		collector.update(category, MetricNames.COUNT, timestamp,
				new CountingMetric(new Counter(count, failedCount, timeoutCount), timestamp, false));
	}

	@Override
	public String getTopic() {
		return TOPIC_NAME;
	}

}
