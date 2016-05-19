package se.inera.monitoring.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import se.riv.clinicalprocess.healthcond.monitoring.v1.InternalPingForConfigurationResponseType;
import se.riv.itintegration.monitoring.v1.PingForConfigurationResponseType;

public class ConfigResponse {

    private String version;
    private List<Configuration> configuration;

    public ConfigResponse() {
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<Configuration> getConfiguration() {
        if (configuration == null) {
            configuration = new ArrayList<>();
        }
        return configuration;
    }

    public void setConfiguration(List<Configuration> configuration) {
        this.configuration = configuration;
    }

    public static ConfigResponse convert(PingForConfigurationResponseType source) {
        ConfigResponse res = new ConfigResponse();
        res.setVersion(source.getVersion());
        res.setConfiguration(source.getConfiguration().stream().map(Configuration::convert).collect(Collectors.toList()));
        return res;
    }

    public static ConfigResponse convert(InternalPingForConfigurationResponseType source) {
        ConfigResponse res = new ConfigResponse();
        res.setVersion(source.getVersion());
        res.setConfiguration(source.getConfiguration().stream().map(Configuration::convert).collect(Collectors.toList()));
        return res;
    }

}
