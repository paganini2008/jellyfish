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

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;

import indi.atlantis.framework.vortex.metric.AbstractUserMetric;
import indi.atlantis.framework.vortex.metric.UserMetric;

/**
 * 
 * HttpStatusCounterMetric
 *
 * @author Fred Feng
 * @since 2.0.1
 */
public class HttpStatusCounterMetric extends AbstractUserMetric<HttpStatusCounter> {

	public HttpStatusCounterMetric(HttpStatus httpStatus, long timestamp) {
		this(new HttpStatusCounter(httpStatus), timestamp);
	}

	public HttpStatusCounterMetric(HttpStatusCounter httpStatusCounter, long timestamp) {
		super(httpStatusCounter, timestamp, false);
	}

	@Override
	public UserMetric<HttpStatusCounter> reset(UserMetric<HttpStatusCounter> newMetric) {
		HttpStatusCounter current = get();
		HttpStatusCounter update = newMetric.get();
		HttpStatusCounter counter = new HttpStatusCounter();
		counter.setCountOf1xx(current.getCountOf1xx() - update.getCountOf1xx());
		counter.setCountOf2xx(current.getCountOf2xx() - update.getCountOf2xx());
		counter.setCountOf3xx(current.getCountOf3xx() - update.getCountOf3xx());
		counter.setCountOf4xx(current.getCountOf4xx() - update.getCountOf4xx());
		counter.setCountOf5xx(current.getCountOf5xx() - update.getCountOf5xx());
		return new HttpStatusCounterMetric(counter, newMetric.getTimestamp());
	}

	@Override
	public UserMetric<HttpStatusCounter> merge(UserMetric<HttpStatusCounter> newMetric) {
		HttpStatusCounter current = get();
		HttpStatusCounter update = newMetric.get();
		HttpStatusCounter counter = new HttpStatusCounter();
		counter.setCountOf1xx(current.getCountOf1xx() + update.getCountOf1xx());
		counter.setCountOf2xx(current.getCountOf2xx() + update.getCountOf2xx());
		counter.setCountOf3xx(current.getCountOf3xx() + update.getCountOf3xx());
		counter.setCountOf4xx(current.getCountOf4xx() + update.getCountOf4xx());
		counter.setCountOf5xx(current.getCountOf5xx() + update.getCountOf5xx());
		return new HttpStatusCounterMetric(counter, newMetric.getTimestamp());
	}

	@Override
	public Map<String, Object> toEntries() {
		HttpStatusCounter httpStatusCounter = get();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("countOf1xx", httpStatusCounter.getCountOf1xx());
		map.put("countOf2xx", httpStatusCounter.getCountOf2xx());
		map.put("countOf3xx", httpStatusCounter.getCountOf3xx());
		map.put("countOf4xx", httpStatusCounter.getCountOf4xx());
		map.put("countOf5xx", httpStatusCounter.getCountOf5xx());
		map.put("timestamp", getTimestamp());
		return map;
	}

}
