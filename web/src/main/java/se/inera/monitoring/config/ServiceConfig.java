package se.inera.monitoring.config;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import se.inera.monitoring.service.configuration.ServiceConfiguration;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@ComponentScan("se.inera.monitoring")
@EnableAsync
@EnableScheduling
public class ServiceConfig {

    @Value("${monitoring.service.configuration}")
    private String serviceConfigurationLocation;

    @Bean
    public ServiceConfiguration getNodes() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            File serviceConfig = new File(serviceConfigurationLocation);
            return mapper.readValue(serviceConfig, ServiceConfiguration.class);
        } catch (JsonParseException e) {
            System.err.println("Could not parse JSON-input from service configuration");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
