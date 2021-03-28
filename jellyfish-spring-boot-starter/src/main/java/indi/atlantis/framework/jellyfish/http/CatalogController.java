package indi.atlantis.framework.jellyfish.http;

import static indi.atlantis.framework.jellyfish.http.MetricNames.CC;
import static indi.atlantis.framework.jellyfish.http.MetricNames.COUNT;
import static indi.atlantis.framework.jellyfish.http.MetricNames.HTTP_STATUS;
import static indi.atlantis.framework.jellyfish.http.MetricNames.QPS;
import static indi.atlantis.framework.jellyfish.http.MetricNames.RT;

import java.util.Collections;
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

import indi.atlantis.framework.jellyfish.Response;
import indi.atlantis.framework.vortex.metric.BigInt;
import indi.atlantis.framework.vortex.metric.GenericUserMetricSequencer;

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

	@Qualifier("secondaryEnvironment")
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
		ApiSummary summary = environment.getSummary(catalog);
		return summary != null ? Response.success(summary.toEntries()) : Response.success();
	}

	@PostMapping("/{metric}/summary")
	public Response metricSummary(@PathVariable("metric") String metric, @RequestBody Catalog catalog) {
		Map<String, Map<String, Object>> data;
		if (metric.equals("combined")) {
			data = fetchCombinedMerticData(catalog);
		} else {
			data = fetchMerticData(catalog, metric);
		}
		return Response.success(data);
	}

	private Map<String, Map<String, Object>> fetchCombinedMerticData(Catalog catalog) {
		GenericUserMetricSequencer<Catalog, BigInt> apiStatisticSequencer = environment.getApiStatisticMetricSequencer();
		return apiStatisticSequencer.sequence(catalog, new String[] { RT, CC, QPS }, true);
	}

	private Map<String, Map<String, Object>> fetchMerticData(Catalog catalog, String metric) {
		switch (metric) {
		case RT:
		case CC:
		case QPS:
			GenericUserMetricSequencer<Catalog, BigInt> apiStatisticSequencer = environment.getApiStatisticMetricSequencer();
			return apiStatisticSequencer.sequence(catalog, metric, true);
		case COUNT:
			GenericUserMetricSequencer<Catalog, ApiCounter> apiCounterSequencer = environment.getApiCounterMetricSequencer();
			return apiCounterSequencer.sequence(catalog, metric, true);
		case HTTP_STATUS:
			GenericUserMetricSequencer<Catalog, HttpStatusCounter> httpStatusCounterMetricSequencer = environment
					.getHttpStatusCounterMetricSequencer();
			return httpStatusCounterMetricSequencer.sequence(catalog, metric, true);
		}
		return MapUtils.emptyMap();
	}

}
