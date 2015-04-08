package se.inera.monitoring.web.controller;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import se.inera.monitoring.service.MonitoringService;
import se.inera.monitoring.web.domain.StatusResponse;
import se.inera.monitoring.web.domain.UserCount;

@RestController
@RequestMapping("/api")
public class MonitoringController {

    @Autowired
    MonitoringService monitoringService;

    @RequestMapping(value = "counters/{system}", method = GET, produces = "application/json")
    public List<UserCount> getCountersBySystem(
            @PathVariable String system,
            @RequestParam(defaultValue = "100", value = "entries") int entries) {
        return monitoringService.getCountersBySystem(system, entries);
    }

    @RequestMapping(value = "status/{system}", method = GET, produces = "application/json")
    public StatusResponse getStatusBySystem(@PathVariable String system) {
        return monitoringService.getStatusBySystem(system);
    }
}
