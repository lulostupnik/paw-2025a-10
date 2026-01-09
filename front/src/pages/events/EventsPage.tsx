import { useNavigate } from "react-router-dom";
import Button from "@/components/ui/Button";
import EmptyState from "@/components/EmptyState";
import EventCard from "@/components/EventCard";
import { useEvents } from "@/hooks/useEvents";
import { useI18n } from "@/lib/i18n";

export default function EventsListPage() {
    const { t } = useI18n();
    const navigate = useNavigate();
    const { events, loading, error, refetch } = useEvents({ size: 12 });

    return (
        <div className="page-shell events-page">
            <section className="section">
                <div className="section__header">
                    <h1>{t("events.page.title")}</h1>
                    <p className="section__subtitle">{t("events.page.subtitle")}</p>
                </div>

                {loading && <p className="section__helper">{t("events.list.loading")}</p>}

                {error && (
                    <div className="section__helper section__helper--error">
                        <p>{t("events.list.error")}</p>
                        <Button variant="outline" size="sm" onClick={refetch}>
                            {t("common.retry")}
                        </Button>
                    </div>
                )}

                {!loading && !error && events.length === 0 && (
                    <EmptyState
                        title={t("events.list.empty")}
                        description={t("events.list.empty.description")}
                    />
                )}

                {events.length > 0 && (
                    <div className="events-grid events-page__grid">
                        {events.map((event) => (
                            <EventCard
                                key={event.id}
                                event={event}
                                actionSlot={
                                    <Button size="sm" variant="outline" onClick={() => navigate(`/events/${event.id}`)}>
                                        {t("landing.event.view")}
                                    </Button>
                                }
                            />
                        ))}
                    </div>
                )}
            </section>
        </div>
    );
}
