/**
* Copyright 2018-2021 Fred Feng (paganini.fy@gmail.com)

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
package indi.atlantis.framework.jellyfish.log;

import static indi.atlantis.framework.jellyfish.SearchResult.SEARCH_FIELD_MESSAGE;
import static indi.atlantis.framework.jellyfish.SearchResult.SEARCH_FIELD_REASON;

import java.util.Collections;
import java.util.List;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;

import com.github.paganini2008.devtools.StringUtils;

import indi.atlantis.framework.jellyfish.SearchQuery;
import indi.atlantis.framework.jellyfish.SearchResult;

/**
 * 
 * RealtimeSearchResultSetSlice
 *
 * @author Fred Feng
 * @version 1.0
 */
public class RealtimeSearchResultSetSlice extends IndexSearchResultSetSlice {

	private final SearchQuery searchQuery;

	public RealtimeSearchResultSetSlice(ElasticsearchTemplate elasticsearchTemplate, SearchQuery searchQuery) {
		super(elasticsearchTemplate);
		this.searchQuery = searchQuery;
	}

	@Override
	public List<SearchResult> list(int maxResults, int firstResult) {
		List<SearchResult> dataList = super.list(maxResults, firstResult);
		if (searchQuery.getAsc()) {
			Collections.reverse(dataList);
		}
		return dataList;
	}

	@Override
	protected QueryBuilder buildFilter() {
		if (searchQuery != null) {
			BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
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
			return queryBuilder;
		}
		return null;
	}

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

}
