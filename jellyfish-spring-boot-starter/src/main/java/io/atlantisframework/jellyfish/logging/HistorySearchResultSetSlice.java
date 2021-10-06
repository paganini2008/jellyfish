/**
* Copyright 2017-2021 Fred Feng (paganini.fy@gmail.com)

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

import static io.atlantisframework.jellyfish.logging.SearchResult.SEARCH_FIELD_MESSAGE;
import static io.atlantisframework.jellyfish.logging.SearchResult.SEARCH_FIELD_REASON;
import static io.atlantisframework.jellyfish.logging.SearchResult.SORTED_FIELD_CREATE_TIME;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;

import com.github.paganini2008.devtools.StringUtils;

/**
 * 
 * HistorySearchResultSetSlice
 *
 * @author Fred Feng
 * @since 2.0.1
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
