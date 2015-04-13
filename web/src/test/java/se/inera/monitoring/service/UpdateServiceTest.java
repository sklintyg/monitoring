package se.inera.monitoring.service;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import se.inera.monitoring.persistence.ApplicationStatusRepository;
import se.inera.monitoring.service.configuration.Node;
import se.inera.monitoring.service.configuration.Service;
import se.inera.monitoring.service.configuration.ServiceConfiguration;
import se.riv.itintegration.monitoring.rivtabp21.v1.PingForConfigurationResponderInterface;
import se.riv.itintegration.monitoring.v1.PingForConfigurationResponseType;
import se.riv.itintegration.monitoring.v1.PingForConfigurationType;

@RunWith(MockitoJUnitRunner.class)
public class UpdateServiceTest {

    private static final String SERVICE = "test_service";

    @Mock
    ServiceConfiguration config;

    @Mock
    ApplicationStatusRepository repo;

    @Mock
    PingForConfigurationServiceFactory serviceFactory;

    @Mock
    PingForConfigurationResponderInterface pingInterface;

    @InjectMocks
    UpdateService updateService;

    @Before
    public void init() {
        Service service = new Service();
        service.setServiceName(SERVICE);
        service.setConfigurations(Arrays.asList("hsa", "db"));

        List<Node> nodes = new ArrayList<>();

        Node node1 = new Node();
        node1.setNodeName("node1");
        node1.setNodeUrl("/dev/null");
        nodes.add(node1);

        Node node2 = new Node();
        node2.setNodeName("node2");
        node2.setNodeUrl("/dev/null");
        nodes.add(node2);

        service.setNodes(nodes);

        ArrayList<Service> serviceList = new ArrayList<>();
        serviceList.add(service);

        when(config.getService(eq(SERVICE))).thenReturn(service);
        when(config.getServices()).thenReturn(serviceList);
        when(serviceFactory.getPingInterface(any(String.class))).thenReturn(pingInterface);
    }

    @Test
    public void testAccessedOnceForEachNode() {
        when(pingInterface.pingForConfiguration(any(String.class), any(PingForConfigurationType.class))).thenReturn(
                new PingForConfigurationResponseType());
        updateService.update();
        verify(pingInterface, times(2)).pingForConfiguration(any(String.class), any(PingForConfigurationType.class));
    }

}
