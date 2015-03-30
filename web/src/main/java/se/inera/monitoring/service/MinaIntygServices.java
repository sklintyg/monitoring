package se.inera.monitoring.service;

import java.util.HashSet;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MinaIntygServices {

    public static final Logger log = LoggerFactory.getLogger(MinaIntygServices.class);

    @Value("${minaintyg.status.fields}")
    private String fieldsProp;

    private HashSet<String> fields = new HashSet<>();

    public MinaIntygServices() {
    }

    @PostConstruct
    public void readFields() {
        String[] split = fieldsProp.split(";");
        for (String field : split) {
            getFields().add(field);
        }
        log.info(String.format("Will import the following statuses from Mina Intyg %s", fields.toString()));
    }

    public HashSet<String> getFields() {
        return fields;
    }
}
