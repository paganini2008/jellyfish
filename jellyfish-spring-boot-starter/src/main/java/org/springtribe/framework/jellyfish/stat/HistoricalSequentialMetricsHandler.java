package org.springtribe.framework.jellyfish.stat;

/**
 * 
 * HistoricalSequentialMetricsHandler
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public interface HistoricalSequentialMetricsHandler {

	void handleHistoricalMetrics(Catalog catalog, String metric, MetricUnit metricUnit);

}
