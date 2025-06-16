package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.form.EditUserForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Optional;

public class UpdateUserValidator implements ConstraintValidator<ValidUpdateUser, EditUserForm> {

    private final UserService userService;

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateUserValidator.class);

    @Autowired
    public UpdateUserValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean isValid(EditUserForm form, ConstraintValidatorContext context) {
        Optional<User> currentUser = userService.findUserById(form.getUserId());
        if (currentUser.isEmpty()) {
            return false;
        }

        User user = currentUser.get();
        LOGGER.debug("Validating update for user: {}", user.getId());
        boolean valid = true;

        LOGGER.debug("Current user: {}", user);
        LOGGER.debug("Form data: {}", form);

        // Check username
        if (!user.getUsername().equals(form.getUsername()) && userService.existsByUsername(form.getUsername())) {
            valid = false;
        }


        return valid;
    }
}