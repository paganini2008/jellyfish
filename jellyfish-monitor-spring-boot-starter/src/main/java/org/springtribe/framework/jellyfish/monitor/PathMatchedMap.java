package org.springtribe.framework.jellyfish.monitor;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import com.github.paganini2008.devtools.collection.KeyMatchedMap;

/**
 * 
 * PathMatchedMap
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public class PathMatchedMap extends KeyMatchedMap<String, Long> {

	private static final long serialVersionUID = 1L;

	public PathMatchedMap() {
		super(new ConcurrentHashMap<String, Long>(), false);
	}

	private final PathMatcher pathMatcher = new AntPathMatcher();

	@Override
	protected boolean apply(String pattern, Object inputKey) {
		return pathMatcher.match(pattern, (String) inputKey);
	}

}
