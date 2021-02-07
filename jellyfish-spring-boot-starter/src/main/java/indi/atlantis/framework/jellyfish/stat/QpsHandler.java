package indi.atlantis.framework.jellyfish.stat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springtribe.framework.gearless.Handler;
import org.springtribe.framework.gearless.common.Tuple;
import org.springtribe.framework.gearless.utils.StatisticalMetric;
import org.springtribe.framework.gearless.utils.StatisticalMetrics;

/**
 * 
 * QpsHandler
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public class QpsHandler implements Handler {

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
		final long timestamp = tuple.getTimestamp();
		int qps = tuple.getField("qps", Integer.class);
		CatalogMetricsCollector<StatisticalMetric> collector = catalogContext.statisticCollector();
		collector.update(catalog, "qps", timestamp, StatisticalMetrics.valueOf(qps, timestamp));
	}

	@Override
	public String getTopic() {
		return "indi.atlantis.framework.jellyfish.monitor.QpsWriter";
	}

}
