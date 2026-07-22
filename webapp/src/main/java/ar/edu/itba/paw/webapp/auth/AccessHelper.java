package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.interfaces.services.EventService;
import ar.edu.itba.paw.interfaces.services.JourneyService;
import ar.edu.itba.paw.webapp.form.PatchDeletionForm;
import ar.edu.itba.paw.webapp.form.PatchEventForm;
import ar.edu.itba.paw.webapp.form.PatchJourneyForm;
import ar.edu.itba.paw.webapp.form.PatchUserForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

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

    private boolean isAdmin() {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
    }

    private boolean isAnonymous() {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth == null || auth instanceof AnonymousAuthenticationToken;
    }

    public boolean canPatchUser(long userId, PatchUserForm form) {
        final boolean admin = isAdmin();
        final boolean self = isCurrentUser(userId);
        if (!self && !admin) {
            return false;
        }
        if (form.getBlocked() != null && !admin) {
            return false;
        }
        if (form.getPassword() != null && !self) {
            return false;
        }
        return true;
    }

    public boolean canListRecommendedFor(final Long userId) {
        return userId == null || isCurrentUser(userId);
    }

    public boolean canListUsers(final String search, final Boolean blocked) {
        if (isAdmin()) {
            return true;
        }
        return blocked == null && (search == null || search.isBlank());
    }

    public boolean isUserEventOwner(long eventId){
        if (isAnonymous()) return false;
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return eventService.isEventOwnedByUser(email, eventId);
    }

    public boolean isUserJourneyOwner(long journeyId){
        if (isAnonymous()) return false;
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return journeyService.isJourneyOwnedByUser(email, journeyId);
    }

    public boolean canPatchJourney(long journeyId, PatchJourneyForm form) {
        final boolean admin = isAdmin();
        final boolean owner = isUserJourneyOwner(journeyId);
        if (!owner && !admin) {
            return false;
        }
        if (form.hasContentChanges() && !owner) {
            return false;
        }
        return admin || !hasDeletionMessage(form.getDeletionMessage());
    }

    public boolean canPatchEvent(long eventId, PatchEventForm form) {
        final boolean admin = isAdmin();
        final boolean owner = isUserEventOwner(eventId);
        if (!owner && !admin) {
            return false;
        }
        if (form.hasContentChanges() && !owner) {
            return false;
        }
        return admin || !hasDeletionMessage(form.getDeletionMessage());
    }

    public boolean canPatchEventResponse(long eventId, long responseId, PatchDeletionForm form) {
        final boolean admin = isAdmin();
        if (!admin && !isUserEventResponseOwner(eventId, responseId)) {
            return false;
        }
        return admin || !hasDeletionMessage(form.getDeletionMessage());
    }

    public boolean canPatchJourneyResponse(long journeyId, long responseId, PatchDeletionForm form) {
        final boolean admin = isAdmin();
        if (!admin && !isUserJourneyResponseOwner(journeyId, responseId)) {
            return false;
        }
        return admin || !hasDeletionMessage(form.getDeletionMessage());
    }

    private boolean isUserEventResponseOwner(long eventId, long responseId) {
        Long userId = AuthUtils.getCurrentUserId();
        if (userId == null) return false;
        return eventService.isEventResponseOwnedByUser(eventId, responseId, userId);
    }

    private boolean isUserJourneyResponseOwner(long journeyId, long responseId) {
        Long userId = AuthUtils.getCurrentUserId();
        if (userId == null) return false;
        return journeyService.isJourneyResponseOwnedByUser(journeyId, responseId, userId);
    }

    private boolean hasDeletionMessage(String deletionMessage) {
        return deletionMessage != null && !deletionMessage.isBlank();
    }


    public boolean isUserTipOwner(long journeyId, long tipId) {
        if (isAnonymous()) return false;
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
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
