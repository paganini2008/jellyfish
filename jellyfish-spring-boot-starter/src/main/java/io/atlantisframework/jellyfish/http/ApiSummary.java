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
import io.atlantisframework.vortex.metric.api.MetricCollector;
import io.atlantisframework.vortex.metric.api.SimpleMetricCollector;
import io.atlantisframework.vortex.metric.api.UserMetric;

/**
 * 
 * ApiSummary
 *
 * @author Fred Feng
 * @since 2.0.1
 */
public final class ApiSummary {

	private final MetricCollector<String, UserMetric<BigInt>> apiStatisticMetricCollector = new SimpleMetricCollector<String, UserMetric<BigInt>>();
	private final MetricCollector<String, UserMetric<ApiCounter>> apiCounterMetricCollector = new SimpleMetricCollector<String, UserMetric<ApiCounter>>();
	private final MetricCollector<String, UserMetric<HttpStatusCounter>> httpStatusCounterMetricCollector = new SimpleMetricCollector<String, UserMetric<HttpStatusCounter>>();

	public Map<String, Object> toEntries() {
		Map<String, Object> data = new HashMap<String, Object>();
		for (Map.Entry<String, UserMetric<ApiCounter>> entry : apiCounterMetricCollector.all().entrySet()) {
			data.put(entry.getKey(), entry.getValue().toEntries());
		}
		for (Map.Entry<String, UserMetric<HttpStatusCounter>> entry : httpStatusCounterMetricCollector.all().entrySet()) {
			data.put(entry.getKey(), entry.getValue().toEntries());
		}
		for (Map.Entry<String, UserMetric<BigInt>> entry : apiStatisticMetricCollector.all().entrySet()) {
			data.put(entry.getKey(), entry.getValue().toEntries());
		}
		return data;
	}

	public MetricCollector<String, UserMetric<BigInt>> getApiStatisticMetricCollector() {
		return apiStatisticMetricCollector;
	}

	public MetricCollector<String, UserMetric<ApiCounter>> getApiCounterMetricCollector() {
		return apiCounterMetricCollector;
	}

	public MetricCollector<String, UserMetric<HttpStatusCounter>> getHttpStatusCounterMetricCollector() {
		return httpStatusCounterMetricCollector;
	}

}
