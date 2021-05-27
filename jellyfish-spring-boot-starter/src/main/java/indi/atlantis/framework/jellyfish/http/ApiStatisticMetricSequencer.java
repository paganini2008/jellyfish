package indi.atlantis.framework.jellyfish.http;

import java.util.HashMap;
import java.util.Map;

import indi.atlantis.framework.vortex.metric.BigInt;
import indi.atlantis.framework.vortex.metric.GenericUserMetricSequencer;
import indi.atlantis.framework.vortex.metric.SpanUnit;

/**
 * 
 * ApiStatisticMetricSequencer
 * 
 * @author Fred Feng
 *
 * @version 1.0
 */
public class ApiStatisticMetricSequencer extends GenericUserMetricSequencer<Api, BigInt> {

	public ApiStatisticMetricSequencer() {
		this(1, SpanUnit.MINUTE, 60);
	}

	public ApiStatisticMetricSequencer(int span, SpanUnit spanUnit, int bufferSize) {
		super(span, spanUnit, bufferSize, null);
	}

	@Override
	protected Map<String, Object> renderNull(long timeInMs) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("highestValue", 0L);
		map.put("middleValue", 0L);
		map.put("lowestValue", 0L);
		map.put("count", 0);
		map.put("timestamp", timeInMs);
		return map;
	}

}
