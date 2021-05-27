package indi.atlantis.framework.jellyfish;

import java.util.HashMap;

import org.springframework.http.HttpStatus;

import com.github.paganini2008.devtools.ExceptionUtils;

/**
 * 
 * Response
 *
 * @author Fred Feng
 * @since 1.0
 */
public class Response extends HashMap<String, Object> {

	private static final long serialVersionUID = 1L;

	Response(boolean success, String msg, Object data) {
		put("success", success);
		put("msg", msg);
		put("data", data);
		put("statusCode", HttpStatus.OK.value());
	}

	public Response setRequestPath(String path) {
		put("requestPath", path);
		return this;
	}

	public Response setError(Throwable e) {
		put("error", ExceptionUtils.toArray(e));
		return this;
	}

	public Response setElapsed(long elapsed) {
		put("elapsed", elapsed);
		return this;
	}

	public Response setStatusCode(HttpStatus httpStatus) {
		put("statusCode", httpStatus.value());
		return this;
	}

	public static Response success(String message) {
		return success(message, null);
	}

	public static Response success(Object data) {
		return success("ok", data);
	}

	public static Response success() {
		return success("ok", null);
	}

	public static Response success(String msg, Object data) {
		return new Response(true, msg, data);
	}

	public static Response failure() {
		return failure(null);
	}

	public static Response failure(String msg) {
		return new Response(false, msg, null);
	}

}
