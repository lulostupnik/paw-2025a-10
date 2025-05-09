package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.interfaces.services.InterestService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidInterestIdValidator implements ConstraintValidator<ValidInterest, long[]> {
    @Autowired
    private InterestService interestService;

    @Override
    public void initialize(ValidInterest constraintAnnotation) {
    }

    @Override
    public boolean isValid(long[] interests, ConstraintValidatorContext context) {
        if (interests == null || interests.length == 0) {
            return false;
        }
        try {
            for(long interest : interests) {
                if (interestService.findById(interest).isEmpty()) {
                    return false; // Si no existe el interes, no es valido
                }
            }
            return true;
        } catch (Exception e) {
            return true; // Si hay error, dejamos que pase y se maneje en el servicio
        }
    }
}
