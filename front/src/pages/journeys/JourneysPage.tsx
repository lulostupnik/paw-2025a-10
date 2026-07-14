import { useCallback, useMemo, useRef, useState } from "react";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import { useNavigate, useSearchParams } from "react-router-dom";
import { getCityByUrl, getJourneyById, getUniversityByUrl } from "@/lib/api/journeys";
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
    const queryClient = useQueryClient();
    const [searchParams, setSearchParams] = useSearchParams();
    const gate = useAuthGate();
    const logged = isLoggedIn();
    const { data: profile } = useProfileDetail({ profileId: "me", enabled: logged });
    const journeyId = parseIdFromUrl(profile?.links?.journeyUrl);
    const hasJourney = Boolean(journeyId);
    const canSeeMyDestination = logged && hasJourney;
    const { data: myDestinationCityId } = useQuery({
        queryKey: ["my-destination-city", journeyId],
        enabled: canSeeMyDestination && journeyId != null,
        queryFn: async ({ signal }) => {
            const journey = await getJourneyById(journeyId as number, signal);
            const university = await getUniversityByUrl(journey?.links?.destinationUniversityUrl, signal, queryClient);
            const city = university?.links?.cityUrl ? await getCityByUrl(university.links.cityUrl, signal, queryClient) : null;
            return city?.id ?? null;
        },
    });
    const [filtersOpen, setFiltersOpen] = useState(false);
    const [sortOpen, setSortOpen] = useState(false);
    const filtersButtonRef = useRef<HTMLButtonElement>(null);
    const sortButtonRef = useRef<HTMLButtonElement>(null);
    const { filters, applyFilters, resetFilters } = useUrlSyncedListingFilters();
    const tabParam = searchParams.get("tab") ?? "all";
    const activeTab = canSeeMyDestination || tabParam !== "myDestination" ? tabParam : "all";
    const sortParam = searchParams.get("sort") ?? DEFAULT_SORT;
    const selectedSort = ["journey-start-asc", "journey-start-desc", "journey-end-asc", "journey-end-desc"].includes(sortParam)
        ? sortParam
        : DEFAULT_SORT;
    const appliedSearch = (searchParams.get("search") ?? "").trim();
    const journeyTabs = useMemo(() => {
        const tabs = [
            { id: "all", label: t("journey.tabs.all") },
            { id: "myDestination", label: t("journey.tabs.myDestination") },
            { id: "ongoing", label: t("journey.tabs.ongoing") },
            { id: "upcoming", label: t("journey.tabs.upcoming") },
            { id: "past", label: t("journey.tabs.past") },
        ];
        return canSeeMyDestination ? tabs : tabs.filter((tab) => tab.id !== "myDestination");
    }, [canSeeMyDestination, t]);
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
    const { journeys, loading, error } = useJourneys({
        city: filters.cityName || undefined,
        university: filters.universityName || undefined,
        startDate: filters.afterDate || undefined,
        endDate: filters.beforeDate || undefined,
        interest: filters.interestName || undefined,
        upcoming: activeTab === "upcoming",
        past: activeTab === "past",
        ongoing: activeTab === "ongoing",
        destinationCity: activeTab === "myDestination" ? (myDestinationCityId ?? undefined) : undefined,
        excludeUser: logged && profile?.id ? profile.id : undefined,
        search: appliedSearch || undefined,
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
        closeSort();
        setSearchParams((prev) => {
            const next = new URLSearchParams(prev);
            if (id === DEFAULT_SORT) {
                next.delete("sort");
            } else {
                next.set("sort", id);
            }
            next.delete("page");
            return next;
        });
    }, [closeSort, setSearchParams]);

    const handleTabChange = useCallback(
        (tab: string) => {
            setSearchParams((prev) => {
                const next = new URLSearchParams(prev);
                if (tab === "all") {
                    next.delete("tab");
                } else {
                    next.set("tab", tab);
                }
                next.delete("page");
                return next;
            });
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
                next.delete("page");
                return next;
            });
        },
        [setSearchParams]
    );

    const handlePageChange = useCallback(
        (page: number | string) => {
            if (typeof page === 'string') {
                const pageNumber = new URL(page).searchParams.get('page') ?? '1';
                setSearchParams((prev) => {
                    const next = new URLSearchParams(prev);
                    next.set('page', pageNumber);
                    return next;
                });
            } else {
                setSearchParams((prev) => {
                    const next = new URLSearchParams(prev);
                    next.set("page", page.toString())
                    return next
                })
            }
        },
        [setSearchParams]
    );
    const viewJourneyIcon = (
        <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
            <path d="M9 20l-5.447-2.724A1 1 0 013 16.382V5.618a1 1 0 011.447-.894L9 7m0 13l6-3m-6 3V7m6 10l4.553 2.276A1 1 0 0021 18.382V7.618a1 1 0 00-.553-.894L15 4m0 13V4m0 0L9 7"></path>
        </svg>
    );

    return (
        <>
            <div className="page-shell listing-page-shell">
                <ListingLayout
                    title={t("journeys.page.title")}
                    searchPlaceholder={t("journeys.search.placeholder")}
                    searchAriaLabel={t("journeys.search.placeholder")}
                    searchDefaultValue={appliedSearch}
                    searchResetKey={appliedSearch}
                    onSearchSubmit={handleSearchSubmit}
                    tabs={journeyTabs}
                    activeTab={activeTab}
                    onTabChange={handleTabChange}
                    toolbarButtons={toolbarButtons}
                    createLabel={hasJourney ? t("journey.view.my") : t("journeys.create")}
                    onCreate={handleCreate}
                    createIcon={hasJourney ? viewJourneyIcon : undefined}
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
