package org.springtribe.framework.jellyfish.monitor;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springtribe.framework.gearless.common.HashPartitioner;
import org.springtribe.framework.gearless.common.TransportClient;
import org.springtribe.framework.gearless.common.Tuple;

import com.github.paganini2008.devtools.collection.MapUtils;
import com.github.paganini2008.devtools.multithreads.AtomicLongSequence;
import com.github.paganini2008.devtools.net.NetUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * BulkStatisticalWriter
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
@Slf4j
public class BulkStatisticalWriter extends StatisticalWriter implements InitializingBean {

	private static final String TOPIC_NAME = BulkStatisticalWriter.class.getName();
	private final Map<String, Context> contexts = new ConcurrentHashMap<String, Context>();

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

	@Autowired
	private ThreadPoolTaskScheduler taskScheduler;

	@Override
	protected void onRequestBegin(String requestId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		getContext(request.getServletPath());
	}

	@Override
	protected void onRequestEnd(String requestId, HttpServletRequest request, HttpServletResponse response, Exception e) throws Exception {
		final String path = request.getServletPath();
		Context context = getContext(path);
		context.totalExecution.incrementAndGet();

		long requestTime = (Long) request.getAttribute(REQUEST_TIMESTAMP);
		long elapsed = System.currentTimeMillis() - requestTime;
		boolean timeout = pathMatcher.matchTimeout(path, elapsed);
		if (timeout) {
			context.timeoutExecution.incrementAndGet();
		}
		HttpStatus status = HttpStatus.valueOf(response.getStatus());
		boolean failed = (e != null) || (!status.is2xxSuccessful());
		if (failed) {
			context.failedExecution.incrementAndGet();
		}

		HttpStatus httpStatus = HttpStatus.valueOf(response.getStatus());
		if (httpStatus.is1xxInformational()) {
			context.countOf1xx.incrementAndGet();
		} else if (httpStatus.is2xxSuccessful()) {
			context.countOf2xx.incrementAndGet();
		} else if (httpStatus.is3xxRedirection()) {
			context.countOf3xx.incrementAndGet();
		} else if (httpStatus.is4xxClientError()) {
			context.countOf4xx.incrementAndGet();
		} else if (httpStatus.is5xxServerError()) {
			context.countOf5xx.incrementAndGet();
		}

	}

	private Context getContext(String path) {
		return MapUtils.get(contexts, path, () -> new Context(path));
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		taskScheduler.scheduleWithFixedDelay(new CheckpointTask(), Duration.ofSeconds(1));
		log.info("BulkStatisticalWriter checkpoint automatically");
	}

	/**
	 * 
	 * CheckpointTask
	 *
	 * @author Jimmy Hoff
	 * @version 1.0
	 */
	class CheckpointTask implements Runnable {

		final String host = NetUtils.getLocalHost();

		@Override
		public void run() {
			Map<String, Object> contextMap;
			for (Context context : contexts.values()) {
				contextMap = getContextMap(context);
				try {
					transportClient.write(TOPIC_NAME, contextMap);
				} catch (RuntimeException e) {
					log.error(e.getMessage(), e);
				}
			}
		}

		private Map<String, Object> getContextMap(Context context) {
			Map<String, Object> contextMap = new HashMap<String, Object>();
			contextMap.put(Tuple.PARTITIONER_NAME, HashPartitioner.class.getName());
			contextMap.put("clusterName", clusterName);
			contextMap.put("applicationName", applicationName);
			contextMap.put("host", host + ":" + port);
			contextMap.put("category", pathMatcher.matchCategory(context.getPath()));
			contextMap.put("path", context.getPath());
			contextMap.putAll(context.checkpoint());
			return contextMap;
		}

	}

	/**
	 * 
	 * Stat
	 *
	 * @author Jimmy Hoff
	 * @version 1.0
	 */
	static class Context {

		final String path;
		final AtomicLongSequence totalExecution = new AtomicLongSequence();
		final AtomicLongSequence timeoutExecution = new AtomicLongSequence();
		final AtomicLongSequence failedExecution = new AtomicLongSequence();

		final AtomicLongSequence countOf1xx = new AtomicLongSequence();
		final AtomicLongSequence countOf2xx = new AtomicLongSequence();
		final AtomicLongSequence countOf3xx = new AtomicLongSequence();
		final AtomicLongSequence countOf4xx = new AtomicLongSequence();
		final AtomicLongSequence countOf5xx = new AtomicLongSequence();

		volatile long lastTotalExecutionCount = 0;

		Context(String path) {
			this.path = path;
		}

		public String getPath() {
			return path;
		}

		public Map<String, Object> checkpoint() {
			long totalExecutionCount = totalExecution.get();
			int qps = 0;
			if (totalExecutionCount > 0) {
				qps = (int) (totalExecutionCount - lastTotalExecutionCount);
				lastTotalExecutionCount = totalExecutionCount;
			}
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("totalExecutionCount", totalExecutionCount);
			data.put("qps", qps);
			data.put("timeoutExecutionCount", timeoutExecution.get());
			data.put("failedExecutionCount", failedExecution.get());

			data.put("countOf1xx", countOf1xx.get());
			data.put("countOf2xx", countOf2xx.get());
			data.put("countOf3xx", countOf3xx.get());
			data.put("countOf4xx", countOf4xx.get());
			data.put("countOf5xx", countOf5xx.get());
			return data;
		}

		public void reset() {
			totalExecution.getAndSet(0);
			timeoutExecution.getAndSet(0);
			failedExecution.getAndSet(0);
		}

	}

}
