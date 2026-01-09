import { useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import ListingLayout from "@/components/listing/ListingLayout";
import EmptyState from "@/components/EmptyState";
import EventCard from "@/components/cards/EventCard";
import Button from "@/components/ui/Button";
import { useEvents } from "@/hooks/useEvents";
import { useI18n } from "@/lib/i18n";
import { useAuthGate } from "@/hooks/useAuthGate";
import LoginRequiredModal from "@/components/LoginRequiredModal";

export default function EventsListPage() {
    const { t } = useI18n();
    const navigate = useNavigate();
    const gate = useAuthGate();
    const { events, loading, error, refetch } = useEvents({ size: 12 });
    const [search, setSearch] = useState("");
    const eventTabs = useMemo(
        () => [
            { id: "all", label: t("events.tabs.all") },
            { id: "upcoming", label: t("events.tabs.upcoming") },
            { id: "past", label: t("events.tabs.past") },
        ],
        [t]
    );
    const toolbarButtons = useMemo(
        () => [
            {
                id: "filters",
                label: t("listing.filters"),
                icon: (
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth={2}>
                        <path d="M4 6h16" />
                        <path d="M6 12h12" />
                        <path d="M10 18h4" />
                    </svg>
                ),
            },
            {
                id: "sort",
                label: t("listing.sort"),
                icon: (
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth={2}>
                        <path d="m3 9 4-4 4 4" />
                        <path d="M7 5v14" />
                        <path d="m21 15-4 4-4-4" />
                        <path d="M17 19V5" />
                    </svg>
                ),
            },
        ],
        [t]
    );
    const [activeTab, setActiveTab] = useState(eventTabs[0].id);

    const handleCreate = () => {
        gate.runOrPrompt(() => navigate("/events/create"));
    };

    return (
        <>
            <div className="page-shell listing-page-shell">
            <ListingLayout
                title={t("events.page.title")}
                searchPlaceholder={t("events.search.placeholder")}
                searchAriaLabel={t("events.search.placeholder")}
                searchValue={search}
                onSearchChange={setSearch}
                tabs={eventTabs}
                activeTab={activeTab}
                onTabChange={setActiveTab}
                toolbarButtons={toolbarButtons}
                createLabel={t("events.create")}
                onCreate={handleCreate}
            >
                {loading && <p className="section__helper">{t("events.list.loading")}</p>}

                {!loading && (error || events.length === 0) ? (
                    <EmptyState
                        title={t("events.list.empty")}
                        description={t("events.list.empty.description") || undefined}
                        action={
                            <div className="listing-cta__actions">
                                <Button type="button" variant="primary" size="sm" onClick={handleCreate}>
                                    {t("events.cta.button")}
                                </Button>
                            </div>
                        }
                    />
                ) : (
                    <div className="listing-grid">
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
            </ListingLayout>
            </div>
            <LoginRequiredModal open={gate.open} onClose={gate.close} nextPath="/events/create" />
        </>
    );
}
