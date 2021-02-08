package indi.atlantis.framework.jellyfish.slf4j.logback;

import indi.atlantis.framework.vortex.common.HttpTransportClient;
import indi.atlantis.framework.vortex.common.TransportClient;

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
