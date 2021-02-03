package org.springdessert.framework.jellyfish.ui;

import org.springdessert.framework.jellyfish.stat.Catalog;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
