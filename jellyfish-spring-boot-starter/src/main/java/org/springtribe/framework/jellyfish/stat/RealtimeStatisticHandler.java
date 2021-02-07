package org.springtribe.framework.jellyfish.stat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springtribe.framework.gearless.Handler;
import org.springtribe.framework.gearless.common.Tuple;
import org.springtribe.framework.gearless.utils.CustomizedMetric;
import org.springtribe.framework.gearless.utils.StatisticalMetric;
import org.springtribe.framework.gearless.utils.StatisticalMetrics;

/**
 * 
 * RealtimeStatisticHandler
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public class RealtimeStatisticHandler implements Handler {

	@Qualifier("primaryCatalogContext")
	@Autowired
	private CatalogContext catalogContext;

	@Override
	public void onData(Tuple tuple) {

		String clusterName = tuple.getField("clusterName", String.class);
		String applicationName = tuple.getField("applicationName", String.class);
		String host = tuple.getField("host", String.class);
		String category = tuple.getField("category", String.class);
		String path = tuple.getField("path", String.class);

		doCollect(new Catalog(clusterName, applicationName, host, category, path), tuple);
		doCollect(new Catalog(clusterName, applicationName, host, category, null), tuple);
		doCollect(new Catalog(clusterName, applicationName, host, null, null), tuple);
		doCollect(new Catalog(clusterName, applicationName, null, null, null), tuple);
		doCollect(new Catalog(clusterName, null, null, null, null), tuple);

	}

	private void doCollect(Catalog catalog, Tuple tuple) {
		final long timestamp = tuple.getField("requestTime", Long.class);
		boolean failed = tuple.getField("failed", Boolean.class);
		boolean timeout = tuple.getField("timeout", Boolean.class);
		int httpStatusCode = tuple.getField("httpStatusCode", Integer.class);

		CountingMetric countingMetric = new CountingMetric(failed, timeout, timestamp);
		CatalogMetricsCollector<CustomizedMetric<Counter>> countingCollector = catalogContext.countingCollector();
		countingCollector.update(catalog, "count", timestamp, countingMetric);

		HttpStatusCountingMetric httpStatusCountingMetric = new HttpStatusCountingMetric(httpStatusCode, timestamp);
		CatalogMetricsCollector<CustomizedMetric<HttpStatusCounter>> httpStatusCountingCollector = catalogContext
				.httpStatusCountingCollector();
		httpStatusCountingCollector.update(catalog, "httpStatus", timestamp, httpStatusCountingMetric);

		CatalogSummary summary = catalogContext.getSummary(catalog);
		summary.merge(countingMetric.get());
		summary.merge(httpStatusCountingMetric.get());

		long elapsed = tuple.getField("elapsed", Long.class);
		long concurrency = tuple.getField("concurrency", Long.class);
		CatalogMetricsCollector<StatisticalMetric> statisticCollector = catalogContext.statisticCollector();
		statisticCollector.update(catalog, "rt", timestamp, StatisticalMetrics.valueOf(elapsed, timestamp));
		statisticCollector.update(catalog, "cons", timestamp, StatisticalMetrics.valueOf(concurrency, timestamp));

	}

	@Override
	public String getTopic() {
		return "org.springtribe.framework.jellyfish.monitor.RealtimeStatisticalWriter";
	}

}
