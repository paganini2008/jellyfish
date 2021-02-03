package org.springdessert.framework.jellyfish.ui;

import org.springdessert.framework.jellyfish.log.LogEntrySearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.paganini2008.devtools.jdbc.PageResponse;

/**
 * 
 * LogEntryController
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
@RequestMapping("/application/cluster/log")
@RestController
public class LogEntryController {

	@Autowired
	private LogEntrySearchService logEntrySearchService;

	@GetMapping("/")
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
