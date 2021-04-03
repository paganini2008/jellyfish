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
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * ApiSummarySynchronizationHandler
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
@Slf4j
public class ApiSummarySynchronizationHandler implements Handler {

	private final String topic;
	private final Environment environment;
	private final boolean merged;

	public ApiSummarySynchronizationHandler(String topic, Environment environment, boolean merged) {
		this.topic = topic;
		this.environment = environment;
		this.merged = merged;
	}

	@Override
	public void onData(Tuple tuple) {
		final String metric = tuple.getField("metric", String.class);
		switch (metric) {
		case COUNT:
			synchronizeCounterMetric(metric, tuple);
			break;
		case HTTP_STATUS:
			synchronizeHttpStatusCounterMetric(metric, tuple);
			break;
		case RT:
		case CC:
		case QPS:
			synchronizeBigIntMetric(metric, tuple);
			break;
		default:
			log.warn("Unknown metric for synchronization: {}", metric);
			break;
		}
	}

	private void synchronizeCounterMetric(String metric, Tuple tuple) {
		Api api = Api.of(tuple);
		long timestamp = tuple.getTimestamp();
		long count = tuple.getField("count", Long.class);
		long failedCount = tuple.getField("failedCount", Long.class);
		long timeoutCount = tuple.getField("timeoutCount", Long.class);
		environment.update(api, metric, new ApiCounterMetric(new ApiCounter(count, failedCount, timeoutCount), timestamp), merged);
	}

	private void synchronizeHttpStatusCounterMetric(String metric, Tuple tuple) {
		Api api = Api.of(tuple);
		long countOf1xx = tuple.getField("countOf1xx", Long.class);
		long countOf2xx = tuple.getField("countOf2xx", Long.class);
		long countOf3xx = tuple.getField("countOf3xx", Long.class);
		long countOf4xx = tuple.getField("countOf4xx", Long.class);
		long countOf5xx = tuple.getField("countOf5xx", Long.class);
		long timestamp = tuple.getTimestamp();
		environment.update(api, metric,
				new HttpStatusCounterMetric(new HttpStatusCounter(countOf1xx, countOf2xx, countOf3xx, countOf4xx, countOf5xx), timestamp),
				merged);
	}

	private void synchronizeBigIntMetric(String metric, Tuple tuple) {
		Api api = Api.of(tuple);
		long timestamp = tuple.getTimestamp();
		long highestValue = tuple.getField("highestValue", Long.class);
		long lowestValue = tuple.getField("lowestValue", Long.class);
		long totalValue = tuple.getField("totalValue", Long.class);
		long count = tuple.getField("count", Long.class);
		environment.update(api, metric, new BigIntMetric(new BigInt(highestValue, lowestValue, totalValue, count), timestamp), merged);
	}

	@Override
	public String getTopic() {
		return topic;
	}

}
