/**
* Copyright 2017-2021 Fred Feng (paganini.fy@gmail.com)

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

import io.atlantisframework.vortex.metric.api.AbstractUserMetric;
import io.atlantisframework.vortex.metric.api.UserMetric;

/**
 * 
 * ApiCounterMetric
 *
 * @author Fred Feng
 * @since 2.0.1
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
