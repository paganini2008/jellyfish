package indi.atlantis.framework.jellyfish.metrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import indi.atlantis.framework.vortex.Handler;
import indi.atlantis.framework.vortex.common.Tuple;
import indi.atlantis.framework.vortex.utils.CustomizedMetric;

/**
 * 
 * HttpStatusCountingSynchronizer
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public class HttpStatusCountingSynchronizer implements Handler {

	public static final String TOPIC_NAME = HttpStatusCountingSynchronizer.class.getName();

	@Qualifier("secondaryCatalogContext")
	@Autowired
	private CatalogContext catalogContext;

	@Override
	public void onData(Tuple tuple) {
		Catalog catalog = Catalog.of(tuple);

		long countOf1xx = tuple.getField("countOf1xx", Long.class);
		long countOf2xx = tuple.getField("countOf2xx", Long.class);
		long countOf3xx = tuple.getField("countOf3xx", Long.class);
		long countOf4xx = tuple.getField("countOf4xx", Long.class);
		long countOf5xx = tuple.getField("countOf5xx", Long.class);
		long timestamp = tuple.getTimestamp();

		CatalogMetricsCollector<CustomizedMetric<HttpStatusCounter>> collector = catalogContext.httpStatusCountingCollector();
		collector.clear();
		collector.update(catalog, MetricNames.HTTP_STATUS, timestamp, new HttpStatusCountingMetric(
				new HttpStatusCounter(countOf1xx, countOf2xx, countOf3xx, countOf4xx, countOf5xx), timestamp, false));

	}

	@Override
	public String getTopic() {
		return TOPIC_NAME;
	}
}
