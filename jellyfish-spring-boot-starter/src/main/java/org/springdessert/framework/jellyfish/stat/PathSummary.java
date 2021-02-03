package org.springdessert.framework.jellyfish.stat;

import org.springtribe.framework.gearless.common.Tuple;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * PathSummary
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
@Getter
@Setter
@ToString
public class PathSummary {

	private String clusterName;
	private String applicationName;
	private String host;
	private String path;

	public PathSummary(String clusterName, String applicationName, String host, String path) {
		this.clusterName = clusterName;
		this.applicationName = applicationName;
		this.host = host;
		this.path = path;
	}

	public PathSummary() {
	}

	private long totalExecutionCount;
	private long timeoutExecutionCount;
	private long failedExecutionCount;

	public long getSuccessExecutionCount() {
		return totalExecutionCount - failedExecutionCount - timeoutExecutionCount;
	}

	public static PathSummary of(Tuple tuple) {
		String clusterName = tuple.getField("clusterName", String.class);
		String applicationName = tuple.getField("applicationName", String.class);
		String host = tuple.getField("host", String.class);
		String path = tuple.getField("path", String.class);
		return new PathSummary(clusterName, applicationName, host, path);
	}

}
