/**
* Copyright 2017-2022 Fred Feng (paganini.fy@gmail.com)

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
package io.atlantisframework.jellyfish.http;

import java.util.HashMap;
import java.util.Map;

import io.atlantisframework.vortex.metric.api.BigInt;
import io.atlantisframework.vortex.metric.api.GenericUserMetricSequencer;
import io.atlantisframework.vortex.metric.api.MetricEvictionHandler;
import io.atlantisframework.vortex.metric.api.TimeWindowUnit;
import io.atlantisframework.vortex.metric.api.UserMetric;

/**
 * 
 * ApiStatisticMetricSequencer
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
 */
public class ApiStatisticMetricSequencer extends GenericUserMetricSequencer<Api, BigInt> {

	public ApiStatisticMetricSequencer(MetricEvictionHandler<Api, UserMetric<BigInt>> metricEvictionHandler) {
		this(1, TimeWindowUnit.MINUTE, 60, metricEvictionHandler);
	}

	public ApiStatisticMetricSequencer(int span, TimeWindowUnit timeWindowUnit, int bufferSize,
			MetricEvictionHandler<Api, UserMetric<BigInt>> metricEvictionHandler) {
		super(span, timeWindowUnit, bufferSize, metricEvictionHandler);
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
