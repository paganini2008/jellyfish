package com.github.paganini2008.logbox.logback;

import org.springtribe.framework.gearless.common.NamedSelectionPartitioner;
import org.springtribe.framework.gearless.common.TcpTransportClient;
import org.springtribe.framework.gearless.common.TransportClient;

/**
 * 
 * TcpTransportClientAppender
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public class TcpTransportClientAppender extends TransportClientAppenderBase {

	private String brokerUrl;

	public void setBrokerUrl(String brokerUrl) {
		this.brokerUrl = brokerUrl;
	}

	@Override
	protected TransportClient buildTransportClient() {
		TcpTransportClient transportClient = new TcpTransportClient(brokerUrl);
		transportClient.setPartitioner(new NamedSelectionPartitioner());
		return transportClient;
	}

}
