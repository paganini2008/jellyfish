package indi.atlantis.framework.jellyfish.http;

import indi.atlantis.framework.vortex.metric.BigInt;
import indi.atlantis.framework.vortex.metric.GenericUserMetricSequencer;

/**
 * 
 * MetricSequencerFactory
 * 
 * @author Fred Feng
 *
 * @version 1.0
 */
public interface MetricSequencerFactory {

	GenericUserMetricSequencer<Api, BigInt> getApiStatisticMetricSequencer();

	GenericUserMetricSequencer<Api, ApiCounter> getApiCounterMetricSequencer();

	GenericUserMetricSequencer<Api, HttpStatusCounter> getHttpStatusCounterMetricSequencer();

}
