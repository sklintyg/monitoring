package se.inera.monitoring.service;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.PageRequest;

import se.inera.monitoring.persistence.ApplicationStatusRepository;
import se.inera.monitoring.persistence.model.ApplicationStatus;
import se.inera.monitoring.persistence.model.SubsystemStatus;
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

    @Before
    public void init() {
        inputList = new ArrayList<>();
        outputList = new ArrayList<>();
        LocalDateTime date = new LocalDateTime();
        for (int i = 0; i < 100; i++) {
            ApplicationStatus newStatus = new ApplicationStatus();
            newStatus.setApplication(SERVICE);
            newStatus.setServer(SERVER);
            newStatus.setTimestamp(date.minusMinutes(i).toDate());
            newStatus.setCurrentUsers(i);
            ArrayList<SubsystemStatus> subservices = new ArrayList<>();
            for (String subsystem : SUBSYSTEMS) {
                SubsystemStatus subsystemStatus = new SubsystemStatus();
                subsystemStatus.setSeverity(0);
                subsystemStatus.setStatus("ok");
                subsystemStatus.setSubsystem(subsystem);
                subservices.add(subsystemStatus);
            }
            newStatus.setSubsystemStatus(subservices);
            inputList.add(newStatus);

            outputList.add(new UserCount(i, date.minusMinutes(i).toString(MonitoringServiceImpl.formatter)));
        }
        Collections.reverse(outputList);
    }

    @Test
    public void testGetCountersCallingRepository() {
        when(repo.findByApplicationOrderByTimestampDesc(eq(SERVICE), any(PageRequest.class))).thenReturn(inputList);
        monitoringService.getCountersBySystem(SERVICE, 100);
        verify(repo, times(1)).findByApplicationOrderByTimestampDesc(eq(SERVICE), any(PageRequest.class));
    }

    @Test
    public void testGetCountersAscendingOrder() {
        when(repo.findByApplicationOrderByTimestampDesc(eq(SERVICE), any(PageRequest.class))).thenReturn(inputList);
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
        when(repo.findByApplicationOrderByTimestampDesc(eq(SERVICE), any(PageRequest.class))).thenReturn(inputList);
        assertArrayEquals("MontioringServiceImpl should return the data recieved from repo",
                outputList.toArray(),
                monitoringService.getCountersBySystem(SERVICE, 100).toArray());
    }

    @Test
    public void testGetCountersReturnEmptyListOnUnknownService() {
        when(repo.findByApplicationOrderByTimestampDesc(eq(SERVICE), any(PageRequest.class))).thenReturn(new ArrayList<ApplicationStatus>());
        assertEquals("Counters list should be empty on unknown service",
                0,
                monitoringService.getCountersBySystem(SERVICE, 100).size());
    }

    @Test
    public void testGetStatusReturnsLastStatus() {
        when(repo.findByApplicationOrderByTimestampDesc(eq(SERVICE), any(PageRequest.class))).thenReturn(inputList);
        assertEquals("Timestamp should be the latest one",
                outputList.get(outputList.size() - 1).getTimeStamp(),
                monitoringService.getStatusBySystem(SERVICE).getTimestamp());
    }

    @Test
    public void testGetStatusReturnsNullOnUnknownService() {
        when(repo.findByApplicationOrderByTimestampDesc(eq(SERVICE), any(PageRequest.class))).thenReturn(null);
        assertNull("Status should be null when none can be found",
                monitoringService.getStatusBySystem(SERVICE));
    }
}
