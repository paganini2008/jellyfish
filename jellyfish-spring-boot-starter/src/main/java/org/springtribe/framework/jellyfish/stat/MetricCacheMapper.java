package org.springtribe.framework.jellyfish.stat;

import org.springtribe.framework.gearless.utils.Metric;

/**
 * 
 * MetricCacheMapper
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public interface MetricCacheMapper<T extends Metric<T>> {

	Object mapObject(Catalog catalog, String metric, T metricUnit);

}
