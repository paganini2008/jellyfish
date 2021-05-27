package indi.atlantis.framework.jellyfish.log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.DeleteQuery;
import org.springframework.data.elasticsearch.core.query.IndexQuery;

import com.github.paganini2008.devtools.multithreads.Executable;
import com.github.paganini2008.devtools.multithreads.ThreadUtils;

import indi.atlantis.framework.tridenter.utils.BeanLifeCycle;
import indi.atlantis.framework.vortex.JacksonUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * LogEntryService
 *
 * @author Fred Feng
 * @version 1.0
 */
@Slf4j
public class LogEntryService implements Executable, BeanLifeCycle {

	@Autowired
	private LogEntryRepository logEntryRepository;

	@Autowired
	private ElasticsearchTemplate elasticsearchTemplate;

	private Timer timer;
	private final List<LogEntry> logEntries = new CopyOnWriteArrayList<LogEntry>();

	public void configure() {
		timer = ThreadUtils.scheduleWithFixedDelay(this, 3, TimeUnit.SECONDS);
	}

	public void saveLogEntry(LogEntry logEntry) {
		if (logEntry != null) {
			logEntryRepository.save(logEntry);
		}
	}

	public void bulkSaveLogEntries(LogEntry logEntry) {
		if (logEntry != null) {
			logEntries.add(logEntry);
		}
	}

	public void retainLatest(Date startDate, Date endDate) {
		RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("createTime");
		long startTime = startDate != null ? startDate.getTime() : 0;
		long endTime = endDate != null ? endDate.getTime() : 0;
		if (startTime == 0 && endTime == 0) {
			endTime = System.currentTimeMillis();
		}
		if (startTime > 0) {
			rangeQuery.gte(startTime);
		}
		if (endTime > 0) {
			rangeQuery.lte(endTime);
		}
		DeleteQuery deleteQuery = new DeleteQuery();
		deleteQuery.setQuery(rangeQuery);
		elasticsearchTemplate.delete(deleteQuery, LogEntry.class);
		log.info("Delete log entries from elasticsearch from '{}' to '{}' successfully.", startDate, endDate);
	}

	@Override
	public boolean execute() {
		if (logEntries.isEmpty()) {
			return true;
		}
		List<IndexQuery> queries = new ArrayList<IndexQuery>();
		try {
			for (LogEntry logEntry : logEntries) {
				IndexQuery indexQuery = new IndexQuery();
				indexQuery.setId(String.valueOf(logEntry.getId()));
				indexQuery.setIndexName(LogEntry.INDEX_NAME);
				indexQuery.setType(LogEntry.INDEX_TYPE);
				indexQuery.setSource(JacksonUtils.toJsonString(logEntry));
				queries.add(indexQuery);
				logEntries.remove(logEntry);
			}
			elasticsearchTemplate.bulkIndex(queries);
			elasticsearchTemplate.refresh(LogEntry.INDEX_NAME);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			log.info("Batch save {} logEntries.", queries.size());
			queries.clear();
			queries = null;
		}
		return true;
	}

	@Override
	public void destroy() {
		if (timer != null) {
			timer.cancel();
		}
	}

}
