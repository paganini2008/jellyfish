package indi.atlantis.framework.jellyfish.http;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.github.paganini2008.devtools.collection.MapUtils;

import indi.atlantis.framework.vortex.metric.BigInt;
import indi.atlantis.framework.vortex.metric.BigIntMetric;
import indi.atlantis.framework.vortex.metric.GenericUserMetricSequencer;

/**
 * 
 * Environment
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public final class Environment {

	private final Map<Catalog, ApiSummary> summaries = new ConcurrentHashMap<Catalog, ApiSummary>();
	private final GenericUserMetricSequencer<Catalog, BigInt> apiStatisticMetricSequencer = new ApiStatisticMetricSequencer();
	private final GenericUserMetricSequencer<Catalog, ApiCounter> apiCounterMetricSequencer = new ApiCounterMetricSequencer();
	private final GenericUserMetricSequencer<Catalog, HttpStatusCounter> httpStatusCounterMetricSequencer = new HttpStatusCounterMetricSequencer();

	public List<Catalog> getCatalogs() {
		return new ArrayList<Catalog>(summaries.keySet());
	}

	public ApiSummary getSummary(Catalog catalog) {
		return MapUtils.get(summaries, catalog, () -> new ApiSummary());
	}

	public void update(Catalog catalog, String metric, BigIntMetric bigIntMetric, boolean merged) {
		ApiSummary summary = getSummary(catalog);
		summary.getApiStatisticMetricCollector().set(metric, bigIntMetric, merged);
	}

	public void update(Catalog catalog, String metric, ApiCounterMetric apiCounterMetric, boolean merged) {
		ApiSummary summary = getSummary(catalog);
		summary.getApiCounterMetricCollector().set(metric, apiCounterMetric, merged);
	}

	public void update(Catalog catalog, String metric, HttpStatusCounterMetric httpStatusCounterMetric, boolean merged) {
		ApiSummary summary = getSummary(catalog);
		summary.getHttpStatusCounterMetricCollector().set(metric, httpStatusCounterMetric, merged);
	}

	public GenericUserMetricSequencer<Catalog, BigInt> getApiStatisticMetricSequencer() {
		return apiStatisticMetricSequencer;
	}

	public GenericUserMetricSequencer<Catalog, ApiCounter> getApiCounterMetricSequencer() {
		return apiCounterMetricSequencer;
	}

	public GenericUserMetricSequencer<Catalog, HttpStatusCounter> getHttpStatusCounterMetricSequencer() {
		return httpStatusCounterMetricSequencer;
	}

}
