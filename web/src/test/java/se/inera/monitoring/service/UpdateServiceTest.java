package se.inera.monitoring.service;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.Assert.*;

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
import se.inera.monitoring.persistence.model.ApplicationStatus;
import se.inera.monitoring.persistence.model.SubsystemStatus;
import se.inera.monitoring.service.configuration.Node;
import se.inera.monitoring.service.configuration.Service;
import se.inera.monitoring.service.configuration.ServiceConfiguration;
import se.riv.itintegration.monitoring.v1.PingForConfigurationResponseType;

@RunWith(MockitoJUnitRunner.class)
public class UpdateServiceTest {

    private static final String SERVICE = "test_service";

    @Mock
    ServiceConfiguration config;

    @Mock
    ApplicationStatusRepository repo;

    @Mock
    PingForConfigurationFactory pingFactory;

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

        Node node2 = node1;
        nodes.add(node2);

        service.setNodes(nodes);

        ArrayList<Service> serviceList = new ArrayList<>();
        serviceList.add(service);

        when(config.getService(eq(SERVICE))).thenReturn(service);
        when(config.getServices()).thenReturn(serviceList);
    }

    @Test
    public void testAccessedOnceForEachNode() throws ServiceNotReachableException {
        when(pingFactory.ping(any(String.class))).thenReturn(new PingForConfigurationResponseType());
        updateService.update();
        verify(pingFactory, times(2)).ping(any(String.class));
    }

    @Test
    public void testSavedOnceForEachNode() throws ServiceNotReachableException {
        when(pingFactory.ping(any(String.class))).thenReturn(new PingForConfigurationResponseType());
        updateService.update();
        verify(repo, times(2)).save(any(ApplicationStatus.class));
    }

    @Test
    public void testUpdateSeverityOk() {
        SubsystemStatus status = new SubsystemStatus();
        status.setStatus("ok");
        status.setSubsystem("test");
        assertEquals("ok in status should equal a severity of OK", UpdateService.OK, updateService.updateSeverity(status).getSeverity());
    }

    @Test
    public void testUpdateSeverityFail() {
        SubsystemStatus status = new SubsystemStatus();
        status.setStatus("fail");
        status.setSubsystem("test");
        assertEquals("fail in status should equal a severity of FAIL", UpdateService.FAIL, updateService.updateSeverity(status).getSeverity());
    }

    @Test
    public void testUpdateSeverityQueueWarn() {
        SubsystemStatus status = new SubsystemStatus();
        status.setSubsystem("randomqueue");
        status.setStatus(Integer.toString(UpdateService.queueWarnThreshold + 1));
        assertEquals("Higher queue size than queueWarnThreshold should return WARN", UpdateService.WARN, updateService.updateSeverity(status)
                .getSeverity());
    }

    @Test
    public void testUpdateSeverityQueueOk() {
        SubsystemStatus status = new SubsystemStatus();
        status.setSubsystem("randomqueue");
        status.setStatus(Integer.toString(UpdateService.queueWarnThreshold - 1));
        assertEquals("lower queue size than queueWarnThreshold should return OK", UpdateService.OK, updateService.updateSeverity(status)
                .getSeverity());
    }

    @Test
    public void testUpdateSeverityQueueFail() {
        SubsystemStatus status = new SubsystemStatus();
        status.setSubsystem("randomqueue");
        status.setStatus(Integer.toString(UpdateService.queueFailThreshold + 1));
        assertEquals("higher queuesize than queueFailThreshold should return FAIL", UpdateService.FAIL, updateService.updateSeverity(status)
                .getSeverity());
    }

    @Test
    public void testUpdateSeverityUnkownShouldNotUpdate() {
        SubsystemStatus status = new SubsystemStatus();
        status.setSubsystem("unknown_service");
        status.setStatus("unknown_status");
        status.setSeverity(-1);
        assertEquals("Unkown substatus should leave severity unchanged", -1, updateService.updateSeverity(status).getSeverity());
    }
}
