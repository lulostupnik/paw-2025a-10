package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.interfaces.services.CityService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class CityNameValidator implements ConstraintValidator<ExistingCity, String> {

    private final CityService cityService;

    @Autowired
    public CityNameValidator(CityService cityService) {
        this.cityService = cityService;
    }

    @Override
    public boolean isValid(String city, ConstraintValidatorContext constraintValidatorContext) {
        if (city == null || city.isEmpty()) {
            return true;
        }

        return cityService.findByName(city).isPresent();
    }
}
