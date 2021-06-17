/**
* Copyright 2021 Fred Feng (paganini.fy@gmail.com)

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

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.date.DateUtils;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * LogEntry
 *
 * @author Fred Feng
 * @version 1.0
 */
@Getter
@Setter
@Document(indexName = LogEntry.INDEX_NAME, type = LogEntry.INDEX_TYPE, replicas = 0, refreshInterval = "10s")
public class LogEntry {

	public static final String INDEX_NAME = "log_entry_0";

	public static final String INDEX_TYPE = "log";

	@Id
	@Field(type = FieldType.Long, store = true)
	private Long id;

	@Field(type = FieldType.Keyword, store = true)
	private String clusterName;

	@Field(type = FieldType.Keyword, store = true)
	private String applicationName;

	@Field(type = FieldType.Keyword, store = true)
	private String host;

	@Field(type = FieldType.Keyword, store = true)
	private String identifier;

	@Field(type = FieldType.Keyword, store = true)
	private String loggerName;

	@Field(type = FieldType.Text, store = true, analyzer = "ik_smart", searchAnalyzer = "ik_smart")
	private String message;

	@Field(type = FieldType.Keyword, store = true)
	private String level;

	@Field(type = FieldType.Text, store = true, analyzer = "ik_smart", searchAnalyzer = "ik_smart")
	private String reason;

	@Field(type = FieldType.Keyword, store = true)
	private String marker;

	@Field(type = FieldType.Long, store = true)
	private long createTime;

	@JsonIgnore
	public String[] getStackTraces() {
		if (StringUtils.isEmpty(reason)) {
			return StringUtils.EMPTY_ARRAY;
		}
		return StringUtils.split(reason, "\r\n", false).toArray(new String[0]);
	}

	@JsonIgnore
	public String getDatetime() {
		return createTime > 0 ? DateUtils.format(createTime) : "";
	}

}
