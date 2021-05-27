package indi.atlantis.framework.jellyfish;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * SearchQuery
 *
 * @author Fred Feng
 * @version 1.0
 */
@Getter
@Setter
public class SearchQuery {

	private String keyword;
	private String clusterName = "default";
	private String applicationName;
	private String host;
	private String identifier;
	private String loggerName;
	private String level;
	private String marker;
	private Boolean asc;
}
