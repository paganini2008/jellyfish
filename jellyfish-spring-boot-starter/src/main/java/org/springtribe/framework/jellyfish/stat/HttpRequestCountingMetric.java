package org.springtribe.framework.jellyfish.stat;

import org.springtribe.framework.gearless.utils.CustomizedMetric;

/**
 * 
 * HttpRequestCountingMetric
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public class HttpRequestCountingMetric implements CustomizedMetric<HttpRequestCounter> {
	
	public HttpRequestCountingMetric(boolean failed, boolean timeout) {
		this(new HttpRequestCounter(1, failed ? 1 : 0, timeout ? 1 : 0));
	}

	HttpRequestCountingMetric(HttpRequestCounter counter) {
		this.counter = counter;
		this.timestamp = System.currentTimeMillis();
	}

	private HttpRequestCounter counter;
	private long timestamp;

	@Override
	public CustomizedMetric<HttpRequestCounter> merge(CustomizedMetric<HttpRequestCounter> anotherUnit) {
		HttpRequestCounter current = this.get();
		HttpRequestCounter update = anotherUnit.get();
		HttpRequestCounter counter = new HttpRequestCounter();
		counter.setCount(current.getCount() + update.getCount());
		counter.setFailedCount(current.getFailedCount() + update.getFailedCount());
		counter.setTimeoutCount(current.getTimeoutCount() + update.getTimeoutCount());
		return new HttpRequestCountingMetric(counter);
	}

	@Override
	public long getTimestamp() {
		return timestamp;
	}

	@Override
	public HttpRequestCounter get() {
		return counter;
	}

}
