package org.springtribe.framework.jellyfish.stat;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
	private final CatalogMetricsCollector<CustomizedMetric<HttpRequestCounter>> countingCollector = new CatalogMetricsCollector<CustomizedMetric<HttpRequestCounter>>();
	private final CatalogMetricsCollector<CustomizedMetric<HttpStatusCounter>> httpStatusCountingCollector = new CatalogMetricsCollector<CustomizedMetric<HttpStatusCounter>>();

	public CatalogSummary getSummary(Catalog catalog) {
		return summary.get(catalog);
	}

	public CatalogMetricsCollector<StatisticalMetric> getStatisticCollector() {
		return statisticCollector;
	}

	public CatalogMetricsCollector<CustomizedMetric<HttpRequestCounter>> getCountingCollector() {
		return countingCollector;
	}

	public CatalogMetricsCollector<CustomizedMetric<HttpStatusCounter>> getHttpStatusCountingCollector() {
		return httpStatusCountingCollector;
	}

}
