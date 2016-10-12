package se.inera.monitoring.service.configuration;

public class Config {

    private String name;
    private ConfigType type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ConfigType getType() {
        return type != null ? type : ConfigType.NORMAL;
    }

    public void setType(ConfigType type) {
        this.type = type;
    }
}
