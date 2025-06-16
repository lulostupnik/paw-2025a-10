package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.interfaces.services.EventService;
import ar.edu.itba.paw.models.Event;
import ar.edu.itba.paw.webapp.form.EditEventForm;
import org.springframework.beans.factory.annotation.Autowired;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Optional;

public class CurrentAttendeeValidator implements ConstraintValidator<CurrentAttendees, EditEventForm> {

    private final EventService eventService;
    @Autowired
    public CurrentAttendeeValidator(EventService eventService) {
        this.eventService = eventService;
    }
    @Override
    public void initialize(CurrentAttendees constraintAnnotation) {}

    @Override
    public boolean isValid(EditEventForm form, ConstraintValidatorContext context) {
        if(form.getAttendeesLimit() == null) {
            return true;
        }
        Optional<Event> event = eventService.findEventById(form.getId());

        return event.filter(value -> value.getAttendeesCount() <= form.getAttendeesLimit()).isPresent();

    }

}
