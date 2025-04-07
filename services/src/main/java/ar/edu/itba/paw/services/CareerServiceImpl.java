package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.CareerDao;
import ar.edu.itba.paw.interfaces.services.CareerService;
import ar.edu.itba.paw.models.Career;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CareerServiceImpl implements CareerService {

    private final CareerDao careerDao;

    @Autowired
    public CareerServiceImpl(CareerDao careerDao) {
        this.careerDao = careerDao;
    }

    @Override
    public Optional<Career> findById(long id) {
        return careerDao.findById(id);
    }


    @Override
    public List<Career> findAll() {
        return careerDao.findAll();
    }

    @Override
    public Optional<Career> findByName(String name) {
        return careerDao.findByName(name);
    }

}
