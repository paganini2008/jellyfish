package indi.atlantis.framework.jellyfish.metrics;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import lombok.ToString;

/**
 * 
 * Summary
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
@ToString
public class Summary {

	private final AtomicLong totalExecution = new AtomicLong();
	private final AtomicLong failedExecution = new AtomicLong();
	private final AtomicLong timeoutExecution = new AtomicLong();

	private final AtomicLong countOf1xx = new AtomicLong();
	private final AtomicLong countOf2xx = new AtomicLong();
	private final AtomicLong countOf3xx = new AtomicLong();
	private final AtomicLong countOf4xx = new AtomicLong();
	private final AtomicLong countOf5xx = new AtomicLong();

	public long getTotalExecutionCount() {
		return totalExecution.get();
	}

	public long getFailedExecutionCount() {
		return failedExecution.get();
	}

	public long getTimeoutExecutionCount() {
		return timeoutExecution.get();
	}

	public long getSuccessExecutionCount() {
		return getTotalExecutionCount() - getFailedExecutionCount() - getTimeoutExecutionCount();
	}

	public long getCountOf1xx() {
		return countOf1xx.get();
	}

	public long getCountOf2xx() {
		return countOf2xx.get();
	}

	public long getCountOf3xx() {
		return countOf3xx.get();
	}

	public long getCountOf4xx() {
		return countOf4xx.get();
	}

	public long getCountOf5xx() {
		return countOf5xx.get();
	}

	public Map<String, Object> toEntries() {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("totalExecutionCount", getTotalExecutionCount());
		data.put("successExecutionCount", getSuccessExecutionCount());
		data.put("failedExecutionCount", getFailedExecutionCount());
		data.put("timeoutExecutionCount", getTimeoutExecutionCount());
		data.put("countOf1xx", getCountOf1xx());
		data.put("countOf2xx", getCountOf2xx());
		data.put("countOf3xx", getCountOf3xx());
		data.put("countOf4xx", getCountOf4xx());
		data.put("countOf5xx", getCountOf5xx());
		return data;
	}

	public void reset(Counter counter) {
		totalExecution.addAndGet(-1 * counter.getCount());
		failedExecution.addAndGet(-1 * counter.getFailedCount());
		timeoutExecution.addAndGet(-1 * counter.getTimeoutCount());
	}

	public void reset(HttpStatusCounter counter) {
		countOf1xx.addAndGet(-1 * counter.getCountOf1xx());
		countOf2xx.addAndGet(-1 * counter.getCountOf2xx());
		countOf3xx.addAndGet(-1 * counter.getCountOf3xx());
		countOf4xx.addAndGet(-1 * counter.getCountOf4xx());
		countOf5xx.addAndGet(-1 * counter.getCountOf5xx());
	}

	public void merge(Counter counter) {
		totalExecution.addAndGet(counter.getCount());
		failedExecution.addAndGet(counter.getFailedCount());
		timeoutExecution.addAndGet(counter.getTimeoutCount());
	}

	public void merge(HttpStatusCounter counter) {
		countOf1xx.addAndGet(counter.getCountOf1xx());
		countOf2xx.addAndGet(counter.getCountOf2xx());
		countOf3xx.addAndGet(counter.getCountOf3xx());
		countOf4xx.addAndGet(counter.getCountOf4xx());
		countOf5xx.addAndGet(counter.getCountOf5xx());
	}

	public void clear() {
		totalExecution.set(0);
		failedExecution.set(0);
		timeoutExecution.set(0);
		
		countOf1xx.set(0);
		countOf2xx.set(0);
		countOf3xx.set(0);
		countOf4xx.set(0);
		countOf5xx.set(0);
	}

}
