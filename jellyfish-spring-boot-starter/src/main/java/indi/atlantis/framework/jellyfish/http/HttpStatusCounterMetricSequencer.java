package indi.atlantis.framework.jellyfish.http;

import java.util.HashMap;
import java.util.Map;

import indi.atlantis.framework.vortex.metric.GenericUserMetricSequencer;
import indi.atlantis.framework.vortex.metric.SpanUnit;

/**
 * 
 * HttpStatusCounterMetricSequencer
 * 
 * @author Fred Feng
 *
 * @version 1.0
 */
public class HttpStatusCounterMetricSequencer extends GenericUserMetricSequencer<Api, HttpStatusCounter> {

	public HttpStatusCounterMetricSequencer() {
		this(1, SpanUnit.MINUTE, 60);
	}

	public HttpStatusCounterMetricSequencer(int span, SpanUnit spanUnit, int bufferSize) {
		super(span, spanUnit, bufferSize, null);
	}

	@Override
	protected Map<String, Object> renderNull(long timeInMs) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("countOf1xx", 0L);
		map.put("countOf2xx", 0L);
		map.put("countOf3xx", 0L);
		map.put("countOf4xx", 0L);
		map.put("countOf5xx", 0L);
		map.put("timestamp", timeInMs);
		return map;
	}

}
