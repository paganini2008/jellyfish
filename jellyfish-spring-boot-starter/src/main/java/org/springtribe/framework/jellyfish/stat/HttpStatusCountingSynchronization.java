package org.springtribe.framework.jellyfish.stat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springtribe.framework.gearless.Handler;
import org.springtribe.framework.gearless.common.Tuple;
import org.springtribe.framework.gearless.utils.CustomizedMetric;

/**
 * 
 * HttpStatusCountingSynchronization
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public class HttpStatusCountingSynchronization implements Handler {

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
		long countOf1xx = tuple.getField("countOf1xx", Long.class);
		long countOf2xx = tuple.getField("countOf2xx", Long.class);
		long countOf3xx = tuple.getField("countOf3xx", Long.class);
		long countOf4xx = tuple.getField("countOf4xx", Long.class);
		long countOf5xx = tuple.getField("countOf5xx", Long.class);
		long timestamp = tuple.getTimestamp();

		CatalogMetricsCollector<CustomizedMetric<HttpStatusCounter>> statisticCollector = catalogContext.getHttpStatusCountingCollector();
		statisticCollector.update(new Catalog(clusterName, applicationName, host, category, path), "httpStatus", timestamp,
				new HttpStatusCountingMetric(new HttpStatusCounter(count, countOf1xx, countOf2xx, countOf3xx, countOf4xx, countOf5xx)));

	}

}
