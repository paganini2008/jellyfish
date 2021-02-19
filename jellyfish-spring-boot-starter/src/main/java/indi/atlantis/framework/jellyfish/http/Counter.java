package indi.atlantis.framework.jellyfish.http;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * Counter
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
@Getter
@Setter
@ToString
public class Counter implements Serializable {

	private static final long serialVersionUID = -8870925173668637122L;
	private long count;
	private long failedCount;
	private long timeoutCount;

	public Counter() {
	}

	public Counter(long count, long failedCount, long timeoutCount) {
		this.count = count;
		this.failedCount = failedCount;
		this.timeoutCount = timeoutCount;
	}

	public long getSuccessCount() {
		return count - failedCount - timeoutCount;
	}

}
