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

    /**
     * Returns the requested service configuration.
     * 
     * @param serviceName
     *            The name of the requested service
     * @return The configuration of the requested service
     */
    public Service getService(String serviceName) {
        for (Service service : services) {
            if (service.getServiceName().equals(serviceName)) {
                return service;
            }
        }
        return null;
    }

    public List<Service> getServices() {
        return services;
    }

    public void setServices(List<Service> services) {
        this.services = services;
    }

}
