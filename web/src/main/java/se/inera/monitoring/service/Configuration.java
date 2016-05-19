package se.inera.monitoring.service;

import se.riv.itintegration.monitoring.v1.ConfigurationType;

public class Configuration {

    private String name;
    private String value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static Configuration convert(ConfigurationType source) {
        Configuration res = new Configuration();
        res.setName(source.getName());
        res.setValue(source.getValue());
        return res;
    }

    public static Configuration convert(se.riv.clinicalprocess.healthcond.monitoring.v1.ConfigurationType source) {
        Configuration res = new Configuration();
        res.setName(source.getName());
        res.setValue(source.getValue());
        return res;
    }
}
