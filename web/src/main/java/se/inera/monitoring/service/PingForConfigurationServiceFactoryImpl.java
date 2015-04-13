package se.inera.monitoring.service;

import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.springframework.stereotype.Component;

import se.riv.itintegration.monitoring.rivtabp21.v1.PingForConfigurationResponderInterface;

@Component
public class PingForConfigurationServiceFactoryImpl implements PingForConfigurationServiceFactory {
    
    /* (non-Javadoc)
     * @see se.inera.monitoring.service.PingForConfigurationServiceFactory#getPingInterface(java.lang.String)
     */
    @Override
    public PingForConfigurationResponderInterface getPingInterface(String nodeUrl) {
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.getInInterceptors().add(new LoggingInInterceptor());
        factory.getOutInterceptors().add(new LoggingOutInterceptor());
        factory.setServiceClass(PingForConfigurationResponderInterface.class);
        factory.setAddress(nodeUrl);
        PingForConfigurationResponderInterface ping = (PingForConfigurationResponderInterface) factory.create();
        return ping;
    }

}
