package indi.atlantis.framework.jellyfish.http;

import java.util.HashMap;
import java.util.Map;

import indi.atlantis.framework.vortex.metric.GenericUserMetricSequencer;
import indi.atlantis.framework.vortex.metric.SpanUnit;

/**
 * 
 * ApiCounterMetricSequencer
 * 
 * @author Fred Feng
 *
 * @version 1.0
 */
public class ApiCounterMetricSequencer extends GenericUserMetricSequencer<Api, ApiCounter> {

	public ApiCounterMetricSequencer() {
		this(1, SpanUnit.MINUTE, 60);
	}

	public ApiCounterMetricSequencer(int span, SpanUnit spanUnit, int bufferSize) {
		super(span, spanUnit, bufferSize, null);
	}

	@Override
	protected Map<String, Object> renderNull(long timeInMs) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("successCount", 0L);
		map.put("failedCount", 0L);
		map.put("timeoutCount", 0L);
		map.put("count", 0);
		map.put("timestamp", timeInMs);
		return map;
	}

}
