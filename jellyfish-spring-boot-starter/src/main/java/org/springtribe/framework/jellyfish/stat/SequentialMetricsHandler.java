package org.springtribe.framework.jellyfish.stat;

import org.springtribe.framework.gearless.utils.Metric;

/**
 * 
 * SequentialMetricsHandler
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public interface SequentialMetricsHandler<T extends Metric<T>> {

	void handleSequentialMetrics(Catalog catalog, String metric, T metricUnit);

}
