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

import java.nio.charset.Charset;

import org.springframework.util.Base64Utils;

import com.github.paganini2008.devtools.Assert;
import com.github.paganini2008.devtools.CharsetUtils;
import com.github.paganini2008.devtools.StringUtils;

import io.atlantisframework.vortex.common.Tuple;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * Api
 *
 * @author Fred Feng
 * @since 2.0.1
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
public final class Api implements Comparable<Api> {

	private static final String IDENTIFIER_PATTERN = "%s+%s+%s+%s+%s";
	private static final String NULL = "*";
	private static final Charset DEFAULT_CHARSET = CharsetUtils.UTF_8;

	private String clusterName;
	private String applicationName;
	private String host;
	private String category;
	private String path;

	public Api(String clusterName, String applicationName, String host, String category, String path) {
		Assert.hasNoText(clusterName, "ClusterName must not be null");
		this.clusterName = clusterName;
		this.applicationName = StringUtils.isNotBlank(applicationName) ? applicationName : NULL;
		this.host = StringUtils.isNotBlank(host) ? host : NULL;
		this.category = StringUtils.isNotBlank(category) ? category : NULL;
		this.path = StringUtils.isNotBlank(path) ? path : NULL;
	}

	public Api() {
	}

	public int getLevel() {
		if (!NULL.equals(applicationName) && !NULL.equals(host) && !NULL.equals(category) && !NULL.equals(path)) {
			return 4;
		} else if (!NULL.equals(applicationName) && !NULL.equals(host) && !NULL.equals(category)) {
			return 3;
		} else if (!NULL.equals(applicationName) && !NULL.equals(host)) {
			return 2;
		} else if (!NULL.equals(applicationName)) {
			return 1;
		} else {
			return 0;
		}
	}

	public String getIdentifier() {
		final String repr = String.format(IDENTIFIER_PATTERN, clusterName, applicationName, host, category, path);
		return Base64Utils.encodeToString(repr.getBytes(DEFAULT_CHARSET));
	}

	public static Api decode(String identifier) {
		String repr = new String(Base64Utils.decodeFromString(identifier), DEFAULT_CHARSET);
		String[] args = repr.split("\\+", 5);
		if (args.length != 5) {
			throw new IllegalArgumentException("Invalid identifier: " + repr);
		}
		return new Api(args[0], args[1], args[2], args[3], args[4]);
	}

	@Override
	public int compareTo(Api other) {
		String repr = String.format(IDENTIFIER_PATTERN, clusterName, applicationName, host, category, path);
		String otherRepr = String.format(IDENTIFIER_PATTERN, other.getClusterName(), other.getApplicationName(), other.getHost(),
				other.getCategory(), other.getPath());
		return repr.compareTo(otherRepr);
	}

	public static Api of(Tuple tuple) {
		String clusterName = tuple.getField("clusterName", String.class);
		String applicationName = tuple.getField("applicationName", String.class);
		String host = tuple.getField("host", String.class);
		String category = tuple.getField("category", String.class);
		String path = tuple.getField("path", String.class);
		return new Api(clusterName, applicationName, host, category, path);
	}

}
