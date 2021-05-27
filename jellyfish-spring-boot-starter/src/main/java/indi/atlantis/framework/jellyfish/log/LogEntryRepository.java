package indi.atlantis.framework.jellyfish.log;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Component;

/**
 * 
 * LogEntryRepository
 *
 * @author Fred Feng
 * @version 1.0
 */
@Component
public interface LogEntryRepository extends ElasticsearchRepository<LogEntry, Long>{

}
