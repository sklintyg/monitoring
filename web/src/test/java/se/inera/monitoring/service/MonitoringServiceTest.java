package se.inera.monitoring.service;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.PageRequest;

import se.inera.monitoring.persistence.ApplicationStatusRepository;
import se.inera.monitoring.persistence.model.ApplicationStatus;
import se.inera.monitoring.persistence.model.SubsystemStatus;
import se.inera.monitoring.service.configuration.Node;
import se.inera.monitoring.service.configuration.Service;
import se.inera.monitoring.service.configuration.ServiceConfiguration;
import se.inera.monitoring.web.domain.UserCount;

@RunWith(MockitoJUnitRunner.class)
public class MonitoringServiceTest {

    private static final String SERVER = "webcert";

    private static final String SERVICE = "randomService";
    private static final String[] SUBSYSTEMS = { "hsa", "db" };

    @Mock
    ServiceConfiguration conf;

    @Mock
    ApplicationStatusRepository repo;

    @InjectMocks
    MonitoringServiceImpl monitoringService;

    List<ApplicationStatus> inputList;
    List<UserCount> outputList;
    Service testService;

    @Before
    public void init() {
        testService = new Service();
        testService.setNodes(Arrays.asList(new Node(), new Node(), new Node()));
        inputList = new ArrayList<>();
        outputList = new ArrayList<>();
        LocalDateTime date = new LocalDateTime();
        for (int i = 0; i < 100; i++) {
            ApplicationStatus newStatus = new ApplicationStatus();
            newStatus.setApplication(SERVICE);
            newStatus.setServer(SERVER);
            newStatus.setTimestamp(new Timestamp(date.minusMinutes(i).toDateTime().getMillis()));
            newStatus.setCurrentUsers(i);
            ArrayList<SubsystemStatus> subservices = new ArrayList<>();
            for (String subsystem : SUBSYSTEMS) {
                SubsystemStatus subsystemStatus = new SubsystemStatus();
                subsystemStatus.setSeverity(0);
                subsystemStatus.setStatus("ok");
                subsystemStatus.setSubsystem(subsystem);
                subservices.add(subsystemStatus);
            }
            newStatus.setSubsystemStatuses(subservices);
            inputList.add(newStatus);

            outputList.add(new UserCount(SERVER, i, date.minusMinutes(i).toString(MonitoringServiceImpl.formatter)));
        }
        Collections.reverse(outputList);
    }

    @Test
    public void testGetCountersCallingRepository() {
        when(repo.findByApplicationOrderByTimestampDescServerDesc(eq(SERVICE), any(PageRequest.class))).thenReturn(inputList);
        when(conf.getService(eq(SERVICE))).thenReturn(testService);
        monitoringService.getCountersBySystem(SERVICE, 100);
        verify(repo, times(1)).findByApplicationOrderByTimestampDescServerDesc(eq(SERVICE), any(PageRequest.class));
    }

    @Test
    public void testGetCountersAscendingOrder() {
        when(repo.findByApplicationOrderByTimestampDescServerDesc(eq(SERVICE), any(PageRequest.class))).thenReturn(inputList);
        List<UserCount> res = monitoringService.getCountersBySystem(SERVICE, 100);

        // We know that nothing happened before 1970-01-01. This is when the UNIX gods created man.
        LocalDateTime date = new LocalDateTime(0);

        for (UserCount count : res) {
            LocalDateTime newdate = LocalDateTime.parse(count.getTimeStamp(), MonitoringServiceImpl.formatter);
            if (newdate.isBefore(date)) {
                fail(String.format("Order is not ascending; %s is supposed to come after %s",
                        newdate.toString(MonitoringServiceImpl.formatter),
                        date.toString(MonitoringServiceImpl.formatter)));
            }
        }
    }

    @Test
    public void testGetCountersReturnValue() {
        when(repo.findByApplicationOrderByTimestampDescServerDesc(eq(SERVICE), any(PageRequest.class))).thenReturn(inputList);
        when(conf.getService(eq(SERVICE))).thenReturn(testService);
        assertArrayEquals("MontioringServiceImpl should return the data recieved from repo",
                outputList.toArray(),
                monitoringService.getCountersBySystem(SERVICE, 100).toArray());
    }

    @Ignore
    @Test
    public void testGetCountersReturnEmptyListOnUnknownService() {
        when(repo.findByApplicationOrderByTimestampDescServerDesc(eq(SERVICE), any(PageRequest.class))).thenReturn(new ArrayList<ApplicationStatus>());
        assertEquals("Counters list should be empty on unknown service",
                0,
                monitoringService.getCountersBySystem(SERVICE, 100).size());
    }

    @Ignore
    @Test
    public void testGetStatusReturnsLastStatus() {
        when(repo.findByApplicationOrderByTimestampDescServerDesc(eq(SERVICE), any(PageRequest.class))).thenReturn(inputList);
        assertEquals("Timestamp should be the latest one",
                outputList.get(outputList.size() - 1).getTimeStamp(),
                monitoringService.getStatusBySystem(SERVICE).get(0).getTimestamp());
    }

    @Ignore
    @Test
    public void testGetStatusReturnsNullOnUnknownService() {
        when(repo.findByApplicationOrderByTimestampDescServerDesc(eq(SERVICE), any(PageRequest.class))).thenReturn(null);
        assertNull("Status should be null when none can be found",
                monitoringService.getStatusBySystem(SERVICE));
    }
}
