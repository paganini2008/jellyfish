package org.springtribe.framework.jellyfish.stat;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * HttpStatusCounter
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
@Getter
@Setter
public class HttpStatusCounter {

	private long count;
	private long countOf1xx;
	private long countOf2xx;
	private long countOf3xx;
	private long countOf4xx;
	private long countOf5xx;
	private long countOfUnknown;

	HttpStatusCounter() {
	}

	HttpStatusCounter(HttpStatus httpStatus) {
		count = 1;

		if (httpStatus.is1xxInformational()) {
			countOf1xx = 1L;
		} else if (httpStatus.is2xxSuccessful()) {
			countOf2xx = 1L;
		} else if (httpStatus.is3xxRedirection()) {
			countOf3xx = 1L;
		} else if (httpStatus.is4xxClientError()) {
			countOf4xx = 1L;
		} else if (httpStatus.is5xxServerError()) {
			countOf5xx = 1L;
		} else {
			countOfUnknown = 1;
		}
	}
	
}
