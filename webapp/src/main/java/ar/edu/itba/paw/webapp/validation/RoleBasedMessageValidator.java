package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.interfaces.services.EventService;
import ar.edu.itba.paw.interfaces.services.JourneyService;
import ar.edu.itba.paw.webapp.form.DeleteForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;



@Component
public class RoleBasedMessageValidator implements ConstraintValidator<RoleBasedMessage, DeleteForm> {

    @Autowired
    private EventService eventService;

    @Autowired
    private JourneyService journeyService;

    private String type;

    @Override
    public void initialize(RoleBasedMessage constraintAnnotation) {
        this.type = constraintAnnotation.type();
    }

    @Override
    public boolean isValid(DeleteForm form, ConstraintValidatorContext context) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        int id = form.getId();

        boolean isOwner;
        if ("event".equalsIgnoreCase(type)) {
            isOwner = eventService.isEventOwnedByUser(userEmail, id);
        } else if ("journey".equalsIgnoreCase(type)) {
            isOwner = journeyService.isJourneyOwnedByUser(userEmail, id);
        } else {
            return false;
        }

        if (isOwner) return true;

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            boolean hasMessage = form.getMessage() != null && !form.getMessage().trim().isEmpty();
            if (!hasMessage) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Please provide a reason for deletion")
                        .addPropertyNode("message")
                        .addConstraintViolation();
            }
            return hasMessage;
        }

        return false;
    }
}
