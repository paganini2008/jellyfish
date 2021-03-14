package indi.atlantis.framework.jellyfish.http;

import java.net.SocketAddress;
import java.util.Map;

import indi.atlantis.framework.vortex.common.NioClient;
import indi.atlantis.framework.vortex.common.Tuple;
import indi.atlantis.framework.vortex.metric.BigInt;
import indi.atlantis.framework.vortex.metric.BigIntMetric;
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
		environment.getCounterMetricSequencer().scan((catalog, metric, data) -> {
			for (Map.Entry<String, UserMetric<ApiCounter>> entry : data.entrySet()) {
				Tuple tuple = forCounter(catalog, metric, entry.getValue());
				nioClient.send(remoteAddress, tuple);
			}
		});
		environment.getHttpStatusCounterMetricSequencer().scan((catalog, metric, data) -> {
			for (Map.Entry<String, UserMetric<HttpStatusCounter>> entry : data.entrySet()) {
				Tuple tuple = forHttpStatusCounter(catalog, metric, entry.getValue());
				nioClient.send(remoteAddress, tuple);
			}
		});
		environment.getBigIntMetricSequencer().scan((catalog, metric, data) -> {
			for (Map.Entry<String, UserMetric<BigInt>> entry : data.entrySet()) {
				Tuple tuple = forBigInt(catalog, metric, entry.getValue());
				nioClient.send(remoteAddress, tuple);
			}
		});
		log.trace("Statistic synchronization end");
	}

	private Tuple forCounter(Catalog catalog, String metric, UserMetric<ApiCounter> metricUnit) {
		Tuple tuple = Tuple.newOne(topic);
		tuple.setField("clusterName", catalog.getClusterName());
		tuple.setField("applicationName", catalog.getApplicationName());
		tuple.setField("host", catalog.getHost());
		tuple.setField("category", catalog.getCategory());
		tuple.setField("path", catalog.getPath());
		tuple.setField("metric", metric);

		ApiCounter counter = metricUnit.get();
		long count = counter.getCount();
		long failedCount = counter.getFailedCount();
		long timeoutCount = counter.getTimeoutCount();
		long timestamp = metricUnit.getTimestamp();
		tuple.setField("count", count);
		tuple.setField("failedCount", failedCount);
		tuple.setField("timeoutCount", timeoutCount);
		tuple.setField("timestamp", timestamp);

		if (incremental) {
			environment.getCounterMetricSequencer().update(catalog, metric, timestamp,
					new ApiCounterMetric(new ApiCounter(count, failedCount, timeoutCount), timestamp).resettable());
		}
		return tuple;
	}

	private Tuple forHttpStatusCounter(Catalog catalog, String metric, UserMetric<HttpStatusCounter> metricUnit) {
		Tuple tuple = Tuple.newOne(topic);
		tuple.setField("clusterName", catalog.getClusterName());
		tuple.setField("applicationName", catalog.getApplicationName());
		tuple.setField("host", catalog.getHost());
		tuple.setField("category", catalog.getCategory());
		tuple.setField("path", catalog.getPath());
		tuple.setField("metric", metric);

		HttpStatusCounter counter = metricUnit.get();
		long countOf1xx = counter.getCountOf1xx();
		long countOf2xx = counter.getCountOf2xx();
		long countOf3xx = counter.getCountOf3xx();
		long countOf4xx = counter.getCountOf4xx();
		long countOf5xx = counter.getCountOf5xx();
		long timestamp = metricUnit.getTimestamp();
		tuple.setField("countOf1xx", countOf1xx);
		tuple.setField("countOf2xx", countOf2xx);
		tuple.setField("countOf3xx", countOf3xx);
		tuple.setField("countOf4xx", countOf4xx);
		tuple.setField("countOf5xx", countOf5xx);
		tuple.setField("timestamp", timestamp);

		if (incremental) {
			environment.getHttpStatusCounterMetricSequencer().update(catalog, metric, timestamp,
					new HttpStatusCounterMetric(new HttpStatusCounter(countOf1xx, countOf2xx, countOf3xx, countOf4xx, countOf5xx),
							timestamp).resettable());
		}
		return tuple;
	}

	private Tuple forBigInt(Catalog catalog, String metric, UserMetric<BigInt> metricUnit) {

		Tuple tuple = Tuple.newOne(topic);
		tuple.setField("clusterName", catalog.getClusterName());
		tuple.setField("applicationName", catalog.getApplicationName());
		tuple.setField("host", catalog.getHost());
		tuple.setField("category", catalog.getCategory());
		tuple.setField("path", catalog.getPath());
		tuple.setField("metric", metric);

		BigInt bigInt = metricUnit.get();
		long highestValue = bigInt.getHighestValue();
		long lowestValue = bigInt.getLowestValue();
		long totalValue = bigInt.getTotalValue();
		long count = bigInt.getCount();
		long timestamp = metricUnit.getTimestamp();
		tuple.setField("highestValue", highestValue);
		tuple.setField("lowestValue", lowestValue);
		tuple.setField("totalValue", totalValue);
		tuple.setField("count", count);
		tuple.setField("timestamp", timestamp);

		if (incremental) {
			environment.getBigIntMetricSequencer().update(catalog, metric, timestamp,
					new BigIntMetric(new BigInt(highestValue, lowestValue, totalValue, count), timestamp).resettable());
		}
		return tuple;
	}

}
