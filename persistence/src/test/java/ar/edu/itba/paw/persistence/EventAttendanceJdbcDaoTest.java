//package ar.edu.itba.paw.persistence;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertFalse;
//import static org.junit.Assert.assertNotNull;
//import static org.junit.Assert.assertTrue;
//
//import java.util.List;
//
//import javax.sql.DataSource;
//
//import ar.edu.itba.paw.persistence.config.TestConfig;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.dao.DataAccessException;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.test.annotation.Rollback;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.test.jdbc.JdbcTestUtils;
//import org.springframework.transaction.annotation.Transactional;
//
//import ar.edu.itba.paw.models.Event;
//
//@Transactional
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes = TestConfig.class)
//public class EventAttendanceJdbcDaoTest {
//
//    @Autowired
//    private DataSource ds;
//
//    @Autowired
//    private EventAttendanceJdbcDao attendanceDao;
//
//    private JdbcTemplate jdbcTemplate;
//
//    @Before
//    public void setUp(){
//        jdbcTemplate = new JdbcTemplate(ds);
//    }
//
//    @Test
//    public void testCreate(){
//        attendanceDao.create(TestUtils.USER_3_ID, TestUtils.EVENT_2_ID);
//
//        assertEquals(TestUtils.TOTAL_EVENT_ATTENDANCES + 1, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.EVENT_ATTENDANCE_TABLE));
//        assertTrue(jdbcTemplate.queryForObject(TestUtils.EVENT_ATTENDANCE_EXISTS, Boolean.class, TestUtils.USER_3_ID, TestUtils.EVENT_2_ID));
//        assertEquals(TestUtils.EVENT_2_ATTENDEES + 1, jdbcTemplate.queryForObject(TestUtils.EVENT_GET_ATTENDEES_BY_ID, Long.class, TestUtils.EVENT_2_ID).longValue());
//        assertEquals(TestUtils.USER_3_ATTENDANCES + 1, jdbcTemplate.queryForObject(TestUtils.USER_GET_ATTENDANCES_COUNT_BY_ID, Long.class, TestUtils.USER_3_ID).longValue());
//    }
//    @Test(expected = DataAccessException.class)
//    public void testCreateDuplicated(){
//        attendanceDao.create(TestUtils.USER_2_ID, TestUtils.EVENT_1_ID);
//    }
//    @Test(expected = DataAccessException.class)
//    public void testCreateWrongUser(){
//        attendanceDao.create(12341243, TestUtils.EVENT_1_ID);
//    }
//    @Test(expected = DataAccessException.class)
//    public void testCreateWrongEvent(){
//        attendanceDao.create(TestUtils.USER_2_ID, 1231234);
//    }
//
//    @Test
//    public void testDelete(){
//        attendanceDao.delete(TestUtils.USER_2_ID, TestUtils.EVENT_1_ID);
//
//        assertEquals(TestUtils.TOTAL_EVENT_ATTENDANCES - 1, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.EVENT_ATTENDANCE_TABLE));
//        assertEquals(TestUtils.EVENT_1_ATTENDEES - 1, jdbcTemplate.queryForObject(TestUtils.EVENT_GET_ATTENDEES_BY_ID, Long.class, TestUtils.EVENT_1_ID).longValue());
//        assertEquals(TestUtils.USER_2_ATTENDANCES - 1, jdbcTemplate.queryForObject(TestUtils.USER_GET_ATTENDANCES_COUNT_BY_ID, Long.class, TestUtils.USER_2_ID).longValue());
//        assertFalse(jdbcTemplate.queryForObject(TestUtils.EVENT_ATTENDANCE_EXISTS, Boolean.class, TestUtils.USER_2_ID, TestUtils.EVENT_1_ID));
//    }
//    @Test
//    public void testDeleteNotParticipating(){
//        attendanceDao.delete(TestUtils.USER_3_ID, TestUtils.EVENT_2_ID);
//
//        assertEquals(TestUtils.TOTAL_EVENT_ATTENDANCES, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.EVENT_ATTENDANCE_TABLE));
//        assertEquals(TestUtils.EVENT_2_ATTENDEES, jdbcTemplate.queryForObject(TestUtils.EVENT_GET_ATTENDEES_BY_ID, Integer.class, TestUtils.EVENT_2_ID).intValue());
//        assertEquals(TestUtils.USER_3_ATTENDANCES, jdbcTemplate.queryForObject(TestUtils.USER_GET_ATTENDANCES_COUNT_BY_ID, Integer.class, TestUtils.EVENT_2_ID).intValue());
//    }
//    @Test
//    public void testDeleteWrongEvent(){
//        int rowsBefore = JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.EVENT_ATTENDANCE_TABLE);
//
//        attendanceDao.delete(TestUtils.USER_2_ID, 12341234);
//        assertEquals(rowsBefore, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.EVENT_ATTENDANCE_TABLE));
//        assertEquals(TestUtils.USER_2_ATTENDANCES, jdbcTemplate.queryForObject(TestUtils.USER_GET_ATTENDANCES_COUNT_BY_ID, Integer.class, TestUtils.USER_2_ID).intValue());
//    }
//    @Test
//    public void testDeleteWrongUser(){
//        int rowsBefore = JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.EVENT_ATTENDANCE_TABLE);
//
//        attendanceDao.delete(1241234, TestUtils.EVENT_1_ID);
//
//        assertEquals(rowsBefore, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.EVENT_ATTENDANCE_TABLE));
//        assertEquals(TestUtils.EVENT_1_ATTENDEES, jdbcTemplate.queryForObject(TestUtils.EVENT_GET_ATTENDEES_BY_ID, Integer.class, TestUtils.EVENT_1_ID).intValue());
//    }
//
//    @Test
//    public void testExistsFalse(){
//        boolean attending = attendanceDao.exists(TestUtils.USER_3_ID, TestUtils.EVENT_2_ID);
//
//        assertFalse(attending);
//    }
//    @Test
//    public void testExistsTrue(){
//        boolean attending = attendanceDao.exists(TestUtils.USER_1_ID, TestUtils.EVENT_1_ID);
//
//        assertTrue(attending);
//    }
//    @Test
//    public void testExistsMissingEvent(){
//        boolean attending = attendanceDao.exists(TestUtils.USER_1_ID, 12341234);
//
//        assertFalse(attending);
//    }
//    @Test
//    public void testExistsMissingUser(){
//        boolean attending = attendanceDao.exists(1241234, TestUtils.EVENT_1_ID);
//
//        assertFalse(attending);
//    }
//
//    @Test
//    public void testFindAllAttendeesByEventIdCount(){
//        int attendees = attendanceDao.countAttendantsByEventId(TestUtils.EVENT_1_ID);
//
//        assertNotNull(attendees);
//        assertEquals(TestUtils.EVENT_1_ATTENDEES, attendees);
//    }
//    @Test
//    public void testFindAllAttendeesCountNoAttendeesByEventId(){
//        int attendees = attendanceDao.countAttendantsByEventId(TestUtils.EVENT_3_ID);
//
//        assertNotNull(attendees);
//        assertEquals(0, attendees);
//    }
//    @Test
//    public void testFindAllAttendeesByEventIdCountMissingEvent(){
//        int attendees = attendanceDao.countAttendantsByEventId(412341234);
//
//        assertNotNull(attendees);
//        assertEquals(0, attendees);
//    }
//
//
//}
