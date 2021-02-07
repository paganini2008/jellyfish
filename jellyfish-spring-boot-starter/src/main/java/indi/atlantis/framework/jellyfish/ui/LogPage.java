package indi.atlantis.framework.jellyfish.ui;

import java.util.Date;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.github.paganini2008.devtools.date.DateUtils;

/**
 * 
 * LogPage
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
@RequestMapping("/atlantis/jellyfish/log")
@Controller
public class LogPage {

	@GetMapping("/realtime")
	public String realtime(Model ui) {
		return "realtime";
	}

	@GetMapping("/query")
	public String query(Model ui) {
		Date now = new Date();
		ui.addAttribute("startDate", DateUtils.format(now, "yyyy-MM-dd 00:00:00"));
		ui.addAttribute("endDate", DateUtils.format(now, "yyyy-MM-dd HH:mm:ss"));
		return "query";
	}

}
