package org.springtribe.framework.jellyfish.monitor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springtribe.framework.gearless.common.HashPartitioner;
import org.springtribe.framework.gearless.common.TransportClient;
import org.springtribe.framework.gearless.common.Tuple;

import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.collection.MapUtils;
import com.github.paganini2008.devtools.net.NetUtils;

/**
 * 
 * RealtimeStatisticalWriter
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public class RealtimeStatisticalWriter extends StatisticalWriter {

	private static final String TOPIC_NAME = RealtimeStatisticalWriter.class.getName();

	private final ConcurrentMap<String, AtomicInteger> concurrencies = new ConcurrentHashMap<String, AtomicInteger>();

	@Value("${spring.application.cluster.name:default}")
	private String clusterName;

	@Value("${spring.application.name}")
	private String applicationName;

	@Value("${server.port}")
	private int port;

	@Autowired
	private PathMatchedMap timeouts;

	@Autowired
	private TransportClient transportClient;

	@Qualifier("planktonTaskExecutor")
	@Autowired
	private ThreadPoolTaskExecutor taskExecutor;

	@Autowired(required = false)
	private StatisticalTracer statisticalTracer;

	private String host = NetUtils.getLocalHost();

	@Override
	protected void onRequestBegin(HttpServletRequest request, String requestId) throws Exception {
		getConcurrency(request.getServletPath()).incrementAndGet();
	}

	@Override
	protected void onRequestEnd(HttpServletRequest request, String requestId, Exception e) throws Exception {
		if (StringUtils.isBlank(requestId)) {
			return;
		}
		Long begin = (Long) request.getAttribute(REQUEST_TIMESTAMP);
		if (begin == null) {
			return;
		}
		final String path = request.getServletPath();
		final long elapsed = System.currentTimeMillis() - begin.longValue();
		final int concurrency = getConcurrency(path).decrementAndGet();
		final boolean isTimeout = isTimeout(path, (Long) request.getAttribute(REQUEST_TIMESTAMP));
		Map<String, Object> contextMap = new HashMap<String, Object>();
		contextMap.put(Tuple.PARTITIONER_NAME, HashPartitioner.class.getName());
		contextMap.put("requestId", requestId);
		contextMap.put("clusterName", clusterName);
		contextMap.put("applicationName", applicationName);
		contextMap.put("host", host + ":" + port);
		contextMap.put("path", path);
		contextMap.put("requestTime", begin.longValue());
		contextMap.put("elapsed", elapsed);
		contextMap.put("timeout", isTimeout);
		contextMap.put("failed", e != null);
		contextMap.put("concurrency", concurrency);
		transportClient.write(TOPIC_NAME, contextMap);

		if (statisticalTracer != null) {
			taskExecutor.execute(() -> {
				if (e != null) {
					statisticalTracer.onException(requestId, path, elapsed, e);
				}
				if (isTimeout) {
					statisticalTracer.onTimeout(requestId, path, elapsed);
				}
			});
		}
	}

	private boolean isTimeout(String path, long requestTime) {
		boolean timeout = false;
		if (timeouts.containsKey(path)) {
			long elapsed = System.currentTimeMillis() - requestTime;
			timeout = elapsed > timeouts.get(path);
		}
		return timeout;
	}

	private AtomicInteger getConcurrency(String path) {
		return MapUtils.get(concurrencies, path, () -> new AtomicInteger(0));
	}

}
