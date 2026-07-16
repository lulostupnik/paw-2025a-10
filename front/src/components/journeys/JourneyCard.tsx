import { Link, useLocation } from "react-router-dom";
import { useMemo } from "react";
import { useQueryClient } from "@tanstack/react-query";
import type { JourneySummary } from "@/types/journey";
import { useI18n } from "@/lib/i18n";
import { pushToNavigationStack } from "@/lib/utils/navigationStack";
import { parseApiDate } from "@/lib/utils/date";
import { prefetchJourneyDetail } from "@/lib/utils/prefetchDetail";
import AvatarFallbackIcon from "@/components/ui/AvatarFallbackIcon";

interface JourneyCardProps {
    journey: JourneySummary;
}

const formatDate = (value: string, locale: string) => {
    const date = parseApiDate(value);
    if (Number.isNaN(date.getTime())) {
        return value;
    }
    return new Intl.DateTimeFormat(locale, { month: "short", day: "numeric", year: "numeric" }).format(date);
};

export default function JourneyCard({ journey }: JourneyCardProps) {
    const { t, locale } = useI18n();
    const location = useLocation();
    const queryClient = useQueryClient();
    const startLabel = useMemo(() => formatDate(journey.startDate, locale), [journey.startDate, locale]);
    const endLabel = useMemo(() => formatDate(journey.endDate, locale), [journey.endDate, locale]);
    const profilePictureUrl = journey.profilePictureUrl ?? null;
    const handlePrefetch = () => prefetchJourneyDetail(queryClient, journey.id);

    return (
        <div className="event-card-wrapper">
            <Link
                to={`/journeys/${journey.id}`}
                state={{ from: `${location.pathname}${location.search}` }}
                className="event-card-link"
                onMouseEnter={handlePrefetch}
                onFocus={handlePrefetch}
                onClick={() => pushToNavigationStack(`${location.pathname}${location.search}`)}
            >
                <div className="featured-event-card">
                    <div className="event-image-container">
                        {profilePictureUrl ? (
                            <img
                                src={profilePictureUrl}
                                alt={t("journey.profile.alt")}
                                className="event-image profile-image"
                            />
                        ) : (
                            <div className="image-placeholder">
                                <AvatarFallbackIcon size={48} />
                            </div>
                        )}
                    </div>

                    <div className="event-card-content">
                        <div className="event-card-header">
                            <div className="event-location">
                                <h3>
                                    {t("journey.destinationCityAndCountry", {
                                        values: { 0: journey.city ?? "—", 1: journey.country ?? "—" },
                                    })}
                                </h3>
                            </div>
                            <p className="event-card-subtitle mt-2">
                                <svg xmlns="http://www.w3.org/2000/svg" className="event-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
                                </svg>
                                <span className="university-name" title={journey.university ?? ""}>
                                    {journey.university ?? "—"}
                                </span>
                            </p>
                            <div className="event-organizer">
                                <svg xmlns="http://www.w3.org/2000/svg" className="event-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M16 7a4 4 0 11-8 0 4 4 0 016 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                                </svg>
                                <span className="user-name">{journey.userName ?? "—"}</span>
                            </div>
                        </div>

                        <div className="card-dates">
                            <svg xmlns="http://www.w3.org/2000/svg" className="card-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                            </svg>
                            <span className="card-date-range">
                                {startLabel} → {endLabel}
                            </span>
                        </div>
                        <p className="event-card-description">{journey.description}</p>
                    </div>
                </div>
            </Link>
        </div>
    );
}
