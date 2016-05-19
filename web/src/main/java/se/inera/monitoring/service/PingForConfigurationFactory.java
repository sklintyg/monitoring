package se.inera.monitoring.service;

import se.inera.monitoring.service.configuration.ConfigVersion;

public interface PingForConfigurationFactory {

    /**
     * Pings the node located at the URL {@code nodeUrl}
     *
     * @param nodeUrl
     *            The URL of which the service can be found
     * @param version
     *            How the service should ping the server
     * @return The configuration of the service
     * @throws ServiceNotReachableException
     */
    ConfigResponse ping(String nodeUrl, ConfigVersion version) throws ServiceNotReachableException;
}
