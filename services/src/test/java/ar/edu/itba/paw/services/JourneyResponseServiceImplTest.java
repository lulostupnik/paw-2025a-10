package ar.edu.itba.paw.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import ar.edu.itba.paw.interfaces.persistence.JourneyResponseDao;
import ar.edu.itba.paw.models.JourneyResponse;

@RunWith(MockitoJUnitRunner.class)
public class JourneyResponseServiceImplTest {

    private static final long JOURNEY_ID = 0;
    private static final long USER_ID = 0;
    private static final String USERNAME = "user";
    private static final long ID = 0;
    private static final String MESSAGE = "message";
    private static final LocalDateTime TIMESTAMP = LocalDateTime.now().withNano(0);
    private static final JourneyResponse RESPONSE = new JourneyResponse(ID, USER_ID, USERNAME, JOURNEY_ID, MESSAGE, TIMESTAMP);

    @InjectMocks
    JourneyResponseServiceImpl responseService;

    @Mock
    JourneyResponseDao responseDao;

    @Test
    public void testCreate(){
        Mockito.when(
            responseDao.create(USER_ID, USERNAME, JOURNEY_ID, MESSAGE, TIMESTAMP)
        ).thenReturn(RESPONSE);

        JourneyResponse response = responseService.create(USER_ID, USERNAME, JOURNEY_ID, MESSAGE, TIMESTAMP);

        assertEquals(RESPONSE, response);
    }

    @Test
    public void testListAllFromJourney(){
        Mockito.when(
            responseDao.listAllFromJourney(JOURNEY_ID)
        ).thenReturn(List.of(RESPONSE));

        List<JourneyResponse> responses = responseService.listAllFromJourney(JOURNEY_ID);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(RESPONSE, responses.getFirst());
    }

    @Test
    public void testGetJourneyIdByResponseId(){
        Mockito.when(
            responseDao.getJourneyIdByResponseId(ID)
        ).thenReturn(JOURNEY_ID);

        long id = responseService.getJourneyIdByResponseId(ID);

        assertEquals(JOURNEY_ID, id);
    }

    @Test
    public void testDelete(){
        responseService.delete(ID, MESSAGE);
    }

    @Test
    public void testDeleteByJourneyId(){
        responseService.deleteByJourneyId(JOURNEY_ID);
    }
}
