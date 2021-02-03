package com.github.paganini2008.logbox.logback;

import org.springtribe.framework.gearless.common.HttpTransportClient;
import org.springtribe.framework.gearless.common.TransportClient;

/**
 * 
 * HttpTransportClientAppender
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public class HttpTransportClientAppender extends TransportClientAppenderBase {

	private String brokerUrl;

	public void setBrokerUrl(String brokerUrl) {
		this.brokerUrl = brokerUrl;
	}

	@Override
	protected TransportClient buildTransportClient() {
		return new HttpTransportClient(brokerUrl);
	}

}
