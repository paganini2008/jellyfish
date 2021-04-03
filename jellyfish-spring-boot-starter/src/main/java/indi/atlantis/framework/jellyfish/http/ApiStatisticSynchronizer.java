package indi.atlantis.framework.jellyfish.http;

import java.net.SocketAddress;
import java.util.Map;

import indi.atlantis.framework.vortex.common.NioClient;
import indi.atlantis.framework.vortex.common.Tuple;
import indi.atlantis.framework.vortex.metric.BigInt;
import indi.atlantis.framework.vortex.metric.BigIntMetric;
import indi.atlantis.framework.vortex.metric.GenericUserMetricSequencer;
import indi.atlantis.framework.vortex.metric.Synchronizer;
import indi.atlantis.framework.vortex.metric.UserMetric;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * StatisticSynchronizer
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
@Slf4j
public class ApiStatisticSynchronizer implements Synchronizer {

	private final String topic;
	private final Environment environment;
	private final boolean incremental;

	public ApiStatisticSynchronizer(String topic, Environment environment, boolean incremental) {
		this.topic = topic;
		this.environment = environment;
		this.incremental = incremental;
	}

	@Override
	public void synchronize(NioClient nioClient, SocketAddress remoteAddress) {
		log.trace("Statistic synchronization begin...");
		GenericUserMetricSequencer<Api, ApiCounter> apiCounterSequencer = environment.getApiCounterMetricSequencer();
		apiCounterSequencer.scan((api, metric, data) -> {
			ApiCounter apiCounter;
			long timestamp;
			for (Map.Entry<String, UserMetric<ApiCounter>> entry : data.entrySet()) {
				apiCounter = entry.getValue().get();
				timestamp = entry.getValue().getTimestamp();
				Tuple tuple = synchronizeApiCounter(api, metric, apiCounter, timestamp);
				nioClient.send(remoteAddress, tuple);
				if (incremental) {
					apiCounterSequencer.update(api, metric, timestamp, new ApiCounterMetric(apiCounter.clone(), timestamp).resettable(),
							true);
				}
			}
		});
		GenericUserMetricSequencer<Api, HttpStatusCounter> httpStatusCounterSequencer = environment
				.getHttpStatusCounterMetricSequencer();
		httpStatusCounterSequencer.scan((api, metric, data) -> {
			HttpStatusCounter httpStatusCounter;
			long timestamp;
			for (Map.Entry<String, UserMetric<HttpStatusCounter>> entry : data.entrySet()) {
				httpStatusCounter = entry.getValue().get();
				timestamp = entry.getValue().getTimestamp();
				Tuple tuple = synchronizeHttpStatusCounter(api, metric, httpStatusCounter, timestamp);
				nioClient.send(remoteAddress, tuple);
				if (incremental) {
					httpStatusCounterSequencer.update(api, metric, timestamp,
							new HttpStatusCounterMetric(httpStatusCounter.clone(), timestamp).resettable(), true);
				}
			}
		});
		GenericUserMetricSequencer<Api, BigInt> apiStatisticSequencer = environment.getApiStatisticMetricSequencer();
		apiStatisticSequencer.scan((api, metric, data) -> {
			BigInt bigInt;
			long timestamp;
			for (Map.Entry<String, UserMetric<BigInt>> entry : data.entrySet()) {
				bigInt = entry.getValue().get();
				timestamp = entry.getValue().getTimestamp();
				Tuple tuple = synchronizeBigInt(api, metric, bigInt, timestamp);
				nioClient.send(remoteAddress, tuple);
				if (incremental) {
					apiStatisticSequencer.update(api, metric, timestamp, new BigIntMetric(bigInt.clone(), timestamp).resettable(),
							true);
				}
			}
		});
		log.trace("Statistic synchronization end");
	}

	private Tuple synchronizeApiCounter(Api api, String metric, ApiCounter apiCounter, long timestamp) {
		Tuple tuple = Tuple.newOne(topic);
		tuple.setField("clusterName", api.getClusterName());
		tuple.setField("applicationName", api.getApplicationName());
		tuple.setField("host", api.getHost());
		tuple.setField("category", api.getCategory());
		tuple.setField("path", api.getPath());
		tuple.setField("metric", metric);

		tuple.setField("count", apiCounter.getCount());
		tuple.setField("failedCount", apiCounter.getFailedCount());
		tuple.setField("timeoutCount", apiCounter.getTimeoutCount());
		tuple.setField("timestamp", timestamp);
		return tuple;
	}

	private Tuple synchronizeHttpStatusCounter(Api api, String metric, HttpStatusCounter counter, long timestamp) {
		Tuple tuple = Tuple.newOne(topic);
		tuple.setField("clusterName", api.getClusterName());
		tuple.setField("applicationName", api.getApplicationName());
		tuple.setField("host", api.getHost());
		tuple.setField("category", api.getCategory());
		tuple.setField("path", api.getPath());
		tuple.setField("metric", metric);

		long countOf1xx = counter.getCountOf1xx();
		long countOf2xx = counter.getCountOf2xx();
		long countOf3xx = counter.getCountOf3xx();
		long countOf4xx = counter.getCountOf4xx();
		long countOf5xx = counter.getCountOf5xx();
		tuple.setField("countOf1xx", countOf1xx);
		tuple.setField("countOf2xx", countOf2xx);
		tuple.setField("countOf3xx", countOf3xx);
		tuple.setField("countOf4xx", countOf4xx);
		tuple.setField("countOf5xx", countOf5xx);
		tuple.setField("timestamp", timestamp);
		return tuple;
	}

	private Tuple synchronizeBigInt(Api api, String metric, BigInt bigInt, long timestamp) {

		Tuple tuple = Tuple.newOne(topic);
		tuple.setField("clusterName", api.getClusterName());
		tuple.setField("applicationName", api.getApplicationName());
		tuple.setField("host", api.getHost());
		tuple.setField("category", api.getCategory());
		tuple.setField("path", api.getPath());
		tuple.setField("metric", metric);

		long highestValue = bigInt.getHighestValue();
		long lowestValue = bigInt.getLowestValue();
		long totalValue = bigInt.getTotalValue();
		long count = bigInt.getCount();
		tuple.setField("highestValue", highestValue);
		tuple.setField("lowestValue", lowestValue);
		tuple.setField("totalValue", totalValue);
		tuple.setField("count", count);
		tuple.setField("timestamp", timestamp);
		return tuple;
	}

}
