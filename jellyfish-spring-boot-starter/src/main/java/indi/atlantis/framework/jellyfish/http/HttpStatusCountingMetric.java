package indi.atlantis.framework.jellyfish.http;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;

import indi.atlantis.framework.vortex.sequence.UserMetric;

/**
 * 
 * HttpStatusCountingMetric
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public class HttpStatusCountingMetric implements UserMetric<HttpStatusCounter> {

	public HttpStatusCountingMetric(HttpStatusCounter httpStatusCounter, long timestamp, boolean reset) {
		this.httpStatusCounter = httpStatusCounter;
		this.timestamp = timestamp;
		this.reset = reset;
	}

	public HttpStatusCountingMetric(int statusCode, long timestamp) {
		this(new HttpStatusCounter(HttpStatus.valueOf(statusCode)), timestamp, false);
	}

	private HttpStatusCounter httpStatusCounter;
	private long timestamp;
	private final boolean reset;

	@Override
	public boolean reset() {
		return this.reset;
	}

	@Override
	public long getTimestamp() {
		return timestamp;
	}

	@Override
	public HttpStatusCounter get() {
		return httpStatusCounter;
	}

	@Override
	public UserMetric<HttpStatusCounter> reset(UserMetric<HttpStatusCounter> currentMetric) {
		HttpStatusCounter current = get();
		HttpStatusCounter update = currentMetric.get();
		HttpStatusCounter counter = new HttpStatusCounter();
		counter.setCountOf1xx(current.getCountOf1xx() - update.getCountOf1xx());
		counter.setCountOf2xx(current.getCountOf2xx() - update.getCountOf2xx());
		counter.setCountOf3xx(current.getCountOf3xx() - update.getCountOf3xx());
		counter.setCountOf4xx(current.getCountOf4xx() - update.getCountOf4xx());
		counter.setCountOf5xx(current.getCountOf5xx() - update.getCountOf5xx());
		return new HttpStatusCountingMetric(counter, currentMetric.getTimestamp(), false);
	}

	@Override
	public UserMetric<HttpStatusCounter> merge(UserMetric<HttpStatusCounter> anotherMetric) {
		HttpStatusCounter current = get();
		HttpStatusCounter update = anotherMetric.get();
		HttpStatusCounter counter = new HttpStatusCounter();
		counter.setCountOf1xx(current.getCountOf1xx() + update.getCountOf1xx());
		counter.setCountOf2xx(current.getCountOf2xx() + update.getCountOf2xx());
		counter.setCountOf3xx(current.getCountOf3xx() + update.getCountOf3xx());
		counter.setCountOf4xx(current.getCountOf4xx() + update.getCountOf4xx());
		counter.setCountOf5xx(current.getCountOf5xx() + update.getCountOf5xx());
		return new HttpStatusCountingMetric(counter, anotherMetric.getTimestamp(), false);
	}

	@Override
	public Map<String, Object> toEntries() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("countOf1xx", httpStatusCounter.getCountOf1xx());
		map.put("countOf2xx", httpStatusCounter.getCountOf2xx());
		map.put("countOf3xx", httpStatusCounter.getCountOf3xx());
		map.put("countOf4xx", httpStatusCounter.getCountOf4xx());
		map.put("countOf5xx", httpStatusCounter.getCountOf5xx());
		map.put("timestamp", timestamp);
		return map;
	}

}
