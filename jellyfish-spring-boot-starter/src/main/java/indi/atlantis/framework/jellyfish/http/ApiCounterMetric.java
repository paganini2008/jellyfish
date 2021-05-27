package indi.atlantis.framework.jellyfish.http;

import java.util.HashMap;
import java.util.Map;

import indi.atlantis.framework.vortex.metric.AbstractUserMetric;
import indi.atlantis.framework.vortex.metric.UserMetric;

/**
 * 
 * ApiCounterMetric
 *
 * @author Fred Feng
 * @version 1.0
 */
public class ApiCounterMetric extends AbstractUserMetric<ApiCounter> {

	public ApiCounterMetric(boolean failed, boolean timeout, long timestamp) {
		this(new ApiCounter(1, failed ? 1 : 0, timeout ? 1 : 0), timestamp);
	}

	public ApiCounterMetric(ApiCounter counter, long timestamp) {
		super(counter, timestamp, false);
	}

	@Override
	public UserMetric<ApiCounter> reset(UserMetric<ApiCounter> newMetric) {
		ApiCounter current = get();
		ApiCounter update = newMetric.get();
		ApiCounter counter = new ApiCounter();
		counter.setCount(current.getCount() - update.getCount());
		counter.setFailedCount(current.getFailedCount() - update.getFailedCount());
		counter.setTimeoutCount(current.getTimeoutCount() - update.getTimeoutCount());
		return new ApiCounterMetric(counter, newMetric.getTimestamp());
	}

	@Override
	public UserMetric<ApiCounter> merge(UserMetric<ApiCounter> newMetric) {
		ApiCounter current = get();
		ApiCounter update = newMetric.get();
		ApiCounter counter = new ApiCounter();
		counter.setCount(current.getCount() + update.getCount());
		counter.setFailedCount(current.getFailedCount() + update.getFailedCount());
		counter.setTimeoutCount(current.getTimeoutCount() + update.getTimeoutCount());
		return new ApiCounterMetric(counter, newMetric.getTimestamp());
	}

	@Override
	public Map<String, Object> toEntries() {
		ApiCounter counter = get();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("count", counter.getCount());
		map.put("successCount", counter.getSuccessCount());
		map.put("failedCount", counter.getFailedCount());
		map.put("timeoutCount", counter.getTimeoutCount());
		map.put("timestamp", getTimestamp());
		return map;
	}

}
