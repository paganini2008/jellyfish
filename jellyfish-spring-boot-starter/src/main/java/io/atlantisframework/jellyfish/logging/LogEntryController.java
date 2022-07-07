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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.paganini2008.devtools.jdbc.PageBean;
import com.github.paganini2008.devtools.jdbc.PageResponse;

import io.atlantisframework.jellyfish.Response;

/**
 * 
 * LogEntryController
 *
 * @author Fred Feng
 * @since 2.0.1
 */
@RequestMapping("/atlantis/jellyfish/log")
@RestController
public class LogEntryController {

	@Autowired
	private LogEntrySearchService logEntrySearchService;

	@Autowired
	private LogEntryService logEntryService;

	@PostMapping("/clean")
	public Response clean(@RequestBody CleanQuery query) {
		logEntryService.retainLatest(query.getStartDate(), query.getEndDate());
		return Response.success();
	}

	@GetMapping("/search")
	public Response search(@RequestParam(value = "page", defaultValue = "1", required = false) int page,
			@CookieValue(value = "PAGE_FETCH_SIZE", required = false, defaultValue = "100") int size) {
		PageResponse<SearchResult> pageResponse = logEntrySearchService.search(page, size);
		return Response.success(PageBean.wrap(pageResponse));
	}

	@PostMapping("/entry/search")
	public Response search(@RequestBody SearchQuery searchQuery,
			@RequestParam(value = "page", defaultValue = "1", required = false) int page,
			@CookieValue(value = "PAGE_FETCH_SIZE", required = false, defaultValue = "100") int size) {
		PageResponse<SearchResult> pageResponse = logEntrySearchService.search(searchQuery, page, size);
		return Response.success(PageBean.wrap(pageResponse));
	}

	@PostMapping("/history/search")
	public Response searchHistory(@RequestBody HistoryQuery searchQuery,
			@RequestParam(value = "page", defaultValue = "1", required = false) int page,
			@CookieValue(value = "PAGE_FETCH_SIZE", required = false, defaultValue = "100") int size) {
		PageResponse<SearchResult> pageResponse = logEntrySearchService.search(searchQuery, page, size);
		return Response.success(PageBean.wrap(pageResponse));
	}

}
