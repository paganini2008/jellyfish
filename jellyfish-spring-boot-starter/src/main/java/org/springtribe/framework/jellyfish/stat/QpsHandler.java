package org.springtribe.framework.jellyfish.stat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springtribe.framework.gearless.Handler;
import org.springtribe.framework.gearless.common.Tuple;
import org.springtribe.framework.gearless.utils.StatisticalMetric;
import org.springtribe.framework.gearless.utils.StatisticalMetrics;

import com.github.paganini2008.devtools.StringUtils;

/**
 * 
 * QpsHandler
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public class QpsHandler implements Handler {

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
		int qps = tuple.getField("qps", Integer.class);
		CatalogMetricsCollector<StatisticalMetric> collector = catalogContext.getStatisticCollector();
		collector.update(catalog, "qps", tuple.getTimestamp(), StatisticalMetrics.valueOf(qps, tuple.getTimestamp()));
	}

	@Override
	public String getTopic() {
		return "org.springtribe.framework.jellyfish.monitor.BulkStatisticalWriter";
	}

}
