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

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;

/**
 * 
 * MatchAllSearchResultSetSlice
 *
 * @author Fred Feng
 * @since 2.0.1
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
