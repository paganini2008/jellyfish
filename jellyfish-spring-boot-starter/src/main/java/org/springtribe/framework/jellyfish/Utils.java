package org.springtribe.framework.jellyfish;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.github.paganini2008.devtools.Assert;

/**
 * 
 * Utils
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public abstract class Utils {

	public static String encodeString(String str) {
		Assert.hasNoText(str, "Null string");
		try {
			return URLEncoder.encode(str, "UTF-8");
		} catch (UnsupportedEncodingException ignored) {
			throw new IllegalStateException();
		}
	}

}
