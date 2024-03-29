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

import static io.atlantisframework.jellyfish.http.MetricNames.CC;
import static io.atlantisframework.jellyfish.http.MetricNames.COUNT;
import static io.atlantisframework.jellyfish.http.MetricNames.HTTP_STATUS;
import static io.atlantisframework.jellyfish.http.MetricNames.QPS;
import static io.atlantisframework.jellyfish.http.MetricNames.RT;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.paganini2008.devtools.collection.MapUtils;

import io.atlantisframework.jellyfish.Response;
import io.atlantisframework.vortex.metric.api.BigInt;
import io.atlantisframework.vortex.metric.api.GenericUserMetricSequencer;

/**
 * 
 * ApiController
 *
 * @author Fred Feng
 * @since 2.0.1
 */
@RequestMapping("/atlantis/jellyfish/api")
@RestController
public class ApiController {

	@Qualifier("secondaryEnvironment")
	@Autowired
	private Environment environment;

	@GetMapping("/list")
	public Response pathList(@RequestParam(name = "level", required = false, defaultValue = "0") int level) {
		List<Api> apiList = environment.apiList();
		apiList = apiList.stream().filter(c -> c.getLevel() == level).collect(Collectors.toList());
		Collections.sort(apiList);
		return Response.success(apiList);
	}

	@PostMapping("/summary")
	public Response summary(@RequestBody Api api) {
		ApiSummary summary = environment.summary(api);
		return summary != null ? Response.success(summary.toEntries()) : Response.success();
	}

	@PostMapping("/{metric}/sequence")
	public Response summary(@PathVariable("metric") String metric, @RequestBody Api api) {
		Map<String, Map<String, Object>> data;
		if ("latest".equals(metric)) {
			data = fetchLatestMerticData(api);
		} else {
			data = fetchMerticData(api, metric);
		}
		return Response.success(data);
	}

	private Map<String, Map<String, Object>> fetchLatestMerticData(Api api) {
		GenericUserMetricSequencer<Api, BigInt> apiStatisticSequencer = environment.getApiStatisticMetricSequencer();
		Map<String, Map<String, Object>> latest = apiStatisticSequencer.sequenceLatest(api, new String[] { RT, CC, QPS }, null);
		GenericUserMetricSequencer<Api, ApiCounter> apiCounterSequencer = environment.getApiCounterMetricSequencer();
		latest.putAll(apiCounterSequencer.sequenceLatest(api, new String[] { COUNT }, null));
		GenericUserMetricSequencer<Api, HttpStatusCounter> httpStatusCounterSequencer = environment.getHttpStatusCounterMetricSequencer();
		latest.putAll(httpStatusCounterSequencer.sequenceLatest(api, new String[] { HTTP_STATUS }, null));
		return latest;
	}

	private Map<String, Map<String, Object>> fetchMerticData(Api api, String metric) {
		switch (metric) {
		case RT:
		case CC:
		case QPS:
			GenericUserMetricSequencer<Api, BigInt> apiStatisticSequencer = environment.getApiStatisticMetricSequencer();
			return apiStatisticSequencer.sequence(api, metric, true, null);
		case COUNT:
			GenericUserMetricSequencer<Api, ApiCounter> apiCounterSequencer = environment.getApiCounterMetricSequencer();
			return apiCounterSequencer.sequence(api, metric, true, null);
		case HTTP_STATUS:
			GenericUserMetricSequencer<Api, HttpStatusCounter> httpStatusCounterMetricSequencer = environment
					.getHttpStatusCounterMetricSequencer();
			return httpStatusCounterMetricSequencer.sequence(api, metric, true, null);
		}
		return MapUtils.emptyMap();
	}

}
