package org.springdessert.framework.jellyfish.log;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Component;

/**
 * 
 * LogEntryRepository
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
@Component
public interface LogEntryRepository extends ElasticsearchRepository<LogEntry, Long>{

}
