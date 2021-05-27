package indi.atlantis.framework.jellyfish.slf4j.logback;

import indi.atlantis.framework.vortex.common.NamedSelectionPartitioner;
import indi.atlantis.framework.vortex.common.TcpTransportClient;
import indi.atlantis.framework.vortex.common.TransportClient;

/**
 * 
 * TcpTransportClientAppender
 *
 * @author Fred Feng
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
