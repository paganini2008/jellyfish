package org.springtribe.framework.jellyfish.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springtribe.framework.jellyfish.stat.Catalog;
import org.springtribe.framework.jellyfish.stat.MetricBean;
import org.springtribe.framework.jellyfish.stat.MetricsCollectorCustomizer;
import org.springtribe.framework.jellyfish.stat.TransientStatisticSynchronizer2;

import com.github.paganini2008.devtools.cache.Cache;
import com.github.paganini2008.devtools.collection.CollectionUtils;

/**
 * 
 * StatisticController
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
@RequestMapping("/application/cluster/statistic")
@RestController
public class StatisticController {

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Autowired
	private MetricsCollectorCustomizer metricsCollectorCustomizer;

	@Autowired
	private TransientStatisticSynchronizer2 transientStatisticSynchronizer;

	@Value("${spring.application.cluster.name}")
	private String clusterName;

	@GetMapping("/list")
	public Response pathList() {
		Set<String> allKeys = redisTemplate.keys(String.format("jellyfish:%s:catalog:*", clusterName));
		List<Catalog> catalogs = new ArrayList<Catalog>();
		if (CollectionUtils.isNotEmpty(allKeys)) {
			allKeys.forEach(key -> {
				catalogs.add(Catalog.decode(key.substring(key.lastIndexOf(':') + 1)));
			});
		}
		Collections.sort(catalogs);
		return Response.success(catalogs);
	}

	@PostMapping("/summary")
	public Response summary(@RequestBody Catalog catalog) {
		Cache cache = transientStatisticSynchronizer.getTotalSummaryCache(catalog);
		return cache != null ? Response.success(cache.toEntries()) : Response.success();
	}

	@PostMapping("/{metric}/summary")
	public Response realtimeSummary(@PathVariable("metric") String metric, @RequestBody Catalog catalog) {
		Cache cache = transientStatisticSynchronizer.getRealtimeSummaryCache(catalog, metric);
		if (cache != null) {
			Map<String, MetricBean> data = metricsCollectorCustomizer.render(cache.toEntries());
			return Response.success(data);
		}
		return Response.success();
	}

}
