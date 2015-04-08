package se.inera.monitoring.config;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import se.inera.monitoring.service.configuration.ServiceConfiguration;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@ComponentScan("se.inera.monitoring.service")
@EnableAsync
@EnableScheduling
public class ServiceConfig {

    @Bean
    public ServiceConfiguration getNodes() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            ClassPathResource cpr = new ClassPathResource("serviceconfiguration.json");
            return mapper.readValue(cpr.getFile(), ServiceConfiguration.class);
        } catch (JsonParseException e) {
            System.err.println("COULD NOT PARSE JSON");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
