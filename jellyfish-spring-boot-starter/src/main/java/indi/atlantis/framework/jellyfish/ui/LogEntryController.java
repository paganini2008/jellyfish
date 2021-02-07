package indi.atlantis.framework.jellyfish.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.paganini2008.devtools.jdbc.PageResponse;

import indi.atlantis.framework.jellyfish.log.CleanQuery;
import indi.atlantis.framework.jellyfish.log.LogEntrySearchService;
import indi.atlantis.framework.jellyfish.log.LogEntryService;

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

	@Autowired
	private LogEntryService logEntryService;

	@PostMapping("/clean")
	public Response clean(@RequestBody CleanQuery query) {
		logEntryService.retainLatest(query.getStartDate(), query.getEndDate());
		return Response.success();
	}

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
