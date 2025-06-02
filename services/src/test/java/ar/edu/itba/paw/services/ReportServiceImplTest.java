package ar.edu.itba.paw.services;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import ar.edu.itba.paw.interfaces.persistence.ReportDao;
import ar.edu.itba.paw.interfaces.services.EventService;
import ar.edu.itba.paw.interfaces.services.JourneyService;
import ar.edu.itba.paw.models.Event;
import ar.edu.itba.paw.models.EventResponse;
import ar.edu.itba.paw.models.Journey;
import ar.edu.itba.paw.models.JourneyResponse;
import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.PageParams;
import ar.edu.itba.paw.models.Report;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.enums.ReportStatus;
import ar.edu.itba.paw.models.exceptions.EventNotFoundException;
import ar.edu.itba.paw.models.exceptions.EventResponseNotFoundException;
import ar.edu.itba.paw.models.exceptions.JourneyNotFoundException;
import ar.edu.itba.paw.models.exceptions.JourneyResponseNotFoundException;
import ar.edu.itba.paw.models.exceptions.ReportNotFoundException;

@RunWith(MockitoJUnitRunner.class)
public class ReportServiceImplTest {

    private static final long REPORT_ID = 0;
    private static final long USER_ID = 1;
    private static final long JOURNEY_ID = 2;
    private static final long EVENT_ID = 3;
    private static final long EVENT_RESPONSE_ID = 4;
    private static final long JOURNEY_RESPONSE_ID = 5;
    private static final String DESC = "illegal";
    private static final User USER = new User(USER_ID, DESC, DESC, DESC, DESC, null, null, JOURNEY_ID, null, false, false);
    private static final Report REPORT = new Report(USER, USER, DESC, DESC);
    private static final Journey JOURNEY = new Journey(JOURNEY_ID, USER, null, null, null, DESC);
    private static final Event EVENT = new Event(EVENT_ID, USER, null, DESC, EVENT_ID, null, DESC, null, DESC, null, 0);
    private static final EventResponse EVENT_RESPONSE = new EventResponse(EVENT_RESPONSE_ID, USER, EVENT, DESC, null);
    private static final JourneyResponse JOURNEY_RESPONSE = new JourneyResponse(JOURNEY_RESPONSE_ID, USER, JOURNEY, DESC, null);
    private static final PageParams PAGE_PARAMS = new PageParams(1, 10);
    private static final List<Report> REPORTS = List.of(REPORT);
    private static final Page<Report> REPORT_PAGE = new Page<>(REPORTS, 1, 1);
    @InjectMocks
    ReportServiceImpl reportService;

    @Mock
    ReportDao reportDao;
    @Mock
    JourneyService journeyService;
    @Mock
    EventService eventService;

    @Test
    public void testCreateReportForJourney(){
        when(
            journeyService.getJourneyById(eq(JOURNEY_ID))
        ).thenReturn(Optional.of(JOURNEY));
        when(
            reportDao.create(
                eq(USER), 
                eq(USER), 
                eq(JOURNEY), 
                eq(DESC), 
                eq(DESC)
            )
        ).thenReturn(REPORT);

        Report report = reportService.createReportForJourney(
            USER, 
            JOURNEY_ID, 
            DESC, 
            DESC
        );

        assertNotNull(report);
        assertEquals(REPORT, report);
    }
    @Test(expected = JourneyNotFoundException.class)
    public void testCreateReportForJourneyNotFound(){
        when(
            journeyService.getJourneyById(eq(JOURNEY_ID))
        ).thenReturn(Optional.empty());

        reportService.createReportForJourney(
            USER, 
            JOURNEY_ID, 
            DESC, 
            DESC
        );
    }
    
    @Test
    public void testCreateReportForEvent(){
        when(
            eventService.findEventById(eq(EVENT_ID))
        ).thenReturn(Optional.of(EVENT));
        when(
            reportDao.create(
                eq(USER), 
                eq(USER), 
                eq(EVENT), 
                eq(DESC), 
                eq(DESC)
            )
        ).thenReturn(REPORT);

        Report report = reportService.createReportForEvent(
            USER, 
            EVENT_ID, 
            DESC,
            DESC
        );

        assertNotNull(report);
        assertEquals(REPORT, report);
    }
    @Test(expected = EventNotFoundException.class)
    public void testCreateReportForEventNotFound(){
        when(
            eventService.findEventById(eq(EVENT_ID))
        ).thenReturn(Optional.empty());

        reportService.createReportForEvent(
            USER, 
            EVENT_ID, 
            DESC, 
            DESC
        );
    }

    @Test
    public void testCreateReportForEventResponse(){
        when(
            eventService.findEventResponseById(eq(EVENT_RESPONSE_ID))
        ).thenReturn(Optional.of(EVENT_RESPONSE));
        when(
            reportDao.create(
                eq(USER), 
                eq(USER), 
                eq(EVENT), 
                eq(DESC), 
                eq(DESC)
            )
        ).thenReturn(REPORT);

        Report report = reportService.createReportForEventResponse(
            USER, 
            EVENT_RESPONSE_ID, 
            DESC, 
            DESC
        );

        assertNotNull(report);
        assertEquals(REPORT, report);
    }
    @Test(expected = EventResponseNotFoundException.class)
    public void testCreateReportForEventResponseNotFound(){
        when(
            eventService.findEventResponseById(eq(EVENT_RESPONSE_ID))
        ).thenReturn(Optional.empty());

        reportService.createReportForEventResponse(
            USER, 
            EVENT_RESPONSE_ID, 
            DESC, 
            DESC
        );
    }

    @Test
    public void testCreateReportForJourneyResponse(){
        when(
            journeyService.findJourneyResponseById(eq(JOURNEY_RESPONSE_ID))
        ).thenReturn(Optional.of(JOURNEY_RESPONSE));
        when(
            reportDao.create(
                eq(USER), 
                eq(USER), 
                eq(JOURNEY), 
                eq(DESC), 
                eq(DESC)
            )
        ).thenReturn(REPORT);

        Report report = reportService.createReportForJourneyResponse(
            USER, 
            JOURNEY_RESPONSE_ID, 
            DESC, 
            DESC
        );

        assertNotNull(report);
        assertEquals(REPORT, report);
    }
    @Test(expected = JourneyResponseNotFoundException.class)
    public void testCreateReportForJourneyResponseNotFound(){
        when(
            journeyService.findJourneyResponseById(eq(JOURNEY_RESPONSE_ID))
        ).thenReturn(Optional.empty());

        reportService.createReportForJourneyResponse(
            USER, 
            JOURNEY_RESPONSE_ID, 
            DESC, 
            DESC
        );
    }

    @Test   
    public void testFindById(){
        when(
            reportDao.findById(eq(REPORT_ID))
        ).thenReturn(Optional.of(REPORT));

        Optional<Report> maybeReport = reportService.findById(REPORT_ID);

        assertNotNull(maybeReport);
        assertEquals(REPORT, maybeReport.get());
    }

    @Test
    public void testFindByUserPaginated(){
        when(
            reportDao.findByUserPaginated(eq(USER), any(PageParams.class))
        ).thenReturn(REPORT_PAGE);

        Page<Report> reports = reportService.findByUserPaginated(USER, PAGE_PARAMS);

        assertNotNull(reports);
        assertEquals(REPORT_PAGE, reports);
    }

    @Test
    public void testCountReportsAgainstUser(){
        when(
            reportDao.countReportsAgainstUser(eq(USER))
        ).thenReturn((long)REPORTS.size());

        long reports = reportService.countReportsAgainstUser(USER);

        assertEquals(REPORTS.size(), reports);
    }

    @Test
    public void testFindAllPaginated(){
        when(
            reportDao.findAllPaginated(any(PageParams.class))
        ).thenReturn(REPORT_PAGE);

        Page<Report> reports = reportService.findAllPaginated(PAGE_PARAMS);

        assertNotNull(reports);
        assertEquals(REPORT_PAGE, reports);
    }

    @Test
    public void testFindByStatusPaginated(){
        when(
            reportDao.findByStatusPaginated(
                eq(ReportStatus.PENDING), 
                any(PageParams.class)
            )
        ).thenReturn(REPORT_PAGE);

        Page<Report> reports = reportService.findByStatusPaginated(ReportStatus.PENDING, PAGE_PARAMS);

        assertNotNull(reports);
        assertEquals(REPORT_PAGE, reports);
    }

    @Test
    public void testDelete(){
        reportService.delete(REPORT);
    }

    @Test
    public void testDeleteById(){
        reportService.deleteById(REPORT_ID);
    }

    @Test
    public void testFindAll(){
        when(
            reportDao.findAll(eq(DESC), any(PageParams.class))
        ).thenReturn(REPORT_PAGE);

        Page<Report> reports = reportService.findAll(DESC, PAGE_PARAMS);

        assertNotNull(reports);
        assertEquals(REPORT_PAGE, reports);
    }

    @Test
    public void testUpdateReportStatus(){
        Report newReport = new Report(USER, USER, DESC, DESC);
        newReport.setStatus(ReportStatus.PENDING);
        when(
            reportDao.findById(eq(REPORT_ID))
        ).thenReturn(Optional.of(REPORT));

        Report report = reportService.updateReportStatus(REPORT_ID, ReportStatus.UNDER_REVIEW);

        assertNotNull(report);
        assertEquals(ReportStatus.UNDER_REVIEW, report.getStatus());
    }
    @Test(expected = ReportNotFoundException.class)
    public void testUpdateReportStatusNotFound(){
        when(
            reportDao.findById(eq(REPORT_ID))
        ).thenReturn(Optional.empty());

        reportService.updateReportStatus(REPORT_ID, ReportStatus.UNDER_REVIEW);
    }

}
