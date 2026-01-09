import type { ReactNode } from "react";
import { useMemo } from "react";
import type { EventSummary } from "@/lib/api/events";
import { classNames } from "@/lib/utils/classNames";
import { useI18n } from "@/lib/i18n";

interface EventCardProps {
    event: EventSummary;
    actionSlot?: ReactNode;
    className?: string;
}

const getDateValue = (date?: string) => (date ? new Date(`${date}T00:00:00`) : null);

const getDateTimeValue = (date?: string, time?: string) => {
    if (!date || !time) {
        return null;
    }
    return new Date(`${date}T${time}`);
};

export default function EventCard({ event, actionSlot, className }: EventCardProps) {
    const { t, locale } = useI18n();

    const dateFormatter = useMemo(() => new Intl.DateTimeFormat(locale, { dateStyle: "medium" }), [locale]);
    const timeFormatter = useMemo(() => new Intl.DateTimeFormat(locale, { hour: "numeric", minute: "2-digit" }), [locale]);

    const eventDate = getDateValue(event.date);
    const dateLabel = eventDate ? dateFormatter.format(eventDate) : t("events.card.dateFallback");
    const dateTimeValue = getDateTimeValue(event.date, event.time);
    const timeLabel = dateTimeValue ? timeFormatter.format(dateTimeValue) : t("events.card.timeFallback");
    const locationLabel = event.address?.trim() || t("events.card.locationFallback");

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

    return (
        <article className={classNames("event-card", "card", className)}>
            <header className="event-card__header">
                {event.isFull && <span className="event-card__badge">{t("events.card.status.full")}</span>}
                <h3>{event.title}</h3>
                <p className="event-card__location">{locationLabel}</p>
            </header>

            {event.description && <p className="event-card__description">{event.description}</p>}

            <dl className="event-card__meta">
                <div>
                    <dt>{t("events.card.date")}</dt>
                    <dd>{dateLabel}</dd>
                </div>
                <div>
                    <dt>{t("events.card.time")}</dt>
                    <dd>{timeLabel}</dd>
                </div>
                <div>
                    <dt>{t("events.card.location")}</dt>
                    <dd>{locationLabel}</dd>
                </div>
                <div>
                    <dt>{t("events.card.capacity")}</dt>
                    <dd>{capacityLabel}</dd>
                </div>
            </dl>

            {actionSlot && <div className="event-card__footer">{actionSlot}</div>}
        </article>
    );
}
