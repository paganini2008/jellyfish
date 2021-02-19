package indi.atlantis.framework.jellyfish.http;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

/**
 * 
 * EnableJellyfishHttpWatcher
 * 
 * @author Fred Feng
 *
 * @version 1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(JellyfishHttpAutoConfiguration.class)
public @interface EnableJellyfishHttpWatcher {
}
