package indi.atlantis.framework.jellyfish.http;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.github.paganini2008.devtools.collection.MapUtils;

import indi.atlantis.framework.vortex.metric.BigInt;
import indi.atlantis.framework.vortex.metric.GenericUserMetricSequencer;

/**
 * 
 * Environment
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public final class Environment {

	private final Map<Catalog, ApiSummary> summary = new ConcurrentHashMap<Catalog, ApiSummary>();
	private final GenericUserMetricSequencer<Catalog, BigInt> bigIntMetricSequencer = new BigIntMetricSequencer();
	private final GenericUserMetricSequencer<Catalog, ApiCounter> counterMetricSequencer = new ApiCounterMetricSequencer();
	private final GenericUserMetricSequencer<Catalog, HttpStatusCounter> httpStatusCounterMetricSequencer = new HttpStatusCounterMetricSequencer();

	public List<Catalog> getCatalogs() {
		return new ArrayList<Catalog>(summary.keySet());
	}

	public ApiSummary getSummary(Catalog catalog) {
		return MapUtils.get(summary, catalog, () -> new ApiSummary());
	}

	public GenericUserMetricSequencer<Catalog, BigInt> getBigIntMetricSequencer() {
		return bigIntMetricSequencer;
	}

	public GenericUserMetricSequencer<Catalog, ApiCounter> getApiCounterMetricSequencer() {
		return counterMetricSequencer;
	}

	public GenericUserMetricSequencer<Catalog, HttpStatusCounter> getHttpStatusCounterMetricSequencer() {
		return httpStatusCounterMetricSequencer;
	}

}
