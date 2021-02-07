package indi.atlantis.framework.jellyfish.ui;

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

import indi.atlantis.framework.gearless.utils.StatisticalMetric;
import indi.atlantis.framework.jellyfish.metrics.Catalog;
import indi.atlantis.framework.jellyfish.metrics.CatalogContext;
import indi.atlantis.framework.jellyfish.metrics.CatalogMetricsCollector;
import indi.atlantis.framework.jellyfish.metrics.CatalogSummary;

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

	@Qualifier("primaryCatalogContext")
	@Autowired
	private CatalogContext catalogContext;

	@GetMapping("/list")
	public Response pathList(@RequestParam(name = "level", required = false, defaultValue = "0") int level) {
		List<Catalog> catalogs = catalogContext.getCatalogs();
		catalogs = catalogs.stream().filter(c -> c.getLevel() == level).collect(Collectors.toList());
		Collections.sort(catalogs);
		return Response.success(catalogs);
	}

	@PostMapping("/summary")
	public Response summary(@RequestBody Catalog catalog) {
		CatalogSummary catalogSummary = catalogContext.getSummary(catalog);
		return catalogSummary != null ? Response.success(catalogSummary.toEntries()) : Response.success();
	}

	@PostMapping("/{metric}/summary")
	public Response metricSummary(@PathVariable("metric") String metric, @RequestBody Catalog catalog) {
		CatalogMetricsCollector<StatisticalMetric> collector = catalogContext.statisticCollector();
		Map<String, StatisticalMetric> sequence = collector.sequence(catalog, metric);
		Map<String, Object> data = new LinkedHashMap<String, Object>();
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
		return Response.success(data);
	}

}
