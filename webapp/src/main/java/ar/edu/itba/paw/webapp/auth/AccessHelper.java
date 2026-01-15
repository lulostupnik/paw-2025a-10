package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.interfaces.services.EventService;
import ar.edu.itba.paw.interfaces.services.JourneyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import java.util.Objects;

@Component
public class AccessHelper {

    private final EventService eventService;
    private final JourneyService journeyService;

    @Autowired
    public AccessHelper(final JourneyService journeyService, final EventService eventService) {
        this.journeyService = journeyService;
        this.eventService = eventService;
    }

    public Long getCurrentUserId() {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }
        final Object principal = auth.getPrincipal();
        if (principal instanceof PawUserDetails) {
            return ((PawUserDetails) principal).getUserId();
        }
        return null;
    }

    public boolean isCurrentUser(long userId) {
        final Long currentUserId = getCurrentUserId();
        return currentUserId != null && currentUserId == userId;
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

    public boolean isUserTipOwner(long tipId) {
        if (Objects.equals(SecurityContextHolder.getContext().getAuthentication().getName(), "AnonymousUser")) return false;
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return journeyService.isTipOwnedByUser(tipId, email);
    }

    public boolean isUserEventAttendee(long eventId) {
        Long userId = getCurrentUserId();
        if (userId == null) return false;
        return eventService.isUserEventAttendee(userId, eventId);
    }

    public boolean isUserRatingOwner(long ratingId) {
        Long userId = getCurrentUserId();
        if (userId == null) return false;
        return eventService.isRatingOwnedByUser(ratingId, userId);
    }

}
