import { useMemo, type ReactElement } from "react";
import { Link, useLocation } from "react-router-dom";
import { useQueryClient } from "@tanstack/react-query";
import BaseCard from "./BaseCard";
import CardMetaRow from "./CardMetaRow";
import type { EventSummary } from "@/lib/api/events";
import type { ProfileEvent } from "@/types/event";
import { useI18n } from "@/lib/i18n";
import { pushToNavigationStack } from "@/lib/utils/navigationStack";
import { prefetchEventDetail } from "@/lib/utils/prefetchDetail";

interface EventCardProps {
    event: EventSummary | ProfileEvent;
    actionSlot?: ReactElement | null;
}

const ClockIcon = () => (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth={2}>
        <circle cx="12" cy="12" r="10" />
        <path d="M12 6v6l3 3" />
    </svg>
);

const MapPinIcon = () => (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth={2}>
        <path d="M12 21s-6-5-6-10a6 6 0 1 1 12 0c0 5-6 10-6 10z" />
        <circle cx="12" cy="11" r="2" />
    </svg>
);

const UsersIcon = () => (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth={2}>
        <circle cx="12" cy="8" r="4" />
        <path d="M6 20c0-3.3 2.7-6 6-6s6 2.7 6 6" />
    </svg>
);

const UserIcon = () => (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth={2}>
        <circle cx="12" cy="8" r="4" />
        <path d="M5.5 20a6.5 6.5 0 0 1 13 0" />
    </svg>
);

export default function EventCard({ event }: EventCardProps) {
    const { t, locale } = useI18n();
    const location = useLocation();
    const queryClient = useQueryClient();

    const dateFormatter = useMemo(() => new Intl.DateTimeFormat(locale, { dateStyle: "medium" }), [locale]);
    const timeFormatter = useMemo(() => new Intl.DateTimeFormat(locale, { hour: "numeric", minute: "2-digit" }), [locale]);

    const dateLabel = event.date ? dateFormatter.format(new Date(`${event.date}T00:00:00`)) : t("events.card.dateFallback");
    const timeLabel =
        "time" in event && event.date && event.time
            ? timeFormatter.format(new Date(`${event.date}T${event.time}`))
            : t("events.card.timeFallback");
    const locationLabel =
        ("city" in event && event.city?.name) || ("address" in event && event.address?.trim()) || t("events.card.locationFallback");
    const organizerName = "user" in event ? event.user?.username : undefined;
    const imageUrl =
        ("flyerImageUrl" in event && event.flyerImageUrl) ||
        ("flyerUrl" in event && event.flyerUrl) ||
        ("imageUrl" in event && event.imageUrl) ||
        undefined;

    let capacityLabel: string;
    if (typeof event.attendeesLimit === "number") {
        const count = event.attendeesCount ?? 0;
        capacityLabel = t("events.card.capacity.withLimit", {
            values: { count, limit: event.attendeesLimit },
        });
        if (event.isFull) {
            capacityLabel = `${capacityLabel} • ${t("events.card.capacity.full")}`;
        }
    } else if (typeof event.attendeesCount === "number" && event.attendeesCount > 0) {
        capacityLabel = t("events.card.capacity.attending", {
            values: { count: event.attendeesCount },
        });
    } else {
        capacityLabel = t("events.card.capacity.unlimited");
    }
    const handlePrefetch = () => prefetchEventDetail(queryClient, event.id);

    return (
        <Link
            to={`/events/${event.id}`}
            state={{ from: `${location.pathname}${location.search}` }}
            className="listing-card-link"
            onMouseEnter={handlePrefetch}
            onFocus={handlePrefetch}
            onClick={() => pushToNavigationStack(`${location.pathname}${location.search}`)}
        >
            <BaseCard imageUrl={imageUrl} badge={event.isFull ? t("events.card.status.full") : undefined}>
                <h3 className="listing-card__title">{event.title}</h3>
                {event.description && <p className="listing-card__description">{event.description}</p>}
                <div className="listing-card__meta">
                    <CardMetaRow icon={<ClockIcon />} label={t("events.card.date")} value={`${dateLabel} • ${timeLabel}`} />
                    <CardMetaRow icon={<MapPinIcon />} label={t("events.card.location")} value={locationLabel} />
                    <CardMetaRow icon={<UsersIcon />} label={t("events.card.capacity")} value={capacityLabel} />
                    {organizerName && <CardMetaRow icon={<UserIcon />} label={t("event.organizer")} value={organizerName} />}
                </div>
            </BaseCard>
        </Link>
    );
}
