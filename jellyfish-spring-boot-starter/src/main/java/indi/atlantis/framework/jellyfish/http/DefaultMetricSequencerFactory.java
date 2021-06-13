package indi.atlantis.framework.jellyfish.http;

import indi.atlantis.framework.vortex.metric.BigInt;
import indi.atlantis.framework.vortex.metric.GenericUserMetricSequencer;
import indi.atlantis.framework.vortex.metric.LoggingMetricEvictionHandler;

/**
 * 
 * DefaultMetricSequencerFactory
 * 
 * @author Fred Feng
 *
 * @version 1.0
 */
public class DefaultMetricSequencerFactory implements MetricSequencerFactory {

	@Override
	public GenericUserMetricSequencer<Api, BigInt> getApiStatisticMetricSequencer() {
		return new ApiStatisticMetricSequencer(new LoggingMetricEvictionHandler<>());
	}

	@Override
	public GenericUserMetricSequencer<Api, ApiCounter> getApiCounterMetricSequencer() {
		return new ApiCounterMetricSequencer(new LoggingMetricEvictionHandler<>());
	}

	@Override
	public GenericUserMetricSequencer<Api, HttpStatusCounter> getHttpStatusCounterMetricSequencer() {
		return new HttpStatusCounterMetricSequencer(new LoggingMetricEvictionHandler<>());
	}

}
