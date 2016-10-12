package se.inera.monitoring.service.configuration;

import java.util.List;

public class Service {

    private String serviceName;
    private List<Config> configurations;
    private List<Node> nodes;
    private ConfigVersion version;

    public Service() {
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public List<Config> getConfigurations() {
        return configurations;
    }

    public void setConfigurations(List<Config> configurations) {
        this.configurations = configurations;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    public ConfigVersion getVersion() {
        return version;
    }

    public void setVersion(ConfigVersion version) {
        this.version = version;
    }

    public boolean hasConfig(String configName) {
        return configurations.stream().anyMatch(c -> c.getName().equals(configName));
    }

    public ConfigType getType(String configName) {
        return configurations.stream().filter(c -> c.getName().equals(configName)).findAny().map(Config::getType).orElse(null);
    }
}
