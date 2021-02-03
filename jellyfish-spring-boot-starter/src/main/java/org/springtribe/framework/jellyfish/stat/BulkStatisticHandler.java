package org.springtribe.framework.jellyfish.stat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springtribe.framework.gearless.Handler;
import org.springtribe.framework.gearless.common.Tuple;

import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.collection.MetricUnits;
import com.github.paganini2008.devtools.collection.SequentialMetricsCollector;

/**
 * 
 * BulkStatisticHandler
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public class BulkStatisticHandler implements Handler {

	@Autowired
	private TransientStatisticSynchronizer statisticSynchronizer;

	@Override
	public void onData(Tuple tuple) {
		String clusterName = tuple.getField("clusterName", String.class);
		String applicationName = tuple.getField("applicationName", String.class);
		String host = tuple.getField("host", String.class);
		String category = tuple.getField("category", String.class);
		String path = tuple.getField("path", String.class);

		long totalExecutionCount = tuple.getField("totalExecutionCount", Long.class);
		long timeoutExecutionCount = tuple.getField("timeoutExecutionCount", Long.class);
		long failedExecutionCount = tuple.getField("failedExecutionCount", Long.class);
		PathSummary pathStatistic = statisticSynchronizer.getPathSummary(Catalog.of(tuple));
		pathStatistic.setTotalExecutionCount(totalExecutionCount);
		pathStatistic.setTimeoutExecutionCount(timeoutExecutionCount);
		pathStatistic.setFailedExecutionCount(failedExecutionCount);

		doCollect(new Catalog(clusterName, applicationName, host, category, path), tuple);
		if (StringUtils.isNotBlank(category)) {
			doCollect(new Catalog(clusterName, applicationName, host, category, null), tuple);
		}
		doCollect(new Catalog(clusterName, applicationName, host, null, null), tuple);
		doCollect(new Catalog(clusterName, applicationName, null, null, null), tuple);
		doCollect(new Catalog(clusterName, null, null, null, null), tuple);

	}

	private void doCollect(Catalog catalog, Tuple tuple) {
		int qps = tuple.getField("qps", Integer.class);
		long timestamp = tuple.getTimestamp();
		SequentialMetricsCollector sequentialMetricsCollector = statisticSynchronizer.getMetricsCollector(catalog);
		sequentialMetricsCollector.set("qps", timestamp, MetricUnits.valueOf(qps));
	}

	@Override
	public String getTopic() {
		return "org.springtribe.framework.jellyfish.monitor.BulkStatisticalWriter";
	}

}
