package indi.atlantis.framework.jellyfish.ui;

import static indi.atlantis.framework.jellyfish.metrics.MetricNames.CC;
import static indi.atlantis.framework.jellyfish.metrics.MetricNames.COUNT;
import static indi.atlantis.framework.jellyfish.metrics.MetricNames.HTTP_STATUS;
import static indi.atlantis.framework.jellyfish.metrics.MetricNames.QPS;
import static indi.atlantis.framework.jellyfish.metrics.MetricNames.RT;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.paganini2008.devtools.collection.MapUtils;

import indi.atlantis.framework.jellyfish.metrics.Catalog;
import indi.atlantis.framework.jellyfish.metrics.Counter;
import indi.atlantis.framework.jellyfish.metrics.Environment;
import indi.atlantis.framework.jellyfish.metrics.HttpStatusCounter;
import indi.atlantis.framework.jellyfish.metrics.Summary;
import indi.atlantis.framework.vortex.sequence.DataRenderer;
import indi.atlantis.framework.vortex.sequence.MetricSequencer;
import indi.atlantis.framework.vortex.sequence.NumberMetric;
import indi.atlantis.framework.vortex.sequence.UserMetric;

/**
 * 
 * CatalogController
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
@RequestMapping("/atlantis/jellyfish/catalog")
@RestController
public class CatalogController {

	@Value("${atlantis.framework.jellyfish.ui.metric.sort:true}")
	private boolean asc;

	@Qualifier("secondaryEnviroment")
	@Autowired
	private Environment environment;

	@GetMapping("/list")
	public Response pathList(@RequestParam(name = "level", required = false, defaultValue = "0") int level) {
		List<Catalog> catalogs = environment.getCatalogs();
		catalogs = catalogs.stream().filter(c -> c.getLevel() == level).collect(Collectors.toList());
		Collections.sort(catalogs);
		return Response.success(catalogs);
	}

	@PostMapping("/summary")
	public Response summary(@RequestBody Catalog catalog) {
		Summary summary = environment.getSummary(catalog);
		return summary != null ? Response.success(summary.toEntries()) : Response.success();
	}

	@PostMapping("/{metric}/summary")
	public Response metricSummary(@PathVariable("metric") String metric, @RequestBody Catalog catalog) {
		Map<String, Map<String, Object>> data = fetchMerticData(catalog, metric);
		return Response.success(data);
	}

	private Map<String, Map<String, Object>> fetchMerticData(Catalog catalog, String metric) {
		Map<String, Map<String, Object>> data = new LinkedHashMap<String, Map<String, Object>>();
		long timestamp = 0;
		Date startTime = null;
		switch (metric) {
		case RT:
		case CC:
		case QPS:
			MetricSequencer<Catalog, NumberMetric<Long>> longSequencer = environment.longMetricSequencer();
			Map<String, NumberMetric<Long>> sequence = longSequencer.sequence(catalog, metric);
			for (Map.Entry<String, NumberMetric<Long>> entry : sequence.entrySet()) {
				data.put(entry.getKey(), entry.getValue().toEntries());
				timestamp = timestamp > 0 ? Math.min(entry.getValue().getTimestamp(), timestamp) : entry.getValue().getTimestamp();
			}
			startTime = asc ? new Date(timestamp) : new Date();
			return DataRenderer.renderNumberMetric(data, startTime, asc, longSequencer.getSpanUnit(), longSequencer.getSpan(),
					longSequencer.getBufferSize());
		case COUNT:
			MetricSequencer<Catalog, UserMetric<Counter>> countingSequencer = environment.countingMetricSequencer();
			Map<String, UserMetric<Counter>> countingSequence = countingSequencer.sequence(catalog, metric);
			for (Map.Entry<String, UserMetric<Counter>> entry : countingSequence.entrySet()) {
				data.put(entry.getKey(), entry.getValue().toEntries());
			}
			startTime = asc ? new Date(timestamp) : new Date();
			return DataRenderer.renderNumberMetric(data, startTime, asc, countingSequencer.getSpanUnit(), countingSequencer.getSpan(),
					countingSequencer.getBufferSize());
		case HTTP_STATUS:
			MetricSequencer<Catalog, UserMetric<HttpStatusCounter>> httpStatusCountingSequencer = environment
					.httpStatusCountingMetricSequencer();
			Map<String, UserMetric<HttpStatusCounter>> httpStatusCountingSequence = httpStatusCountingSequencer.sequence(catalog, metric);
			for (Map.Entry<String, UserMetric<HttpStatusCounter>> entry : httpStatusCountingSequence.entrySet()) {
				data.put(entry.getKey(), entry.getValue().toEntries());
			}
			startTime = asc ? new Date(timestamp) : new Date();
			return DataRenderer.renderNumberMetric(data, startTime, asc, httpStatusCountingSequencer.getSpanUnit(),
					httpStatusCountingSequencer.getSpan(), httpStatusCountingSequencer.getBufferSize());
		}
		return MapUtils.emptyMap();
	}

}
