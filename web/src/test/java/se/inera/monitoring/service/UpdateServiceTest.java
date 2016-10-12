package se.inera.monitoring.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.*;

import org.joda.time.DateTimeUtils;
import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

import se.inera.monitoring.persistence.ApplicationStatusRepository;
import se.inera.monitoring.service.configuration.*;
import se.inera.monitoring.web.domain.StatusResponse;
import se.inera.monitoring.web.domain.UserCount;

@RunWith(MockitoJUnitRunner.class)
public class UpdateServiceTest {

    private static final String CONFIGURATION_NAME_QUEUE = "queue";
    private static final String CONFIGURATION_NAME_STANDARD = "hsa";
    private static final String SERVER_NAME = "node1";
    private static final String TIME_STRING = "2016-01-02 11:23";
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
        service.setConfigurations(Arrays.asList(buildConfig(CONFIGURATION_NAME_STANDARD, ConfigType.NORMAL), buildConfig("db", ConfigType.NORMAL),
                buildConfig(CONFIGURATION_NAME_QUEUE, ConfigType.NORMAL)));

        List<Node> nodes = new ArrayList<>();

        Node node1 = new Node();
        node1.setNodeName(SERVER_NAME);
        node1.setNodeUrl("/dev/null");
        nodes.add(node1);

        service.setNodes(nodes);

        ArrayList<Service> serviceList = new ArrayList<>();
        serviceList.add(service);

        when(config.getServices()).thenReturn(serviceList);
        LocalDateTime date = LocalDateTime.parse(TIME_STRING, UpdateService.formatter);
        DateTimeUtils.setCurrentMillisFixed(date.toDateTime().getMillis());
    }

    @Test
    public void testSavedOnceForEachNode() throws ServiceNotReachableException {
        when(pingFactory.ping(any(String.class), any(ConfigVersion.class))).thenReturn(new ConfigResponse());
        updateService.update();
        verify(repo, times(1)).save(any(UserCount.class), anyString());
        verify(repo, times(1)).save(any(StatusResponse.class));
        verify(pingFactory, times(1)).ping(any(String.class), any(ConfigVersion.class));
    }

    @Test
    public void testUpdateSavesCounter() throws ServiceNotReachableException {
        ConfigResponse configResponse = new ConfigResponse();
        Configuration configuration = new Configuration();
        configuration.setName(UpdateService.CURRENT_USERS_NAME);
        configuration.setValue("123");
        configResponse.setConfiguration(Arrays.asList(configuration));
        when(pingFactory.ping(any(String.class), any(ConfigVersion.class))).thenReturn(configResponse);

        updateService.update();

        ArgumentCaptor<UserCount> captor = ArgumentCaptor.forClass(UserCount.class);
        verify(repo, times(1)).save(captor.capture(), eq(SERVICE));
        assertEquals(SERVER_NAME, captor.getValue().getServer());
        assertEquals(123, captor.getValue().getCount());
        assertEquals(TIME_STRING, captor.getValue().getTimeStamp());
    }

    @Test
    public void testUpdateSavesStatusResponse() throws ServiceNotReachableException {
        ConfigResponse configResponse = new ConfigResponse();
        Configuration configuration = new Configuration();
        configuration.setName(CONFIGURATION_NAME_STANDARD);
        configuration.setValue("ok");
        configResponse.setConfiguration(Arrays.asList(configuration));
        when(pingFactory.ping(any(String.class), any(ConfigVersion.class))).thenReturn(configResponse);
        updateService.update();
        ArgumentCaptor<StatusResponse> captor = ArgumentCaptor.forClass(StatusResponse.class);
        verify(repo, times(1)).save(captor.capture());
        assertEquals(SERVER_NAME, captor.getValue().getServer());
        assertEquals(SERVICE, captor.getValue().getApplication());
        assertEquals(TIME_STRING, captor.getValue().getTimestamp());
        assertEquals(1, captor.getValue().getStatuses().size());
        assertEquals(CONFIGURATION_NAME_STANDARD, captor.getValue().getStatuses().get(0).getServiceName());
        assertEquals("ok", captor.getValue().getStatuses().get(0).getStatuscode());
        assertEquals(UpdateService.OK, captor.getValue().getStatuses().get(0).getSeverity());
    }

    @Test
    public void testUpdateSavesStatusResponseFail() throws ServiceNotReachableException {
        ConfigResponse configResponse = new ConfigResponse();
        Configuration configuration = new Configuration();
        configuration.setName(CONFIGURATION_NAME_STANDARD);
        configuration.setValue("fail");
        configResponse.setConfiguration(Arrays.asList(configuration));
        when(pingFactory.ping(any(String.class), any(ConfigVersion.class))).thenReturn(configResponse);
        updateService.update();
        ArgumentCaptor<StatusResponse> captor = ArgumentCaptor.forClass(StatusResponse.class);
        verify(repo, times(1)).save(captor.capture());
        assertEquals(SERVER_NAME, captor.getValue().getServer());
        assertEquals(SERVICE, captor.getValue().getApplication());
        assertEquals(TIME_STRING, captor.getValue().getTimestamp());
        assertEquals(1, captor.getValue().getStatuses().size());
        assertEquals(CONFIGURATION_NAME_STANDARD, captor.getValue().getStatuses().get(0).getServiceName());
        assertEquals("fail", captor.getValue().getStatuses().get(0).getStatuscode());
        assertEquals(UpdateService.FAIL, captor.getValue().getStatuses().get(0).getSeverity());
    }

    @Test
    public void testUpdateSavesStatusResponseQueue() throws ServiceNotReachableException {
        ConfigResponse configResponse = new ConfigResponse();
        Configuration configuration = new Configuration();
        configuration.setName(CONFIGURATION_NAME_QUEUE);
        configuration.setValue("1");
        configResponse.setConfiguration(Arrays.asList(configuration));
        when(pingFactory.ping(any(String.class), any(ConfigVersion.class))).thenReturn(configResponse);
        updateService.update();
        ArgumentCaptor<StatusResponse> captor = ArgumentCaptor.forClass(StatusResponse.class);
        verify(repo, times(1)).save(captor.capture());
        assertEquals(SERVER_NAME, captor.getValue().getServer());
        assertEquals(SERVICE, captor.getValue().getApplication());
        assertEquals(TIME_STRING, captor.getValue().getTimestamp());
        assertEquals(1, captor.getValue().getStatuses().size());
        assertEquals(CONFIGURATION_NAME_QUEUE, captor.getValue().getStatuses().get(0).getServiceName());
        assertEquals("1", captor.getValue().getStatuses().get(0).getStatuscode());
        assertEquals(UpdateService.OK, captor.getValue().getStatuses().get(0).getSeverity());
    }

    @Test
    public void testUpdateServiceNotReachable() throws ServiceNotReachableException {
        when(pingFactory.ping(any(String.class), any(ConfigVersion.class))).thenThrow(new ServiceNotReachableException());
        updateService.update();
        verify(repo, times(1)).save(any(StatusResponse.class));
        verify(repo, times(1)).save(any(UserCount.class), eq(SERVICE));
    }

    private Config buildConfig(String name, ConfigType type) {
        Config config = new Config();
        config.setName(name);
        config.setType(type);
        return config;
    }
}
