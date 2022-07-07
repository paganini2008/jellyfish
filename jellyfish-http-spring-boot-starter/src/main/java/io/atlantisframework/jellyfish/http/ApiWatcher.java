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
package io.atlantisframework.jellyfish.http;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;

import com.github.paganini2008.devtools.StringUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * ApiWatcher
 *
 * @author Fred Feng
 * @since 2.0.1
 */
@Slf4j
public abstract class ApiWatcher implements HandlerInterceptor {

	static final String REQUEST_ID = "jellyfish-http-request-id";
	static final String REQUEST_TIMESTAMP = "jellyfish-http-request-time";

	@Override
	public final boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if (request.getAttribute(REQUEST_TIMESTAMP) == null) {
			request.setAttribute(REQUEST_TIMESTAMP, System.currentTimeMillis());
		}
		onRequestBegin(getRequestId(request), request, response);
		return true;
	}

	@Override
	public final void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception e)
			throws Exception {
		try {
			onRequestEnd(getRequestId(request), request, response, e);
		} catch (Exception delta) {
			log.error(delta.getMessage(), delta);
		}
	}

	private String getRequestId(HttpServletRequest request) {
		String requestId = request.getHeader(REQUEST_ID);
		if (StringUtils.isBlank(requestId)) {
			requestId = (String) request.getAttribute(REQUEST_ID);
			if (StringUtils.isBlank(requestId)) {
				request.setAttribute(REQUEST_ID, generateRequestId(request));
				requestId = (String) request.getAttribute(REQUEST_ID);
			}
		}
		return requestId;
	}

	protected String generateRequestId(HttpServletRequest request) {
		return UUID.randomUUID().toString();
	}

	protected void onRequestBegin(String requestId, HttpServletRequest request, HttpServletResponse response) throws Exception {
	}

	protected void onRequestEnd(String requestId, HttpServletRequest request, HttpServletResponse response, Exception e) throws Exception {
	}

}
