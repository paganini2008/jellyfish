package indi.atlantis.framework.jellyfish.log;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.springworld.reditools.common.IdGenerator;

import indi.atlantis.framework.vortex.Handler;
import indi.atlantis.framework.vortex.common.Tuple;

/**
 * 
 * LogboxHandler
 *
 * @author Fred Feng
 * @version 1.0
 */
public class Slf4jHandler implements Handler {

	private static final String TOPIC_NAME = "slf4j";

	@Autowired
	private IdGenerator idGenerator;

	@Autowired
	private LogEntryService logEntryService;

	@Value("${spring.application.cluster.jellyfish.logbox.interferedCharacter:}")
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
