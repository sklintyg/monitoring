package se.inera.monitoring.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource({ "classpath:META-INF/cxf/cxf.xml", "classpath:/ws-config.xml" })
public class WsConfig {
}
