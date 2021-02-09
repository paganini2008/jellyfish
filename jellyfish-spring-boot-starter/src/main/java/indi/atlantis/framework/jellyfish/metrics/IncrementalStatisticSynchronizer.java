package indi.atlantis.framework.jellyfish.metrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import indi.atlantis.framework.vortex.Handler;
import indi.atlantis.framework.vortex.aggregation.StatisticalMetric;
import indi.atlantis.framework.vortex.aggregation.StatisticalMetrics;
import indi.atlantis.framework.vortex.common.Tuple;

/**
 * 
 * IncrementalStatisticSynchronizer
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public class IncrementalStatisticSynchronizer implements Handler {

	public static final String TOPIC_NAME = IncrementalStatisticSynchronizer.class.getName();

	@Qualifier("secondaryCatalogMetricContext")
	@Autowired
	private CatalogMetricContext catalogMetricContext;

	@Override
	public void onData(Tuple tuple) {
		Catalog catalog = Catalog.of(tuple);
		long timestamp = tuple.getTimestamp();
		String metric = tuple.getField("metric", String.class);
		long highestValue = tuple.getField("highestValue", Long.class);
		long lowestValue = tuple.getField("lowestValue", Long.class);
		long totalValue = tuple.getField("totalValue", Long.class);
		long count = tuple.getField("count", Long.class);
		CatalogMetricCollector<StatisticalMetric> collector = catalogMetricContext.statisticCollector();
		collector.update(catalog, metric, timestamp,
				new StatisticalMetrics.LongMetric(highestValue, lowestValue, totalValue, count, timestamp, false));
	}

	@Override
	public String getTopic() {
		return TOPIC_NAME;
	}

}
