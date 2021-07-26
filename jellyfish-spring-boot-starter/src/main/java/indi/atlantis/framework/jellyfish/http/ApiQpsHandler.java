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
package indi.atlantis.framework.jellyfish.http;

import static indi.atlantis.framework.jellyfish.http.MetricNames.QPS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import indi.atlantis.framework.vortex.Handler;
import indi.atlantis.framework.vortex.common.Tuple;
import indi.atlantis.framework.vortex.metric.BigInt;
import indi.atlantis.framework.vortex.metric.BigIntMetric;
import indi.atlantis.framework.vortex.metric.MetricSequencer;
import indi.atlantis.framework.vortex.metric.UserMetric;

/**
 * 
 * ApiQpsHandler
 *
 * @author Fred Feng
 * @since 2.0.1
 */
public class ApiQpsHandler implements Handler {

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
		final long timestamp = tuple.getTimestamp();
		int qps = tuple.getField(QPS, Integer.class);
		MetricSequencer<Api, UserMetric<BigInt>> sequencer = environment.getApiStatisticMetricSequencer();
		sequencer.update(catalog, QPS, timestamp, new BigIntMetric(qps, timestamp), true);

		environment.update(catalog, QPS, new BigIntMetric(qps, timestamp), true);
	}

	@Override
	public String getTopic() {
		return "indi.atlantis.framework.jellyfish.http.ApiQpsWatcher";
	}

}
