/**
* Copyright 2018-2021 Fred Feng (paganini.fy@gmail.com)

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
