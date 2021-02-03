package org.springtribe.framework.jellyfish.stat;

import java.util.LinkedHashMap;
import java.util.Map;

import com.github.paganini2008.devtools.collection.MapUtils;

/**
 * 
 * SequentialMetricsCollector
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public interface SequentialMetricsCollector extends MetricsCollector {

	default MetricUnit set(String metric, MetricUnit metricUnit) {
		return set(metric, Long.min(System.currentTimeMillis(), metricUnit.getTimestamp()), metricUnit);
	}

	MetricUnit set(String metric, long timestamp, MetricUnit metricUnit);

	default MetricUnit get(String metric) {
		Map<String, MetricUnit> data = sequence(metric);
		Map.Entry<String, MetricUnit> lastEntry = MapUtils.getLastEntry(data);
		return lastEntry != null ? lastEntry.getValue() : null;
	}

	default Map<String, MetricUnit> fetch() {
		Map<String, MetricUnit> data = new LinkedHashMap<String, MetricUnit>();
		for (String metric : metrics()) {
			data.put(metric, get(metric));
		}
		return data;
	}

	Map<String, MetricUnit> sequence(String metric);

}
