package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.interfaces.services.EventService;
import ar.edu.itba.paw.models.Event;
import ar.edu.itba.paw.webapp.form.EditEventForm;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Optional;

public class CurrentAttendeeValidator implements ConstraintValidator<CurrentAttendees, EditEventForm> {

    @Autowired
    private EventService eventService;
    @Override
    public void initialize(CurrentAttendees constraintAnnotation) {}

    @Override
    public boolean isValid(EditEventForm form, ConstraintValidatorContext context) {
        if(form.getAttendeesLimit() == null) {
            return true;
        }
        System.out.println(form.getId());
        Optional<Event> event = eventService.getEventById(form.getId());

        System.out.println("Event: " + event);

        return event.filter(value -> value.getAttendeesCount() <= form.getAttendeesLimit()).isPresent();

    }

}
