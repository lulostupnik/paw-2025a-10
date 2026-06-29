package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.interfaces.services.UniversityService;
import org.springframework.beans.factory.annotation.Autowired;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ExistingUniversityValidator implements ConstraintValidator<ExistingUniversity, Long> {

    private final UniversityService universityService;
    @Autowired
    public ExistingUniversityValidator(UniversityService universityService) {
        this.universityService = universityService;
    }

    @Override
    public void initialize(ExistingUniversity constraintAnnotation) {
    }

    @Override
    public boolean isValid(Long universityId, ConstraintValidatorContext context) {
        if (universityId == null) {
            return true;
        }
        return universityService.findById(universityId).isPresent();
    }
}
