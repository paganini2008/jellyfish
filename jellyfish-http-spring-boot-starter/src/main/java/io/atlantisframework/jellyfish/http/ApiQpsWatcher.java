/**
* Copyright 2017-2022 Fred Feng (paganini.fy@gmail.com)

* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package io.atlantisframework.jellyfish.http;

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
import org.springframework.scheduling.TaskScheduler;

import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.collection.MapUtils;
import com.github.paganini2008.devtools.multithreads.AtomicLongSequence;
import com.github.paganini2008.devtools.net.NetUtils;

import io.atlantisframework.vortex.common.HashPartitioner;
import io.atlantisframework.vortex.common.Partitioner;
import io.atlantisframework.vortex.common.TransportClient;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * ApiQpsWatcher
 *
 * @author Fred Feng
 * @since 2.0.1
 */
@Slf4j
public class ApiQpsWatcher extends ApiWatcher implements InitializingBean {

	private static final String TOPIC_NAME = ApiQpsWatcher.class.getName();
	private static final String DEFAULT_HOST_NAME = NetUtils.getLocalHost();
	private final Map<String, QPS> contexts = new ConcurrentHashMap<String, QPS>();

	@Value("${spring.application.cluster.name:default}")
	private String clusterName;

	@Value("${spring.application.name}")
	private String applicationName;

	@Value("${server.port}")
	private int port;

	@Value("${server.hostName:}")
	private String hostName;

	@Autowired
	private PathMatcher pathMatcher;

	@Autowired
	private TransportClient transportClient;

	@Qualifier("apiWatcherTaskScheduler")
	@Autowired
	private TaskScheduler taskScheduler;

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
		path = pathMatcher.matchDecoration(path);
		return MapUtils.get(contexts, path, () -> new QPS());
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		taskScheduler.scheduleWithFixedDelay(new CheckpointTask(), Duration.ofSeconds(1L));
		log.info("CheckpointTask run automatically");
	}

	/**
	 * 
	 * CheckpointTask
	 *
	 * @author Fred Feng
	 * @since 2.0.1
	 */
	private class CheckpointTask implements Runnable {

		@Override
		public void run() {
			Map<String, Object> contextMap;
			int qps;
			for (Map.Entry<String, QPS> entry : contexts.entrySet()) {
				qps = entry.getValue().checkpoint();
				if (qps > 0) {
					try {
						contextMap = getContextMap(entry.getKey(), qps);
						transportClient.write(TOPIC_NAME, contextMap);
					} catch (RuntimeException e) {
						log.error(e.getMessage(), e);
					}
				}
			}
		}

		private Map<String, Object> getContextMap(final String path, int qps) {
			String host = StringUtils.isNotBlank(hostName) ? hostName : DEFAULT_HOST_NAME + ":" + port;
			String category = pathMatcher.matchCategory(path);
			String decorator = pathMatcher.matchDecoration(path);
			Map<String, Object> contextMap = new HashMap<String, Object>();
			contextMap.put(Partitioner.class.getName(), HashPartitioner.class.getName());
			contextMap.put("clusterName", clusterName);
			contextMap.put("applicationName", applicationName);
			contextMap.put("host", host);
			contextMap.put("category", category);
			contextMap.put("path", decorator);
			contextMap.put("qps", qps);
			return contextMap;
		}

	}

	/**
	 * 
	 * QPS
	 *
	 * @author Fred Feng
	 * @since 2.0.1
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
