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

	HttpStatusCountingMetric(HttpStatusCounter httpStatusCategory) {
		this.httpStatusCategory = httpStatusCategory;
		this.timestamp = System.currentTimeMillis();
	}

	public HttpStatusCountingMetric(int statusCode) {
		this(new HttpStatusCounter(HttpStatus.valueOf(statusCode)));
	}

	private HttpStatusCounter httpStatusCategory;
	private long timestamp;

	@Override
	public long getTimestamp() {
		return timestamp;
	}

	@Override
	public HttpStatusCounter get() {
		return httpStatusCategory;
	}

	@Override
	public CustomizedMetric<HttpStatusCounter> merge(CustomizedMetric<HttpStatusCounter> anotherUnit) {
		HttpStatusCounter current = get();
		HttpStatusCounter update = anotherUnit.get();
		HttpStatusCounter httpStatusCategory = new HttpStatusCounter();
		httpStatusCategory.setCount(current.getCount() + update.getCount());
		httpStatusCategory.setCountOf1xx(current.getCountOf1xx() + update.getCountOf1xx());
		httpStatusCategory.setCountOf2xx(current.getCountOf2xx() + update.getCountOf2xx());
		httpStatusCategory.setCountOf3xx(current.getCountOf3xx() + update.getCountOf3xx());
		httpStatusCategory.setCountOf4xx(current.getCountOf4xx() + update.getCountOf4xx());
		httpStatusCategory.setCountOf5xx(current.getCountOf5xx() + update.getCountOf5xx());
		httpStatusCategory.setCountOfUnknown(current.getCountOfUnknown() + update.getCountOfUnknown());
		return new HttpStatusCountingMetric(httpStatusCategory);
	}

}
