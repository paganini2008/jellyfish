package org.springtribe.framework.jellyfish.stat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springtribe.framework.gearless.Handler;
import org.springtribe.framework.gearless.common.Tuple;
import org.springtribe.framework.gearless.utils.StatisticalMetric;
import org.springtribe.framework.gearless.utils.StatisticalMetrics;

import com.github.paganini2008.devtools.StringUtils;

/**
 * 
 * BulkStatisticHandler
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public class BulkStatisticHandler implements Handler {

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
		long totalExecutionCount = tuple.getField("totalExecutionCount", Long.class);
		long timeoutExecutionCount = tuple.getField("timeoutExecutionCount", Long.class);
		long failedExecutionCount = tuple.getField("failedExecutionCount", Long.class);
		long countOf1xx = tuple.getField("countOf1xx", Long.class);
		long countOf2xx = tuple.getField("countOf2xx", Long.class);
		long countOf3xx = tuple.getField("countOf3xx", Long.class);
		long countOf4xx = tuple.getField("countOf4xx", Long.class);
		long countOf5xx = tuple.getField("countOf5xx", Long.class);

		CatalogSummary catalogSummary = catalogContext.getSummary(catalog);
		catalogSummary.totalExecution.addAndGet(totalExecutionCount);
		catalogSummary.failedExecution.addAndGet(failedExecutionCount);
		catalogSummary.timeoutExecution.addAndGet(timeoutExecutionCount);
		catalogSummary.countOf1xx.addAndGet(countOf1xx);
		catalogSummary.countOf2xx.addAndGet(countOf2xx);
		catalogSummary.countOf3xx.addAndGet(countOf3xx);
		catalogSummary.countOf4xx.addAndGet(countOf4xx);
		catalogSummary.countOf5xx.addAndGet(countOf5xx);

		int qps = tuple.getField("qps", Integer.class);
		CatalogMetricsCollector<StatisticalMetric> collector = catalogContext.getStatisticCollector();
		collector.update(catalog, "qps", tuple.getTimestamp(), StatisticalMetrics.valueOf(qps));
	}

	@Override
	public String getTopic() {
		return "org.springtribe.framework.jellyfish.monitor.BulkStatisticalWriter";
	}

}
