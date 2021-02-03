package org.springtribe.framework.jellyfish.monitor;

/**
 * 
 * StatisticalTracer
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public interface StatisticalTracer {

	void onException(String requestId, String path, long elapsed, Exception cause);

	void onTimeout(String requestId, String path, long elapsed);

}
