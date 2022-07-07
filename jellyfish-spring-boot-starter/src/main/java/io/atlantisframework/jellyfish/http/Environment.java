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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.github.paganini2008.devtools.collection.MapUtils;

import io.atlantisframework.vortex.metric.api.BigInt;
import io.atlantisframework.vortex.metric.api.BigIntMetric;
import io.atlantisframework.vortex.metric.api.GenericUserMetricSequencer;

/**
 * 
 * Environment
 *
 * @author Fred Feng
 * @since 2.0.1
 */
public final class Environment {

	private final Map<Api, ApiSummary> summaries = new ConcurrentHashMap<Api, ApiSummary>();
	private final GenericUserMetricSequencer<Api, BigInt> apiStatisticMetricSequencer;
	private final GenericUserMetricSequencer<Api, ApiCounter> apiCounterMetricSequencer;
	private final GenericUserMetricSequencer<Api, HttpStatusCounter> httpStatusCounterMetricSequencer;

	public Environment(MetricSequencerFactory metricSequencerFactory) {
		this.apiStatisticMetricSequencer = metricSequencerFactory.getApiStatisticMetricSequencer();
		this.apiCounterMetricSequencer = metricSequencerFactory.getApiCounterMetricSequencer();
		this.httpStatusCounterMetricSequencer = metricSequencerFactory.getHttpStatusCounterMetricSequencer();
	}

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
