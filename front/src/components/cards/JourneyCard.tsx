import BaseCard from "./BaseCard";
import CardMetaRow from "./CardMetaRow";
import { useI18n } from "@/lib/i18n";

interface JourneyCardProps {
    journey: JourneyPreview;
}

export interface JourneyPreview {
    id: string;
    title: string;
    location: string;
    startDate: string;
    endDate: string;
    host: string;
    imageUrl?: string;
    status?: string;
}

const CalendarIcon = () => (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth={2}>
        <rect x="3" y="4" width="18" height="18" rx="2" />
        <line x1="16" y1="2" x2="16" y2="6" />
        <line x1="8" y1="2" x2="8" y2="6" />
        <line x1="3" y1="10" x2="21" y2="10" />
    </svg>
);

const MapPinIcon = () => (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth={2}>
        <path d="M12 21s-6-5-6-10a6 6 0 1 1 12 0c0 5-6 10-6 10z" />
        <circle cx="12" cy="11" r="2" />
    </svg>
);

const UserIcon = () => (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth={2}>
        <circle cx="12" cy="8" r="4" />
        <path d="M6 20c0-3.3 2.7-6 6-6s6 2.7 6 6" />
    </svg>
);

export default function JourneyCard({ journey }: JourneyCardProps) {
    const { t } = useI18n();

    return (
        <BaseCard imageUrl={journey.imageUrl} badge={journey.status}>
            <h3 className="listing-card__title">{journey.title}</h3>
            <p className="listing-card__subtitle">{journey.location}</p>
            <div className="listing-card__meta">
                <CardMetaRow
                    icon={<CalendarIcon />}
                    label={t("journeys.meta.duration")}
                    value={`${journey.startDate} · ${journey.endDate}`}
                />
                <CardMetaRow icon={<UserIcon />} label={t("journeys.meta.host")} value={journey.host} />
                <CardMetaRow icon={<MapPinIcon />} label={t("journeys.meta.location")} value={journey.location} />
            </div>
        </BaseCard>
    );
}
