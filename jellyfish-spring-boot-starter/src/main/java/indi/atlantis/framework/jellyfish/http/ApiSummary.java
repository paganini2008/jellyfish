package indi.atlantis.framework.jellyfish.http;

import java.util.HashMap;
import java.util.Map;

import indi.atlantis.framework.vortex.metric.BigInt;
import indi.atlantis.framework.vortex.metric.MetricCollector;
import indi.atlantis.framework.vortex.metric.SimpleMetricCollector;
import indi.atlantis.framework.vortex.metric.UserMetric;

/**
 * 
 * ApiSummary
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public final class ApiSummary {

	private final MetricCollector<UserMetric<BigInt>> bigIntMetricCollector = new SimpleMetricCollector<UserMetric<BigInt>>();
	private final MetricCollector<UserMetric<ApiCounter>> counterMetricCollector = new SimpleMetricCollector<UserMetric<ApiCounter>>();
	private final MetricCollector<UserMetric<HttpStatusCounter>> httpStatusCounterMetricCollector = new SimpleMetricCollector<UserMetric<HttpStatusCounter>>();

	public Map<String, Object> toEntries() {
		Map<String, Object> data = new HashMap<String, Object>();
		for (Map.Entry<String, UserMetric<ApiCounter>> entry : counterMetricCollector.all().entrySet()) {
			data.put(entry.getKey(), entry.getValue().toEntries());
		}
		for (Map.Entry<String, UserMetric<HttpStatusCounter>> entry : httpStatusCounterMetricCollector.all().entrySet()) {
			data.put(entry.getKey(), entry.getValue().toEntries());
		}
		for (Map.Entry<String, UserMetric<BigInt>> entry : bigIntMetricCollector.all().entrySet()) {
			data.put(entry.getKey(), entry.getValue().toEntries());
		}
		return data;
	}

	public MetricCollector<UserMetric<BigInt>> getBigIntMetricCollector() {
		return bigIntMetricCollector;
	}

	public MetricCollector<UserMetric<ApiCounter>> getCounterMetricCollector() {
		return counterMetricCollector;
	}

	public MetricCollector<UserMetric<HttpStatusCounter>> getHttpStatusCounterMetricCollector() {
		return httpStatusCounterMetricCollector;
	}

}
