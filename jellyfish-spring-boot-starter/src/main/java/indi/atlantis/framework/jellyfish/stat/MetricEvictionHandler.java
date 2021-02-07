package indi.atlantis.framework.jellyfish.stat;

import org.springtribe.framework.gearless.utils.Metric;

/**
 * 
 * MetricEvictionHandler
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public interface MetricEvictionHandler<T extends Metric<T>> {

	void onEldestMetricRemoval(Catalog catalog, String metric, T metricUnit);

}
