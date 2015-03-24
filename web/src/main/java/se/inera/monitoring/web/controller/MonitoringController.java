package se.inera.monitoring.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import se.inera.monitoring.web.domain.Counters;
import se.inera.monitoring.web.service.MonitoringService;

@Controller
@RequestMapping("/")
public class MonitoringController {
    
    @Autowired
	MonitoringService monitoringService;
	
	@RequestMapping("counters/{system}")
	@ResponseBody
	public Counters getCountersBySystem(@PathVariable String system) {
		return monitoringService.getCountersBySystem(system);
	}
}
