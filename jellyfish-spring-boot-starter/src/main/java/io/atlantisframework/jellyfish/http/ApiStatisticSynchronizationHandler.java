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

import static io.atlantisframework.jellyfish.http.MetricNames.CC;
import static io.atlantisframework.jellyfish.http.MetricNames.COUNT;
import static io.atlantisframework.jellyfish.http.MetricNames.HTTP_STATUS;
import static io.atlantisframework.jellyfish.http.MetricNames.QPS;
import static io.atlantisframework.jellyfish.http.MetricNames.RT;

import io.atlantisframework.vortex.Handler;
import io.atlantisframework.vortex.common.Tuple;
import io.atlantisframework.vortex.metric.api.BigInt;
import io.atlantisframework.vortex.metric.api.BigIntMetric;
import io.atlantisframework.vortex.metric.api.MetricSequencer;
import io.atlantisframework.vortex.metric.api.UserMetric;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * ApiStatisticSynchronizationHandler
 *
 * @author Fred Feng
 * @since 2.0.1
 */
@Slf4j
public class ApiStatisticSynchronizationHandler implements Handler {

	private final String topic;
	private final Environment environment;
	private final boolean merged;

	public ApiStatisticSynchronizationHandler(String topic, Environment environment, boolean merged) {
		this.topic = topic;
		this.environment = environment;
		this.merged = merged;
	}

	@Override
	public void onData(Tuple tuple) {
		final String metric = tuple.getField("metric", String.class);
		switch (metric) {
		case COUNT:
			synchronizeApiCounterMetric(metric, tuple);
			break;
		case HTTP_STATUS:
			synchronizeHttpStatusCounterMetric(metric, tuple);
			break;
		case RT:
		case CC:
		case QPS:
			synchronizeBigIntMetric(metric, tuple);
			break;
		default:
			log.warn("Unknown metric for synchronization: {}", metric);
			break;
		}
	}

	private void synchronizeApiCounterMetric(String metric, Tuple tuple) {
		Api catalog = Api.of(tuple);
		long timestamp = tuple.getTimestamp();
		long count = tuple.getField("count", Long.class);
		long failedCount = tuple.getField("failedCount", Long.class);
		long timeoutCount = tuple.getField("timeoutCount", Long.class);
		MetricSequencer<Api, UserMetric<ApiCounter>> sequencer = environment.getApiCounterMetricSequencer();
		sequencer.trace(catalog, metric, timestamp, new ApiCounterMetric(new ApiCounter(count, failedCount, timeoutCount), timestamp),
				merged);
	}

	private void synchronizeHttpStatusCounterMetric(String metric, Tuple tuple) {
		Api catalog = Api.of(tuple);
		long countOf1xx = tuple.getField("countOf1xx", Long.class);
		long countOf2xx = tuple.getField("countOf2xx", Long.class);
		long countOf3xx = tuple.getField("countOf3xx", Long.class);
		long countOf4xx = tuple.getField("countOf4xx", Long.class);
		long countOf5xx = tuple.getField("countOf5xx", Long.class);
		long timestamp = tuple.getTimestamp();
		MetricSequencer<Api, UserMetric<HttpStatusCounter>> sequencer = environment.getHttpStatusCounterMetricSequencer();
		sequencer.trace(catalog, metric, timestamp,
				new HttpStatusCounterMetric(new HttpStatusCounter(countOf1xx, countOf2xx, countOf3xx, countOf4xx, countOf5xx), timestamp),
				merged);
	}

	private void synchronizeBigIntMetric(String metric, Tuple tuple) {
		Api catalog = Api.of(tuple);
		long timestamp = tuple.getTimestamp();
		long highestValue = tuple.getField("highestValue", Long.class);
		long lowestValue = tuple.getField("lowestValue", Long.class);
		long totalValue = tuple.getField("totalValue", Long.class);
		long count = tuple.getField("count", Long.class);
		MetricSequencer<Api, UserMetric<BigInt>> sequencer = environment.getApiStatisticMetricSequencer();
		sequencer.trace(catalog, metric, timestamp, new BigIntMetric(new BigInt(highestValue, lowestValue, totalValue, count), timestamp),
				merged);
	}

	@Override
	public String getTopic() {
		return topic;
	}

}
