package indi.atlantis.framework.jellyfish.http;

import static indi.atlantis.framework.jellyfish.http.MetricNames.CC;
import static indi.atlantis.framework.jellyfish.http.MetricNames.COUNT;
import static indi.atlantis.framework.jellyfish.http.MetricNames.HTTP_STATUS;
import static indi.atlantis.framework.jellyfish.http.MetricNames.QPS;
import static indi.atlantis.framework.jellyfish.http.MetricNames.RT;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
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
import com.github.paganini2008.devtools.date.DateUtils;

import indi.atlantis.framework.jellyfish.Response;
import indi.atlantis.framework.vortex.metric.BigInt;
import indi.atlantis.framework.vortex.metric.DataRenderer;
import indi.atlantis.framework.vortex.metric.MetricSequencer;
import indi.atlantis.framework.vortex.metric.Sequencer;
import indi.atlantis.framework.vortex.metric.UserMetric;

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
		Map<String, Map<String, Object>> data = new LinkedHashMap<String, Map<String, Object>>();
		long timestamp = System.currentTimeMillis();
		MetricSequencer<Catalog, UserMetric<BigInt>> bigIntSequencer = environment.getBigIntMetricSequencer();
		Map<String, UserMetric<BigInt>> sequence;
		String time;
		UserMetric<BigInt> metricUnit;
		for (String metric : new String[] { RT, CC, QPS }) {
			sequence = bigIntSequencer.sequence(catalog, metric);
			for (Map.Entry<String, UserMetric<BigInt>> entry : sequence.entrySet()) {
				time = entry.getKey();
				metricUnit = entry.getValue();
				Map<String, Object> map = MapUtils.get(data, time, () -> {
					Map<String, Object> blank = new HashMap<String, Object>();
					blank.put("rt-middleValue", 0L);
					blank.put("cc-middleValue", 0L);
					blank.put("qps-middleValue", 0L);
					return blank;
				});
				map.put(metric + "-middleValue", metricUnit.get().getMiddleValue());
				map.putIfAbsent("count", metricUnit.get().getCount());
				map.putIfAbsent("timestamp", metricUnit.getTimestamp());
				timestamp = timestamp > 0 ? Math.min(entry.getValue().getTimestamp(), timestamp) : entry.getValue().getTimestamp();
			}
		}
		return renderData(data, timestamp, bigIntSequencer, ms -> {
			Map<String, Object> blank = new HashMap<String, Object>();
			blank.put("rt-middleValue", 0L);
			blank.put("cc-middleValue", 0L);
			blank.put("qps-middleValue", 0L);
			blank.put("count", 0);
			blank.put("timestamp", ms);
			return blank;
		});
	}

	private Map<String, Map<String, Object>> fetchMerticData(Catalog catalog, String metric) {
		switch (metric) {
		case RT:
		case CC:
		case QPS:
			Sequencer bigIntSequencer = environment.getBigIntMetricSequencer();
			return bigIntSequencer.sequence(catalog, metric, asc, time -> {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("highestValue", 0L);
				map.put("middleValue", 0L);
				map.put("lowestValue", 0L);
				map.put("count", 0);
				map.put("timestamp", time);
				return map;
			});
		case COUNT:
			Sequencer counterSequencer = environment.getCounterMetricSequencer();
			return counterSequencer.sequence(catalog, metric, asc, time -> {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("successCount", 0L);
				map.put("failedCount", 0L);
				map.put("timeoutCount", 0L);
				map.put("count", 0);
				map.put("timestamp", time);
				return map;
			});
		case HTTP_STATUS:
			Sequencer httpStatusCounterSequencer = environment.getHttpStatusCounterMetricSequencer();
			httpStatusCounterSequencer.sequence(catalog, metric, asc, time -> {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("countOf1xx", 0L);
				map.put("countOf2xx", 0L);
				map.put("countOf3xx", 0L);
				map.put("countOf4xx", 0L);
				map.put("countOf5xx", 0L);
				map.put("timestamp", time);
				return map;
			});
		}
		return MapUtils.emptyMap();
	}

	private Map<String, Map<String, Object>> renderData(Map<String, Map<String, Object>> data, long timestamp, Sequencer sequencer,
			Function<Long, Map<String, Object>> f) {
		boolean asc = this.asc;
		Date startTime = null;
		if (asc) {
			Date date = new Date(timestamp);
			int amount = sequencer.getSpan() * sequencer.getBufferSize();
			Date endTime = DateUtils.addField(date, sequencer.getSpanUnit().getCalendarField(), amount);
			if (endTime.compareTo(new Date()) <= 0) {
				asc = false;
				startTime = new Date();
			} else {
				startTime = date;
			}
		} else {
			startTime = new Date();
		}
		return DataRenderer.render(data, startTime, asc, sequencer.getSpanUnit(), sequencer.getSpan(), sequencer.getBufferSize(), f);
	}

}
