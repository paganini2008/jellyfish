package org.springtribe.framework.jellyfish.stat;

import org.springframework.http.HttpStatus;
import org.springtribe.framework.gearless.utils.CustomizedMetric;

/**
 * 
 * HttpStatusCountingMetric
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public class HttpStatusCountingMetric implements CustomizedMetric<HttpStatusCounter> {

	HttpStatusCountingMetric(HttpStatusCounter httpStatusCounter) {
		this.httpStatusCounter = httpStatusCounter;
		this.timestamp = System.currentTimeMillis();
	}

	public HttpStatusCountingMetric(int statusCode) {
		this(new HttpStatusCounter(HttpStatus.valueOf(statusCode)));
	}

	private HttpStatusCounter httpStatusCounter;
	private long timestamp;
	private boolean reset = false;

	@Override
	public boolean reset() {
		return this.reset;
	}

	@Override
	public void reset(boolean reset) {
		this.reset = reset;
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
		return new HttpStatusCountingMetric(httpStatusCategory);
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
		return new HttpStatusCountingMetric(httpStatusCategory);
	}

}
