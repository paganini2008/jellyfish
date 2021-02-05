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

	private long countOf1xx;
	private long countOf2xx;
	private long countOf3xx;
	private long countOf4xx;
	private long countOf5xx;

	public HttpStatusCounter() {
	}

	HttpStatusCounter(HttpStatus httpStatus) {

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
		}
	}

	public HttpStatusCounter(long countOf1xx, long countOf2xx, long countOf3xx, long countOf4xx, long countOf5xx) {
		this.countOf1xx = countOf1xx;
		this.countOf2xx = countOf2xx;
		this.countOf3xx = countOf3xx;
		this.countOf4xx = countOf4xx;
		this.countOf5xx = countOf5xx;
	}

}
