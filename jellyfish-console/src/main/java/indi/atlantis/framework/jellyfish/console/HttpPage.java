package indi.atlantis.framework.jellyfish.console;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import indi.atlantis.framework.jellyfish.http.Api;

/**
 * 
 * HttpPage
 *
 * @author Fred Feng
 * @version 1.0
 */
@RequestMapping("/jellyfish/http")
@Controller
public class HttpPage {

	@GetMapping("/")
	public String realtime(Model ui) {
		return "stat";
	}

	@GetMapping("/detail")
	public String query(@RequestParam("identifier") String identifier, Model ui) {
		ui.addAttribute("api", Api.decode(identifier));
		return "stat_detail";
	}
}
