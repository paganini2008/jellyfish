package org.springtribe.framework.jellyfish.monitor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
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

	@Qualifier("jellyfishMonitorTaskExecutor")
	@Autowired
	private ThreadPoolTaskExecutor taskExecutor;

	@Autowired(required = false)
	private StatisticalTracer statisticalTracer;

	private String host = NetUtils.getLocalHost();

	@Override
	protected void onRequestBegin(String requestId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		getConcurrency(request.getServletPath()).incrementAndGet();
	}

	@Override
	protected void onRequestEnd(String requestId, HttpServletRequest request, HttpServletResponse response, Exception e) throws Exception {
		if (StringUtils.isBlank(requestId)) {
			return;
		}
		Long begin = (Long) request.getAttribute(REQUEST_TIMESTAMP);
		if (begin == null) {
			return;
		}
		final String path = request.getServletPath();
		long elapsed = System.currentTimeMillis() - begin.longValue();
		int concurrency = getConcurrency(path).decrementAndGet();
		final boolean timeout = isTimeout(path, (Long) request.getAttribute(REQUEST_TIMESTAMP));
		HttpStatus status = HttpStatus.valueOf(response.getStatus());
		final boolean failed = (e != null) || (!status.is2xxSuccessful());
		Map<String, Object> contextMap = new HashMap<String, Object>();
		contextMap.put(Tuple.PARTITIONER_NAME, HashPartitioner.class.getName());
		contextMap.put("requestId", requestId);
		contextMap.put("clusterName", clusterName);
		contextMap.put("applicationName", applicationName);
		contextMap.put("host", host + ":" + port);
		contextMap.put("path", path);
		contextMap.put("requestTime", begin.longValue());
		contextMap.put("elapsed", elapsed);
		contextMap.put("timeout", timeout);
		contextMap.put("failed", failed);
		contextMap.put("concurrency", concurrency);
		contextMap.put("httpStatus", status);
		transportClient.write(TOPIC_NAME, contextMap);

		if (statisticalTracer != null) {
			taskExecutor.execute(() -> {
				if (failed) {
					statisticalTracer.onError(requestId, path, elapsed, request, response, status, e);
				}
				if (timeout) {
					statisticalTracer.onTimeout(requestId, path, elapsed, request, response);
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
