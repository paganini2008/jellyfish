package org.springtribe.framework.jellyfish.stat;

import java.util.Map;

import org.springtribe.framework.gearless.utils.CustomizedMetric;
import org.springtribe.framework.gearless.utils.SequentialMetricsCollector;
import org.springtribe.framework.gearless.utils.SpanUnit;
import org.springtribe.framework.gearless.utils.StatisticalMetric;

/**
 * 
 * MetricsCollectorCustomizer
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public interface MetricsCollectorCustomizer {

	SequentialMetricsCollector<StatisticalMetric> createNewForStatistic(Catalog catalog);

	SequentialMetricsCollector<CustomizedMetric<HttpRequestCounter>> createNewForCounter(Catalog catalog);

	SequentialMetricsCollector<CustomizedMetric<HttpStatusCounter>> createNewForHttpStatusCategory(Catalog catalog);

	Map<String, MetricBean> render(Map<Object, Object> entries);

	default int getBufferSize() {
		return 60;
	}

	default SpanUnit getSpanUnit() {
		return SpanUnit.MINUTE;
	}

	default int getSpan() {
		return 1;
	}
}
