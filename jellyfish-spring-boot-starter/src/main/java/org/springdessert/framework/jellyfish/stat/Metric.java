package org.springdessert.framework.jellyfish.stat;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * Metric
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
@Getter
@Setter
@ToString
public class Metric implements Serializable {

	private static final long serialVersionUID = -3894234853704794221L;

	private String clusterName;
	private String applicationName;
	private String host;
	private String path;
	private Long highestValue;
	private Long lowestValue;
	private Long totalValue;
	private Long middleValue;
	private Integer count;
	private long timestamp;
	
	@JsonInclude(Include.NON_NULL)
	private Integer successCount;
	
	@JsonInclude(Include.NON_NULL)
	private Integer failedCount;
	
	@JsonInclude(Include.NON_NULL)
	private Integer timeoutCount;
	


}
