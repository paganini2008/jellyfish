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
 * ApiSummarySynchronizer
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
@Slf4j
public class ApiSummarySynchronizer implements Synchronizer {

	private final String topic;
	private final Environment environment;
	private final boolean incremental;

	public ApiSummarySynchronizer(String topic, Environment environment, boolean incremental) {
		this.topic = topic;
		this.environment = environment;
		this.incremental = incremental;
	}

	@Override
	public void synchronize(NioClient nioClient, SocketAddress remoteAddress) {
		log.trace("Summary synchronization begin...");
		environment.getCatalogs().forEach(catalog -> {
			ApiSummary summary = environment.getSummary(catalog);
			for (Map.Entry<String, UserMetric<ApiCounter>> entry : summary.getCounterMetricCollector().all().entrySet()) {
				Tuple tuple = forCounter(catalog, entry.getKey(), entry.getValue(), summary);
				nioClient.send(remoteAddress, tuple);
			}
			for (Map.Entry<String, UserMetric<HttpStatusCounter>> entry : summary.getHttpStatusCounterMetricCollector().all().entrySet()) {
				Tuple tuple = forHttpStatusCounter(catalog, entry.getKey(), entry.getValue(), summary);
				nioClient.send(remoteAddress, tuple);
			}
			for (Map.Entry<String, UserMetric<BigInt>> entry : summary.getBigIntMetricCollector().all().entrySet()) {
				Tuple tuple = forBigInt(catalog, entry.getKey(), entry.getValue(), summary);
				nioClient.send(remoteAddress, tuple);
			}
		});
		log.trace("Summary synchronization end.");
	}

	private Tuple forHttpStatusCounter(Catalog catalog, String metric, UserMetric<HttpStatusCounter> metricUnit, ApiSummary summary) {
		Tuple tuple = Tuple.newOne(topic);
		tuple.setField("clusterName", catalog.getClusterName());
		tuple.setField("applicationName", catalog.getApplicationName());
		tuple.setField("host", catalog.getHost());
		tuple.setField("category", catalog.getCategory());
		tuple.setField("path", catalog.getPath());
		tuple.setField("metric", metric);

		long timestamp = metricUnit.getTimestamp();
		HttpStatusCounter counter = metricUnit.get();
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

		if (incremental) {
			summary.getHttpStatusCounterMetricCollector().set(metric,
					new HttpStatusCounterMetric(new HttpStatusCounter(countOf1xx, countOf2xx, countOf3xx, countOf4xx, countOf5xx),
							timestamp).resettable(),
					true);
		}
		return tuple;
	}

	private Tuple forCounter(Catalog catalog, String metric, UserMetric<ApiCounter> metricUnit, ApiSummary summary) {
		Tuple tuple = Tuple.newOne(topic);
		tuple.setField("clusterName", catalog.getClusterName());
		tuple.setField("applicationName", catalog.getApplicationName());
		tuple.setField("host", catalog.getHost());
		tuple.setField("category", catalog.getCategory());
		tuple.setField("path", catalog.getPath());
		tuple.setField("metric", metric);

		long timestamp = metricUnit.getTimestamp();
		ApiCounter counter = metricUnit.get();
		long count = counter.getCount();
		long failedCount = counter.getFailedCount();
		long timeoutCount = counter.getTimeoutCount();
		tuple.setField("count", count);
		tuple.setField("failedCount", failedCount);
		tuple.setField("timeoutCount", timeoutCount);
		tuple.setField("timestamp", timestamp);

		if (incremental) {
			summary.getCounterMetricCollector().set(metric,
					new ApiCounterMetric(new ApiCounter(count, failedCount, timeoutCount), timestamp).resettable(), true);
		}
		return tuple;
	}

	private Tuple forBigInt(Catalog catalog, String metric, UserMetric<BigInt> metricUnit, ApiSummary summary) {
		Tuple tuple = Tuple.newOne(topic);
		tuple.setField("clusterName", catalog.getClusterName());
		tuple.setField("applicationName", catalog.getApplicationName());
		tuple.setField("host", catalog.getHost());
		tuple.setField("category", catalog.getCategory());
		tuple.setField("path", catalog.getPath());
		tuple.setField("metric", metric);

		BigInt bigInt = metricUnit.get();
		long timestamp = metricUnit.getTimestamp();
		long highestValue = bigInt.getHighestValue();
		long lowestValue = bigInt.getLowestValue();
		long totalValue = bigInt.getTotalValue();
		long count = bigInt.getCount();
		tuple.setField("highestValue", highestValue);
		tuple.setField("lowestValue", lowestValue);
		tuple.setField("totalValue", totalValue);
		tuple.setField("count", count);
		tuple.setField("timestamp", timestamp);

		if (incremental) {
			summary.getBigIntMetricCollector().set(metric,
					new BigIntMetric(new BigInt(highestValue, lowestValue, totalValue, count), timestamp).resettable(), true);
		}
		return tuple;
	}

}
