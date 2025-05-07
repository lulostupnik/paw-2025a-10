package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.interfaces.services.EventService;
import ar.edu.itba.paw.interfaces.services.JourneyService;
import ar.edu.itba.paw.interfaces.services.UniversityService;
import ar.edu.itba.paw.interfaces.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class AccessHelper {

    private final EventService eventService;
    private final JourneyService journeyService;
    private final UniversityService universityService;
    private final UserService userService;

    @Autowired
    public AccessHelper(final JourneyService journeyService, final EventService eventService, UniversityService universityService, UserService userService) {
        this.journeyService = journeyService;
        this.eventService = eventService;
        this.universityService = universityService;
        this.userService = userService;
    }

    public boolean isUserEventOwner(long eventId){
        if (Objects.equals(SecurityContextHolder.getContext().getAuthentication().getName(), "AnonymousUser")) return false;
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return eventService.isEventOwnedByUser(email, eventId);
    }

    public boolean isUserJourneyOwner(long journeyId){
        if (Objects.equals(SecurityContextHolder.getContext().getAuthentication().getName(), "AnonymousUser")) return false;
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return journeyService.isJourneyOwnedByUser(email, journeyId);
    }

    public boolean isUserBlocked(){
        if (Objects.equals(SecurityContextHolder.getContext().getAuthentication().getName(), "AnonymousUser")) return false;
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.findByEmail(email).orElseThrow(() ->
                new IllegalArgumentException("No user by the name " + email)).isBlocked();
    }

}
