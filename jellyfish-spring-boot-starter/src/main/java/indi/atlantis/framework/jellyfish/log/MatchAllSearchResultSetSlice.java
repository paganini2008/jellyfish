package indi.atlantis.framework.jellyfish.log;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;

/**
 * 
 * MatchAllSearchResultSetSlice
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public class MatchAllSearchResultSetSlice extends IndexSearchResultSetSlice {

	public MatchAllSearchResultSetSlice(ElasticsearchTemplate elasticsearchTemplate) {
		super(elasticsearchTemplate);
	}

	@Override
	protected QueryBuilder buildQuery() {
		return QueryBuilders.matchAllQuery();
	}

}
