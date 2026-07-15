package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.interfaces.services.CityService;
import org.springframework.beans.factory.annotation.Autowired;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class ExistingCityValidator implements ConstraintValidator<ExistingCity, Long> {

    private final CityService cityService;

    @Autowired
    public ExistingCityValidator(CityService cityService) {
        this.cityService = cityService;
    }

    @Override
    public boolean isValid(Long cityId, ConstraintValidatorContext constraintValidatorContext) {
        if (cityId == null) {
            return true;
        }
        return cityService.findCityById(cityId).isPresent();
    }
}
