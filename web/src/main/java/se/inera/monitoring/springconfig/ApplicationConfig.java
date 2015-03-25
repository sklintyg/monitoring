package se.inera.monitoring.springconfig;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@ComponentScan("se.inera.monitoring")
@Import({WebConfig.class, PersistenceConfig.class})
@PropertySource(value = "classpath:application.properties")
@EnableAsync
@EnableScheduling
public class ApplicationConfig {

}