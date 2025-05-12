package ar.edu.itba.paw.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public class EventWithUserInfo {
    private final Event event;
    private final boolean isAttending;
    private final boolean isCreator;
}
