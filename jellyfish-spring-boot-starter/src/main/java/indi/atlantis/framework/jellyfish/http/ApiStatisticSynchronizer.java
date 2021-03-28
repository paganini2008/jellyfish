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
		GenericUserMetricSequencer<Catalog, ApiCounter> apiCounterSequencer = environment.getApiCounterMetricSequencer();
		apiCounterSequencer.scan((catalog, metric, data) -> {
			ApiCounter apiCounter;
			long timestamp;
			for (Map.Entry<String, UserMetric<ApiCounter>> entry : data.entrySet()) {
				apiCounter = entry.getValue().get();
				timestamp = entry.getValue().getTimestamp();
				Tuple tuple = synchronizeApiCounter(catalog, metric, apiCounter, timestamp);
				nioClient.send(remoteAddress, tuple);
				if (incremental) {
					apiCounterSequencer.update(catalog, metric, timestamp,
							new ApiCounterMetric(
									new ApiCounter(apiCounter.getCount(), apiCounter.getFailedCount(), apiCounter.getTimeoutCount()),
									timestamp).resettable(),
							true);
				}
			}
		});
		GenericUserMetricSequencer<Catalog, HttpStatusCounter> httpStatusCounterSequencer = environment
				.getHttpStatusCounterMetricSequencer();
		httpStatusCounterSequencer.scan((catalog, metric, data) -> {
			HttpStatusCounter httpStatusCounter;
			long timestamp;
			for (Map.Entry<String, UserMetric<HttpStatusCounter>> entry : data.entrySet()) {
				httpStatusCounter = entry.getValue().get();
				timestamp = entry.getValue().getTimestamp();
				Tuple tuple = synchronizeHttpStatusCounter(catalog, metric, httpStatusCounter, timestamp);
				nioClient.send(remoteAddress, tuple);
				if (incremental) {
					httpStatusCounterSequencer.update(catalog, metric, timestamp,
							new HttpStatusCounterMetric(new HttpStatusCounter(httpStatusCounter.getCountOf1xx(),
									httpStatusCounter.getCountOf2xx(), httpStatusCounter.getCountOf3xx(), httpStatusCounter.getCountOf4xx(),
									httpStatusCounter.getCountOf5xx()), timestamp).resettable(),
							true);
				}
			}
		});
		GenericUserMetricSequencer<Catalog, BigInt> apiStatisticSequencer = environment.getApiStatisticMetricSequencer();
		apiStatisticSequencer.scan((catalog, metric, data) -> {
			BigInt bigInt;
			long timestamp;
			for (Map.Entry<String, UserMetric<BigInt>> entry : data.entrySet()) {
				bigInt = entry.getValue().get();
				timestamp = entry.getValue().getTimestamp();
				Tuple tuple = synchronizeBigInt(catalog, metric, bigInt, timestamp);
				nioClient.send(remoteAddress, tuple);
				if (incremental) {
					apiStatisticSequencer.update(catalog, metric, timestamp, new BigIntMetric(
							new BigInt(bigInt.getHighestValue(), bigInt.getLowestValue(), bigInt.getTotalValue(), bigInt.getCount()),
							timestamp).resettable(), true);
				}
			}
		});
		log.trace("Statistic synchronization end");
	}

	private Tuple synchronizeApiCounter(Catalog catalog, String metric, ApiCounter apiCounter, long timestamp) {
		Tuple tuple = Tuple.newOne(topic);
		tuple.setField("clusterName", catalog.getClusterName());
		tuple.setField("applicationName", catalog.getApplicationName());
		tuple.setField("host", catalog.getHost());
		tuple.setField("category", catalog.getCategory());
		tuple.setField("path", catalog.getPath());
		tuple.setField("metric", metric);

		tuple.setField("count", apiCounter.getCount());
		tuple.setField("failedCount", apiCounter.getFailedCount());
		tuple.setField("timeoutCount", apiCounter.getTimeoutCount());
		tuple.setField("timestamp", timestamp);
		return tuple;
	}

	private Tuple synchronizeHttpStatusCounter(Catalog catalog, String metric, HttpStatusCounter counter, long timestamp) {
		Tuple tuple = Tuple.newOne(topic);
		tuple.setField("clusterName", catalog.getClusterName());
		tuple.setField("applicationName", catalog.getApplicationName());
		tuple.setField("host", catalog.getHost());
		tuple.setField("category", catalog.getCategory());
		tuple.setField("path", catalog.getPath());
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

	private Tuple synchronizeBigInt(Catalog catalog, String metric, BigInt bigInt, long timestamp) {

		Tuple tuple = Tuple.newOne(topic);
		tuple.setField("clusterName", catalog.getClusterName());
		tuple.setField("applicationName", catalog.getApplicationName());
		tuple.setField("host", catalog.getHost());
		tuple.setField("category", catalog.getCategory());
		tuple.setField("path", catalog.getPath());
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
