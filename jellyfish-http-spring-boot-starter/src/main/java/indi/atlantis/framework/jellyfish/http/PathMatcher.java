/**
* Copyright 2021 Fred Feng (paganini.fy@gmail.com)

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

/**
 * 
 * PathMatcher
 *
 * @author Fred Feng
 * @version 1.0
 */
public class PathMatcher {

	private static final String DEFAULT_CATEGORY_NAME = "default";
	private final PathMatcherMap<Long> timeouts = new PathMatcherMap<Long>();
	private final PathMatcherMap<String> decorations = new PathMatcherMap<String>();
	private final PathMatcherMap<String> categories = new PathMatcherMap<String>();

	public PathMatcher() {
		applyDefaultSettings();
	}

	protected void applyDefaultSettings() {
		setTimeout("/**", 3000L);
	}

	public void setTimeout(String urlPattern, long timeout) {
		timeouts.put(urlPattern, timeout);
	}

	public void setDecoration(String urlPattern, String decoration) {
		decorations.put(urlPattern, decoration);
	}

	public void setCategory(String urlPattern, String category) {
		categories.put(urlPattern, category);
	}

	public boolean matchTimeout(String path, long elapsed) {
		if (timeouts.containsKey(path)) {
			return elapsed > timeouts.get(path).longValue();
		}
		return false;
	}

	public String matchDecoration(String path) {
		return decorations.containsKey(path) ? decorations.get(path) : path;
	}

	public String matchCategory(String path) {
		return categories.getOrDefault(path, DEFAULT_CATEGORY_NAME);
	}
}
