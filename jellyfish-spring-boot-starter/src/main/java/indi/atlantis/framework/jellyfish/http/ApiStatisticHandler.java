package indi.atlantis.framework.jellyfish.http;

import static indi.atlantis.framework.jellyfish.http.MetricNames.CC;
import static indi.atlantis.framework.jellyfish.http.MetricNames.COUNT;
import static indi.atlantis.framework.jellyfish.http.MetricNames.HTTP_STATUS;
import static indi.atlantis.framework.jellyfish.http.MetricNames.RT;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;

import indi.atlantis.framework.vortex.Handler;
import indi.atlantis.framework.vortex.common.Tuple;
import indi.atlantis.framework.vortex.metric.BigInt;
import indi.atlantis.framework.vortex.metric.BigIntMetric;
import indi.atlantis.framework.vortex.metric.MetricSequencer;
import indi.atlantis.framework.vortex.metric.UserMetric;

/**
 * 
 * ApiStatisticHandler
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public class ApiStatisticHandler implements Handler {

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

		ApiCounterMetric counterMetric = new ApiCounterMetric(failed, timeout, timestamp);
		MetricSequencer<Catalog, UserMetric<ApiCounter>> counterMetricSequencer = environment.getCounterMetricSequencer();
		counterMetricSequencer.update(catalog, COUNT, timestamp, counterMetric);

		HttpStatusCounterMetric httpStatusCounterMetric = new HttpStatusCounterMetric(HttpStatus.valueOf(httpStatusCode), timestamp);
		MetricSequencer<Catalog, UserMetric<HttpStatusCounter>> httpStatusCounterMetricSequencer = environment
				.getHttpStatusCounterMetricSequencer();
		httpStatusCounterMetricSequencer.update(catalog, HTTP_STATUS, timestamp, httpStatusCounterMetric);

		long elapsed = tuple.getField("elapsed", Long.class);
		long concurrency = tuple.getField("concurrency", Long.class);
		MetricSequencer<Catalog, UserMetric<BigInt>> bigIntMetricSequencer = environment.getBigIntMetricSequencer();
		bigIntMetricSequencer.update(catalog, RT, timestamp, new BigIntMetric(elapsed, timestamp));
		bigIntMetricSequencer.update(catalog, CC, timestamp, new BigIntMetric(concurrency, timestamp));

		ApiSummary summary = environment.getSummary(catalog);
		summary.getCounterMetricCollector().set(COUNT, counterMetric, true);
		summary.getHttpStatusCounterMetricCollector().set(HTTP_STATUS, httpStatusCounterMetric, true);
		summary.getBigIntMetricCollector().set(RT, new BigIntMetric(elapsed, timestamp), true);
		summary.getBigIntMetricCollector().set(CC, new BigIntMetric(concurrency, timestamp), true);
	}

	@Override
	public String getTopic() {
		return "indi.atlantis.framework.jellyfish.http.StatisticWatcher";
	}

}
