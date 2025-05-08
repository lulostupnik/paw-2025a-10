package ar.edu.itba.paw.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import ar.edu.itba.paw.models.PageParams;
import ar.edu.itba.paw.models.User;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import ar.edu.itba.paw.interfaces.persistence.EventDao;
import ar.edu.itba.paw.interfaces.persistence.EventResponseDao;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Event;
import ar.edu.itba.paw.models.EventResponse;
import ar.edu.itba.paw.models.Page;

@RunWith(MockitoJUnitRunner.class)
public class EventResponseServiceImplTest {

    private static final long USER_ID = 0;
    private static final String USERNAME = "user";
    private static final long EVENT_ID = 1;
    private static final long ID = 2;
    private static final String MESSAGE = "message";
    private static final LocalDateTime TIMESTAMP = LocalDateTime.now().withNano(0);
    private static final EventResponse RESPONSE = new EventResponse(ID, USER_ID, USERNAME, EVENT_ID, MESSAGE, TIMESTAMP);
    private static final Event EVENT = new Event(EVENT_ID, null, null, MESSAGE, ID, null, USERNAME, null, MESSAGE, null, 0);
    private static final User USER = new User(ID, null, null, null, null, null, null, ID, null, false);

//    @InjectMocks
//    private EventResponseServiceImpl responseService;
//
//    @Mock
//    private EventResponseDao responseDao;
//    @Mock
//    private EventDao eventDao;
//    @Mock
//    private EmailService emailService;
//    @Mock
//    private UserService userService;
//
//    @Test
//    public void testCreate(){
//        Mockito.when(
//            responseDao.create(Mockito.eq(USER_ID), Mockito.eq(USERNAME), Mockito.eq(EVENT_ID), Mockito.eq(MESSAGE), Mockito.eq(TIMESTAMP))
//        ).thenReturn(RESPONSE);
//
//        EventResponse response = responseService.createResponse(USER_ID, USERNAME, EVENT_ID, MESSAGE, TIMESTAMP);
//
//        assertEquals(RESPONSE, response);
//    }
//
//    @Test
//    public void testDelete(){
//        Mockito.when(
//            responseDao.findById(Mockito.eq(ID))
//        ).thenReturn(Optional.of(RESPONSE));
//        Mockito.when(
//            eventDao.findById(Mockito.eq(EVENT_ID))
//        ).thenReturn(Optional.of(EVENT));
//        Mockito.when(
//            userService.findById(Mockito.eq(USER_ID))
//        ).thenReturn(Optional.of(USER));
//
//        responseService.deleteResponse(ID, MESSAGE);
//    }
//    @Test(expected = IllegalArgumentException.class)
//    public void testDeleteUserNotFound(){
//        Mockito.when(
//            responseDao.findById(Mockito.eq(ID))
//        ).thenReturn(Optional.of(RESPONSE));
//        Mockito.when(
//            eventDao.findById(Mockito.eq(EVENT_ID))
//        ).thenReturn(Optional.of(EVENT));
//        Mockito.when(
//            userService.findById(Mockito.eq(USER_ID))
//        ).thenReturn(Optional.empty());
//
//        responseService.deleteResponse(ID, MESSAGE);
//    }
//    @Test(expected = IllegalStateException.class)
//    public void testDeleteEventNotFound(){
//        Mockito.when(
//            responseDao.findById(Mockito.eq(ID))
//        ).thenReturn(Optional.of(RESPONSE));
//        Mockito.when(
//            eventDao.findById(Mockito.eq(EVENT_ID))
//        ).thenReturn(Optional.empty());
//
//        responseService.deleteResponse(ID, MESSAGE);
//    }
//    @Test(expected = IllegalArgumentException.class)
//    public void testDeleteResponseNotFound(){
//        Mockito.when(
//            responseDao.findById(Mockito.eq(ID))
//        ).thenReturn(Optional.empty());
//
//        responseService.deleteResponse(ID, MESSAGE);
//    }
//
//    @Test
//    public void testGetCount(){
//        Mockito.when(
//            responseDao.getCount(Mockito.eq(EVENT_ID))
//        ).thenReturn(1);
//
//        int count = responseService.getResponseCount(EVENT_ID);
//
//        assertEquals(1, count);
//    }
//
//    @Test
//    public void testGetEventIdByResponseId(){
//        Mockito.when(
//            responseDao.getEventIdByResponseId(Mockito.eq(ID))
//        ).thenReturn(EVENT_ID);
//
//        long eventId = responseService.getEventIdByResponseId(ID);
//
//        assertEquals(EVENT_ID, eventId);
//    }
//
//    @Test
//    public void testListAllFromEvent(){
//        Mockito.when(
//            responseDao.listAllFromEvent(Mockito.eq(EVENT_ID))
//        ).thenReturn(List.of(RESPONSE));
//
//        List<EventResponse> responses = responseService.listAllResponseFromEvent(EVENT_ID);
//
//        assertNotNull(responses);
//        assertEquals(1, responses.size());
//        assertEquals(RESPONSE, responses.getFirst());
//    }
//
//    @Test
//    public void testListAllFromEventPaged(){
//        Page<EventResponse> testPage = new Page<EventResponse>(List.of(RESPONSE), 1, 1);
//        Mockito.when(
//            responseDao.listAllFromEvent(Mockito.eq(EVENT_ID), Mockito.eq(1), Mockito.eq(2))
//        ).thenReturn(testPage);
//
//        Page<EventResponse> page = responseService.listAllResponseFromEvent(EVENT_ID,new PageParams(1,2));
//
//        assertNotNull(page);
//        assertEquals(testPage, page);
//    }
//
//    @Test
//    public void testDeleteByEventId(){
//        responseService.deleteResponseByEventId(EVENT_ID);
//    }


}
