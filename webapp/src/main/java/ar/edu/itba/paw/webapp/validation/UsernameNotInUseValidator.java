package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.interfaces.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UsernameNotInUseValidator implements ConstraintValidator<UsernameNotInUse, String> {

    private final UserService userService;
    @Autowired
    public UsernameNotInUseValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void initialize(UsernameNotInUse constraintAnnotation) {
    }

    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {
        if (username == null || username.isEmpty()) {
            return true;
        }
        return !userService.existsByUsername(username);
    }
}