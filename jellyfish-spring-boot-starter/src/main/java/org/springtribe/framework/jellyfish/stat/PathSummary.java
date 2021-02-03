package org.springtribe.framework.jellyfish.stat;

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
	private String category;
	private String path;

	public PathSummary(String clusterName, String applicationName, String host, String category, String path) {
		this.clusterName = clusterName;
		this.applicationName = applicationName;
		this.host = host;
		this.category = category;
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

}
