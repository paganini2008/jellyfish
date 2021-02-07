package indi.atlantis.framework.jellyfish.agent;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;

/**
 * 
 * StatisticalTracer
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public interface StatisticalTracer {

	void onError(String requestId, String path, long elapsed, HttpServletRequest request, HttpServletResponse response, HttpStatus status,
			Exception cause);

	void onTimeout(String requestId, String path, long elapsed, HttpServletRequest request, HttpServletResponse response);

}
