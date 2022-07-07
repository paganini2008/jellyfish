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
package io.atlantisframework.jellyfish.logging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.springdessert.reditools.common.IdGenerator;

import io.atlantisframework.vortex.Handler;
import io.atlantisframework.vortex.common.Tuple;

/**
 * 
 * Slf4jHandler
 *
 * @author Fred Feng
 * @since 2.0.1
 */
public class Slf4jHandler implements Handler {

	private static final String TOPIC_NAME = "slf4j";

	@Autowired
	private IdGenerator idGenerator;

	@Autowired
	private LogEntryService logEntryService;

	@Value("${atlantis.framework.jellyfish.handler.interferedCharacter:}")
	private String interferedCharacterRegex;

	@Override
	public void onData(Tuple tuple) {
		LogEntry logEntry = new LogEntry();
		logEntry.setId(idGenerator.generateId());
		logEntry.setClusterName(tuple.getField("clusterName", String.class));
		logEntry.setApplicationName(tuple.getField("applicationName", String.class));
		logEntry.setHost(tuple.getField("host", String.class));
		logEntry.setIdentifier(tuple.getField("identifier", String.class));
		logEntry.setLoggerName(tuple.getField("loggerName", String.class));
		logEntry.setMessage(tuple.getField("message", String.class));
		logEntry.setLevel(tuple.getField("level", String.class));
		logEntry.setReason(tuple.getField("reason", String.class));
		logEntry.setMarker(tuple.getField("marker", String.class));
		logEntry.setCreateTime(tuple.getField("timestamp", Long.class));
		if (StringUtils.isNotBlank(interferedCharacterRegex)) {
			logEntry.setMessage(logEntry.getMessage().replaceAll(interferedCharacterRegex, ""));
			logEntry.setReason(logEntry.getReason().replaceAll(interferedCharacterRegex, ""));
		}
		logEntryService.bulkSaveLogEntries(logEntry);
	}

	@Override
	public String getTopic() {
		return TOPIC_NAME;
	}

}
