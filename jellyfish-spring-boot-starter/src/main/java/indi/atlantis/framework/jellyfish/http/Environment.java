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
 * @author Fred Feng
 * @version 1.0
 */
public final class Environment {

	private final Map<Api, ApiSummary> summaries = new ConcurrentHashMap<Api, ApiSummary>();
	private final GenericUserMetricSequencer<Api, BigInt> apiStatisticMetricSequencer = new ApiStatisticMetricSequencer();
	private final GenericUserMetricSequencer<Api, ApiCounter> apiCounterMetricSequencer = new ApiCounterMetricSequencer();
	private final GenericUserMetricSequencer<Api, HttpStatusCounter> httpStatusCounterMetricSequencer = new HttpStatusCounterMetricSequencer();

	public List<Api> apiList() {
		return new ArrayList<Api>(summaries.keySet());
	}

	public ApiSummary summary(Api api) {
		return MapUtils.get(summaries, api, () -> new ApiSummary());
	}

	public void update(Api api, String metric, BigIntMetric bigIntMetric, boolean merged) {
		ApiSummary summary = summary(api);
		summary.getApiStatisticMetricCollector().set(metric, bigIntMetric, merged);
	}

	public void update(Api api, String metric, ApiCounterMetric apiCounterMetric, boolean merged) {
		ApiSummary summary = summary(api);
		summary.getApiCounterMetricCollector().set(metric, apiCounterMetric, merged);
	}

	public void update(Api api, String metric, HttpStatusCounterMetric httpStatusCounterMetric, boolean merged) {
		ApiSummary summary = summary(api);
		summary.getHttpStatusCounterMetricCollector().set(metric, httpStatusCounterMetric, merged);
	}

	public GenericUserMetricSequencer<Api, BigInt> getApiStatisticMetricSequencer() {
		return apiStatisticMetricSequencer;
	}

	public GenericUserMetricSequencer<Api, ApiCounter> getApiCounterMetricSequencer() {
		return apiCounterMetricSequencer;
	}

	public GenericUserMetricSequencer<Api, HttpStatusCounter> getHttpStatusCounterMetricSequencer() {
		return httpStatusCounterMetricSequencer;
	}

}
