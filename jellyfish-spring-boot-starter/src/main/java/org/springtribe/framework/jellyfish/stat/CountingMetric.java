package org.springtribe.framework.jellyfish.stat;

import org.springtribe.framework.gearless.utils.CustomizedMetric;

/**
 * 
 * CountingMetric
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public class CountingMetric implements CustomizedMetric<Counter> {

	public CountingMetric(boolean failed, boolean timeout) {
		this(new Counter(1, failed ? 1 : 0, timeout ? 1 : 0));
	}

	CountingMetric(Counter counter) {
		this.counter = counter;
		this.timestamp = System.currentTimeMillis();
	}

	private Counter counter;
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
	public CustomizedMetric<Counter> reset(CustomizedMetric<Counter> currentMetric) {
		Counter current = this.get();
		Counter update = currentMetric.get();
		Counter counter = new Counter();
		counter.setCount(current.getCount() - update.getCount());
		counter.setFailedCount(current.getFailedCount() - update.getFailedCount());
		counter.setTimeoutCount(current.getTimeoutCount() - update.getTimeoutCount());
		return new CountingMetric(counter);
	}

	@Override
	public CustomizedMetric<Counter> merge(CustomizedMetric<Counter> anotherMetric) {
		Counter current = this.get();
		Counter update = anotherMetric.get();
		Counter counter = new Counter();
		counter.setCount(current.getCount() + update.getCount());
		counter.setFailedCount(current.getFailedCount() + update.getFailedCount());
		counter.setTimeoutCount(current.getTimeoutCount() + update.getTimeoutCount());
		return new CountingMetric(counter);
	}

	@Override
	public long getTimestamp() {
		return timestamp;
	}

	@Override
	public Counter get() {
		return counter;
	}

}
