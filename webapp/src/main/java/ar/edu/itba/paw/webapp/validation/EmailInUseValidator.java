package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.interfaces.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EmailInUseValidator implements ConstraintValidator<EmailInUse, String> {

    private final UserService userService;
    @Autowired
    public EmailInUseValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void initialize(EmailInUse constraintAnnotation) {

    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null || email.isEmpty()) {
            return true;
        }
        return userService.existsByEmail(email);
    }
}
