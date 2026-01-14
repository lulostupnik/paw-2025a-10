import { useCallback, useMemo, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import ListingLayout from "@/components/listing/ListingLayout";
import EmptyState from "@/components/EmptyState";
import EventCard from "@/components/cards/EventCard";
import Button from "@/components/ui/Button";
import ListingFiltersDialog from "@/components/listing/ListingFiltersDialog";
import ListingSortDropdown from "@/components/listing/ListingSortDropdown";
import { useEvents } from "@/hooks/useEvents";
import { useI18n } from "@/lib/i18n";
import { useAuthGate } from "@/hooks/useAuthGate";
import LoginRequiredModal from "@/components/LoginRequiredModal";
import { useUrlSyncedListingFilters, type ListingFiltersState } from "@/hooks/useListingFilters";

export default function EventsListPage() {
    const { t } = useI18n();
    const navigate = useNavigate();
    const gate = useAuthGate();
    const { events, loading, error } = useEvents({ size: 12 });
    const [search, setSearch] = useState("");
    const [filtersOpen, setFiltersOpen] = useState(false);
    const [sortOpen, setSortOpen] = useState(false);
    const filtersButtonRef = useRef<HTMLButtonElement>(null);
    const sortButtonRef = useRef<HTMLButtonElement>(null);
    const { filters, applyFilters, resetFilters } = useUrlSyncedListingFilters();
    const [selectedSort, setSelectedSort] = useState("event-date-asc");
    const eventSortOptions = useMemo(
        () => [
            { id: "event-date-asc", label: t("event.sort.date.asc") },
            { id: "event-date-desc", label: t("event.sort.date.desc") },
            { id: "event-attendees-asc", label: t("event.sort.attendees.asc") },
            { id: "event-attendees-desc", label: t("event.sort.attendees.desc") },
            { id: "event-rating-asc", label: t("event.sort.rating.asc") },
            { id: "event-rating-desc", label: t("event.sort.rating.desc") },
        ],
        [t]
    );
    const eventTabs = useMemo(
        () => [
            { id: "all", label: t("events.tabs.all") },
            { id: "upcoming", label: t("events.tabs.upcoming") },
            { id: "past", label: t("events.tabs.past") },
        ],
        [t]
    );
    const openFilters = useCallback(() => {
        setSortOpen(false);
        setFiltersOpen((prev) => !prev);
    }, []);
    const closeFilters = useCallback(() => setFiltersOpen(false), []);
    const toggleSort = useCallback(() => {
        setFiltersOpen(false);
        setSortOpen((prev) => !prev);
    }, []);
    const closeSort = useCallback(() => setSortOpen(false), []);
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
                onClick: openFilters,
                ref: filtersButtonRef,
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
                onClick: toggleSort,
                ref: sortButtonRef,
            },
        ],
        [openFilters, t, toggleSort]
    );
    const [activeTab, setActiveTab] = useState(eventTabs[0].id);

    const handleCreate = () => {
        gate.runOrPrompt(() => navigate("/events/create"));
    };

    const handleApplyFilters = useCallback(
        (next: ListingFiltersState) => {
            applyFilters(next);
            closeFilters();
        },
        [applyFilters, closeFilters]
    );

    const handleResetFilters = useCallback(() => {
        resetFilters();
    }, [resetFilters]);

    const handleSortSelect = useCallback(
        (id: string) => {
            setSelectedSort(id);
            closeSort();
        },
        [closeSort]
    );

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
                                />
                            ))}
                        </div>
                    )}
                </ListingLayout>
            </div>
            <ListingFiltersDialog
                mode="events"
                open={filtersOpen}
                filters={filters}
                anchorRef={filtersButtonRef}
                onClose={closeFilters}
                onApply={handleApplyFilters}
                onReset={handleResetFilters}
            />
            <ListingSortDropdown
                title={t("listing.sort")}
                open={sortOpen}
                anchorRef={sortButtonRef}
                options={eventSortOptions}
                selectedId={selectedSort}
                onSelect={handleSortSelect}
                onClose={closeSort}
            />
            <LoginRequiredModal open={gate.open} onClose={gate.close} nextPath="/events/create" />
        </>
    );
}
