package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.InterestDao;
import ar.edu.itba.paw.interfaces.services.InterestService;
import ar.edu.itba.paw.models.Interest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InterestServiceImpl implements InterestService {
    private final InterestDao interestDao;
    @Autowired
    public InterestServiceImpl(InterestDao interestDao) {
        this.interestDao = interestDao;
    }

    @Override
    public Optional<Interest> findById(Long id) {
        return this.interestDao.findById(id);
    }

    @Override
    public List<Interest> findAll() {
        return interestDao.findAll();
    }

    @Override
    public List<Interest> findByUserId(Long id) {
        return interestDao.findByUserId(id);
    }

    @Override
    public Optional<Interest> findByName(String name) {
        return interestDao.findByName(name);
    }
    @Override
    public List<Interest> findIdByName(String[] names) {
        return interestDao.findIdByName(names);
    }

    @Override
    public Optional<Interest> createUserInterest(Interest interest, Long userId) {
        return Optional.empty();
    }

    @Override
    public List<Interest> createUserInterests(String[] interests, Long userId) {
        return interestDao.createUserInterests(interests, userId);
    }

    @Override
    public void updateScoreByInterest(Interest interest, Long userId) {
        interestDao.updateScoreByInterest(interest, userId);
    }

    @Override
    public void updateScoreByInterests(List<Interest> interests, Long userId) {
        interestDao.updateScoreByInterests(interests, userId);

    }

}
