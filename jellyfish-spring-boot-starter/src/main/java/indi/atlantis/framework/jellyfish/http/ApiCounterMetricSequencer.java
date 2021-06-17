/**
* Copyright 2021 Fred Feng (paganini.fy@gmail.com)

* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package indi.atlantis.framework.jellyfish.http;

import java.util.HashMap;
import java.util.Map;

import indi.atlantis.framework.vortex.metric.GenericUserMetricSequencer;
import indi.atlantis.framework.vortex.metric.MetricEvictionHandler;
import indi.atlantis.framework.vortex.metric.SpanUnit;
import indi.atlantis.framework.vortex.metric.UserMetric;

/**
 * 
 * ApiCounterMetricSequencer
 * 
 * @author Fred Feng
 *
 * @version 1.0
 */
public class ApiCounterMetricSequencer extends GenericUserMetricSequencer<Api, ApiCounter> {

	public ApiCounterMetricSequencer(MetricEvictionHandler<Api, UserMetric<ApiCounter>> metricEvictionHandler) {
		this(1, SpanUnit.MINUTE, 60, metricEvictionHandler);
	}

	public ApiCounterMetricSequencer(int span, SpanUnit spanUnit, int bufferSize,
			MetricEvictionHandler<Api, UserMetric<ApiCounter>> metricEvictionHandler) {
		super(span, spanUnit, bufferSize, metricEvictionHandler);
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
