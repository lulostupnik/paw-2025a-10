package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.interfaces.services.EventService;
import ar.edu.itba.paw.interfaces.services.JourneyService;
import ar.edu.itba.paw.models.Tip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import java.util.Objects;
import java.util.Optional;

@Component
public class AccessHelper {

    private final EventService eventService;
    private final JourneyService journeyService;

    @Autowired
    public AccessHelper(final JourneyService journeyService, final EventService eventService) {
        this.journeyService = journeyService;
        this.eventService = eventService;
    }

    public boolean isCurrentUser(long userId) {
        final Long currentUserId = AuthUtils.getCurrentUserId();
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

    public boolean isUserTipOwner(long journeyId, long tipId) {
        if (Objects.equals(SecurityContextHolder.getContext().getAuthentication().getName(), "AnonymousUser")) return false;
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return journeyService.isTipOwnedByUser(journeyId, tipId, email);
    }


    public boolean isUserTipOwner(long tipId) {
        if (Objects.equals(SecurityContextHolder.getContext().getAuthentication().getName(), "AnonymousUser")) return false;
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Tip> tip = journeyService.findTipById(tipId);
        if (tip.isEmpty()) return false;
        long journeyId = tip.get().getJourney().getId();
        return journeyService.isTipOwnedByUser(journeyId, tipId, email);
    }

    public boolean isUserEventAttendee(long eventId) {
        Long userId = AuthUtils.getCurrentUserId();
        if (userId == null) return false;
        return eventService.isUserEventAttendee(userId, eventId);
    }

    public boolean isUserRatingOwner(long eventId, long ratingId) {
        Long userId = AuthUtils.getCurrentUserId();
        if (userId == null) return false;
        return eventService.isRatingOwnedByUser(eventId, ratingId, userId);
    }

}
