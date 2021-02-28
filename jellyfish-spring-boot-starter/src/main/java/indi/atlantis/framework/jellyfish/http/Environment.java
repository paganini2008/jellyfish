package indi.atlantis.framework.jellyfish.http;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.github.paganini2008.devtools.collection.MapUtils;

import indi.atlantis.framework.vortex.metric.MetricSequencer;
import indi.atlantis.framework.vortex.metric.NumberMetric;
import indi.atlantis.framework.vortex.metric.UserMetric;

/**
 * 
 * Environment
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public final class Environment {

	private final Map<Catalog, Summary> summary = new ConcurrentHashMap<Catalog, Summary>();
	private final MetricSequencer<Catalog, NumberMetric<Long>> longMetricSequencer = new MetricSequencer<Catalog, NumberMetric<Long>>();
	private final MetricSequencer<Catalog, UserMetric<Counter>> countingMetricSequencer = new MetricSequencer<Catalog, UserMetric<Counter>>();
	private final MetricSequencer<Catalog, UserMetric<HttpStatusCounter>> httpStatusCountingMetricSequencer = new MetricSequencer<Catalog, UserMetric<HttpStatusCounter>>();

	public List<Catalog> getCatalogs() {
		return new ArrayList<Catalog>(summary.keySet());
	}
	
	public Summary getSummary(Catalog catalog) {
		return MapUtils.get(summary, catalog, () -> new Summary());
	}

	public MetricSequencer<Catalog, NumberMetric<Long>> longMetricSequencer() {
		return longMetricSequencer;
	}

	public MetricSequencer<Catalog, UserMetric<Counter>> countingMetricSequencer() {
		return countingMetricSequencer;
	}

	public MetricSequencer<Catalog, UserMetric<HttpStatusCounter>> httpStatusCountingMetricSequencer() {
		return httpStatusCountingMetricSequencer;
	}

}
