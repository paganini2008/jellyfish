package indi.atlantis.framework.jellyfish.http;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;

import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.collection.MapUtils;
import com.github.paganini2008.devtools.net.NetUtils;

import indi.atlantis.framework.vortex.common.HashPartitioner;
import indi.atlantis.framework.vortex.common.TransportClient;
import indi.atlantis.framework.vortex.common.Tuple;

/**
 * 
 * StatisticWriter
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public class StatisticWriter extends MonitorWriter {

	private static final String TOPIC_NAME = StatisticWriter.class.getName();

	private final ConcurrentMap<String, AtomicInteger> concurrencies = new ConcurrentHashMap<String, AtomicInteger>();

	@Value("${spring.application.cluster.name:default}")
	private String clusterName;

	@Value("${spring.application.name}")
	private String applicationName;

	@Value("${server.port}")
	private int port;

	@Autowired
	private PathMatcher pathMatcher;

	@Autowired
	private TransportClient transportClient;

	@Autowired(required = false)
	private StatisticTracer statisticTracer;

	private String hostName = NetUtils.getLocalHost();

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

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
		final boolean timeout = pathMatcher.matchTimeout(path, elapsed);
		HttpStatus status = HttpStatus.valueOf(response.getStatus());
		final boolean failed = (e != null) || (!status.is2xxSuccessful());

		String host = this.hostName + ":" + port;
		String category = pathMatcher.matchCategory(path);
		String decorator = pathMatcher.matchDecoration(path);
		Map<String, Object> contextMap = new HashMap<String, Object>();
		contextMap.put(Tuple.PARTITIONER_NAME, HashPartitioner.class.getName());
		contextMap.put("requestId", requestId);
		contextMap.put("clusterName", clusterName);
		contextMap.put("applicationName", applicationName);
		contextMap.put("host", host);
		contextMap.put("category", category);
		contextMap.put("path", decorator);
		contextMap.put("requestTime", begin.longValue());
		contextMap.put("elapsed", elapsed);
		contextMap.put("timeout", timeout);
		contextMap.put("failed", failed);
		contextMap.put("concurrency", concurrency);
		contextMap.put("httpStatusCode", status.value());

		transportClient.write(TOPIC_NAME, contextMap);

		if (statisticTracer != null) {
			if (failed) {
				statisticTracer.onError(requestId, path, elapsed, request, response, status, e);
			}
			if (timeout) {
				statisticTracer.onTimeout(requestId, path, elapsed, request, response);
			}
		}
	}

	private AtomicInteger getConcurrency(String path) {
		return MapUtils.get(concurrencies, path, () -> new AtomicInteger(0));
	}

}
