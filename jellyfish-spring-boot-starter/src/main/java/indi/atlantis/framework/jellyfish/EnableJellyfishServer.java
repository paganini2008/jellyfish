package indi.atlantis.framework.jellyfish;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import indi.atlantis.framework.jellyfish.ui.JellyfishUIAutoConfiguration;
import indi.atlantis.framework.seafloor.EnableApplicationCluster;
import indi.atlantis.framework.vortex.EnableNioTransport;

/**
 * 
 * EnableJellyfishServer
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@EnableNioTransport
@EnableApplicationCluster(enableLeaderElection = true, enableMonitor = true)
@Import({ JellyfishAutoConfiguration.class, JellyfishUIAutoConfiguration.class })
public @interface EnableJellyfishServer {
}
