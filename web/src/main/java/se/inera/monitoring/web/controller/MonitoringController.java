package se.inera.monitoring.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import se.inera.monitoring.web.domain.Status;
import se.inera.monitoring.web.domain.UserCount;
import se.inera.monitoring.web.service.MonitoringService;

@Controller
@RequestMapping("/api")
public class MonitoringController {

	@Autowired
	MonitoringService monitoringService;

	@RequestMapping("counters/{system}")
	@ResponseBody
	public List<UserCount> getCountersBySystem(
			@PathVariable String system,
			@RequestParam(defaultValue = "100", value = "entries") String entries) {
		int count = 100;
		try {
			count = Integer.parseInt(entries);
		} catch (NumberFormatException e) {
			// TODO What do here?
		}
		return monitoringService.getCountersBySystem(system, count);
	}

	@RequestMapping("status/{system}")
	@ResponseBody
	public List<Status> getStatusBySystem(@PathVariable String system) {
		return monitoringService.getStatusBySystem(system);
	}
}
