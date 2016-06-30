package se.inera.monitoring.service.configuration;

import java.util.List;

/**
 * The format of which the json file should be on. An object mapper will construct this object with which contain where
 * relevant information about the services can be found.
 *
 * @author kaan
 *
 */
public class ServiceConfiguration {
    private List<Service> services;

    public ServiceConfiguration() {
    }

    public List<Service> getServices() {
        return services;
    }

    public void setServices(List<Service> services) {
        this.services = services;
    }

}
