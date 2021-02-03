package org.springtribe.framework.jellyfish.stat;

import java.nio.charset.Charset;

import org.springframework.util.Base64Utils;
import org.springtribe.framework.gearless.common.Tuple;

import com.github.paganini2008.devtools.Assert;
import com.github.paganini2008.devtools.CharsetUtils;
import com.github.paganini2008.devtools.StringUtils;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * Catalog
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
public final class Catalog implements Comparable<Catalog> {

	private static final String IDENTIFIER_PATTERN = "%s+%s+%s+%s+%s";
	private static final String NULL = "*";
	private static final Charset DEFAULT_CHARSET = CharsetUtils.UTF_8;

	private String clusterName;
	private String applicationName;
	private String host;
	private String category;
	private String path;

	public Catalog(String clusterName, String applicationName, String host, String category, String path) {
		Assert.hasNoText(clusterName, "ClusterName must not be null");
		this.clusterName = clusterName;
		this.applicationName = StringUtils.isNotBlank(applicationName) ? applicationName : NULL;
		this.host = StringUtils.isNotBlank(host) ? host : NULL;
		this.category = StringUtils.isNotBlank(category) ? category : NULL;
		this.path = StringUtils.isNotBlank(path) ? path : NULL;
	}

	public Catalog() {
	}

	public String getIdentifier() {
		final String repr = String.format(IDENTIFIER_PATTERN, clusterName, applicationName, host, category, path);
		return Base64Utils.encodeToString(repr.getBytes(DEFAULT_CHARSET));
	}

	public static Catalog decode(String identifier) {
		String repr = new String(Base64Utils.decodeFromString(identifier), DEFAULT_CHARSET);
		String[] args = repr.split("\\+", 5);
		if (args.length != 5) {
			throw new IllegalArgumentException("Invalid identifier: " + repr);
		}
		return new Catalog(args[0], args[1], args[2], args[3], args[4]);
	}

	public static Catalog of(Tuple tuple) {
		String clusterName = tuple.getField("clusterName", String.class);
		String applicationName = tuple.getField("applicationName", String.class);
		String host = tuple.getField("host", String.class);
		String category = tuple.getField("category", String.class);
		String path = tuple.getField("path", String.class);
		return new Catalog(clusterName, applicationName, host, category, path);
	}

	@Override
	public int compareTo(Catalog other) {
		String repr = String.format(IDENTIFIER_PATTERN, clusterName, applicationName, host, category, path);
		String otherRepr = String.format(IDENTIFIER_PATTERN, other.getClusterName(), other.getApplicationName(), other.getHost(),
				other.getCategory(), other.getPath());
		return repr.compareTo(otherRepr);
	}

}
