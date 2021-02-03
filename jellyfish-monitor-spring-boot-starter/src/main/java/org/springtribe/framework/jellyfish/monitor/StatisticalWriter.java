package org.springtribe.framework.jellyfish.monitor;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;

import com.github.paganini2008.devtools.StringUtils;

/**
 * 
 * StatisticalWriter
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public abstract class StatisticalWriter implements HandlerInterceptor {

	static final String REQUEST_ID = "cooper-req-id";
	static final String REQUEST_TIMESTAMP = "cooper-req-time";

	@Override
	public final boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if (request.getAttribute(REQUEST_TIMESTAMP) == null) {
			request.setAttribute(REQUEST_TIMESTAMP, System.currentTimeMillis());
		}
		onRequestBegin(request, getRequestId(request));
		return true;
	}

	@Override
	public final void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception e)
			throws Exception {
		onRequestEnd(request, getRequestId(request), e);
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

	protected void onRequestBegin(HttpServletRequest request, String requestId) throws Exception {
	}

	protected void onRequestEnd(HttpServletRequest request, String requestId, Exception e) throws Exception {
	}

}
