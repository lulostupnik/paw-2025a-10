package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.interfaces.services.EventService;
import ar.edu.itba.paw.webapp.form.DeleteForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Collection;

/**
 * Custom validator that implements role-based message validation logic.
 * For ADMIN users: message must not be null or empty
 */

@Component
public class RoleBasedMessageValidator implements ConstraintValidator<RoleBasedMessage, DeleteForm> {

    @Autowired
    private EventService eventService;

    @Override
    public void initialize(RoleBasedMessage constraintAnnotation) {
    }

    @Override
    public boolean isValid(DeleteForm form, ConstraintValidatorContext context) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        int eventId = form.getId();
        boolean isOwner = eventService.isEventOwnedByUser(userEmail, eventId);
        if(isOwner){
            return true;
        }
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        boolean isAdmin = authorities.stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            boolean isValid = form.getMessage() != null && !form.getMessage().trim().isEmpty();

            if (!isValid) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Please provide a reason for deletion")
                        .addPropertyNode("message")
                        .addConstraintViolation();
            }

            return isValid;
        }

        return false;
    }
}
