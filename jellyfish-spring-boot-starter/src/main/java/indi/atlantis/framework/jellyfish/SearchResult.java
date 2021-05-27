package indi.atlantis.framework.jellyfish;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * SearchResult
 *
 * @author Fred Feng
 * @version 1.0
 */
@Getter
@Setter
public class SearchResult implements Serializable {

	private static final long serialVersionUID = 8569951355416294604L;

	public static final String SEARCH_FIELD_MESSAGE = "message";
	public static final String SEARCH_FIELD_REASON = "reason";
	public static final String SORTED_FIELD_CREATE_TIME = "createTime";

	private Long id;
	private String clusterName;
	private String applicationName;
	private String host;
	private String identifier;
	private String marker;
	private String loggerName;
	private String message;
	private String level;
	private String[] stackTraces;
	private String datetime;

	public String getId() {
		return String.valueOf(id);
	}

}
