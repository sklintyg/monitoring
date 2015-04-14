package se.inera.monitoring.service;

import se.riv.itintegration.monitoring.v1.PingForConfigurationResponseType;

public interface PingForConfigurationFactory {

    /**
     * Pings the node located at the URL {@code nodeUrl}
     * 
     * @param nodeUrl
     *            The URL of which the service can be found
     * @return The configuration of the service
     * @throws ServiceNotReachableException
     */
    PingForConfigurationResponseType ping(String nodeUrl) throws ServiceNotReachableException;

}
