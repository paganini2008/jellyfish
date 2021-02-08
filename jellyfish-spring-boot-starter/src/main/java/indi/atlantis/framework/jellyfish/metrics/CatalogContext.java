package indi.atlantis.framework.jellyfish.metrics;

import static indi.atlantis.framework.jellyfish.metrics.MetricNames.CC;
import static indi.atlantis.framework.jellyfish.metrics.MetricNames.COUNT;
import static indi.atlantis.framework.jellyfish.metrics.MetricNames.HTTP_STATUS;
import static indi.atlantis.framework.jellyfish.metrics.MetricNames.QPS;
import static indi.atlantis.framework.jellyfish.metrics.MetricNames.RT;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.github.paganini2008.devtools.collection.MapUtils;

import indi.atlantis.framework.vortex.common.NioClient;
import indi.atlantis.framework.vortex.common.Tuple;
import indi.atlantis.framework.vortex.utils.CustomizedMetric;
import indi.atlantis.framework.vortex.utils.StatisticalMetric;
import indi.atlantis.framework.vortex.utils.StatisticalMetrics;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * CatalogContext
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
@Slf4j
public final class CatalogContext {

	private final Map<Catalog, Summary> summary = new ConcurrentHashMap<Catalog, Summary>();
	private final CatalogMetricsCollector<StatisticalMetric> statisticCollector = new CatalogMetricsCollector<StatisticalMetric>();
	private final CatalogMetricsCollector<CustomizedMetric<Counter>> countingCollector = new CatalogMetricsCollector<CustomizedMetric<Counter>>();
	private final CatalogMetricsCollector<CustomizedMetric<HttpStatusCounter>> httpStatusCountingCollector = new CatalogMetricsCollector<CustomizedMetric<HttpStatusCounter>>();

	public List<Catalog> getCatalogs() {
		return new ArrayList<Catalog>(summary.keySet());
	}

	public Summary getSummary(Catalog catalog) {
		return MapUtils.get(summary, catalog, () -> new Summary());
	}

	public CatalogMetricsCollector<StatisticalMetric> statisticCollector() {
		return statisticCollector;
	}

	public CatalogMetricsCollector<CustomizedMetric<Counter>> countingCollector() {
		return countingCollector;
	}

	public CatalogMetricsCollector<CustomizedMetric<HttpStatusCounter>> httpStatusCountingCollector() {
		return httpStatusCountingCollector;
	}

	public void synchronizeHttpStatusCountingData(NioClient nioClient, SocketAddress remoteAddress, boolean incremental) {
		Map<String, CustomizedMetric<HttpStatusCounter>> data;
		int n = 0;
		for (Catalog catalog : summary.keySet()) {
			data = httpStatusCountingCollector.sequence(catalog, HTTP_STATUS);
			CustomizedMetric<HttpStatusCounter> customizedMetric;
			for (Map.Entry<String, CustomizedMetric<HttpStatusCounter>> entry : data.entrySet()) {
				customizedMetric = entry.getValue();
				Tuple tuple = getHttpStatusCountingTuple(catalog, HTTP_STATUS, customizedMetric, incremental);
				nioClient.send(remoteAddress, tuple);
				n++;
			}
		}
		if (log.isTraceEnabled()) {
			log.trace("[{}]Synchronize HttpStatus counting data completed. Effected numbers: {}", (incremental ? '+' : '-'), n);
		}

	}

	private Tuple getHttpStatusCountingTuple(Catalog catalog, String metric, CustomizedMetric<HttpStatusCounter> customizedMetric,
			boolean incremental) {
		Tuple tuple = Tuple
				.newOne(incremental ? IncrementalHttpStatusCountingSynchronizer.TOPIC_NAME : FullHttpStatusCountingSynchronizer.TOPIC_NAME);
		tuple.setField("clusterName", catalog.getClusterName());
		tuple.setField("applicationName", catalog.getApplicationName());
		tuple.setField("host", catalog.getHost());
		tuple.setField("category", catalog.getCategory());
		tuple.setField("path", catalog.getPath());
		tuple.setField("metric", metric);
		tuple.setField("incremental", incremental);

		HttpStatusCounter counter = customizedMetric.get();
		long countOf1xx = counter.getCountOf1xx();
		long countOf2xx = counter.getCountOf2xx();
		long countOf3xx = counter.getCountOf3xx();
		long countOf4xx = counter.getCountOf4xx();
		long countOf5xx = counter.getCountOf5xx();
		long timestamp = customizedMetric.getTimestamp();
		tuple.setField("countOf1xx", countOf1xx);
		tuple.setField("countOf2xx", countOf2xx);
		tuple.setField("countOf3xx", countOf3xx);
		tuple.setField("countOf4xx", countOf4xx);
		tuple.setField("countOf5xx", countOf5xx);
		tuple.setField("timestamp", timestamp);

		if (incremental) {
			httpStatusCountingCollector.update(catalog, metric, timestamp, new HttpStatusCountingMetric(
					new HttpStatusCounter(countOf1xx, countOf2xx, countOf3xx, countOf4xx, countOf5xx), timestamp, true));
		}
		return tuple;
	}

	public void synchronizeCountingData(NioClient nioClient, SocketAddress remoteAddress, boolean incremental) {
		int n = 0;
		Map<String, CustomizedMetric<Counter>> data;
		for (Catalog catalog : summary.keySet()) {
			data = countingCollector.sequence(catalog, COUNT);
			CustomizedMetric<Counter> customizedMetric;
			for (Map.Entry<String, CustomizedMetric<Counter>> entry : data.entrySet()) {
				customizedMetric = entry.getValue();
				Tuple tuple = getCountingTuple(catalog, COUNT, customizedMetric, incremental);
				nioClient.send(remoteAddress, tuple);
				n++;
			}
		}
		if (log.isTraceEnabled()) {
			log.trace("[{}]Synchronize counting data completed. Effected numbers: {}", (incremental ? '+' : '-'), n);
		}
	}

	private Tuple getCountingTuple(Catalog catalog, String metric, CustomizedMetric<Counter> customizedMetric, boolean incremental) {
		Tuple tuple = Tuple.newOne(incremental ? IncrementalCountingSynchronizer.TOPIC_NAME : FullCountingSynchronizer.TOPIC_NAME);
		tuple.setField("clusterName", catalog.getClusterName());
		tuple.setField("applicationName", catalog.getApplicationName());
		tuple.setField("host", catalog.getHost());
		tuple.setField("category", catalog.getCategory());
		tuple.setField("path", catalog.getPath());
		tuple.setField("metric", metric);
		tuple.setField("incremental", incremental);

		Counter counter = customizedMetric.get();
		long count = counter.getCount();
		long failedCount = counter.getFailedCount();
		long timeoutCount = counter.getTimeoutCount();
		long timestamp = customizedMetric.getTimestamp();
		tuple.setField("count", count);
		tuple.setField("failedCount", failedCount);
		tuple.setField("timeoutCount", timeoutCount);
		tuple.setField("timestamp", timestamp);

		if (incremental) {
			countingCollector.update(catalog, metric, customizedMetric.getTimestamp(),
					new CountingMetric(new Counter(count, failedCount, timeoutCount), timestamp, true));
		}
		return tuple;
	}

	public void synchronizeStatisticData(NioClient nioClient, SocketAddress remoteAddress, boolean incremental) {
		int n = 0;
		Map<String, StatisticalMetric> data;
		for (Catalog catalog : summary.keySet()) {
			data = statisticCollector.sequence(catalog, RT);
			n += doSyncStatisticData(catalog, RT, data, nioClient, remoteAddress, incremental);
			data = statisticCollector.sequence(catalog, CC);
			n += doSyncStatisticData(catalog, CC, data, nioClient, remoteAddress, incremental);
			data = statisticCollector.sequence(catalog, QPS);
			n += doSyncStatisticData(catalog, QPS, data, nioClient, remoteAddress, incremental);
		}
		if (log.isTraceEnabled()) {
			log.trace("[{}]Synchronize statistic data completed. Effected numbers: {}", (incremental ? '+' : '-'), n);
		}

	}

	private int doSyncStatisticData(Catalog catalog, String metric, Map<String, StatisticalMetric> data, NioClient nioClient,
			SocketAddress remoteAddress, boolean incremental) {
		int n = 0;
		StatisticalMetric statisticalMetric;
		for (Map.Entry<String, StatisticalMetric> entry : data.entrySet()) {
			statisticalMetric = entry.getValue();
			Tuple tuple = getStatisticTuple(catalog, metric, statisticalMetric, incremental);
			nioClient.send(remoteAddress, tuple);
			n++;
		}
		return n;
	}

	private Tuple getStatisticTuple(Catalog catalog, String metric, StatisticalMetric statisticalMetric, boolean incremental) {
		long highestValue = statisticalMetric.getHighestValue().longValue();
		long lowestValue = statisticalMetric.getLowestValue().longValue();
		long totalValue = statisticalMetric.getTotalValue().longValue();
		long count = statisticalMetric.getCount();
		long timestamp = statisticalMetric.getTimestamp();

		Tuple tuple = Tuple.newOne(incremental ? IncrementalStatisticSynchronizer.TOPIC_NAME : FullStatisticSynchronizer.TOPIC_NAME);
		tuple.setField("clusterName", catalog.getClusterName());
		tuple.setField("applicationName", catalog.getApplicationName());
		tuple.setField("host", catalog.getHost());
		tuple.setField("category", catalog.getCategory());
		tuple.setField("path", catalog.getPath());
		tuple.setField("metric", metric);
		tuple.setField("incremental", incremental);

		tuple.setField("highestValue", highestValue);
		tuple.setField("lowestValue", lowestValue);
		tuple.setField("totalValue", totalValue);
		tuple.setField("count", count);
		tuple.setField("timestamp", timestamp);

		if (incremental) {
			statisticCollector.update(catalog, metric, timestamp,
					new StatisticalMetrics.LongMetric(highestValue, lowestValue, totalValue, count, timestamp, true));
		}
		return tuple;
	}

	public void synchronizeSummaryData(NioClient nioClient, SocketAddress remoteAddress, boolean incremental) {
		int n = 0;
		Catalog catalog;
		Summary catalogSummary;
		for (Map.Entry<Catalog, Summary> entry : summary.entrySet()) {
			catalog = entry.getKey();
			catalogSummary = entry.getValue();
			Tuple tuple = getSummaryTuple(catalog, catalogSummary, incremental);
			nioClient.send(remoteAddress, tuple);
			n++;
		}
		if (log.isTraceEnabled()) {
			log.trace("[{}]Synchronize summary data completed. Effected numbers: {}", (incremental ? '+' : '-'), n);
		}
	}

	private Tuple getSummaryTuple(Catalog catalog, Summary catalogSummary, boolean incremental) {
		Tuple tuple = Tuple.newOne(incremental ? IncrementalSummarySynchronizer.TOPIC_NAME : FullSummarySynchronizer.TOPIC_NAME);
		tuple.setField("clusterName", catalog.getClusterName());
		tuple.setField("applicationName", catalog.getApplicationName());
		tuple.setField("host", catalog.getHost());
		tuple.setField("category", catalog.getCategory());
		tuple.setField("path", catalog.getPath());
		tuple.setField("incremental", incremental);

		long count = catalogSummary.getTotalExecutionCount();
		long failedCount = catalogSummary.getFailedExecutionCount();
		long timeoutCount = catalogSummary.getTimeoutExecutionCount();
		tuple.setField("count", count);
		tuple.setField("failedCount", failedCount);
		tuple.setField("timeoutCount", timeoutCount);
		if (incremental) {
			catalogSummary.reset(new Counter(count, failedCount, timeoutCount));
		}
		
		long countOf1xx = catalogSummary.getCountOf1xx();
		long countOf2xx = catalogSummary.getCountOf2xx();
		long countOf3xx = catalogSummary.getCountOf3xx();
		long countOf4xx = catalogSummary.getCountOf4xx();
		long countOf5xx = catalogSummary.getCountOf5xx();
		tuple.setField("countOf1xx", countOf1xx);
		tuple.setField("countOf2xx", countOf2xx);
		tuple.setField("countOf3xx", countOf3xx);
		tuple.setField("countOf4xx", countOf4xx);
		tuple.setField("countOf5xx", countOf5xx);
		if (incremental) {
			catalogSummary.reset(new HttpStatusCounter(countOf1xx, countOf2xx, countOf3xx, countOf4xx, countOf5xx));
		}
		return tuple;
	}

}
