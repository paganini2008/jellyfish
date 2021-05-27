package indi.atlantis.framework.jellyfish.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;

/**
 * 
 * ApiStatisticTracer
 *
 * @author Fred Feng
 * @version 1.0
 */
public interface ApiStatisticTracer {

	void onError(String requestId, String path, long elapsed, HttpServletRequest request, HttpServletResponse response, HttpStatus status,
			Exception cause);

	void onTimeout(String requestId, String path, long elapsed, HttpServletRequest request, HttpServletResponse response);

}
