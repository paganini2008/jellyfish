package org.springtribe.framework.jellyfish.stat;

/**
 * 
 * HistoricalMetricsHandler
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public interface HistoricalMetricsHandler {

	void handleHistoricalMetrics(String metric, MetricUnit metricUnit);

}
