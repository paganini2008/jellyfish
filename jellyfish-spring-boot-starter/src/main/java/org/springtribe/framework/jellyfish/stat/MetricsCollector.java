package org.springtribe.framework.jellyfish.stat;

import java.util.Map;

/**
 * 
 * MetricsCollector
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public interface MetricsCollector {

	MetricUnit set(String metric, MetricUnit metricUnit);

	MetricUnit get(String metric);
	
	String[] metrics();
	
	Map<String, MetricUnit> fetch();

}