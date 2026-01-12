import { Link, useNavigate } from "react-router-dom";
import Button from "@/components/ui/Button";
import EmptyState from "@/components/EmptyState";
import { useI18n } from "@/lib/i18n";

interface ActionIconProps {
    name: "plus" | "calendar" | "map" | "sparkles";
}

function ActionIcon({ name }: ActionIconProps) {
    switch (name) {
        case "plus":
            return (
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth={1.8}>
                    <path strokeLinecap="round" strokeLinejoin="round" d="M12 5v14m7-7H5" />
                </svg>
            );
        case "calendar":
            return (
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth={1.8}>
                    <rect x="3" y="4" width="18" height="18" rx="2" />
                    <path d="M16 2v4" />
                    <path d="M8 2v4" />
                    <path d="M3 10h18" />
                </svg>
            );
        case "map":
            return (
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth={1.8}>
                    <path d="M9.5 4 3 6.5v13l6.5-2.5 5 2.5 6.5-2.5v-13l-6.5 2.5-5-2.5Z" />
                    <path d="m9.5 4 .01 13" />
                    <path d="m14.5 6.5 .01 13" />
                </svg>
            );
        default:
            return (
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth={1.8}>
                    <path strokeLinecap="round" strokeLinejoin="round" d="M12 3v3m0 12v3m6.364-13.364-2.121 2.121M7.757 16.243l-2.121 2.121M21 12h-3M6 12H3" />
                </svg>
            );
    }
}

export default function ExplorePage() {
    const navigate = useNavigate();
    const { t } = useI18n();

    const quickActions = [
        {
            id: "create-journey",
            title: t("explore.quick.createJourney.title"),
            description: t("explore.quick.createJourney.description"),
            to: "/journeys/create",
            icon: "plus" as const,
        },
        {
            id: "create-event",
            title: t("explore.quick.createEvent.title"),
            description: t("explore.quick.createEvent.description"),
            to: "/events/create",
            icon: "calendar" as const,
        },
        {
            id: "browse-journeys",
            title: t("explore.quick.browseJourneys.title"),
            description: t("explore.quick.browseJourneys.description"),
            to: "/journeys",
            icon: "map" as const,
        },
        {
            id: "browse-events",
            title: t("explore.quick.browseEvents.title"),
            description: t("explore.quick.browseEvents.description"),
            to: "/events",
            icon: "sparkles" as const,
        },
    ];

    return (
        <div className="page-shell explore-page">
            <h1>{t("nav.explore")}</h1>

            <section className="explore-section">
                <div className="explore-section__header">
                    <div>
                        <p className="eyebrow">{t("explore.quick.eyebrow")}</p>
                        <h2>{t("explore.quick.title")}</h2>
                        <p className="explore-section__description">{t("explore.quick.description")}</p>
                    </div>
                </div>
                <div className="explore-quick-actions">
                    {quickActions.map((action) => (
                        <Link key={action.id} to={action.to} className="explore-action-card">
                            <span className="explore-action-icon" aria-hidden="true">
                                <ActionIcon name={action.icon} />
                            </span>
                            <div>
                                <p className="explore-action-title">{action.title}</p>
                                <p className="explore-action-desc">{action.description}</p>
                            </div>
                        </Link>
                    ))}
                </div>
            </section>

            <section className="explore-section">
                <div className="explore-section__header">
                    <div>
                        <p className="eyebrow">{t("explore.journeys.eyebrow")}</p>
                        <h2>{t("explore.journeys.title")}</h2>
                        <p className="explore-section__description">{t("explore.journeys.description")}</p>
                    </div>
                    <Button type="button" variant="ghost" onClick={() => navigate("/journeys")}>
                        {t("explore.journeys.button")}
                    </Button>
                </div>
                <EmptyState
                    title={t("explore.journeys.empty.title")}
                    description={t("explore.journeys.empty.description")}
                    action={
                        <Button type="button" variant="primary" onClick={() => navigate("/journeys")}>
                            {t("explore.journeys.empty.cta")}
                        </Button>
                    }
                    className="explore-empty"
                />
            </section>

            <section className="explore-section">
                <div className="explore-section__header">
                    <div>
                        <p className="eyebrow">{t("explore.events.eyebrow")}</p>
                        <h2>{t("explore.events.title")}</h2>
                        <p className="explore-section__description">{t("explore.events.description")}</p>
                    </div>
                    <Button type="button" variant="ghost" onClick={() => navigate("/events")}>
                        {t("explore.events.button")}
                    </Button>
                </div>
                <EmptyState
                    title={t("explore.events.empty.title")}
                    description={t("explore.events.empty.description")}
                    action={
                        <Button type="button" variant="primary" onClick={() => navigate("/events")}>
                            {t("explore.events.empty.cta")}
                        </Button>
                    }
                    className="explore-empty"
                />
            </section>
        </div>
    );
}
