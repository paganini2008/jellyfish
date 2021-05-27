package indi.atlantis.framework.jellyfish.http;

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
 * @version 1.0
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
