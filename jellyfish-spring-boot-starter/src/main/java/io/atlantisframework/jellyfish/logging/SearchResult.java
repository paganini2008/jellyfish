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

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * SearchResult
 *
 * @author Fred Feng
 * @since 2.0.1
 */
@Getter
@Setter
public class SearchResult implements Serializable {

	private static final long serialVersionUID = 8569951355416294604L;

	public static final String SEARCH_FIELD_MESSAGE = "message";
	public static final String SEARCH_FIELD_REASON = "reason";
	public static final String SORTED_FIELD_CREATE_TIME = "createTime";

	private Long id;
	private String clusterName;
	private String applicationName;
	private String host;
	private String identifier;
	private String marker;
	private String loggerName;
	private String message;
	private String level;
	private String[] stackTraces;
	private String datetime;

	public String getId() {
		return String.valueOf(id);
	}

}
