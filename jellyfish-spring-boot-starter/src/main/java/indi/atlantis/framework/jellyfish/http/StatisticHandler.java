package indi.atlantis.framework.jellyfish.http;

import static indi.atlantis.framework.jellyfish.http.MetricNames.CC;
import static indi.atlantis.framework.jellyfish.http.MetricNames.COUNT;
import static indi.atlantis.framework.jellyfish.http.MetricNames.HTTP_STATUS;
import static indi.atlantis.framework.jellyfish.http.MetricNames.RT;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import indi.atlantis.framework.vortex.Handler;
import indi.atlantis.framework.vortex.common.Tuple;
import indi.atlantis.framework.vortex.sequence.MetricSequencer;
import indi.atlantis.framework.vortex.sequence.NumberMetric;
import indi.atlantis.framework.vortex.sequence.NumberMetrics;
import indi.atlantis.framework.vortex.sequence.UserMetric;

/**
 * 
 * StatisticHandler
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public class StatisticHandler implements Handler {

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
		final long timestamp = tuple.getField("requestTime", Long.class);
		boolean failed = tuple.getField("failed", Boolean.class);
		boolean timeout = tuple.getField("timeout", Boolean.class);
		int httpStatusCode = tuple.getField("httpStatusCode", Integer.class);

		CountingMetric countingMetric = new CountingMetric(failed, timeout, timestamp);
		MetricSequencer<Catalog, UserMetric<Counter>> countingMetricSequencer = environment.countingMetricSequencer();
		countingMetricSequencer.update(catalog, COUNT, timestamp, countingMetric);

		HttpStatusCountingMetric httpStatusCountingMetric = new HttpStatusCountingMetric(httpStatusCode, timestamp);
		MetricSequencer<Catalog, UserMetric<HttpStatusCounter>> httpStatusCountingMetricSequencer = environment
				.httpStatusCountingMetricSequencer();
		httpStatusCountingMetricSequencer.update(catalog, HTTP_STATUS, timestamp, httpStatusCountingMetric);

		long elapsed = tuple.getField("elapsed", Long.class);
		long concurrency = tuple.getField("concurrency", Long.class);
		MetricSequencer<Catalog, NumberMetric<Long>> longMetricSequencer = environment.longMetricSequencer();
		longMetricSequencer.update(catalog, RT, timestamp, NumberMetrics.valueOf(elapsed, timestamp));
		longMetricSequencer.update(catalog, CC, timestamp, NumberMetrics.valueOf(concurrency, timestamp));

		Summary summary = environment.getSummary(catalog);
		summary.countingMetricCollector().set(COUNT, countingMetric, true);
		summary.httpStatusCountingMetricCollector().set(HTTP_STATUS, httpStatusCountingMetric, true);
		summary.longMetricCollector().set(RT, NumberMetrics.valueOf(elapsed, timestamp), true);
		summary.longMetricCollector().set(CC, NumberMetrics.valueOf(concurrency, timestamp), true);
	}

	@Override
	public String getTopic() {
		return "indi.atlantis.framework.jellyfish.http.StatisticWatcher";
	}

}
