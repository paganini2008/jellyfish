package indi.atlantis.framework.jellyfish.console;

import java.io.File;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.github.paganini2008.devtools.Env;
import com.github.paganini2008.devtools.io.FileUtils;

import indi.atlantis.framework.jellyfish.EnableJellyfishServer;

/**
 * 
 * JellyfishConsoleServer
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
@EnableJellyfishServer
@SpringBootApplication
public class JellyfishConsoleServer {

	static {
		System.setProperty("spring.devtools.restart.enabled", "false");
		File logDir = FileUtils.getFile(FileUtils.getUserDirectory(), "logs", "indi", "atlantis", "framework", "jellyfish", "console");
		if (!logDir.exists()) {
			logDir.mkdirs();
		}
		System.setProperty("LOG_BASE", logDir.getAbsolutePath());
	}

	public static void main(String[] args) {
		SpringApplication.run(JellyfishConsoleServer.class, args);
		System.out.println(Env.getPid());
	}
	
}
