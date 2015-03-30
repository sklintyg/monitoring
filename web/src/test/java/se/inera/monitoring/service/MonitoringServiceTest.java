package se.inera.monitoring.service;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;

import se.inera.monitoring.persistence.StatusRepository;
import se.inera.monitoring.persistence.UserCountRepository;
import se.inera.monitoring.persistence.dao.UserCount;
import se.inera.monitoring.service.MonitoringServiceImpl;

@RunWith(MockitoJUnitRunner.class)
public class MonitoringServiceTest {

    @Mock
    private StatusRepository statusRepo;

    @Mock
    private UserCountRepository countRepo;

    @InjectMocks
    private MonitoringServiceImpl monitoring;

    private List<UserCount> counts = new ArrayList<>();
    private List<se.inera.monitoring.web.domain.UserCount> dtos = new ArrayList<>();

    @Before
    public void init() {
        LocalDateTime date = LocalDateTime.now();
        Random rand = new Random();
        for (int i = 0; i < 100; i++) {
            date = date.minusMinutes(1);
            int count = rand.nextInt(1000);

            UserCount newCount = new UserCount();
            newCount.setService("webcert");
            newCount.setCount(count);
            newCount.setTimestamp(new Timestamp(date.toDateTime().getMillis()));

            se.inera.monitoring.web.domain.UserCount newDto = new se.inera.monitoring.web.domain.UserCount(count,
                    date.toString(MonitoringServiceImpl.formatter));

            counts.add(newCount);
            dtos.add(newDto);
        }
        Collections.reverse(dtos);
    }

    @Test
    public void testCounter() {
        int testValue = 337;
        counts.get(0).setCount(testValue);
        when(countRepo.findByServiceOrderByTimestampDesc(eq("webcert"), any(Pageable.class))).thenReturn(
                new SliceImpl<UserCount>(counts.subList(0, 1)));

        List<se.inera.monitoring.web.domain.UserCount> testCounters = monitoring.getCountersBySystem("webcert", 1);
        assertNotNull(testCounters);
        assertEquals(1, testCounters.size());
        assertEquals(testValue, testCounters.get(0).getCount());
    }

    @Test
    public void testTimestamp() {
        String timeString = "2015-03-25 22:45:10";
        counts.get(0).setTimestamp(new Timestamp(MonitoringServiceImpl.formatter.parseDateTime(timeString).toDateTime().getMillis()));
        when(countRepo.findByServiceOrderByTimestampDesc(eq("webcert"), any(Pageable.class))).thenReturn(
                new SliceImpl<UserCount>(counts.subList(0, 1)));

        List<se.inera.monitoring.web.domain.UserCount> count = monitoring.getCountersBySystem("webcert", 1);
        assertNotNull(count);
        assertEquals(1, count.size());
        assertEquals(timeString, count.get(0).getTimeStamp());
    }

    @Test
    public void testCountersAreAscending() {
        when(countRepo.findByServiceOrderByTimestampDesc(Mockito.eq("webcert"), any(Pageable.class))).thenReturn(new SliceImpl<UserCount>(counts));

        assertArrayEquals(dtos.toArray(), monitoring.getCountersBySystem("webcert", 100).toArray());
    }
}
