package indi.atlantis.framework.jellyfish.metrics;

import static indi.atlantis.framework.jellyfish.metrics.MetricNames.CC;
import static indi.atlantis.framework.jellyfish.metrics.MetricNames.COUNT;
import static indi.atlantis.framework.jellyfish.metrics.MetricNames.HTTP_STATUS;
import static indi.atlantis.framework.jellyfish.metrics.MetricNames.RT;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import indi.atlantis.framework.vortex.Handler;
import indi.atlantis.framework.vortex.common.Tuple;
import indi.atlantis.framework.vortex.utils.CustomizedMetric;
import indi.atlantis.framework.vortex.utils.StatisticalMetric;
import indi.atlantis.framework.vortex.utils.StatisticalMetrics;

/**
 * 
 * RealtimeStatisticHandler
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public class RealtimeStatisticHandler implements Handler {

	@Qualifier("primaryCatalogMetricContext")
	@Autowired
	private CatalogMetricContext catalogMetricContext;

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
		CatalogMetricCollector<CustomizedMetric<Counter>> countingCollector = catalogMetricContext.countingCollector();
		countingCollector.update(catalog, COUNT, timestamp, countingMetric);

		HttpStatusCountingMetric httpStatusCountingMetric = new HttpStatusCountingMetric(httpStatusCode, timestamp);
		CatalogMetricCollector<CustomizedMetric<HttpStatusCounter>> httpStatusCountingCollector = catalogMetricContext
				.httpStatusCountingCollector();
		httpStatusCountingCollector.update(catalog, HTTP_STATUS, timestamp, httpStatusCountingMetric);

		Summary summary = catalogMetricContext.getSummary(catalog);
		summary.merge(countingMetric.get());
		summary.merge(httpStatusCountingMetric.get());

		long elapsed = tuple.getField("elapsed", Long.class);
		long concurrency = tuple.getField("concurrency", Long.class);
		CatalogMetricCollector<StatisticalMetric> statisticCollector = catalogMetricContext.statisticCollector();
		statisticCollector.update(catalog, RT, timestamp, StatisticalMetrics.valueOf(elapsed, timestamp));
		statisticCollector.update(catalog, CC, timestamp, StatisticalMetrics.valueOf(concurrency, timestamp));

	}

	@Override
	public String getTopic() {
		return "indi.atlantis.framework.jellyfish.agent.RealtimeMetricsWriter";
	}

}
