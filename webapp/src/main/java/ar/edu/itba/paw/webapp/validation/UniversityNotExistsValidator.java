package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.interfaces.services.UniversityService;
import org.springframework.beans.factory.annotation.Autowired;
import javax.validation.ConstraintValidator;

public class UniversityNotExistsValidator implements ConstraintValidator<UniversityNotExists, String> {
    private final UniversityService universityService;
    @Autowired
    public UniversityNotExistsValidator(final UniversityService universityService) {
        this.universityService = universityService;
    }
    @Override
    public void initialize(final UniversityNotExists constraintAnnotation) {
    }

    @Override
    public boolean isValid(final String universityName, final javax.validation.ConstraintValidatorContext context) {
        if(universityName == null || universityName.isEmpty()) {
            return true;
        }
        return universityService.findByName(universityName).isEmpty();
    }
}
