package se.inera.monitoring.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@ComponentScan("se.inera.monitoring.service")
@EnableAsync
@EnableScheduling
public class ServiceConfig {

}
