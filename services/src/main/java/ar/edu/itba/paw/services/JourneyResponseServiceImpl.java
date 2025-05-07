package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.JourneyDao;
import ar.edu.itba.paw.interfaces.persistence.JourneyResponseDao;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.JourneyResponseService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Journey;
import ar.edu.itba.paw.models.JourneyResponse;
import ar.edu.itba.paw.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class JourneyResponseServiceImpl implements JourneyResponseService {
    private final JourneyResponseDao journeyResponseDao;

    private final JourneyDao journeyDao;
    private final EmailService emailService;
    private final UserService userService;
    @Autowired
    public JourneyResponseServiceImpl(final JourneyResponseDao journeyResponseDao, JourneyDao journeyDao, EmailService emailService, UserService userService) {
        this.journeyResponseDao = journeyResponseDao;
        this.journeyDao = journeyDao;
        this.emailService = emailService;
        this.userService = userService;
    }

    @Transactional
    @Override
    public JourneyResponse create(long userId, String username, long journeyId, String message, LocalDateTime dateTime) {
        return journeyResponseDao.create(userId, username, journeyId, message, dateTime);
    }

    @Override
    public List<JourneyResponse> listAllFromJourney(long journeyId) {
        return journeyResponseDao.listAllFromJourney(journeyId);
    }

    @Override
    public Optional<JourneyResponse> findById(long id) {
        return journeyResponseDao.findById(id);
    }


    @Transactional
    @CacheEvict(value = "journeysByResponseId", key = "#id")
    @Override
    public void delete(long id, String message) {

        JourneyResponse deletedComment = findById(id).orElseThrow(() -> new IllegalArgumentException("Journey response doesn't exists"));
        Journey journey = journeyDao.findById(deletedComment.getJourneyId()).orElseThrow(()->new IllegalStateException("Journey from journey response doesn't exist"));
        User commentAuthor = userService.findById(deletedComment.getUserId()).orElseThrow(() -> new IllegalArgumentException("User from journey response doesn't exists"));

        emailService.sendJourneyCommentDeletionNotification(deletedComment,journey,commentAuthor,message);

        journeyResponseDao.deletionMessage(id, message);
        journeyResponseDao.delete(id);
    }


    @Cacheable(value = "journeysByResponseId", key = "#journeyResponseId")
    @Override
    public long getJourneyIdByResponseId(long journeyResponseId) {
        return journeyResponseDao.getJourneyIdByResponseId(journeyResponseId);
    }

    @Transactional
    @CacheEvict(value = "journeysByResponseId", allEntries = true) //FIXME: check if should CACHE EVICT
    @Override
    public void deleteByJourneyId(long journeyId) {
        journeyResponseDao.deleteByJourneyId(journeyId);
    }



}
