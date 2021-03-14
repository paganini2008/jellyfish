package indi.atlantis.framework.jellyfish.http;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.github.paganini2008.devtools.collection.MapUtils;

import indi.atlantis.framework.vortex.metric.BigInt;
import indi.atlantis.framework.vortex.metric.MetricSequencer;
import indi.atlantis.framework.vortex.metric.SimpleMetricSequencer;
import indi.atlantis.framework.vortex.metric.UserMetric;

/**
 * 
 * Environment
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public final class Environment {

	private final Map<Catalog, ApiSummary> summary = new ConcurrentHashMap<Catalog, ApiSummary>();
	private final MetricSequencer<Catalog, UserMetric<BigInt>> bigIntMetricSequencer = new SimpleMetricSequencer<Catalog, UserMetric<BigInt>>();
	private final MetricSequencer<Catalog, UserMetric<ApiCounter>> counterMetricSequencer = new SimpleMetricSequencer<Catalog, UserMetric<ApiCounter>>();
	private final MetricSequencer<Catalog, UserMetric<HttpStatusCounter>> httpStatusCounterMetricSequencer = new SimpleMetricSequencer<Catalog, UserMetric<HttpStatusCounter>>();

	public List<Catalog> getCatalogs() {
		return new ArrayList<Catalog>(summary.keySet());
	}

	public ApiSummary getSummary(Catalog catalog) {
		return MapUtils.get(summary, catalog, () -> new ApiSummary());
	}

	public MetricSequencer<Catalog, UserMetric<BigInt>> getBigIntMetricSequencer() {
		return bigIntMetricSequencer;
	}

	public MetricSequencer<Catalog, UserMetric<ApiCounter>> getCounterMetricSequencer() {
		return counterMetricSequencer;
	}

	public MetricSequencer<Catalog, UserMetric<HttpStatusCounter>> getHttpStatusCounterMetricSequencer() {
		return httpStatusCounterMetricSequencer;
	}

}
