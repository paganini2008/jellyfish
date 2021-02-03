package org.springtribe.framework.jellyfish.stat;

import java.util.Map;

import com.github.paganini2008.devtools.collection.SequentialMetricsCollector;
import com.github.paganini2008.devtools.date.SpanUnit;

/**
 * 
 * MetricsCollectorCustomizer
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public interface MetricsCollectorCustomizer {

	SequentialMetricsCollector createSequentialMetricsCollector(Catalog catalog);

	Map<String, Metric> render(Map<Object, Object> entries);

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
