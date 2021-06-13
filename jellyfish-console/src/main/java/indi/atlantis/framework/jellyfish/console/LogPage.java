/**
* Copyright 2021 Fred Feng (paganini.fy@gmail.com)

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
package indi.atlantis.framework.jellyfish.console;

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
 * @author Fred Feng
 * @version 1.0
 */
@RequestMapping("/jellyfish/log")
@Controller
public class LogPage {

	@GetMapping("/")
	public String realtime(Model ui) {
		return "log";
	}

	@GetMapping("/query")
	public String query(Model ui) {
		Date now = new Date();
		ui.addAttribute("startDate", DateUtils.format(now, "yyyy-MM-dd 00:00:00"));
		ui.addAttribute("endDate", DateUtils.format(now, "yyyy-MM-dd HH:mm:ss"));
		return "log_history";
	}

}
