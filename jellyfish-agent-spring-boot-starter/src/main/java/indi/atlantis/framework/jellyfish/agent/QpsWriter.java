package indi.atlantis.framework.jellyfish.agent;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.util.Base64Utils;

import com.github.paganini2008.devtools.CharsetUtils;
import com.github.paganini2008.devtools.collection.MapUtils;
import com.github.paganini2008.devtools.multithreads.AtomicLongSequence;
import com.github.paganini2008.devtools.net.NetUtils;

import indi.atlantis.framework.gearless.common.HashPartitioner;
import indi.atlantis.framework.gearless.common.TransportClient;
import indi.atlantis.framework.gearless.common.Tuple;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * QpsWriter
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
@Slf4j
public class QpsWriter extends StatisticalWriter implements InitializingBean {

	private static final String TOPIC_NAME = QpsWriter.class.getName();
	private static final String IDENTIFIER_PATTERN = "%s+%s+%s+%s+%s";
	private final Map<String, QPS> contexts = new ConcurrentHashMap<String, QPS>();

	@Value("${spring.application.cluster.name:default}")
	private String clusterName;

	@Value("${spring.application.name}")
	private String applicationName;

	@Value("${server.port}")
	private int port;

	private String hostName = NetUtils.getLocalHost();

	@Autowired
	private PathMatcher pathMatcher;

	@Autowired
	private TransportClient transportClient;

	@Autowired
	private ThreadPoolTaskScheduler taskScheduler;

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	@Override
	protected void onRequestBegin(String requestId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		getContext(request.getServletPath());
	}

	@Override
	protected void onRequestEnd(String requestId, HttpServletRequest request, HttpServletResponse response, Exception e) throws Exception {
		final String path = request.getServletPath();
		QPS qps = getContext(path);
		qps.totalExecution.incrementAndGet();
	}

	private QPS getContext(String path) {
		return MapUtils.get(contexts, path, () -> new QPS());
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		taskScheduler.scheduleWithFixedDelay(new CheckpointTask(), Duration.ofSeconds(1));
		log.info("QpsWriter checkpoint automatically");
	}

	/**
	 * 
	 * CheckpointTask
	 *
	 * @author Jimmy Hoff
	 * @version 1.0
	 */
	class CheckpointTask implements Runnable {

		@Override
		public void run() {
			Map<String, Object> contextMap;
			for (Map.Entry<String, QPS> entry : contexts.entrySet()) {
				contextMap = getContextMap(entry.getKey(), entry.getValue());
				try {
					transportClient.write(TOPIC_NAME, contextMap);
				} catch (RuntimeException e) {
					log.error(e.getMessage(), e);
				}
			}
		}

		private Map<String, Object> getContextMap(final String path, QPS qps) {
			String host = hostName + port;
			String category = pathMatcher.matchCategory(path);
			String decorator = pathMatcher.matchDecoration(path);
			String repr = String.format(IDENTIFIER_PATTERN, clusterName, applicationName, host, category, decorator);
			String identifier = Base64Utils.encodeToString(repr.getBytes(CharsetUtils.UTF_8));

			Map<String, Object> contextMap = new HashMap<String, Object>();
			contextMap.put(Tuple.PARTITIONER_NAME, HashPartitioner.class.getName());
			contextMap.put("clusterName", clusterName);
			contextMap.put("applicationName", applicationName);
			contextMap.put("host", host);
			contextMap.put("category", category);
			contextMap.put("path", decorator);
			contextMap.put("identifier", identifier);
			contextMap.put("qps", qps.checkpoint());
			return contextMap;
		}

	}

	/**
	 * 
	 * QPS
	 *
	 * @author Jimmy Hoff
	 * @version 1.0
	 */
	static class QPS {

		final AtomicLongSequence totalExecution = new AtomicLongSequence();
		volatile long lastTotalExecutionCount = 0;

		public int checkpoint() {
			long totalExecutionCount = totalExecution.get();
			int qps = 0;
			if (totalExecutionCount > 0) {
				qps = (int) (totalExecutionCount - lastTotalExecutionCount);
				lastTotalExecutionCount = totalExecutionCount;
			}
			return qps;
		}

	}

}
