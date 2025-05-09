// package ar.edu.itba.paw.services;

// import static org.junit.Assert.assertEquals;
// import static org.junit.Assert.assertNotNull;

// import java.time.LocalDateTime;
// import java.util.List;
// import java.util.Optional;

// import org.junit.Test;
// import org.junit.runner.RunWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.Mockito;
// import org.mockito.junit.MockitoJUnitRunner;

// import ar.edu.itba.paw.interfaces.persistence.JourneyDao;
// import ar.edu.itba.paw.interfaces.persistence.JourneyResponseDao;
// import ar.edu.itba.paw.interfaces.services.EmailService;
// import ar.edu.itba.paw.interfaces.services.UserService;
// import ar.edu.itba.paw.models.Journey;
// import ar.edu.itba.paw.models.JourneyResponse;
// import ar.edu.itba.paw.models.Page;
// import ar.edu.itba.paw.models.PageParams;
// import ar.edu.itba.paw.models.User;

// @RunWith(MockitoJUnitRunner.class)
// public class JourneyResponseServiceImplTest {

//     private static final long JOURNEY_ID = 0;
//     private static final long USER_ID = 0;
//     private static final String USERNAME = "user";
//     private static final long ID = 0;
//     private static final String MESSAGE = "message";
//     private static final LocalDateTime TIMESTAMP = LocalDateTime.now().withNano(0);
//     private static final JourneyResponse RESPONSE = new JourneyResponse(ID, USER_ID, USERNAME, JOURNEY_ID, MESSAGE, TIMESTAMP);
//     private static final List<JourneyResponse> RESPONSES = List.of(RESPONSE);
//     private static final Page<JourneyResponse> RESPONSE_PAGE = new Page<JourneyResponse>(RESPONSES, 1, 1);
//     private static final int PAGE_NUM = 1;
//     private static final int PAGE_SIZE = 2;
//     private static final PageParams PAGE_PARAMS = new PageParams(1, 2);
//     private static final int REPLY_COUNT = 1;
//     private static final User USER = new User(ID, null, null, null, null, null, null, ID, null, false);
//     private static final Journey JOURNEY = new Journey(ID, USER, null, null, null, MESSAGE);


//    @InjectMocks
//    JourneyResponseServiceImpl responseService;

    // @Mock
    // JourneyResponseDao responseDao;
    
    // @Mock
    // JourneyDao journeyDao;
    // @Mock
    // EmailService emailService;
    // @Mock
    // UserService userService;

//    @Test
//    public void testCreate(){
//        Mockito.when(
//            responseDao.create(USER_ID, USERNAME, JOURNEY_ID, MESSAGE, TIMESTAMP)
//        ).thenReturn(RESPONSE);
//
//        JourneyResponse response = responseService.createJourneyResponse(USER_ID, USERNAME, JOURNEY_ID, MESSAGE, TIMESTAMP);
//
//        assertEquals(RESPONSE, response);
//    }
//
//    @Test
//    public void testListAllFromJourney(){
//        Mockito.when(
//            responseDao.listAllFromJourney(JOURNEY_ID)
//        ).thenReturn(List.of(RESPONSE));
//
//        List<JourneyResponse> responses = responseService.listAllResponsesFromJourney(JOURNEY_ID);
//
//        assertNotNull(responses);
//        assertEquals(1, responses.size());
//        assertEquals(RESPONSE, responses.getFirst());
//    }
//
//    @Test
//    public void testGetJourneyIdByResponseId(){
//        Mockito.when(
//            responseDao.getJourneyIdByResponseId(ID)
//        ).thenReturn(JOURNEY_ID);
//
//        long id = responseService.getJourneyIdByResponseId(ID);
//
//        assertEquals(JOURNEY_ID, id);
//    }
//
//    @Test
//    public void testDelete(){
//        Mockito.when(
//            responseDao.findById(Mockito.eq(ID))
//        ).thenReturn(Optional.of(RESPONSE));
//        Mockito.when(
//            journeyDao.findById(Mockito.eq(JOURNEY_ID))
//        ).thenReturn(Optional.of(JOURNEY));
//        Mockito.when(
//            userService.findById(Mockito.eq(USER_ID))
//        ).thenReturn(Optional.of(USER));
//
//        responseService.deleteJourneyResponse(ID, MESSAGE);
//    }
//    @Test(expected = IllegalArgumentException.class)
//    public void testDeleteUserNotFound(){
//        Mockito.when(
//            responseDao.findById(Mockito.eq(ID))
//        ).thenReturn(Optional.of(RESPONSE));
//        Mockito.when(
//            journeyDao.findById(Mockito.eq(JOURNEY_ID))
//        ).thenReturn(Optional.of(JOURNEY));
//        Mockito.when(
//            userService.findById(Mockito.eq(USER_ID))
//        ).thenReturn(Optional.empty());
//
//        responseService.deleteJourneyResponse(ID, MESSAGE);
//    }
//    @Test(expected = IllegalStateException.class)
//    public void testDeleteEventNotFound(){
//        Mockito.when(
//            responseDao.findById(Mockito.eq(ID))
//        ).thenReturn(Optional.of(RESPONSE));
//        Mockito.when(
//            journeyDao.findById(Mockito.eq(JOURNEY_ID))
//        ).thenReturn(Optional.empty());
//
//        responseService.deleteJourneyResponse(ID, MESSAGE);
//    }
//    @Test(expected = IllegalArgumentException.class)
//    public void testDeleteResponseNotFound(){
//        Mockito.when(
//            responseDao.findById(Mockito.eq(ID))
//        ).thenReturn(Optional.empty());
//
//        responseService.deleteJourneyResponse(ID, MESSAGE);
//    }
//
//    @Test
//    public void testDeleteByJourneyId(){
//        responseService.deleteByJourneyId(JOURNEY_ID);
//    }
//
//    @Test
//    public void testListAllFromJourneyPaged(){
//        Mockito.when(
//            responseDao.listAllFromJourney(Mockito.eq(JOURNEY_ID), Mockito.eq(PAGE_NUM), Mockito.eq(PAGE_SIZE))
//        ).thenReturn(RESPONSE_PAGE);
//
//        Page<JourneyResponse> page = responseService.listAllResponsesFromJourney(JOURNEY_ID, PAGE_PARAMS);
//
//        assertNotNull(page);
//        assertEquals(RESPONSE_PAGE, page);
//    }
//
//    @Test
//    public void testGetCount(){
//        Mockito.when(
//            responseDao.getJourneyResponseCount(Mockito.eq(JOURNEY_ID))
//        ).thenReturn(REPLY_COUNT);
//
//        int count = responseService.getJourneyResponseCount(JOURNEY_ID);
//
//        assertEquals(REPLY_COUNT, count);
//    }
//}
