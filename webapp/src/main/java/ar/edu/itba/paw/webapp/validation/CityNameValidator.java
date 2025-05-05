package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.interfaces.services.CityService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


public class CityNameValidator implements ConstraintValidator<ExistingCity, String> {
    @Autowired
    private CityService cityService;

    @Override
    public boolean isValid(String city, ConstraintValidatorContext constraintValidatorContext) {
        if (city == null || city.isEmpty()) {
            return false;
        }
        // Assuming a method exists to check if the city is valid
        // This should be replaced with actual logic to check if the city exists
        return cityService.findByName(city).isPresent();
    }
}
