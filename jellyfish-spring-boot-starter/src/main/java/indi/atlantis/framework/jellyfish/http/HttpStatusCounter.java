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
package indi.atlantis.framework.jellyfish.http;

import java.io.Serializable;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * HttpStatusCounter
 *
 * @author Fred Feng
 * @version 1.0
 */
@Getter
@Setter
@ToString
public class HttpStatusCounter implements Serializable, Cloneable {

	private static final long serialVersionUID = -3704644371457858977L;
	private long countOf1xx;
	private long countOf2xx;
	private long countOf3xx;
	private long countOf4xx;
	private long countOf5xx;

	public HttpStatusCounter() {
	}

	public HttpStatusCounter(HttpStatus httpStatus) {

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

	public HttpStatusCounter clone() {
		try {
			return (HttpStatusCounter) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new IllegalStateException(e);
		}
	}

}
