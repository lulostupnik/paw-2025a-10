package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.interfaces.services.UniversityService;
import org.springframework.beans.factory.annotation.Autowired;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ExistingUniversityValidator implements ConstraintValidator<ExistingUniversity, String> {

    private final UniversityService universityService;
    @Autowired
    public ExistingUniversityValidator(UniversityService universityService) {
        this.universityService = universityService;
    }

    @Override
    public void initialize(ExistingUniversity constraintAnnotation) {
    }

    @Override
    public boolean isValid(String universityName, ConstraintValidatorContext context) {
        if (universityName == null || universityName.isEmpty()) {
            return true;
        }
        return universityService.findByName(universityName).isPresent();
    }
}
