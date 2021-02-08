package indi.atlantis.framework.jellyfish.metrics;

import org.springframework.http.HttpStatus;

import indi.atlantis.framework.vortex.utils.CustomizedMetric;

/**
 * 
 * HttpStatusCountingMetric
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public class HttpStatusCountingMetric implements CustomizedMetric<HttpStatusCounter> {

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
	public CustomizedMetric<HttpStatusCounter> reset(CustomizedMetric<HttpStatusCounter> currentMetric) {
		HttpStatusCounter current = get();
		HttpStatusCounter update = currentMetric.get();
		HttpStatusCounter httpStatusCategory = new HttpStatusCounter();
		httpStatusCategory.setCountOf1xx(current.getCountOf1xx() - update.getCountOf1xx());
		httpStatusCategory.setCountOf2xx(current.getCountOf2xx() - update.getCountOf2xx());
		httpStatusCategory.setCountOf3xx(current.getCountOf3xx() - update.getCountOf3xx());
		httpStatusCategory.setCountOf4xx(current.getCountOf4xx() - update.getCountOf4xx());
		httpStatusCategory.setCountOf5xx(current.getCountOf5xx() - update.getCountOf5xx());
		return new HttpStatusCountingMetric(httpStatusCategory, currentMetric.getTimestamp(), false);
	}

	@Override
	public CustomizedMetric<HttpStatusCounter> merge(CustomizedMetric<HttpStatusCounter> anotherMetric) {
		HttpStatusCounter current = get();
		HttpStatusCounter update = anotherMetric.get();
		HttpStatusCounter httpStatusCategory = new HttpStatusCounter();
		httpStatusCategory.setCountOf1xx(current.getCountOf1xx() + update.getCountOf1xx());
		httpStatusCategory.setCountOf2xx(current.getCountOf2xx() + update.getCountOf2xx());
		httpStatusCategory.setCountOf3xx(current.getCountOf3xx() + update.getCountOf3xx());
		httpStatusCategory.setCountOf4xx(current.getCountOf4xx() + update.getCountOf4xx());
		httpStatusCategory.setCountOf5xx(current.getCountOf5xx() + update.getCountOf5xx());
		return new HttpStatusCountingMetric(httpStatusCategory, anotherMetric.getTimestamp(), false);
	}

}
