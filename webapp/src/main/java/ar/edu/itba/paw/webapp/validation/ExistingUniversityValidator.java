package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.interfaces.services.UniversityService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ExistingUniversityValidator implements ConstraintValidator<ExistingUniversity, String> {

    @Autowired
    private UniversityService universityService;

    @Override
    public void initialize(ExistingUniversity constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(String universityName, ConstraintValidatorContext context) {
        if (universityName == null || universityName.isEmpty()) {
            return true;
        }
        // Implement the logic to check if the university exists in the database
        // For example:
        // return universityService.existsByName(universityName);
        return universityService.findByName(universityName).isPresent(); // Placeholder, replace with actual logic
    }
}
