package indi.atlantis.framework.jellyfish.metrics;

import java.util.HashMap;
import java.util.Map;

import indi.atlantis.framework.vortex.sequence.MetricCollector;
import indi.atlantis.framework.vortex.sequence.NumberMetric;
import indi.atlantis.framework.vortex.sequence.SimpleMetricCollector;
import indi.atlantis.framework.vortex.sequence.UserMetric;
import lombok.ToString;

/**
 * 
 * Summary
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
@ToString
public final class Summary {

	private final MetricCollector<NumberMetric<Long>> longMetricCollector = new SimpleMetricCollector<NumberMetric<Long>>();
	private final MetricCollector<UserMetric<Counter>> countingMetricCollector = new SimpleMetricCollector<UserMetric<Counter>>();
	private final MetricCollector<UserMetric<HttpStatusCounter>> httpStatusCountingMetricCollector = new SimpleMetricCollector<UserMetric<HttpStatusCounter>>();

	public Map<String, Object> toEntries() {
		Map<String, Object> data = new HashMap<String, Object>();
		for (Map.Entry<String, UserMetric<Counter>> entry : countingMetricCollector.all().entrySet()) {
			data.put(entry.getKey(), entry.getValue().toEntries());
		}
		for (Map.Entry<String, UserMetric<HttpStatusCounter>> entry : httpStatusCountingMetricCollector.all().entrySet()) {
			data.put(entry.getKey(), entry.getValue().toEntries());
		}
		for (Map.Entry<String, NumberMetric<Long>> entry : longMetricCollector.all().entrySet()) {
			data.put(entry.getKey(), entry.getValue().toEntries());
		}
		return data;
	}

	public MetricCollector<NumberMetric<Long>> longMetricCollector() {
		return longMetricCollector;
	}

	public MetricCollector<UserMetric<Counter>> countingMetricCollector() {
		return countingMetricCollector;
	}

	public MetricCollector<UserMetric<HttpStatusCounter>> httpStatusCountingMetricCollector() {
		return httpStatusCountingMetricCollector;
	}

}
