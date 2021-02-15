package indi.atlantis.framework.jellyfish.slf4j.logback;

import java.util.Map;

import com.github.paganini2008.devtools.Env;
import com.github.paganini2008.devtools.SystemPropertyUtils;
import com.github.paganini2008.devtools.collection.MapUtils;
import com.github.paganini2008.devtools.net.NetUtils;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import indi.atlantis.framework.vortex.common.TransportClient;
import indi.atlantis.framework.vortex.common.Tuple;

/**
 * 
 * TransportClientAppenderBase
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public abstract class TransportClientAppenderBase extends UnsynchronizedAppenderBase<ILoggingEvent> {

	public static final String GLOBAL_TOPIC_NAME = "slf4j";
	private TransportClient transportClient;
	private String clusterName = "default";
	private String applicationName;
	private String host = getHostIfAvailable();
	private String identifier = String.valueOf(Env.getPid());

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
		this.transportClient = buildTransportClient();
		super.start();
	}

	protected abstract TransportClient buildTransportClient();

	private static String getHostIfAvailable() {
		String localAddress = NetUtils.getLocalHost();
		Integer port = SystemPropertyUtils.getInteger("server.port");
		if (port != null && port.intValue() > 0) {
			return localAddress + ":" + port;
		}
		return localAddress;
	}

}
