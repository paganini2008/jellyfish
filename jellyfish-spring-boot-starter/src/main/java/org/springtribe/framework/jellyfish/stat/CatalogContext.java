package org.springtribe.framework.jellyfish.stat;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springtribe.framework.gearless.common.NioClient;
import org.springtribe.framework.gearless.utils.CustomizedMetric;
import org.springtribe.framework.gearless.utils.StatisticalMetric;

/**
 * 
 * CatalogContext
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public class CatalogContext {

	private final Map<Catalog, CatalogSummary> summary = new ConcurrentHashMap<Catalog, CatalogSummary>();
	private final CatalogMetricsCollector<StatisticalMetric> statisticCollector = new CatalogMetricsCollector<StatisticalMetric>();
	private final CatalogMetricsCollector<CustomizedMetric<Counter>> countingCollector = new CatalogMetricsCollector<CustomizedMetric<Counter>>();
	private final CatalogMetricsCollector<CustomizedMetric<HttpStatusCounter>> httpStatusCountingCollector = new CatalogMetricsCollector<CustomizedMetric<HttpStatusCounter>>();

	@Autowired
	private ThreadPoolTaskScheduler taskScheduler;
	
	@Autowired
	private NioClient nioClient;
	
	public CatalogSummary getSummary(Catalog catalog) {
		return summary.get(catalog);
	}

	public CatalogMetricsCollector<StatisticalMetric> getStatisticCollector() {
		return statisticCollector;
	}

	public CatalogMetricsCollector<CustomizedMetric<Counter>> getCountingCollector() {
		return countingCollector;
	}

	public CatalogMetricsCollector<CustomizedMetric<HttpStatusCounter>> getHttpStatusCountingCollector() {
		return httpStatusCountingCollector;
	}
	
	public void sync() {
		
	}

}
