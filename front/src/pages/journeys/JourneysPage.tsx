import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import ListingLayout from "@/components/listing/ListingLayout";
import JourneyCard from "@/components/journeys/JourneyCard";
import EmptyState from "@/components/EmptyState";
import Button from "@/components/ui/Button";
import ListingFiltersDialog from "@/components/listing/ListingFiltersDialog";
import ListingSortDropdown from "@/components/listing/ListingSortDropdown";
import LoginRequiredModal from "@/components/LoginRequiredModal";
import ListingSkeletonGrid from "@/components/listing/ListingSkeletonGrid";
import { useAuthGate } from "@/hooks/useAuthGate";
import { useI18n } from "@/lib/i18n";
import { useJourneys } from "@/hooks/useJourneys";
import { useUrlSyncedListingFilters, type ListingFiltersState } from "@/hooks/useListingFilters";
import { isLoggedIn } from "@/lib/auth/auth";
import Pagination from "@/components/listing/Pagination";
import { useProfileDetail } from "@/hooks/profiles/useProfileDetail";

const DEFAULT_SORT = "journey-start-asc";

const parseIdFromUrl = (url?: string | null) => {
    if (!url) {
        return null;
    }
    const match = url.match(/\/(\d+)(?:\/)?$/);
    return match ? Number(match[1]) : null;
};

export default function JourneysListPage() {
    const { t } = useI18n();
    const nav = useNavigate();
    const [searchParams, setSearchParams] = useSearchParams();
    const gate = useAuthGate();
    const logged = isLoggedIn();
    const { data: profile } = useProfileDetail({ profileId: "me", enabled: logged });
    const journeyId = parseIdFromUrl(profile?.links?.journeyUrl);
    const hasJourney = Boolean(journeyId);
    const initialSearch = searchParams.get("search") ?? "";
    const initialTabParam = searchParams.get("tab") ?? "all";
    const initialTab = logged || initialTabParam !== "myDestination" ? initialTabParam : "all";
    const initialSortParam = searchParams.get("sort") ?? DEFAULT_SORT;
    const initialSort = ["journey-start-asc", "journey-start-desc", "journey-end-asc", "journey-end-desc"].includes(initialSortParam)
        ? initialSortParam
        : DEFAULT_SORT;
    const [search, setSearch] = useState(initialSearch);
    const [filtersOpen, setFiltersOpen] = useState(false);
    const [sortOpen, setSortOpen] = useState(false);
    const filtersButtonRef = useRef<HTMLButtonElement>(null);
    const sortButtonRef = useRef<HTMLButtonElement>(null);
    const { filters, applyFilters, resetFilters } = useUrlSyncedListingFilters();
    const [selectedSort, setSelectedSort] = useState(initialSort);
    const journeyTabs = useMemo(() => {
        const tabs = [
            { id: "all", label: t("journey.tabs.all") },
            { id: "myDestination", label: t("journey.tabs.myDestination") },
            { id: "ongoing", label: t("journey.tabs.ongoing") },
            { id: "upcoming", label: t("journey.tabs.upcoming") },
            { id: "past", label: t("journey.tabs.past") },
        ];
        return logged ? tabs : tabs.filter((tab) => tab.id !== "myDestination");
    }, [logged, t]);
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
    const [activeTab, setActiveTab] = useState(initialTab);

    useEffect(() => {
        const nextSearch = searchParams.get("search") ?? "";
        const nextTabParam = searchParams.get("tab") ?? "all";
        const nextTab = logged || nextTabParam !== "myDestination" ? nextTabParam : "all";
        const nextSortParam = searchParams.get("sort") ?? DEFAULT_SORT;
        const nextSort = ["journey-start-asc", "journey-start-desc", "journey-end-asc", "journey-end-desc"].includes(nextSortParam)
            ? nextSortParam
            : DEFAULT_SORT;
        if (nextSearch !== search) {
            setSearch(nextSearch);
        }
        if (nextTab !== activeTab) {
            setActiveTab(nextTab);
        }
        if (nextSort !== selectedSort) {
            setSelectedSort(nextSort);
        }
    }, [activeTab, logged, search, searchParams, selectedSort]);

    const { journeys, loading, error } = useJourneys({
        destination: filters.cityName || undefined,
        startDate: filters.afterDate || undefined,
        endDate: filters.beforeDate || undefined,
        interest: filters.interestName || undefined,
        upcoming: activeTab === "upcoming",
        past: activeTab === "past",
        ongoing: activeTab === "ongoing",
        myDestination: logged && activeTab === "myDestination",
        search: search || undefined,
        sort: selectedSort.includes("start") ? "start_date" : "end_date",
        direction: selectedSort.endsWith("desc") ? "desc" : "asc",
        page: parseInt(searchParams.get("page") ?? "1"),
        size: 12,
    });

    const handleCreate = () => {
        gate.runOrPrompt(() => nav(journeyId ? `/journeys/${journeyId}` : "/journeys/create"));
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
        setSearchParams((prev) => {
            const next = new URLSearchParams(prev);
            if (id === DEFAULT_SORT) {
                next.delete("sort");
            } else {
                next.set("sort", id);
            }
            return next;
        }, { replace: true });
    }, [closeSort, setSearchParams]);

    const handleTabChange = useCallback(
        (tab: string) => {
            setActiveTab(tab);
            setSearchParams((prev) => {
                const next = new URLSearchParams(prev);
                if (tab === "all") {
                    next.delete("tab");
                } else {
                    next.set("tab", tab);
                }
                return next;
            }, { replace: true });
        },
        [setSearchParams]
    );

    const handleSearchSubmit = useCallback(
        (value: string) => {
            const trimmed = value.trim();
            setSearchParams((prev) => {
                const next = new URLSearchParams(prev);
                if (trimmed) {
                    next.set("search", trimmed);
                } else {
                    next.delete("search");
                }
                return next;
            }, { replace: true });
        },
        [setSearchParams]
    );

    const handlePageChange = useCallback(
        (page: number | string) => {
            if (typeof(page) === 'string'){
                const url = new URL(page);
                setSearchParams((prev) => {
                    return url.searchParams
                }, {replace: true})
            } else {
                setSearchParams((prev) => {
                    const next = new URLSearchParams(prev);
                    next.set("page", page.toString())
                    return next
                }, {replace: true})
            }
        },
        [setSearchParams]
    );

    return (
        <>
            <div className="page-shell listing-page-shell">
                <ListingLayout
                    title={t("journeys.page.title")}
                    searchPlaceholder={t("journeys.search.placeholder")}
                    searchAriaLabel={t("journeys.search.placeholder")}
                    searchValue={search}
                    onSearchChange={setSearch}
                    onSearchSubmit={handleSearchSubmit}
                    tabs={journeyTabs}
                    activeTab={activeTab}
                    onTabChange={handleTabChange}
                    toolbarButtons={toolbarButtons}
                    createLabel={hasJourney ? t("journey.view.my") : t("journeys.create")}
                    onCreate={handleCreate}
                >
                    {loading && <ListingSkeletonGrid count={12} />}
                    {!loading && error && journeys.content.length > 0 && (
                        <p className="section__helper">{t("admin.dashboard.error", { defaultValue: "Error cargando datos." })}</p>
                    )}

                    {!loading && error && journeys.content.length === 0 ? (
                        <EmptyState title={t("admin.dashboard.error", { defaultValue: "Error cargando datos." })} />
                    ) : journeys.content.length === 0 ? (
                        <EmptyState
                            title={t("journey.no.journeys")}
                            action={
                                <div className="listing-cta__actions">
                                    <Button type="button" variant="primary" size="sm" onClick={handleCreate}>
                                        {hasJourney ? t("journey.view.my") : t("journey.create.button")}
                                    </Button>
                                </div>
                            }
                        />
                    ) : (
                        <div className="listing-grid">
                            {journeys.content.map((journey) => (
                                <JourneyCard key={journey.id} journey={journey} />
                            ))}
                        </div>
                    )}
                    <Pagination
                        totalPages={journeys.totalPages}
                        currentPage={journeys.currentPage}
                        pageSize={journeys.pageSize}
                        previousLabel=""
                        nextLabel=""
                        nextPage={journeys.next}
                        prevPage={journeys.prev}
                        lastPage={journeys.last}
                        firstPage={journeys.first}
                        onPageChange={handlePageChange}
                    />
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
