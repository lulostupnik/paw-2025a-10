import { useCallback, useMemo, useRef, useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import ListingLayout from "@/components/listing/ListingLayout";
import EmptyState from "@/components/EmptyState";
import EventCard from "@/components/cards/EventCard";
import Button from "@/components/ui/Button";
import ListingFiltersDialog from "@/components/listing/ListingFiltersDialog";
import ListingSortDropdown from "@/components/listing/ListingSortDropdown";
import ListingSkeletonGrid from "@/components/listing/ListingSkeletonGrid";
import { useEvents } from "@/hooks/useEvents";
import { useI18n } from "@/lib/i18n";
import { useAuthGate } from "@/hooks/useAuthGate";
import LoginRequiredModal from "@/components/LoginRequiredModal";
import { useUrlSyncedListingFilters, type ListingFiltersState } from "@/hooks/useListingFilters";
import type { FetchEventsParams } from "@/lib/api/events";
import { getUserId, isLoggedIn } from "@/lib/auth/auth";
import Pagination from "@/components/listing/Pagination";
import { getTodayIsoDate } from "@/lib/utils/date";

const SORT_MAPPING: Record<
    string,
    { sort: FetchEventsParams["sort"]; direction: FetchEventsParams["direction"] }
> = {
    "event-date-asc": { sort: "date", direction: "asc" },
    "event-date-desc": { sort: "date", direction: "desc" },
    "event-attendees-asc": { sort: "attendees", direction: "asc" },
    "event-attendees-desc": { sort: "attendees", direction: "desc" },
    "event-rating-asc": { sort: "rating", direction: "asc" },
    "event-rating-desc": { sort: "rating", direction: "desc" },
};
const DEFAULT_SORT = "event-date-asc";
const SORT_IDS = new Set(Object.keys(SORT_MAPPING));

const mapSortParams = (id: string) => SORT_MAPPING[id] ?? SORT_MAPPING["event-date-asc"];

const mapTabParams = (
    tab: string,
    userId: number | null,
    today: string
): Pick<FetchEventsParams, "afterDate" | "beforeDate" | "attendedBy"> => {
    if (tab === "upcoming") {
        return { afterDate: today };
    }
    if (tab === "past") {
        return { beforeDate: today };
    }
    if (tab === "attending" && typeof userId === "number") {
        return { attendedBy: userId };
    }
    return {};
};

export default function EventsListPage() {
    const { t } = useI18n();
    const navigate = useNavigate();
    const [searchParams, setSearchParams] = useSearchParams();
    const gate = useAuthGate();
    const logged = isLoggedIn();
    const userId = logged ? getUserId() : null;
    const today = getTodayIsoDate();
    const [filtersOpen, setFiltersOpen] = useState(false);
    const [sortOpen, setSortOpen] = useState(false);
    const filtersButtonRef = useRef<HTMLButtonElement>(null);
    const sortButtonRef = useRef<HTMLButtonElement>(null);
    const { filters, applyFilters, resetFilters } = useUrlSyncedListingFilters();
    const tabParam = searchParams.get("tab") ?? "all";
    const activeTab = logged || tabParam !== "attending" ? tabParam : "all";
    const sortParam = searchParams.get("sort") ?? DEFAULT_SORT;
    const selectedSort = SORT_IDS.has(sortParam) ? sortParam : DEFAULT_SORT;
    const appliedSearch = (searchParams.get("search") ?? "").trim();

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
    const eventTabs = useMemo(() => {
        const tabs = [
            { id: "all", label: t("events.tabs.all") },
            { id: "upcoming", label: t("events.tabs.upcoming") },
            { id: "past", label: t("events.tabs.past") },
        ];
        if (logged) {
            tabs.splice(2, 0, { id: "attending", label: t("events.tabs.attending") });
        }
        return tabs;
    }, [logged, t]);
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
    const sortParams = useMemo(() => mapSortParams(selectedSort), [selectedSort]);
    const tabParams = useMemo(() => mapTabParams(activeTab, userId, today), [activeTab, today, userId]);
    const eventQueryParams = useMemo<FetchEventsParams>(() => {
        const params: FetchEventsParams = {
            page: parseInt(searchParams.get("page") ?? "1"),
            size: 12,
            ...sortParams,
            ...tabParams,
        };
        if (appliedSearch) {
            params.search = appliedSearch;
        }
        if (filters.cityName) {
            params.destination = filters.cityName;
        }
        if (filters.universityName) {
            params.university = filters.universityName;
        }
        if (filters.interestName) {
            params.interest = filters.interestName;
        }
        if (filters.minRating) {
            params.minRating = filters.minRating;
        }
        if (filters.hasCapacity) {
            params.hasCapacity = true;
        }
        const filterAfter = filters.afterDate || undefined;
        const filterBefore = filters.beforeDate || undefined;
        const tabAfter = tabParams.afterDate;
        const tabBefore = tabParams.beforeDate;
        const effectiveAfter = tabAfter && filterAfter ? (tabAfter > filterAfter ? tabAfter : filterAfter) : tabAfter || filterAfter;
        const effectiveBefore = tabBefore && filterBefore ? (tabBefore < filterBefore ? tabBefore : filterBefore) : tabBefore || filterBefore;
        if (effectiveAfter) {
            params.afterDate = effectiveAfter;
        }
        if (effectiveBefore) {
            params.beforeDate = effectiveBefore;
        }
        return params;
    }, [appliedSearch, filters.afterDate, filters.beforeDate, filters.cityName, filters.hasCapacity, filters.interestName, filters.minRating, filters.universityName, sortParams, tabParams, searchParams]);

    const { events, loading, error } = useEvents(eventQueryParams);

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
        },
        [closeSort, setSearchParams]
    );

    const handleTabChange = useCallback(
        (tab: string) => {
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
                next.delete("page");
                return next;
            }, { replace: true });
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
                }, { replace: true });
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

    const emptyStateTitle = error ? t("events.list.error") : t("events.list.empty");
    const emptyStateDescription = error
        ? t("events.list.error.description")
        : t("events.list.empty.description") || undefined;

    return (
        <>
            <div className="page-shell listing-page-shell">
                <ListingLayout
                    title={t("events.page.title")}
                    searchPlaceholder={t("events.search.placeholder")}
                    searchAriaLabel={t("events.search.placeholder")}
                    searchDefaultValue={appliedSearch}
                    searchResetKey={appliedSearch}
                    onSearchSubmit={handleSearchSubmit}
                    tabs={eventTabs}
                    activeTab={activeTab}
                    onTabChange={handleTabChange}
                    toolbarButtons={toolbarButtons}
                    createLabel={t("events.create")}
                    onCreate={handleCreate}
                >
                    {loading && <ListingSkeletonGrid count={12} />}

                    {!loading && (error || events.content.length === 0) ? (
                        <EmptyState
                            title={emptyStateTitle}
                            description={emptyStateDescription}
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
                            {events.content.map((event) => (
                                <EventCard
                                    key={event.id}
                                    event={event}
                                />
                            ))}
                        </div>
                    )}
                    <Pagination
                        totalPages={events.totalPages}
                        currentPage={events.currentPage}
                        pageSize={events.pageSize}
                        nextPage={events.next}
                        lastPage={events.last}
                        prevPage={events.prev}
                        firstPage={events.first}
                        onPageChange={handlePageChange}
                        previousLabel={t("pagination.prev")}
                        nextLabel={t("pagination.next")}
                    />
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
