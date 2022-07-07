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

import java.net.SocketAddress;
import java.util.Map;

import io.atlantisframework.vortex.common.NioClient;
import io.atlantisframework.vortex.common.Tuple;
import io.atlantisframework.vortex.metric.Synchronizer;
import io.atlantisframework.vortex.metric.api.BigInt;
import io.atlantisframework.vortex.metric.api.BigIntMetric;
import io.atlantisframework.vortex.metric.api.UserMetric;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * ApiSummarySynchronizer
 *
 * @author Fred Feng
 * @since 2.0.1
 */
@Slf4j
public class ApiSummarySynchronizer implements Synchronizer {

	private final String topic;
	private final Environment environment;
	private final boolean incremental;

	public ApiSummarySynchronizer(String topic, Environment environment, boolean incremental) {
		this.topic = topic;
		this.environment = environment;
		this.incremental = incremental;
	}

	@Override
	public void synchronize(NioClient nioClient, SocketAddress remoteAddress) {
		log.trace("Summary synchronization begin...");
		environment.apiList().forEach(api -> {
			ApiSummary summary = environment.summary(api);
			String metric;
			long timestamp;
			ApiCounter apiCounter;
			for (Map.Entry<String, UserMetric<ApiCounter>> entry : summary.getApiCounterMetricCollector().all().entrySet()) {
				metric = entry.getKey();
				apiCounter = entry.getValue().get();
				timestamp = entry.getValue().getTimestamp();
				Tuple tuple = synchronizeApiCounter(api, metric, apiCounter, timestamp);
				nioClient.send(remoteAddress, tuple);
				if (incremental) {
					summary.getApiCounterMetricCollector().set(metric, new ApiCounterMetric(apiCounter.clone(), timestamp).resettable(),
							true);
				}
			}
			HttpStatusCounter httpStatusCounter;
			for (Map.Entry<String, UserMetric<HttpStatusCounter>> entry : summary.getHttpStatusCounterMetricCollector().all().entrySet()) {
				metric = entry.getKey();
				httpStatusCounter = entry.getValue().get();
				timestamp = entry.getValue().getTimestamp();
				Tuple tuple = synchronizeHttpStatusCounter(api, metric, httpStatusCounter, timestamp);
				nioClient.send(remoteAddress, tuple);
				if (incremental) {
					summary.getHttpStatusCounterMetricCollector().set(metric,
							new HttpStatusCounterMetric(httpStatusCounter.clone(), timestamp).resettable(), true);
				}
			}
			BigInt bigInt;
			for (Map.Entry<String, UserMetric<BigInt>> entry : summary.getApiStatisticMetricCollector().all().entrySet()) {
				metric = entry.getKey();
				bigInt = entry.getValue().get();
				timestamp = entry.getValue().getTimestamp();
				Tuple tuple = synchronizeBigInt(api, entry.getKey(), bigInt, timestamp);
				nioClient.send(remoteAddress, tuple);
				if (incremental) {
					summary.getApiStatisticMetricCollector().set(metric, new BigIntMetric(bigInt.clone(), timestamp).resettable(), true);
				}
			}
		});
		log.trace("Summary synchronization end.");
	}

	private Tuple synchronizeHttpStatusCounter(Api api, String metric, HttpStatusCounter httpStatusCounter, long timestamp) {
		Tuple tuple = Tuple.newOne(topic);
		tuple.setField("clusterName", api.getClusterName());
		tuple.setField("applicationName", api.getApplicationName());
		tuple.setField("host", api.getHost());
		tuple.setField("category", api.getCategory());
		tuple.setField("path", api.getPath());
		tuple.setField("metric", metric);

		long countOf1xx = httpStatusCounter.getCountOf1xx();
		long countOf2xx = httpStatusCounter.getCountOf2xx();
		long countOf3xx = httpStatusCounter.getCountOf3xx();
		long countOf4xx = httpStatusCounter.getCountOf4xx();
		long countOf5xx = httpStatusCounter.getCountOf5xx();
		tuple.setField("countOf1xx", countOf1xx);
		tuple.setField("countOf2xx", countOf2xx);
		tuple.setField("countOf3xx", countOf3xx);
		tuple.setField("countOf4xx", countOf4xx);
		tuple.setField("countOf5xx", countOf5xx);
		tuple.setField("timestamp", timestamp);
		return tuple;
	}

	private Tuple synchronizeApiCounter(Api api, String metric, ApiCounter apiCounter, long timestamp) {
		Tuple tuple = Tuple.newOne(topic);
		tuple.setField("clusterName", api.getClusterName());
		tuple.setField("applicationName", api.getApplicationName());
		tuple.setField("host", api.getHost());
		tuple.setField("category", api.getCategory());
		tuple.setField("path", api.getPath());
		tuple.setField("metric", metric);

		long count = apiCounter.getCount();
		long failedCount = apiCounter.getFailedCount();
		long timeoutCount = apiCounter.getTimeoutCount();
		tuple.setField("count", count);
		tuple.setField("failedCount", failedCount);
		tuple.setField("timeoutCount", timeoutCount);
		tuple.setField("timestamp", timestamp);
		return tuple;
	}

	private Tuple synchronizeBigInt(Api api, String metric, BigInt bigInt, long timestamp) {
		Tuple tuple = Tuple.newOne(topic);
		tuple.setField("clusterName", api.getClusterName());
		tuple.setField("applicationName", api.getApplicationName());
		tuple.setField("host", api.getHost());
		tuple.setField("category", api.getCategory());
		tuple.setField("path", api.getPath());
		tuple.setField("metric", metric);

		long highestValue = bigInt.getHighestValue();
		long lowestValue = bigInt.getLowestValue();
		long totalValue = bigInt.getTotalValue();
		long count = bigInt.getCount();
		tuple.setField("highestValue", highestValue);
		tuple.setField("lowestValue", lowestValue);
		tuple.setField("totalValue", totalValue);
		tuple.setField("count", count);
		tuple.setField("timestamp", timestamp);
		return tuple;
	}

}
