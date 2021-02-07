package indi.atlantis.framework.jellyfish.metrics;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.github.paganini2008.devtools.collection.MapUtils;

import indi.atlantis.framework.gearless.utils.Metric;
import indi.atlantis.framework.gearless.utils.SequentialMetricsCollector;
import indi.atlantis.framework.gearless.utils.SimpleSequentialMetricsCollector;
import indi.atlantis.framework.gearless.utils.SpanUnit;

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
	private MetricEvictionHandler<T> evictionHandler;

	public void setSpan(int span) {
		this.span = span;
	}

	public void setSpanUnit(SpanUnit spanUnit) {
		this.spanUnit = spanUnit;
	}

	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	public void setMetricEvictionHandler(MetricEvictionHandler<T> evictionHandler) {
		this.evictionHandler = evictionHandler;
	}

	public int getSpan() {
		return span;
	}

	public SpanUnit getSpanUnit() {
		return spanUnit;
	}

	public int getBufferSize() {
		return bufferSize;
	}

	public int update(Catalog catalog, String metric, long timestamp, T metricUnit) {
		SequentialMetricsCollector<T> collector = MapUtils.get(collectors, catalog, () -> {
			return new SimpleSequentialMetricsCollector<T>(bufferSize, span, spanUnit, (eldestMetric, eldestMetricUnit) -> {
				if (evictionHandler != null) {
					evictionHandler.onEldestMetricRemoval(catalog, eldestMetric, eldestMetricUnit);
				}
			});
		});
		collector.set(metric, timestamp, metricUnit);
		return collector.size();
	}

	public Map<String, T> sequence(Catalog catalog, String metric) {
		SequentialMetricsCollector<T> collector = collectors.get(catalog);
		return collector != null ? collector.sequence(metric) : MapUtils.emptyMap();
	}

	public int size(Catalog catalog) {
		SequentialMetricsCollector<T> collector = collectors.get(catalog);
		return collector != null ? collector.size() : 0;
	}

}
