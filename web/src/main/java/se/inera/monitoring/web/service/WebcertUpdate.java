package se.inera.monitoring.web.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import se.riv.itintegration.monitoring.rivtabp21.v1.PingForConfigurationResponderInterface;
import se.riv.itintegration.monitoring.v1.PingForConfigurationResponseType;
import se.riv.itintegration.monitoring.v1.PingForConfigurationType;

@Service
public class WebcertUpdate {

	private static final Logger log = LoggerFactory
			.getLogger(WebcertUpdate.class);

	@Autowired
	PingForConfigurationResponderInterface webcert;

	@Scheduled(cron = "${webcert.ping.cron}")
	public void update() {
		PingForConfigurationResponseType res = webcert.pingForConfiguration("123", new PingForConfigurationType());
		log.info(res.getConfiguration().toString());
	}
}
