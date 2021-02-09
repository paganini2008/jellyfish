package indi.atlantis.framework.jellyfish.ui;

import static indi.atlantis.framework.jellyfish.metrics.MetricNames.CC;
import static indi.atlantis.framework.jellyfish.metrics.MetricNames.COUNT;
import static indi.atlantis.framework.jellyfish.metrics.MetricNames.HTTP_STATUS;
import static indi.atlantis.framework.jellyfish.metrics.MetricNames.QPS;
import static indi.atlantis.framework.jellyfish.metrics.MetricNames.RT;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.paganini2008.devtools.collection.MapUtils;

import indi.atlantis.framework.jellyfish.metrics.Catalog;
import indi.atlantis.framework.jellyfish.metrics.CatalogMetricCollector;
import indi.atlantis.framework.jellyfish.metrics.CatalogMetricContext;
import indi.atlantis.framework.jellyfish.metrics.Counter;
import indi.atlantis.framework.jellyfish.metrics.HttpStatusCounter;
import indi.atlantis.framework.jellyfish.metrics.Summary;
import indi.atlantis.framework.vortex.utils.CustomizedMetric;
import indi.atlantis.framework.vortex.utils.StatisticalMetric;

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

	@Qualifier("secondaryCatalogMetricContext")
	@Autowired
	private CatalogMetricContext catalogMetricContext;

	@GetMapping("/list")
	public Response pathList(@RequestParam(name = "level", required = false, defaultValue = "0") int level) {
		List<Catalog> catalogs = catalogMetricContext.getCatalogs();
		catalogs = catalogs.stream().filter(c -> c.getLevel() == level).collect(Collectors.toList());
		Collections.sort(catalogs);
		return Response.success(catalogs);
	}

	@PostMapping("/summary")
	public Response summary(@RequestBody Catalog catalog) {
		Summary catalogSummary = catalogMetricContext.getSummary(catalog);
		return catalogSummary != null ? Response.success(catalogSummary.toEntries()) : Response.success();
	}

	@PostMapping("/{metric}/summary")
	public Response metricSummary(@PathVariable("metric") String metric, @RequestBody Catalog catalog) {
		Map<String, Object> data = fetchMerticData(catalog, metric);
		return Response.success(data);
	}

	private Map<String, Object> fetchMerticData(Catalog catalog, String metric) {
		Map<String, Object> data = new LinkedHashMap<String, Object>();
		switch (metric) {
		case RT:
		case CC:
		case QPS:
			CatalogMetricCollector<StatisticalMetric> collector = catalogMetricContext.statisticCollector();
			Map<String, StatisticalMetric> sequence = collector.sequence(catalog, metric);
			for (Map.Entry<String, StatisticalMetric> entry : sequence.entrySet()) {
				data.put(entry.getKey(), entry.getValue().toEntries());
			}
			if (data.size() > 0) {
				data = ChartDataRender.render(data, collector.getSpanUnit(), collector.getSpan(), collector.getBufferSize(), () -> {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("highestValue", 0L);
					map.put("middleValue", 0L);
					map.put("lowestValue", 0L);
					map.put("count", 0);
					return map;
				});
			}
			return data;
		case COUNT:
			CatalogMetricCollector<CustomizedMetric<Counter>> countingCollector = catalogMetricContext.countingCollector();
			Map<String, CustomizedMetric<Counter>> countingSequence = countingCollector.sequence(catalog, metric);
			for (Map.Entry<String, CustomizedMetric<Counter>> entry : countingSequence.entrySet()) {
				data.put(entry.getKey(), entry.getValue().get());
			}
			if (data.size() > 0) {
				data = ChartDataRender.render(data, countingCollector.getSpanUnit(), countingCollector.getSpan(),
						countingCollector.getBufferSize(), () -> new Counter());
			}
			return data;
		case HTTP_STATUS:
			CatalogMetricCollector<CustomizedMetric<HttpStatusCounter>> httpStatusCountingCollector = catalogMetricContext
					.httpStatusCountingCollector();
			Map<String, CustomizedMetric<HttpStatusCounter>> httpStatusCountingSequence = httpStatusCountingCollector.sequence(catalog,
					metric);
			for (Map.Entry<String, CustomizedMetric<HttpStatusCounter>> entry : httpStatusCountingSequence.entrySet()) {
				data.put(entry.getKey(), entry.getValue().get());
			}
			if (data.size() > 0) {
				data = ChartDataRender.render(data, httpStatusCountingCollector.getSpanUnit(), httpStatusCountingCollector.getSpan(),
						httpStatusCountingCollector.getBufferSize(), () -> new HttpStatusCounter());
			}
			return data;
		}
		return MapUtils.emptyMap();
	}

}
