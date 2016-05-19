package se.inera.monitoring.service;

import static se.inera.monitoring.service.ConfigResponse.convert;

import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import se.inera.monitoring.service.configuration.ConfigVersion;
import se.riv.clinicalprocess.healthcond.monitoring.rivtabp21.v1.InternalPingForConfigurationResponderInterface;
import se.riv.clinicalprocess.healthcond.monitoring.v1.InternalPingForConfigurationType;
import se.riv.itintegration.monitoring.rivtabp21.v1.PingForConfigurationResponderInterface;
import se.riv.itintegration.monitoring.v1.PingForConfigurationType;

@Component
public class PingForConfigurationFactoryImpl implements PingForConfigurationFactory {

    private static final Logger log = LoggerFactory.getLogger(PingForConfigurationFactoryImpl.class);

    /*
     * (non-Javadoc)
     *
     * @see se.inera.monitoring.service.PingForConfigurationFactory#ping(java.lang.String)
     */
    @Override
    public ConfigResponse ping(String nodeUrl, ConfigVersion version) throws ServiceNotReachableException {

        try {
            switch (version) {
            case PING_FOR_CONFIGURATION:
                PingForConfigurationResponderInterface pingInterface = getInterface(nodeUrl, PingForConfigurationResponderInterface.class);
                return convert(pingInterface.pingForConfiguration("", new PingForConfigurationType()));
            case INTERNAL_PING_FOR_CONFIGURATION:
                InternalPingForConfigurationResponderInterface internalPingInterface = getInterface(nodeUrl, InternalPingForConfigurationResponderInterface.class);
                return convert(internalPingInterface.internalPingForConfiguration("", new InternalPingForConfigurationType()));
            default:
                throw new IllegalArgumentException("Unsupported version");
            }

        } catch (Exception e) {
            log.error(String.format("Could not reach target URL %s", nodeUrl), e);
            throw new ServiceNotReachableException();
        }
    }

    /**
     * Retrieves the interface to ping.
     *
     * @param nodeUrl
     * @return
     */
    @SuppressWarnings("unchecked")
    private <T> T getInterface(String nodeUrl, Class<T> clazz) {
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.getInInterceptors().add(new LoggingInInterceptor());
        factory.getOutInterceptors().add(new LoggingOutInterceptor());
        factory.setServiceClass(clazz);
        factory.setAddress(nodeUrl);
        T ping = (T) factory.create();
        return ping;
    }
}
