package indi.atlantis.framework.jellyfish.http;

import static indi.atlantis.framework.jellyfish.http.MetricNames.CC;
import static indi.atlantis.framework.jellyfish.http.MetricNames.COUNT;
import static indi.atlantis.framework.jellyfish.http.MetricNames.HTTP_STATUS;
import static indi.atlantis.framework.jellyfish.http.MetricNames.QPS;
import static indi.atlantis.framework.jellyfish.http.MetricNames.RT;

import indi.atlantis.framework.vortex.Handler;
import indi.atlantis.framework.vortex.common.Tuple;
import indi.atlantis.framework.vortex.metric.MetricCollector;
import indi.atlantis.framework.vortex.metric.NumberMetric;
import indi.atlantis.framework.vortex.metric.NumberMetrics;
import indi.atlantis.framework.vortex.metric.UserMetric;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * SummarySynchronizationHandler
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
@Slf4j
public class SummarySynchronizationHandler implements Handler {

	private final String topic;
	private final Environment environment;
	private final boolean merged;

	public SummarySynchronizationHandler(String topic, Environment environment, boolean merged) {
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
		Summary summary = environment.getSummary(catalog);
		long timestamp = tuple.getTimestamp();
		long count = tuple.getField("count", Long.class);
		long failedCount = tuple.getField("failedCount", Long.class);
		long timeoutCount = tuple.getField("timeoutCount", Long.class);
		MetricCollector<UserMetric<Counter>> collector = summary.countingMetricCollector();
		collector.set(metric, new CountingMetric(new Counter(count, failedCount, timeoutCount), timestamp, false), merged);
	}

	private void synchronizeHttpStatusCountingMetric(String metric, Tuple tuple) {
		Catalog catalog = Catalog.of(tuple);
		Summary summary = environment.getSummary(catalog);
		long countOf1xx = tuple.getField("countOf1xx", Long.class);
		long countOf2xx = tuple.getField("countOf2xx", Long.class);
		long countOf3xx = tuple.getField("countOf3xx", Long.class);
		long countOf4xx = tuple.getField("countOf4xx", Long.class);
		long countOf5xx = tuple.getField("countOf5xx", Long.class);
		long timestamp = tuple.getTimestamp();
		MetricCollector<UserMetric<HttpStatusCounter>> collector = summary.httpStatusCountingMetricCollector();
		collector.set(metric, new HttpStatusCountingMetric(
				new HttpStatusCounter(countOf1xx, countOf2xx, countOf3xx, countOf4xx, countOf5xx), timestamp, false), merged);
	}

	private void synchronizeLongMetric(String metric, Tuple tuple) {
		Catalog catalog = Catalog.of(tuple);
		Summary summary = environment.getSummary(catalog);
		long timestamp = tuple.getTimestamp();
		long highestValue = tuple.getField("highestValue", Long.class);
		long lowestValue = tuple.getField("lowestValue", Long.class);
		long totalValue = tuple.getField("totalValue", Long.class);
		long count = tuple.getField("count", Long.class);
		MetricCollector<NumberMetric<Long>> collector = summary.longMetricCollector();
		collector.set(metric, new NumberMetrics.LongMetric(highestValue, lowestValue, totalValue, count, timestamp, false), merged);
	}

	@Override
	public String getTopic() {
		return topic;
	}

}