package se.inera.monitoring.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import se.inera.monitoring.persistence.ApplicationStatusRepository;
import se.inera.monitoring.service.configuration.ServiceConfiguration;
import se.inera.monitoring.web.domain.StatusResponse;
import se.inera.monitoring.web.domain.UserCount;

@RunWith(MockitoJUnitRunner.class)
public class MonitoringServiceTest {
    @Mock
    ServiceConfiguration conf;

    @Mock
    ApplicationStatusRepository repo;

    @InjectMocks
    MonitoringServiceImpl monitoringService;

    @Test
    public void testGetStatusBySystem() {
        final String system = "System";
        when(repo.getStatus(eq(system))).thenReturn(Arrays.asList(new StatusResponse()));
        List<StatusResponse> res = monitoringService.getStatusBySystem(system);
        assertNotNull(res);
        assertEquals(1, res.size());
        verify(repo, times(1)).getStatus(eq(system));
    }

    @Test
    public void testGetStatusBySystemEmptyListOnNull() {
        final String system = "System";
        when(repo.getStatus(eq(system))).thenReturn(null);
        List<StatusResponse> res = monitoringService.getStatusBySystem(system);
        assertNotNull(res);
        assertTrue(res.isEmpty());
        verify(repo, times(1)).getStatus(eq(system));
    }

    @Test
    public void testGetCountersBySystem() {
        final String system = "System";
        when(repo.getUserCount(eq(system))).thenReturn(Arrays.asList(new UserCount(system, 0, "now")));
        List<UserCount> res = monitoringService.getCountersBySystem(system);
        assertNotNull(res);
        assertEquals(1, res.size());
        assertEquals(system, res.get(0).getServer());
        verify(repo, times(1)).getUserCount(eq(system));
    }

    @Test
    public void testGetCounterBySystemEmptyListOnNull() {
        final String system = "System";
        when(repo.getUserCount(eq(system))).thenReturn(null);
        List<UserCount> res = monitoringService.getCountersBySystem(system);
        assertNotNull(res);
        assertTrue(res.isEmpty());
        verify(repo, times(1)).getUserCount(eq(system));
    }
}
