package org.springtribe.framework.jellyfish.stat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springtribe.framework.gearless.Handler;
import org.springtribe.framework.gearless.common.Tuple;
import org.springtribe.framework.gearless.utils.StatisticalMetric;
import org.springtribe.framework.gearless.utils.StatisticalMetrics;

/**
 * 
 * StatisticSynchronizer
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public class StatisticSynchronizer implements Handler {

	public static final String TOPIC_NAME = StatisticSynchronizer.class.getName();

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

		long timestamp = tuple.getTimestamp();
		String metric = tuple.getField("metric", String.class);
		long highestValue = tuple.getField("highestValue", Long.class);
		long lowestValue = tuple.getField("lowestValue", Long.class);
		long totalValue = tuple.getField("totalValue", Long.class);
		long count = tuple.getField("count", Long.class);
		Catalog catalog = new Catalog(clusterName, applicationName, host, category, path);
		CatalogMetricsCollector<StatisticalMetric> statisticCollector = catalogContext.statisticCollector();
		statisticCollector.update(catalog, metric, timestamp,
				new StatisticalMetrics.LongMetric(highestValue, lowestValue, totalValue, count, timestamp, false));
	}

	@Override
	public String getTopic() {
		return TOPIC_NAME;
	}

}
