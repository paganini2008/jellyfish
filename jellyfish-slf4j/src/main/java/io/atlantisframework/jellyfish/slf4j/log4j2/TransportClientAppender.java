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
package io.atlantisframework.jellyfish.slf4j.log4j2;

import java.io.Serializable;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.util.ReadOnlyStringMap;

import com.github.paganini2008.devtools.Env;
import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.SystemPropertyUtils;
import com.github.paganini2008.devtools.net.NetUtils;

import io.atlantisframework.vortex.common.HttpTransportClient;
import io.atlantisframework.vortex.common.MultipleChoicePartitioner;
import io.atlantisframework.vortex.common.TcpTransportClient;
import io.atlantisframework.vortex.common.TransportClient;
import io.atlantisframework.vortex.common.Tuple;

/**
 * 
 * TransportClientAppender
 *
 * @author Fred Feng
 *
 * @since 2.0.4
 */
@Plugin(name = "LogTracker", category = "Core", elementType = "appender", printObject = true)
public class TransportClientAppender extends AbstractAppender {

	private static final String GLOBAL_TOPIC_NAME = "slf4j";
	private TransportClient transportClient;
	private String clusterName = "default";
	private String applicationName;
	private String host = getHostIfAvailable();
	private String identifier = String.valueOf(Env.getPid());
	private String protocal = "http";
	private String brokerUrl;

	public TransportClientAppender(String name, Filter filter, Layout<? extends Serializable> layout, Property[] properties) {
		super(name, filter, layout, true, properties);
	}

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
	public final void start() {
		this.setStarting();
		switch (protocal) {
		case "http":
			this.transportClient = new HttpTransportClient(brokerUrl);
			break;
		case "tcp":
			TcpTransportClient transportClient = new TcpTransportClient(brokerUrl);
			transportClient.setPartitioner(new MultipleChoicePartitioner());
			this.transportClient = transportClient;
			break;
		default:
			throw new UnsupportedOperationException(protocal);
		}
		super.start();
	}

	@Override
	public void append(LogEvent event) {
		if (transportClient == null) {
			return;
		}
		Tuple tuple = Tuple.newOne(GLOBAL_TOPIC_NAME);
		tuple.setField("clusterName", clusterName);
		tuple.setField("applicationName", applicationName);
		tuple.setField("host", host);
		tuple.setField("identifier", identifier);
		tuple.setField("loggerName", event.getLoggerName());
		String msg = event.getMessage().getFormattedMessage();
		tuple.setField("message", msg);
		tuple.setField("level", event.getLevel().name());
		String reason = event.getThrownProxy() != null ? event.getThrownProxy().getExtendedStackTraceAsString() : "";
		tuple.setField("reason", reason);
		tuple.setField("marker", event.getMarker() != null ? event.getMarker().getName() : "");
		tuple.setField("timestamp", event.getTimeMillis());
		ReadOnlyStringMap mdc = event.getContextData();
		if (mdc != null && mdc.size() > 0) {
			tuple.append(mdc.toMap());
		}
		transportClient.write(tuple);
	}

	private static String getHostIfAvailable() {
		String localAddress = NetUtils.getLocalHost();
		Integer port = SystemPropertyUtils.getInteger("server.port");
		if (port != null && port.intValue() > 0) {
			return localAddress + ":" + port;
		}
		return localAddress;
	}

	@PluginFactory
	public static TransportClientAppender createAppender(@PluginAttribute("name") String name,
			@PluginElement("Layout") Layout<? extends Serializable> layout, @PluginElement("Filters") Filter filter,
			@PluginAttribute("clusterName") String clusterName, @PluginAttribute("applicationName") String applicationName,
			@PluginAttribute("host") String host, @PluginAttribute("identifier") String identifier,
			@PluginAttribute("protocal") String protocal, @PluginAttribute("brokerUrl") String brokerUrl) {
		TransportClientAppender transportClientAppender = new TransportClientAppender(name, filter, layout, null);
		if (StringUtils.isNotBlank(clusterName)) {
			transportClientAppender.setClusterName(clusterName);
		}
		if (StringUtils.isNotBlank(applicationName)) {
			transportClientAppender.setApplicationName(applicationName);
		}
		if (StringUtils.isNotBlank(host)) {
			transportClientAppender.setHost(host);
		}
		if (StringUtils.isNotBlank(identifier)) {
			transportClientAppender.setIdentifier(identifier);
		}
		if (StringUtils.isNotBlank(protocal)) {
			transportClientAppender.setProtocal(protocal);
		}
		if (StringUtils.isNotBlank(brokerUrl)) {
			transportClientAppender.setBrokerUrl(brokerUrl);
		}
		return transportClientAppender;
	}

}
