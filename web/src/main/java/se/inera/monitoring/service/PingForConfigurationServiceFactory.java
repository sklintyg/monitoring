package se.inera.monitoring.service;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

import se.riv.itintegration.monitoring.rivtabp21.v1.PingForConfigurationResponderInterface;

public interface PingForConfigurationServiceFactory {

    /**
     * Builds the interface to be pinged through a {@link JaxWsProxyFactoryBean}.
     * 
     * @param nodeUrl
     *            The URL for the node
     * @return Returns the Interface to ask for configuration of the node
     */
    PingForConfigurationResponderInterface getPingInterface(String nodeUrl);

}
