package org.springtribe.framework.jellyfish.stat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springtribe.framework.gearless.Handler;
import org.springtribe.framework.gearless.common.Tuple;
import org.springtribe.framework.gearless.utils.CustomizedMetric;
import org.springtribe.framework.gearless.utils.StatisticalMetric;
import org.springtribe.framework.gearless.utils.StatisticalMetrics;

import com.github.paganini2008.devtools.StringUtils;

/**
 * 
 * RealtimeStatisticHandler
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public class RealtimeStatisticHandler implements Handler {

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

		if (StringUtils.isNotBlank(category)) {
			doCollect(new Catalog(clusterName, applicationName, host, category, null), tuple);
		}
		doCollect(new Catalog(clusterName, applicationName, host, null, null), tuple);
		doCollect(new Catalog(clusterName, applicationName, null, null, null), tuple);
		doCollect(new Catalog(clusterName, null, null, null, null), tuple);

	}

	private void doCollect(Catalog catalog, Tuple tuple) {
		long elapsed = tuple.getField("elapsed", Long.class);
		long concurrency = tuple.getField("concurrency", Long.class);
		long timestamp = tuple.getField("requestTime", Long.class);
		boolean failed = tuple.getField("failed", Boolean.class);
		boolean timeout = tuple.getField("timeout", Boolean.class);
		int httpStatusCode = tuple.getField("httpStatusCode", Integer.class);

		CatalogMetricsCollector<StatisticalMetric> statisticCollector = catalogContext.getStatisticCollector();
		statisticCollector.update(catalog, "rt", timestamp, StatisticalMetrics.valueOf(elapsed));
		statisticCollector.update(catalog, "cons", timestamp, StatisticalMetrics.valueOf(concurrency));

		CatalogMetricsCollector<CustomizedMetric<HttpRequestCounter>> countingCollector = catalogContext.getCountingCollector();
		countingCollector.update(catalog, "count", timestamp, new HttpRequestCountingMetric(failed, timeout));

		CatalogMetricsCollector<CustomizedMetric<HttpStatusCounter>> httpStatusCountingCollector = catalogContext
				.getHttpStatusCountingCollector();
		httpStatusCountingCollector.update(catalog, "httpStatus", timestamp, new HttpStatusCountingMetric(httpStatusCode));
	}

	@Override
	public String getTopic() {
		return "org.springtribe.framework.jellyfish.monitor.RealtimeStatisticalWriter";
	}

}
