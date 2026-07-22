import { Link, useLocation } from "react-router-dom";
import { useMemo } from "react";
import type { ProfileEvent } from "@/types/event";
import { useI18n } from "@/lib/i18n";
import { pushToNavigationStack } from "@/lib/utils/navigationStack";
import { parseApiDate } from "@/lib/utils/date";

interface ProfileEventCardProps {
    event: ProfileEvent;
    isOwner?: boolean;
}

const formatDate = (value: string, locale: string) => {
    const date = parseApiDate(value);
    if (Number.isNaN(date.getTime())) {
        return value;
    }
    return new Intl.DateTimeFormat(locale, { month: "long", day: "numeric", year: "numeric" }).format(date);
};

export default function ProfileEventCard({ event, isOwner = false }: ProfileEventCardProps) {
    const { t, locale } = useI18n();
    const location = useLocation();
    const dateLabel = useMemo(() => formatDate(event.date, locale), [event.date, locale]);
    const isFull = event.attendeesLimit ? (event.attendeesCount ?? 0) >= event.attendeesLimit : event.isFull ?? false;

    return (
        <div className="event-card-wrapper">
            <Link
                to={`/events/${event.id}`}
                state={{ from: `${location.pathname}${location.search}` }}
                className="event-card-link"
                onClick={() => pushToNavigationStack(`${location.pathname}${location.search}`)}
            >
                <div className="featured-event-card">
                    <div className="event-image-container">
                        {event.flyerImageUrl ? (
                            <img src={event.flyerImageUrl} alt={t("event.flyer.alt")} className="event-image profile-image" />
                        ) : (
                            <div className="image-placeholder">
                                <svg xmlns="http://www.w3.org/2000/svg" className="placeholder-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                                </svg>
                            </div>
                        )}

                        {!isOwner && isFull && (
                            <div className="event-full-badge">{t("event.full")}</div>
                        )}
                    </div>
                    <div className="event-card-content">
                        <div className="event-card-header">
                            <h3 className="event-card-title">{event.title}</h3>
                            <p className="event-card-subtitle">{event.city.name}</p>
                        </div>
                        {event.description && <p className="event-card-description">{event.description}</p>}
                        <div className="event-card-footer">
                            <div className="event-date">
                                <svg xmlns="http://www.w3.org/2000/svg" className="event-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                                </svg>
                                <span>{dateLabel}</span>
                            </div>
                            <div className="event-organizer">
                                <svg xmlns="http://www.w3.org/2000/svg" className="event-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M16 7a4 4 0 11-8 0 4 4 0 016 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                                </svg>
                                <span className="organizer-name" title={event.user.username}>{event.user.username}</span>
                            </div>
                        </div>
                    </div>
                </div>
            </Link>
        </div>
    );
}
