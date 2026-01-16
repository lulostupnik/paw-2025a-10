import { useCallback, useMemo, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import ListingLayout from "@/components/listing/ListingLayout";
import JourneyCard from "@/components/journeys/JourneyCard";
import EmptyState from "@/components/EmptyState";
import Button from "@/components/ui/Button";
import ListingFiltersDialog from "@/components/listing/ListingFiltersDialog";
import ListingSortDropdown from "@/components/listing/ListingSortDropdown";
import LoginRequiredModal from "@/components/LoginRequiredModal";
import { useAuthGate } from "@/hooks/useAuthGate";
import { useI18n } from "@/lib/i18n";
import { useJourneys } from "@/hooks/useJourneys";
import { useUrlSyncedListingFilters, type ListingFiltersState } from "@/hooks/useListingFilters";

export default function JourneysListPage() {
    const { t } = useI18n();
    const nav = useNavigate();
    const gate = useAuthGate();
    const [search, setSearch] = useState("");
    const [filtersOpen, setFiltersOpen] = useState(false);
    const [sortOpen, setSortOpen] = useState(false);
    const filtersButtonRef = useRef<HTMLButtonElement>(null);
    const sortButtonRef = useRef<HTMLButtonElement>(null);
    const { filters, applyFilters, resetFilters } = useUrlSyncedListingFilters();
    const [selectedSort, setSelectedSort] = useState("journey-start-asc");
    const journeyTabs = useMemo(
        () => [
            { id: "all", label: t("journey.tabs.all") },
            { id: "myDestination", label: t("journey.tabs.myDestination") },
            { id: "ongoing", label: t("journey.tabs.ongoing") },
            { id: "upcoming", label: t("journey.tabs.upcoming") },
            { id: "past", label: t("journey.tabs.past") },
        ],
        [t]
    );
    const journeySortOptions = useMemo(
        () => [
            { id: "journey-start-asc", label: t("journey.sort.startDate.asc") },
            { id: "journey-start-desc", label: t("journey.sort.startDate.desc") },
            { id: "journey-end-asc", label: t("journey.sort.endDate.asc") },
            { id: "journey-end-desc", label: t("journey.sort.endDate.desc") },
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
    const [activeTab, setActiveTab] = useState(journeyTabs[0].id);

    const { journeys, loading, error } = useJourneys({
        destination: filters.cityName || undefined,
        startDate: filters.afterDate || undefined,
        endDate: filters.beforeDate || undefined,
        interest: filters.interestName || undefined,
        upcoming: activeTab === "upcoming",
        past: activeTab === "past",
        ongoing: activeTab === "ongoing",
        myDestination: activeTab === "myDestination",
        search: search || undefined,
        sort: selectedSort.includes("start") ? "start_date" : "end_date",
        direction: selectedSort.endsWith("desc") ? "desc" : "asc",
        page: 0,
        size: 12,
    });

    const handleCreate = () => {
        gate.runOrPrompt(() => nav("/journeys/create"));
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

    const handleSortSelect = useCallback((id: string) => {
        setSelectedSort(id);
        closeSort();
    }, [closeSort]);

    return (
        <>
            <div className="page-shell listing-page-shell">
                <ListingLayout
                    title={t("journeys.page.title")}
                    searchPlaceholder={t("journeys.search.placeholder")}
                    searchAriaLabel={t("journeys.search.placeholder")}
                    searchValue={search}
                    onSearchChange={setSearch}
                    tabs={journeyTabs}
                    activeTab={activeTab}
                    onTabChange={setActiveTab}
                    toolbarButtons={toolbarButtons}
                    createLabel={t("journeys.create")}
                    onCreate={handleCreate}
                >
                    {loading && <p className="section__helper">{t("admin.dashboard.loading", { defaultValue: "Cargando..." })}</p>}
                    {!loading && error && journeys.length > 0 && (
                        <p className="section__helper">{t("admin.dashboard.error", { defaultValue: "Error cargando datos." })}</p>
                    )}

                    {!loading && error && journeys.length === 0 ? (
                        <EmptyState title={t("admin.dashboard.error", { defaultValue: "Error cargando datos." })} />
                    ) : journeys.length === 0 ? (
                        <EmptyState
                            title={t("journey.no.journeys")}
                            action={
                                <div className="listing-cta__actions">
                                    <Button type="button" variant="primary" size="sm" onClick={handleCreate}>
                                        {t("journey.create.button")}
                                    </Button>
                                </div>
                            }
                        />
                    ) : (
                        <div className="listing-grid">
                            {journeys.map((journey) => (
                                <JourneyCard key={journey.id} journey={journey} />
                            ))}
                        </div>
                    )}
                </ListingLayout>
            </div>
            <ListingFiltersDialog
                mode="journeys"
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
                options={journeySortOptions}
                selectedId={selectedSort}
                onSelect={handleSortSelect}
                onClose={closeSort}
            />
            <LoginRequiredModal open={gate.open} onClose={gate.close} nextPath="/journeys/create" />
        </>
    );
}
