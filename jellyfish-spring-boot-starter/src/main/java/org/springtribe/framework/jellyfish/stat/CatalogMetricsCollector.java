package org.springtribe.framework.jellyfish.stat;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springtribe.framework.gearless.utils.Metric;
import org.springtribe.framework.gearless.utils.SequentialMetricsCollector;
import org.springtribe.framework.gearless.utils.SimpleSequentialMetricsCollector;
import org.springtribe.framework.gearless.utils.SpanUnit;

import com.github.paganini2008.devtools.collection.MapUtils;

/**
 * 
 * CatalogMetricsCollector
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public class CatalogMetricsCollector<T extends Metric<T>> {

	private final Map<Catalog, SequentialMetricsCollector<T>> collectors = new ConcurrentHashMap<Catalog, SequentialMetricsCollector<T>>();
	private int span = 1;
	private SpanUnit spanUnit = SpanUnit.MINUTE;
	private int bufferSize = 60;
	private SequentialMetricsHandler<T> historicalSequentialMetricsHandler;

	public void setSpan(int span) {
		this.span = span;
	}

	public void setSpanUnit(SpanUnit spanUnit) {
		this.spanUnit = spanUnit;
	}

	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	public void setHistoricalSequentialMetricsHandler(SequentialMetricsHandler<T> historicalSequentialMetricsHandler) {
		this.historicalSequentialMetricsHandler = historicalSequentialMetricsHandler;
	}

	public void update(Catalog catalog, String metric, long timestamp, T metricUnit) {
		SequentialMetricsCollector<T> collector = MapUtils.get(collectors, catalog, () -> {
			return new SimpleSequentialMetricsCollector<T>(bufferSize, span, spanUnit, (eldestMetric, eldestMetricUnit) -> {
				if (historicalSequentialMetricsHandler != null) {
					historicalSequentialMetricsHandler.handleSequentialMetrics(catalog, eldestMetric, eldestMetricUnit);
				}
			});
		});
		collector.set(metric, timestamp, metricUnit);
	}

	public Map<String, T> sequence(Catalog catalog, String metric) {
		SequentialMetricsCollector<T> collector = collectors.get(catalog);
		return collector != null ? collector.sequence(metric) : MapUtils.emptyMap();
	}

}
