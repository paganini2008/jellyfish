package org.springtribe.framework.jellyfish.stat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springtribe.framework.gearless.Handler;
import org.springtribe.framework.gearless.common.Tuple;

import com.github.paganini2008.devtools.collection.MetricUnits;
import com.github.paganini2008.devtools.collection.SequentialMetricsCollector;

/**
 * 
 * RealtimeStatisticHandler
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public class RealtimeStatisticHandler implements Handler {

	@Autowired
	private TransientStatisticSynchronizer transientStatisticSynchronizer;

	@Override
	public void onData(Tuple tuple) {
		long elapsed = tuple.getField("elapsed", Long.class);
		long concurrency = tuple.getField("concurrency", Long.class);
		long timestamp = tuple.getField("requestTime", Long.class);
		boolean failed = tuple.getField("failed", Boolean.class);
		boolean timeout = tuple.getField("timeout", Boolean.class);
		SequentialMetricsCollector sequentialMetricsCollector = transientStatisticSynchronizer.getMetricsCollector(Catalog.of(tuple));
		sequentialMetricsCollector.set("rt", timestamp, RealtimeMetricUnit.valueOf(elapsed, failed, timeout));
		sequentialMetricsCollector.set("cons", timestamp, MetricUnits.valueOf(concurrency));
	}

	@Override
	public String getTopic() {
		return "org.springtribe.framework.jellyfish.monitor.RealtimeStatisticalWriter";
	}

}
