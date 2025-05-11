package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.interfaces.services.JourneyService;
import org.springframework.beans.factory.annotation.Autowired;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NoExistingJourneyValidator implements ConstraintValidator<NoExistingJourney, String> {

    private final JourneyService journeyService;
    @Autowired
    public NoExistingJourneyValidator(JourneyService journeyService) {
        this.journeyService = journeyService;
    }

    @Override
    public void initialize(NoExistingJourney constraintAnnotation) {
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null || email.isEmpty()) {
            return true; // Dejamos que @Email maneje esto
        }
        try {
            return !journeyService.userHasJourney(email);
        } catch (Exception e) {
            return true; // Si hay error, dejamos que pase y se maneje en el servicio
        }
    }
}