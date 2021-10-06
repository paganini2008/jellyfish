/**
* Copyright 2017-2021 Fred Feng (paganini.fy@gmail.com)

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
package io.atlantisframework.jellyfish.slf4j.logback;

import java.util.Map;

import com.github.paganini2008.devtools.Env;
import com.github.paganini2008.devtools.SystemPropertyUtils;
import com.github.paganini2008.devtools.collection.MapUtils;
import com.github.paganini2008.devtools.net.NetUtils;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import io.atlantisframework.vortex.common.HttpTransportClient;
import io.atlantisframework.vortex.common.NamedSelectionPartitioner;
import io.atlantisframework.vortex.common.TcpTransportClient;
import io.atlantisframework.vortex.common.TransportClient;
import io.atlantisframework.vortex.common.Tuple;

/**
 * 
 * TransportClientAppender
 *
 * @author Fred Feng
 * @since 2.0.1
 */
public class TransportClientAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

	private static final String GLOBAL_TOPIC_NAME = "slf4j";
	private TransportClient transportClient;
	private String clusterName = "default";
	private String applicationName;
	private String host = getHostIfAvailable();
	private String identifier = String.valueOf(Env.getPid());
	private String protocal = "http";
	private String brokerUrl;

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public void setProtocal(String protocal) {
		this.protocal = protocal;
	}

	public void setBrokerUrl(String brokerUrl) {
		this.brokerUrl = brokerUrl;
	}

	@Override
	protected void append(ILoggingEvent eventObject) {
		if (transportClient == null) {
			return;
		}
		Tuple tuple = Tuple.newOne(GLOBAL_TOPIC_NAME);
		tuple.setField("clusterName", clusterName);
		tuple.setField("applicationName", applicationName);
		tuple.setField("host", host);
		tuple.setField("identifier", identifier);
		tuple.setField("loggerName", eventObject.getLoggerName());
		String msg = eventObject.getFormattedMessage();
		tuple.setField("message", msg);
		tuple.setField("level", eventObject.getLevel().toString());
		String reason = ThrowableProxyUtil.asString(eventObject.getThrowableProxy());
		tuple.setField("reason", reason);
		tuple.setField("marker", eventObject.getMarker() != null ? eventObject.getMarker().getName() : "");
		tuple.setField("timestamp", eventObject.getTimeStamp());
		Map<String, String> mdc = eventObject.getMDCPropertyMap();
		if (MapUtils.isNotEmpty(mdc)) {
			tuple.append(mdc);
		}
		transportClient.write(tuple);
	}

	@Override
	public final void start() {
		switch (protocal) {
		case "http":
			this.transportClient = new HttpTransportClient(brokerUrl);
			break;
		case "tcp":
			TcpTransportClient transportClient = new TcpTransportClient(brokerUrl);
			transportClient.setPartitioner(new NamedSelectionPartitioner());
			this.transportClient = transportClient;
			break;
		}
		super.start();
	}

	private static String getHostIfAvailable() {
		String localAddress = NetUtils.getLocalHost();
		Integer port = SystemPropertyUtils.getInteger("server.port");
		if (port != null && port.intValue() > 0) {
			return localAddress + ":" + port;
		}
		return localAddress;
	}

}
