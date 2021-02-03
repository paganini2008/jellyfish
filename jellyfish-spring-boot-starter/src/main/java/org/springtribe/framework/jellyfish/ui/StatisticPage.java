package org.springtribe.framework.jellyfish.ui;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springtribe.framework.jellyfish.stat.Catalog;

/**
 * 
 * StatisticPage
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
@RequestMapping("/application/cluster/statistic")
@Controller
public class StatisticPage {

	@GetMapping("/")
	public String realtime(Model ui) {
		return "stat";
	}

	@GetMapping("/detail")
	public String query(@RequestParam("identifier") String identifier, Model ui) {
		ui.addAttribute("catalog", Catalog.decode(identifier));
		return "stat_detail";
	}
}
