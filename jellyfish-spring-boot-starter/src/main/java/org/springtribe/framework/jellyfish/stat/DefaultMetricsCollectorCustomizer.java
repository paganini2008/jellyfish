package org.springtribe.framework.jellyfish.stat;

import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;

import com.github.paganini2008.devtools.collection.MapUtils;
import com.github.paganini2008.devtools.date.DateUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * DefaultMetricsCollectorCustomizer
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
@Slf4j
public class DefaultMetricsCollectorCustomizer implements MetricsCollectorCustomizer {

	private int span = 1;
	private SpanUnit spanUnit = SpanUnit.MINUTE;
	private int bufferSize = 60;
	private HistoricalSequentialMetricsHandler historicalSequentialMetricsHandler;

	public void setSpan(int span) {
		this.span = span;
	}

	public void setSpanUnit(SpanUnit spanUnit) {
		this.spanUnit = spanUnit;
	}

	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	public void setHistoricalSequentialMetricsHandler(HistoricalSequentialMetricsHandler historicalSequentialMetricsHandler) {
		this.historicalSequentialMetricsHandler = historicalSequentialMetricsHandler;
	}

	@Override
	public int getSpan() {
		return span;
	}

	@Override
	public SpanUnit getSpanUnit() {
		return spanUnit;
	}

	@Override
	public int getBufferSize() {
		return bufferSize;
	}

	@Override
	public SequentialMetricsCollector createSequentialMetricsCollector(final Catalog catalog) {
		return new SimpleSequentialMetricsCollector(bufferSize, span, spanUnit, (metric, metricUnit) -> {
			if (historicalSequentialMetricsHandler != null) {
				historicalSequentialMetricsHandler.handleHistoricalMetrics(catalog, metric, metricUnit);
			}
			if (log.isTraceEnabled()) {
				log.trace("Discard history metric '{}' for catalog: {}", metric, catalog);
			}
		});
	}

	@Override
	public Map<String, Metric> render(Map<Object, Object> entries) {
		Map<Object, Object> data = entries;
		Map<String, Metric> sequentialMap = sequentialMap();
		String datetime;
		for (Map.Entry<Object, Object> entry : data.entrySet()) {
			datetime = (String) entry.getKey();
			if (sequentialMap.containsKey(datetime)) {
				sequentialMap.put(datetime, (Metric) entry.getValue());
			}
		}
		return sequentialMap;
	}

	protected Map<String, Metric> sequentialMap() {
		Map<String, Metric> map = new LinkedHashMap<String, Metric>();
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(System.currentTimeMillis());
		c.set(Calendar.SECOND, 0);
		for (int i = 0; i < bufferSize; i++) {
			map.put(DateUtils.format(c.getTime(), SimpleSequentialMetricsCollector.DEFAULT_DATETIME_PATTERN), newMetric());
			c.add(Calendar.MINUTE, -1 * span);
		}
		return MapUtils.reverse(map);
	}

	private Metric newMetric() {
		Metric vo = new Metric();
		vo.setTotalValue(0L);
		vo.setHighestValue(0L);
		vo.setMiddleValue(0L);
		vo.setLowestValue(0L);
		vo.setCount(0);
		vo.setSuccessCount(0);
		vo.setFailedCount(0);
		vo.setTimeoutCount(0);
		return vo;
	}

}
