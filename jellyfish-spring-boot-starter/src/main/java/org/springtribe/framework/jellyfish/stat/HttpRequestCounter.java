package org.springtribe.framework.jellyfish.stat;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * HttpRequestCounter
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
@Getter
@Setter
public class HttpRequestCounter {

	private long count;
	private long failedCount;
	private long timeoutCount;

	HttpRequestCounter() {
	}

	HttpRequestCounter(long count, long failedCount, long timeoutCount) {
		super();
		this.count = count;
		this.failedCount = failedCount;
		this.timeoutCount = timeoutCount;
	}

	public long getSuccessCount() {
		return count - failedCount - timeoutCount;
	}

}
