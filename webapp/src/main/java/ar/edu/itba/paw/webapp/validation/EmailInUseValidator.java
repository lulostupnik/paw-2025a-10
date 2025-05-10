package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.interfaces.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EmailInUseValidator implements ConstraintValidator<EmailInUse, String> {
    @Autowired
    private UserService userService;

    @Override
    public void initialize(EmailInUse constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null || email.isEmpty()) {
            return true; // Skip validation for empty emails
        }
        return userService.existsByEmail(email); // Return true if email exists
    }
}
