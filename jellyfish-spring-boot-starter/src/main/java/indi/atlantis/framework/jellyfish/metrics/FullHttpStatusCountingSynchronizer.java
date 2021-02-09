package indi.atlantis.framework.jellyfish.metrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import indi.atlantis.framework.vortex.Handler;
import indi.atlantis.framework.vortex.aggregation.CustomizedMetric;
import indi.atlantis.framework.vortex.common.Tuple;

/**
 * 
 * FullHttpStatusCountingSynchronizer
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public class FullHttpStatusCountingSynchronizer implements Handler {

	public static final String TOPIC_NAME = FullHttpStatusCountingSynchronizer.class.getName();

	@Qualifier("secondaryCatalogMetricContext")
	@Autowired
	private CatalogMetricContext catalogMetricContext;

	@Override
	public void onData(Tuple tuple) {
		Catalog catalog = Catalog.of(tuple);

		long countOf1xx = tuple.getField("countOf1xx", Long.class);
		long countOf2xx = tuple.getField("countOf2xx", Long.class);
		long countOf3xx = tuple.getField("countOf3xx", Long.class);
		long countOf4xx = tuple.getField("countOf4xx", Long.class);
		long countOf5xx = tuple.getField("countOf5xx", Long.class);
		long timestamp = tuple.getTimestamp();

		CatalogMetricCollector<CustomizedMetric<HttpStatusCounter>> collector = catalogMetricContext.httpStatusCountingCollector();
		collector.update(catalog, MetricNames.HTTP_STATUS, timestamp, new HttpStatusCountingMetric(
				new HttpStatusCounter(countOf1xx, countOf2xx, countOf3xx, countOf4xx, countOf5xx), timestamp, false), false);

	}

	@Override
	public String getTopic() {
		return TOPIC_NAME;
	}
}
