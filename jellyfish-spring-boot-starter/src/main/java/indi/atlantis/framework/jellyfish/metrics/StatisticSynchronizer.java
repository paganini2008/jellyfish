package indi.atlantis.framework.jellyfish.metrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import indi.atlantis.framework.vortex.Handler;
import indi.atlantis.framework.vortex.common.Tuple;
import indi.atlantis.framework.vortex.utils.StatisticalMetric;
import indi.atlantis.framework.vortex.utils.StatisticalMetrics;

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
		Catalog catalog = Catalog.of(tuple);
		long timestamp = tuple.getTimestamp();
		String metric = tuple.getField("metric", String.class);
		long highestValue = tuple.getField("highestValue", Long.class);
		long lowestValue = tuple.getField("lowestValue", Long.class);
		long totalValue = tuple.getField("totalValue", Long.class);
		long count = tuple.getField("count", Long.class);
		
		CatalogMetricsCollector<StatisticalMetric> collector = catalogContext.statisticCollector();
		collector.clear();
		collector.update(catalog, metric, timestamp,
				new StatisticalMetrics.LongMetric(highestValue, lowestValue, totalValue, count, timestamp, false));
	}

	@Override
	public String getTopic() {
		return TOPIC_NAME;
	}
}
