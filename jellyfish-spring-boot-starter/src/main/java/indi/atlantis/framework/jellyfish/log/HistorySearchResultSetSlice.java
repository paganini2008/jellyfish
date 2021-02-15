package indi.atlantis.framework.jellyfish.log;

import static indi.atlantis.framework.jellyfish.SearchResult.SEARCH_FIELD_MESSAGE;
import static indi.atlantis.framework.jellyfish.SearchResult.SEARCH_FIELD_REASON;
import static indi.atlantis.framework.jellyfish.SearchResult.SORTED_FIELD_CREATE_TIME;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;

import com.github.paganini2008.devtools.StringUtils;

import indi.atlantis.framework.jellyfish.HistoryQuery;

/**
 * 
 * HistorySearchResultSetSlice
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public class HistorySearchResultSetSlice extends IndexSearchResultSetSlice {

	public HistorySearchResultSetSlice(ElasticsearchTemplate elasticsearchTemplate, HistoryQuery searchQuery) {
		super(elasticsearchTemplate);
		this.searchQuery = searchQuery;
	}

	private final HistoryQuery searchQuery;

	@Override
	protected QueryBuilder buildQuery() {
		BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
		String keyword = searchQuery.getKeyword();
		if (StringUtils.isNotBlank(keyword)) {
			queryBuilder.should(QueryBuilders.matchQuery(SEARCH_FIELD_MESSAGE, keyword))
					.should(QueryBuilders.matchQuery(SEARCH_FIELD_REASON, keyword));
		}
		return queryBuilder;
	}

	@Override
	protected QueryBuilder buildFilter() {
		BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
		if (searchQuery != null) {
			if (StringUtils.isNotBlank(searchQuery.getClusterName())) {
				queryBuilder.filter(QueryBuilders.termQuery("clusterName", searchQuery.getClusterName()));
			}
			if (StringUtils.isNotBlank(searchQuery.getApplicationName())) {
				queryBuilder.filter(QueryBuilders.termQuery("applicationName", searchQuery.getApplicationName()));
			}
			if (StringUtils.isNotBlank(searchQuery.getHost())) {
				queryBuilder.filter(QueryBuilders.termQuery("host", searchQuery.getHost()));
			}
			if (StringUtils.isNotBlank(searchQuery.getLevel())) {
				queryBuilder.filter(QueryBuilders.termQuery("level", searchQuery.getLevel()));
			}
			if (StringUtils.isNotBlank(searchQuery.getLoggerName())) {
				queryBuilder.filter(QueryBuilders.termQuery("loggerName", searchQuery.getLoggerName()));
			}
			if (StringUtils.isNotBlank(searchQuery.getMarker())) {
				queryBuilder.filter(QueryBuilders.termQuery("marker", searchQuery.getMarker()));
			}
		}
		RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("createTime");
		long startTime = searchQuery.getStartDate() != null ? searchQuery.getStartDate().getTime() : 0;
		long endTime = searchQuery.getEndDate() != null ? searchQuery.getEndDate().getTime() : 0;
		if (startTime == 0 && endTime == 0) {
			endTime = System.currentTimeMillis();
		}
		if (startTime > 0) {
			rangeQuery.gte(startTime);
		}
		if (endTime > 0) {
			rangeQuery.lte(endTime);
		}
		queryBuilder.filter(rangeQuery);
		return queryBuilder;
	}

	@Override
	protected FieldSortBuilder buildSort() {
		return SortBuilders.fieldSort(SORTED_FIELD_CREATE_TIME).order(searchQuery.getAsc() ? SortOrder.ASC : SortOrder.DESC);
	}

}
