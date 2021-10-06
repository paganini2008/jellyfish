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
import static io.atlantisframework.jellyfish.http.MetricNames.RT;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;

import io.atlantisframework.vortex.Handler;
import io.atlantisframework.vortex.common.Tuple;
import io.atlantisframework.vortex.metric.BigInt;
import io.atlantisframework.vortex.metric.BigIntMetric;
import io.atlantisframework.vortex.metric.MetricSequencer;
import io.atlantisframework.vortex.metric.UserMetric;

/**
 * 
 * ApiStatisticHandler
 *
 * @author Fred Feng
 * @since 2.0.1
 */
public class ApiStatisticHandler implements Handler {

	@Qualifier("primaryEnvironment")
	@Autowired
	private Environment environment;

	@Override
	public void onData(Tuple tuple) {

		String clusterName = tuple.getField("clusterName", String.class);
		String applicationName = tuple.getField("applicationName", String.class);
		String host = tuple.getField("host", String.class);
		String category = tuple.getField("category", String.class);
		String path = tuple.getField("path", String.class);

		doCollect(new Api(clusterName, applicationName, host, category, path), tuple);
		doCollect(new Api(clusterName, applicationName, host, category, null), tuple);
		doCollect(new Api(clusterName, applicationName, host, null, null), tuple);
		doCollect(new Api(clusterName, applicationName, null, null, null), tuple);
		doCollect(new Api(clusterName, null, null, null, null), tuple);

	}

	private void doCollect(Api catalog, Tuple tuple) {
		final long timestamp = tuple.getField("requestTime", Long.class);
		boolean failed = tuple.getField("failed", Boolean.class);
		boolean timeout = tuple.getField("timeout", Boolean.class);
		int httpStatusCode = tuple.getField("httpStatusCode", Integer.class);

		ApiCounterMetric apiCounterMetric = new ApiCounterMetric(failed, timeout, timestamp);
		MetricSequencer<Api, UserMetric<ApiCounter>> apiCounterMetricSequencer = environment.getApiCounterMetricSequencer();
		apiCounterMetricSequencer.update(catalog, COUNT, timestamp, apiCounterMetric, true);

		HttpStatusCounterMetric httpStatusCounterMetric = new HttpStatusCounterMetric(HttpStatus.valueOf(httpStatusCode), timestamp);
		MetricSequencer<Api, UserMetric<HttpStatusCounter>> httpStatusCounterMetricSequencer = environment
				.getHttpStatusCounterMetricSequencer();
		httpStatusCounterMetricSequencer.update(catalog, HTTP_STATUS, timestamp, httpStatusCounterMetric, true);

		long elapsed = tuple.getField("elapsed", Long.class);
		long concurrency = tuple.getField("concurrency", Long.class);
		MetricSequencer<Api, UserMetric<BigInt>> apiStatisticMetricSequencer = environment.getApiStatisticMetricSequencer();
		apiStatisticMetricSequencer.update(catalog, RT, timestamp, new BigIntMetric(elapsed, timestamp), true);
		apiStatisticMetricSequencer.update(catalog, CC, timestamp, new BigIntMetric(concurrency, timestamp), true);

		environment.update(catalog, COUNT, apiCounterMetric, true);
		environment.update(catalog, HTTP_STATUS, httpStatusCounterMetric, true);
		environment.update(catalog, RT, new BigIntMetric(elapsed, timestamp), true);
		environment.update(catalog, CC, new BigIntMetric(concurrency, timestamp), true);
	}

	@Override
	public String getTopic() {
		return "io.atlantisframework.jellyfish.http.ApiStatisticWatcher";
	}

}
