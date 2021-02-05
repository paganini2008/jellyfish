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

	final AtomicLong totalExecution = new AtomicLong();
	final AtomicLong failedExecution = new AtomicLong();
	final AtomicLong timeoutExecution = new AtomicLong();

	final AtomicLong countOf1xx = new AtomicLong();
	final AtomicLong countOf2xx = new AtomicLong();
	final AtomicLong countOf3xx = new AtomicLong();
	final AtomicLong countOf4xx = new AtomicLong();
	final AtomicLong countOf5xx = new AtomicLong();

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

}
