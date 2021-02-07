package indi.atlantis.framework.jellyfish.stat;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * Counter
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
@Getter
@Setter
public class Counter {

	private long count;
	private long failedCount;
	private long timeoutCount;

	public Counter() {
	}

	public Counter(long count, long failedCount, long timeoutCount) {
		super();
		this.count = count;
		this.failedCount = failedCount;
		this.timeoutCount = timeoutCount;
	}

	public long getSuccessCount() {
		return count - failedCount - timeoutCount;
	}

}
