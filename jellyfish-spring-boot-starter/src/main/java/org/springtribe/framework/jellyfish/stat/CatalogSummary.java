package org.springtribe.framework.jellyfish.stat;

import java.util.concurrent.atomic.AtomicLong;

import lombok.ToString;

/**
 * 
 * CatalogSummary
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
@ToString
public class CatalogSummary {

	public CatalogSummary(Catalog catalog) {
		this.catalog = catalog;
	}

	public CatalogSummary() {
	}

	private Catalog catalog;

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

	public long getSuccessExecutionCount() {
		return getTotalExecutionCount() - getFailedExecutionCount() - getTimeoutExecutionCount();
	}

	public void update(Counter counter) {
		totalExecution.addAndGet(counter.getCount());
		failedExecution.addAndGet(counter.getFailedCount());
		timeoutExecution.addAndGet(counter.getTimeoutCount());
	}

	public void update(HttpStatusCounter counter) {
		countOf1xx.addAndGet(counter.getCountOf1xx());
		countOf2xx.addAndGet(counter.getCountOf2xx());
		countOf3xx.addAndGet(counter.getCountOf3xx());
		countOf4xx.addAndGet(counter.getCountOf4xx());
		countOf5xx.addAndGet(counter.getCountOf5xx());
	}

}
