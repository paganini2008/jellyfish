package org.springtribe.framework.jellyfish.stat;

import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springtribe.framework.gearless.utils.CustomizedMetric;
import org.springtribe.framework.gearless.utils.SequentialMetricsCollector;
import org.springtribe.framework.gearless.utils.SimpleSequentialMetricsCollector;
import org.springtribe.framework.gearless.utils.SpanUnit;
import org.springtribe.framework.gearless.utils.StatisticalMetric;

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

	
	private MetricEvictionHandler<StatisticalMetric> historicalStatisticMetricsHandler;
	private MetricEvictionHandler<CustomizedMetric<Counter>> historicalCounterMetricsHandler;
	private MetricEvictionHandler<CustomizedMetric<HttpStatusCounter>> historicalHttpStatusCounterMetricsHandler;

	private int span = 1;
	private SpanUnit spanUnit = SpanUnit.MINUTE;
	private int bufferSize = 60;
	
	public void setSpan(int span) {
		this.span = span;
	}

	public void setSpanUnit(SpanUnit spanUnit) {
		this.spanUnit = spanUnit;
	}

	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	public void setHistoricalStatisticMetricsHandler(
			MetricEvictionHandler<StatisticalMetric> historicalStatisticMetricsHandler) {
		this.historicalStatisticMetricsHandler = historicalStatisticMetricsHandler;
	}

	public void setHistoricalCounterMetricsHandler(
			MetricEvictionHandler<CustomizedMetric<Counter>> historicalCounterMetricsHandler) {
		this.historicalCounterMetricsHandler = historicalCounterMetricsHandler;
	}

	public void setHistoricalHttpStatusCounterMetricsHandler(
			MetricEvictionHandler<CustomizedMetric<HttpStatusCounter>> historicalHttpStatusCounterMetricsHandler) {
		this.historicalHttpStatusCounterMetricsHandler = historicalHttpStatusCounterMetricsHandler;
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
	public SequentialMetricsCollector<StatisticalMetric> createNewForStatistic(final Catalog catalog) {
		return new SimpleSequentialMetricsCollector<StatisticalMetric>(bufferSize, span, spanUnit, (metric, metricUnit) -> {
			if (historicalStatisticMetricsHandler != null) {
				historicalStatisticMetricsHandler.handleHistoricalMetrics(catalog, metric, metricUnit);
			}
			if (log.isTraceEnabled()) {
				log.trace("Discard history metric '{}' for catalog: {}", metric, catalog);
			}
		});
	}

	@Override
	public SequentialMetricsCollector<CustomizedMetric<Counter>> createNewForCounter(Catalog catalog) {
		return new SimpleSequentialMetricsCollector<CustomizedMetric<Counter>>(bufferSize, span, spanUnit, (metric, metricUnit) -> {
			if (historicalCounterMetricsHandler != null) {
				historicalCounterMetricsHandler.handleHistoricalMetrics(catalog, metric, metricUnit);
			}
			if (log.isTraceEnabled()) {
				log.trace("Discard history metric '{}' for catalog: {}", metric, catalog);
			}
		});
	}

	@Override
	public SequentialMetricsCollector<CustomizedMetric<HttpStatusCounter>> createNewForHttpStatusCategory(Catalog catalog) {
		return new SimpleSequentialMetricsCollector<CustomizedMetric<HttpStatusCounter>>(bufferSize, span, spanUnit,
				(metric, metricUnit) -> {
					if (historicalCounterMetricsHandler != null) {
						historicalHttpStatusCounterMetricsHandler.handleHistoricalMetrics(catalog, metric, metricUnit);
					}
					if (log.isTraceEnabled()) {
						log.trace("Discard history metric '{}' for catalog: {}", metric, catalog);
					}
				});
	}

	@Override
	public Map<String, MetricBean> render(Map<Object, Object> entries) {
		Map<Object, Object> data = entries;
		Map<String, MetricBean> sequentialMap = sequentialMap();
		String datetime;
		for (Map.Entry<Object, Object> entry : data.entrySet()) {
			datetime = (String) entry.getKey();
			if (sequentialMap.containsKey(datetime)) {
				sequentialMap.put(datetime, (MetricBean) entry.getValue());
			}
		}
		return sequentialMap;
	}

	protected Map<String, MetricBean> sequentialMap() {
		Map<String, MetricBean> map = new LinkedHashMap<String, MetricBean>();
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(System.currentTimeMillis());
		c.set(Calendar.SECOND, 0);
		for (int i = 0; i < bufferSize; i++) {
			map.put(DateUtils.format(c.getTime(), SimpleSequentialMetricsCollector.DEFAULT_DATETIME_PATTERN), newMetric());
			c.add(Calendar.MINUTE, -1 * span);
		}
		return MapUtils.reverse(map);
	}

	private MetricBean newMetric() {
		MetricBean vo = new MetricBean();
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
