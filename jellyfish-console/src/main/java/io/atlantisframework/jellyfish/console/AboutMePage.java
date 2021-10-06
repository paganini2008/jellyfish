package io.atlantisframework.jellyfish.console;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 
 * AboutMePage
 *
 * @author Fred Feng
 *
 * @since 2.0.4
 */
@RequestMapping("/jellyfish/about")
@Controller
public class AboutMePage {

	@GetMapping("/")
	public String quickstart() {
		return "about";
	}
}
