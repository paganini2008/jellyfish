package indi.atlantis.framework.jellyfish.http;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * ApiCounter
 *
 * @author Fred Feng
 * @version 1.0
 */
@Getter
@Setter
@ToString
public class ApiCounter implements Serializable, Cloneable {

	private static final long serialVersionUID = -8870925173668637122L;
	private long count;
	private long failedCount;
	private long timeoutCount;

	public ApiCounter() {
	}

	public ApiCounter(long count, long failedCount, long timeoutCount) {
		this.count = count;
		this.failedCount = failedCount;
		this.timeoutCount = timeoutCount;
	}

	public long getSuccessCount() {
		return count - failedCount - timeoutCount;
	}

	public ApiCounter clone() {
		try {
			return (ApiCounter) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new IllegalStateException(e);
		}
	}

}
