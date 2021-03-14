package indi.atlantis.framework.jellyfish.http;

import static indi.atlantis.framework.jellyfish.http.MetricNames.CC;
import static indi.atlantis.framework.jellyfish.http.MetricNames.COUNT;
import static indi.atlantis.framework.jellyfish.http.MetricNames.HTTP_STATUS;
import static indi.atlantis.framework.jellyfish.http.MetricNames.QPS;
import static indi.atlantis.framework.jellyfish.http.MetricNames.RT;

import indi.atlantis.framework.vortex.Handler;
import indi.atlantis.framework.vortex.common.Tuple;
import indi.atlantis.framework.vortex.metric.BigInt;
import indi.atlantis.framework.vortex.metric.BigIntMetric;
import indi.atlantis.framework.vortex.metric.MetricSequencer;
import indi.atlantis.framework.vortex.metric.UserMetric;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * ApiStatisticSynchronizationHandler
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
@Slf4j
public class ApiStatisticSynchronizationHandler implements Handler {

	private final String topic;
	private final Environment environment;
	private final boolean merged;

	public ApiStatisticSynchronizationHandler(String topic, Environment environment, boolean merged) {
		this.topic = topic;
		this.environment = environment;
		this.merged = merged;
	}

	@Override
	public void onData(Tuple tuple) {
		final String metric = tuple.getField("metric", String.class);
		switch (metric) {
		case COUNT:
			synchronizeCountingMetric(metric, tuple);
			break;
		case HTTP_STATUS:
			synchronizeHttpStatusCountingMetric(metric, tuple);
			break;
		case RT:
		case CC:
		case QPS:
			synchronizeLongMetric(metric, tuple);
			break;
		default:
			log.warn("Unknown metric for synchronization: {}", metric);
			break;
		}
	}

	private void synchronizeCountingMetric(String metric, Tuple tuple) {
		Catalog catalog = Catalog.of(tuple);
		long timestamp = tuple.getTimestamp();
		long count = tuple.getField("count", Long.class);
		long failedCount = tuple.getField("failedCount", Long.class);
		long timeoutCount = tuple.getField("timeoutCount", Long.class);
		MetricSequencer<Catalog, UserMetric<ApiCounter>> sequencer = environment.getCounterMetricSequencer();
		sequencer.update(catalog, metric, timestamp, new ApiCounterMetric(new ApiCounter(count, failedCount, timeoutCount), timestamp), merged);
	}

	private void synchronizeHttpStatusCountingMetric(String metric, Tuple tuple) {
		Catalog catalog = Catalog.of(tuple);
		long countOf1xx = tuple.getField("countOf1xx", Long.class);
		long countOf2xx = tuple.getField("countOf2xx", Long.class);
		long countOf3xx = tuple.getField("countOf3xx", Long.class);
		long countOf4xx = tuple.getField("countOf4xx", Long.class);
		long countOf5xx = tuple.getField("countOf5xx", Long.class);
		long timestamp = tuple.getTimestamp();
		MetricSequencer<Catalog, UserMetric<HttpStatusCounter>> sequencer = environment.getHttpStatusCounterMetricSequencer();
		sequencer.update(catalog, metric, timestamp,
				new HttpStatusCounterMetric(new HttpStatusCounter(countOf1xx, countOf2xx, countOf3xx, countOf4xx, countOf5xx), timestamp),
				merged);
	}

	private void synchronizeLongMetric(String metric, Tuple tuple) {
		Catalog catalog = Catalog.of(tuple);
		long timestamp = tuple.getTimestamp();
		long highestValue = tuple.getField("highestValue", Long.class);
		long lowestValue = tuple.getField("lowestValue", Long.class);
		long totalValue = tuple.getField("totalValue", Long.class);
		long count = tuple.getField("count", Long.class);
		MetricSequencer<Catalog, UserMetric<BigInt>> sequencer = environment.getBigIntMetricSequencer();
		sequencer.update(catalog, metric, timestamp, new BigIntMetric(new BigInt(highestValue, lowestValue, totalValue, count), timestamp),
				merged);
	}

	@Override
	public String getTopic() {
		return topic;
	}

}
