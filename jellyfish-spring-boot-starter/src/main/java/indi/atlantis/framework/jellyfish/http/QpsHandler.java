package indi.atlantis.framework.jellyfish.http;

import static indi.atlantis.framework.jellyfish.http.MetricNames.QPS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import indi.atlantis.framework.vortex.Handler;
import indi.atlantis.framework.vortex.common.Tuple;
import indi.atlantis.framework.vortex.metric.MetricSequencer;
import indi.atlantis.framework.vortex.metric.NumberMetric;
import indi.atlantis.framework.vortex.metric.NumberMetrics;

/**
 * 
 * QpsHandler
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public class QpsHandler implements Handler {

	@Qualifier("primaryEnvironment")
	@Autowired
	private Environment environment;

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
		int qps = tuple.getField(QPS, Integer.class);
		MetricSequencer<Catalog, NumberMetric<Long>> sequencer = environment.longMetricSequencer();
		sequencer.update(catalog, QPS, timestamp, NumberMetrics.valueOf(qps, timestamp));

		Summary summary = environment.getSummary(catalog);
		summary.longMetricCollector().set(QPS, NumberMetrics.valueOf(qps, timestamp), true);
	}

	@Override
	public String getTopic() {
		return "indi.atlantis.framework.jellyfish.http.QpsWatcher";
	}

}
