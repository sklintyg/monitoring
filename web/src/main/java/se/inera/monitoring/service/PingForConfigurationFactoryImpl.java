package se.inera.monitoring.service;

import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import se.riv.itintegration.monitoring.rivtabp21.v1.PingForConfigurationResponderInterface;
import se.riv.itintegration.monitoring.v1.PingForConfigurationResponseType;
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
    public PingForConfigurationResponseType ping(String nodeUrl) throws ServiceNotReachableException {

        try {
            PingForConfigurationResponderInterface pingInterface = getInterface(nodeUrl);
            return pingInterface.pingForConfiguration("", new PingForConfigurationType());
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
    private PingForConfigurationResponderInterface getInterface(String nodeUrl) {
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.getInInterceptors().add(new LoggingInInterceptor());
        factory.getOutInterceptors().add(new LoggingOutInterceptor());
        factory.setServiceClass(PingForConfigurationResponderInterface.class);
        factory.setAddress(nodeUrl);
        PingForConfigurationResponderInterface ping = (PingForConfigurationResponderInterface) factory.create();
        return ping;
    }
}
