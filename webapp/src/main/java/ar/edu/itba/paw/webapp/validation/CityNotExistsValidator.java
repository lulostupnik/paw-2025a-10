package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.interfaces.services.CareerService;
import ar.edu.itba.paw.interfaces.services.CityService;
import ar.edu.itba.paw.models.City;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CityNotExistsValidator implements ConstraintValidator<CityNotExists, String> {

    private final CityService cityService;


    @Autowired
    public CityNotExistsValidator(CityService cityService) {
        this.cityService = cityService;
    }

    @Override
    public void initialize(CityNotExists constraintAnnotation) {
        // Initialization logic if needed
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true;
        }
        return cityService.findByName(value).isEmpty();
    }

}
