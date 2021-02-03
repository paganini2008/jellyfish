package org.springtribe.framework.jellyfish.monitor;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
	private final Map<String, Stat> stats = new ConcurrentHashMap<String, Stat>();

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

	@Qualifier("jellyfishMonitorTaskScheduler")
	@Autowired
	private ThreadPoolTaskScheduler taskScheduler;

	public void setTimeout(Map<String, Long> m) {
		if (m != null) {
			timeouts.putAll(m);
		}
	}

	public void setTimeout(String urlPattern, long timeout) {
		timeouts.put(urlPattern, timeout);
	}

	@Override
	protected void onRequestBegin(String requestId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		getStat(request.getServletPath());
	}

	@Override
	protected void onRequestEnd(String requestId, HttpServletRequest request, HttpServletResponse response, Exception e) throws Exception {
		final String path = request.getServletPath();
		Stat stat = getStat(path);
		stat.totalExecution.incrementAndGet();

		boolean timeout = false;
		if (timeouts.containsKey(path)) {
			long requestTime = (Long) request.getAttribute(REQUEST_TIMESTAMP);
			long elapsed = System.currentTimeMillis() - requestTime;
			timeout = elapsed > timeouts.get(path);
		}
		if (timeout) {
			stat.timeoutExecution.incrementAndGet();
		}
		HttpStatus status = HttpStatus.valueOf(response.getStatus());
		boolean failed = (e != null) || (!status.is2xxSuccessful());
		if (failed) {
			stat.failedExecution.incrementAndGet();
		}
	}

	private Stat getStat(String path) {
		return MapUtils.get(stats, path, () -> new Stat(path));
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
			Map<String, String> contextMap;
			for (Stat stat : stats.values()) {
				contextMap = getContextMap(stat);
				try {
					transportClient.write(TOPIC_NAME, contextMap);
				} catch (RuntimeException e) {
					log.error(e.getMessage(), e);
				}
			}
		}

		private Map<String, String> getContextMap(Stat stat) {
			Map<String, String> contextMap = new HashMap<String, String>();
			contextMap.put(Tuple.PARTITIONER_NAME, HashPartitioner.class.getName());
			contextMap.put("clusterName", clusterName);
			contextMap.put("applicationName", applicationName);
			contextMap.put("host", host + ":" + port);
			contextMap.put("path", stat.getPath());
			contextMap.putAll(stat.checkpoint());
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
	static class Stat {

		final String path;
		final AtomicLongSequence totalExecution = new AtomicLongSequence();
		final AtomicLongSequence timeoutExecution = new AtomicLongSequence();
		final AtomicLongSequence failedExecution = new AtomicLongSequence();

		volatile long lastTotalExecutionCount = 0;

		Stat(String path) {
			this.path = path;
		}

		public String getPath() {
			return path;
		}

		public long getTotalExecutionCount() {
			return totalExecution.get();
		}

		public long getTimeoutExecutionCount() {
			return timeoutExecution.get();
		}

		public long getFailedExecutionCount() {
			return failedExecution.get();
		}

		public Map<String, String> checkpoint() {
			long totalExecutionCount = totalExecution.get();
			int qps = 0;
			if (totalExecutionCount > 0) {
				qps = (int) (totalExecutionCount - lastTotalExecutionCount);
				lastTotalExecutionCount = totalExecutionCount;
			}
			long timeoutExecutionCount = timeoutExecution.get();
			long failedExecutionCount = failedExecution.get();
			Map<String, String> data = new HashMap<String, String>();
			data.put("totalExecutionCount", String.valueOf(totalExecutionCount));
			data.put("qps", String.valueOf(qps));
			data.put("timeoutExecutionCount", String.valueOf(timeoutExecutionCount));
			data.put("failedExecutionCount", String.valueOf(failedExecutionCount));
			return data;
		}

		public void reset() {
			totalExecution.getAndSet(0);
			timeoutExecution.getAndSet(0);
			failedExecution.getAndSet(0);
		}

	}

}
