package org.springtribe.framework.jellyfish.monitor;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import com.github.paganini2008.devtools.collection.KeyMatchedMap;

/**
 * 
 * PathMatcherMap
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public class PathMatcherMap<T> extends KeyMatchedMap<String, T> {

	private static final long serialVersionUID = 1L;

	public PathMatcherMap() {
		super(new ConcurrentHashMap<String, T>(), false);
	}

	private final PathMatcher pathMatcher = new AntPathMatcher();

	@Override
	protected boolean match(String pattern, Object inputKey) {
		return pathMatcher.match(pattern, (String) inputKey);
	}

}
