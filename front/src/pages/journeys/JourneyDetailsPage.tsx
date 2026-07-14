import { apiErrorMessage } from "@/lib/api/client";
import { useCallback, useEffect, useState, type FormEvent } from "react";
import { Link, useLocation, useNavigate, useParams, useSearchParams } from "react-router-dom";
import { keepPreviousData, useQuery, useQueryClient } from "@tanstack/react-query";
import { useI18n } from "@/lib/i18n";
import { getUserId, getUsername, isAdmin } from "@/lib/auth/auth";
import CreatorCard from "@/components/detail/CreatorCard";
import Pagination from "@/components/listing/Pagination";
import PageStatus from "@/components/ui/PageStatus";
import { useJourneyDetailData } from "@/hooks/useJourneyDetailData";
import { createJourneyResponse, getCityByUrl, getJourneyResponses, getUserByUrl, listJourneyTips } from "@/lib/api/journeys";
import { fetchEvents, type EventDto } from "@/lib/api/events";
import { emptyPage, mapPageList, type PageResult } from "@/types/pagination";
import { getUserInterests } from "@/lib/api/users";
import { popFromNavigationStack, pushToNavigationStack } from "@/lib/utils/navigationStack";
import { useAuthGate } from "@/hooks/useAuthGate";
import LoginRequiredModal from "@/components/LoginRequiredModal";
import NotFoundPage from "@/pages/errors/NotFoundPage";
import type { JourneyComment, JourneyEvent, JourneyTip } from "@/types/journey";
import type { ProfileInterest } from "@/types/profile";
import { parseApiDate } from "@/lib/utils/date";
import AvatarFallbackIcon from "@/components/ui/AvatarFallbackIcon";
import type { QueryClient } from "@tanstack/react-query";

const TIPS_PAGE_PARAM = "tipsPage";
const COMMENTS_PAGE_PARAM = "commentsPage";
const INTERESTS_PAGE_PARAM = "interestsPage";
const EVENTS_TAB_PARAM = "eventsTab";
const CREATED_EVENTS_PAGE_PARAM = "createdEventsPage";
const ATTENDING_EVENTS_PAGE_PARAM = "attendingEventsPage";
const TIPS_PAGE_SIZE = 4;
const COMMENTS_PAGE_SIZE = 4;

const parsePageParam = (value: string | null) => {
    const raw = Number(value ?? "1");
    return Number.isFinite(raw) && raw > 0 ? raw : 1;
};

const parseEventsTabParam = (value: string | null): "created" | "attending" => {
    if (value === "attending") {
        return value;
    }
    return "created";
};

const formatDate = (value: string, locale: string) => {
    const date = parseApiDate(value);
    if (Number.isNaN(date.getTime())) {
        return value;
    }
    return new Intl.DateTimeFormat(locale, { year: "numeric", month: "long", day: "numeric" }).format(date);
};

const formatDateTime = (value: string, locale: string) => {
    const date = parseApiDate(value);
    if (Number.isNaN(date.getTime())) {
        return value;
    }
    return new Intl.DateTimeFormat(locale, {
        year: "numeric",
        month: "long",
        day: "numeric",
        hour: "2-digit",
        minute: "2-digit",
    }).format(date);
};

const mapEventPageToJourneyEvents = async (page: PageResult<EventDto>, signal?: AbortSignal, queryClient?: QueryClient): Promise<PageResult<JourneyEvent>> => {
    const cities = await Promise.all(
        page.content.map((event) => (event.links?.cityUrl ? getCityByUrl(event.links.cityUrl, signal, queryClient) : Promise.resolve(null)))
    );
    const mapped = page.content.map((event, index) => ({
        id: event.id,
        title: event.title,
        description: event.description ?? "",
        date: event.date ?? "",
        time: event.time ?? null,
        city: cities[index]?.name ?? "—",
        flyerImageUrl: event.links?.flyerUrl ?? null,
    }));
    return mapPageList(page, mapped);
};

interface JourneyResponseApi {
    id: number;
    message: string;
    dateTime: string;
    links?: {
        authorUrl?: string | null;
    } | null;
}

const mapJourneyResponsesPage = async (page: PageResult<JourneyResponseApi>, signal?: AbortSignal, queryClient?: QueryClient): Promise<PageResult<JourneyComment>> => {
    const users = await Promise.all(
        page.content.map((response) => (response.links?.authorUrl ? getUserByUrl(response.links.authorUrl, signal, queryClient) : Promise.resolve(null)))
    );
    const mapped = page.content.map((response, index) => ({
        id: response.id,
        message: response.message,
        dateTime: response.dateTime,
        user: {
            username: users[index]?.username ?? "—",
        },
    }));
    return mapPageList(page, mapped);
};

export default function JourneyDetailPage() {
    const { t, locale } = useI18n();
    const navigate = useNavigate();
    const location = useLocation();
    const [searchParams, setSearchParams] = useSearchParams();
    const { id } = useParams();
    const numericId = Number(id);
    const hasValidId = Number.isInteger(numericId) && numericId > 0 && numericId <= Number.MAX_SAFE_INTEGER;
    const queryClient = useQueryClient();
    const gate = useAuthGate();
    const {
        data,
        isLoading,
        isError,
        isNotFound,
        refetch,
        isFetching,
        creatorLoading,
        creatorError,
        creatorReady,
        destinationLoading,
        destinationReady,
    } = useJourneyDetailData({ journeyId: hasValidId ? id : undefined });
    const [actionMenuOpen, setActionMenuOpen] = useState(false);
    const [commentsOpen, setCommentsOpen] = useState(true);
    const [eventsOpen, setEventsOpen] = useState(true);
    const [openCommentMenuId, setOpenCommentMenuId] = useState<number | null>(null);
    const [openTipMenuId, setOpenTipMenuId] = useState<number | null>(null);
    const [interestsPage, setInterestsPage] = useState(() => parsePageParam(searchParams.get(INTERESTS_PAGE_PARAM)));
    const [eventsSubtab, setEventsSubtab] = useState<"created" | "attending">(() => parseEventsTabParam(searchParams.get(EVENTS_TAB_PARAM)));
    const [createdEventsPage, setCreatedEventsPage] = useState(() => parsePageParam(searchParams.get(CREATED_EVENTS_PAGE_PARAM)));
    const [attendingEventsPage, setAttendingEventsPage] = useState(() => parsePageParam(searchParams.get(ATTENDING_EVENTS_PAGE_PARAM)));
    const [tipsPage, setTipsPage] = useState(() => parsePageParam(searchParams.get(TIPS_PAGE_PARAM)));
    const [commentsPage, setCommentsPage] = useState(() => parsePageParam(searchParams.get(COMMENTS_PAGE_PARAM)));
    const [replyMessage, setReplyMessage] = useState("");
    const [replyError, setReplyError] = useState<string | null>(null);
    const [replySubmitting, setReplySubmitting] = useState(false);
    const [replySuccess, setReplySuccess] = useState<string | null>(null);

    const isOwner = data?.user?.id === getUserId();
    const admin = isAdmin();
    const currentUsername = getUsername();

    const interestsPageSize = 8;
    const tipsPageSize = TIPS_PAGE_SIZE;
    const commentsPageSize = COMMENTS_PAGE_SIZE;
    const eventsPageSize = 6;
    const journeyOwnerId = data?.user?.id ?? null;

    const interestsQuery = useQuery<PageResult<ProfileInterest>>({
        queryKey: ["journeyInterests", journeyOwnerId, interestsPage],
        queryFn: ({ signal }) => {
            if (!journeyOwnerId) {
                return Promise.resolve(emptyPage<ProfileInterest>());
            }
            return getUserInterests(journeyOwnerId, { page: interestsPage, size: interestsPageSize }, signal);
        },
        enabled: Boolean(journeyOwnerId),
        placeholderData: keepPreviousData,
    });

    const tipsQuery = useQuery<PageResult<JourneyTip>>({
        queryKey: ["journeyTips", id, tipsPage],
        queryFn: ({ signal }) => {
            if (!id) {
                return Promise.resolve(emptyPage<JourneyTip>());
            }
            return listJourneyTips(Number(id), { page: tipsPage, size: tipsPageSize }, signal);
        },
        enabled: hasValidId,
        placeholderData: keepPreviousData,
    });

    const commentsQuery = useQuery<PageResult<JourneyComment>>({
        queryKey: ["journeyComments", id, commentsPage],
        queryFn: async ({ signal }) => {
            if (!id) {
                return Promise.resolve(emptyPage<JourneyComment>());
            }
            const page = await getJourneyResponses(Number(id), { page: commentsPage, size: commentsPageSize }, signal);
            return mapJourneyResponsesPage(page, signal, queryClient);
        },
        enabled: hasValidId,
        placeholderData: keepPreviousData,
    });

    const createdEventsQuery = useQuery<PageResult<JourneyEvent>>({
        queryKey: ["journeyEvents", "created", journeyOwnerId, data?.startDate, data?.endDate, createdEventsPage],
        queryFn: async ({ signal }) => {
            if (!journeyOwnerId || !data?.startDate || !data?.endDate) {
                return emptyPage<JourneyEvent>();
            }
            const page = await fetchEvents(
                {
                    creatorId: journeyOwnerId,
                    afterDate: data.startDate,
                    beforeDate: data.endDate,
                    page: createdEventsPage,
                    size: eventsPageSize,
                },
                signal
            );
            return mapEventPageToJourneyEvents(page, signal, queryClient);
        },
        enabled: Boolean(journeyOwnerId && data?.startDate && data?.endDate),
        placeholderData: keepPreviousData,
    });

    const attendingEventsQuery = useQuery<PageResult<JourneyEvent>>({
        queryKey: ["journeyEvents", "attending", journeyOwnerId, data?.startDate, data?.endDate, attendingEventsPage],
        queryFn: async ({ signal }) => {
            if (!journeyOwnerId || !data?.startDate || !data?.endDate) {
                return emptyPage<JourneyEvent>();
            }
            const page = await fetchEvents(
                {
                    attendedBy: journeyOwnerId,
                    afterDate: data.startDate,
                    beforeDate: data.endDate,
                    page: attendingEventsPage,
                    size: eventsPageSize,
                },
                signal
            );
            return mapEventPageToJourneyEvents(page, signal, queryClient);
        },
        enabled: Boolean(journeyOwnerId && data?.startDate && data?.endDate),
        placeholderData: keepPreviousData,
    });

    const activeEventsPage =
        eventsSubtab === "created"
            ? createdEventsQuery.data ?? emptyPage<JourneyEvent>()
            : attendingEventsQuery.data ?? emptyPage<JourneyEvent>();
    const eventsLoading = eventsSubtab === "created" ? createdEventsQuery.isLoading : attendingEventsQuery.isLoading;

    const interestsPageData = interestsQuery.data ?? emptyPage<ProfileInterest>();
    const tipsPageData = tipsQuery.data ?? emptyPage<JourneyTip>();
    const commentsPageData = commentsQuery.data ?? emptyPage<JourneyComment>();

    const updatePageParam = useCallback(
        (paramName: string, page: number) => {
            const nextParams = new URLSearchParams(searchParams);
            if (page > 1) {
                nextParams.set(paramName, String(page));
            } else {
                nextParams.delete(paramName);
            }
            setSearchParams(nextParams);
        },
        [searchParams, setSearchParams]
    );

    const updateEventsTabParam = useCallback(
        (tab: "created" | "attending") => {
            const nextParams = new URLSearchParams(searchParams);
            if (tab === "created") {
                nextParams.delete(EVENTS_TAB_PARAM);
            } else {
                nextParams.set(EVENTS_TAB_PARAM, tab);
            }
            setSearchParams(nextParams);
        },
        [searchParams, setSearchParams]
    );

    useEffect(() => {
        const nextPage = parsePageParam(searchParams.get(INTERESTS_PAGE_PARAM));
        setInterestsPage((currentPage) => (currentPage === nextPage ? currentPage : nextPage));
    }, [searchParams]);

    useEffect(() => {
        const nextTab = parseEventsTabParam(searchParams.get(EVENTS_TAB_PARAM));
        setEventsSubtab((currentTab) => (currentTab === nextTab ? currentTab : nextTab));
    }, [searchParams]);

    useEffect(() => {
        const nextPage = parsePageParam(searchParams.get(CREATED_EVENTS_PAGE_PARAM));
        setCreatedEventsPage((currentPage) => (currentPage === nextPage ? currentPage : nextPage));
    }, [searchParams]);

    useEffect(() => {
        const nextPage = parsePageParam(searchParams.get(ATTENDING_EVENTS_PAGE_PARAM));
        setAttendingEventsPage((currentPage) => (currentPage === nextPage ? currentPage : nextPage));
    }, [searchParams]);

    useEffect(() => {
        const nextPage = parsePageParam(searchParams.get(TIPS_PAGE_PARAM));
        setTipsPage((currentPage) => (currentPage === nextPage ? currentPage : nextPage));
    }, [searchParams]);

    useEffect(() => {
        const nextPage = parsePageParam(searchParams.get(COMMENTS_PAGE_PARAM));
        setCommentsPage((currentPage) => (currentPage === nextPage ? currentPage : nextPage));
    }, [searchParams]);

    useEffect(() => {
        if (tipsQuery.isLoading || tipsQuery.isFetching) {
            return;
        }
        if (tipsPageData.content.length === 0 && tipsPageData.totalElements > 0 && tipsPage > 1) {
            const fallbackPage = Math.max(1, Math.min(tipsPage - 1, tipsPageData.totalPages || tipsPage - 1));
            if (fallbackPage === tipsPage) {
                return;
            }
            setTipsPage(fallbackPage);
            updatePageParam(TIPS_PAGE_PARAM, fallbackPage);
        }
    }, [
        tipsQuery.isLoading,
        tipsQuery.isFetching,
        tipsPageData.content.length,
        tipsPageData.totalElements,
        tipsPageData.totalPages,
        tipsPage,
        updatePageParam,
    ]);

    useEffect(() => {
        if (commentsQuery.isLoading || commentsQuery.isFetching) {
            return;
        }
        if (commentsPageData.content.length === 0 && commentsPageData.totalElements > 0 && commentsPage > 1) {
            const fallbackPage = Math.max(1, Math.min(commentsPage - 1, commentsPageData.totalPages || commentsPage - 1));
            if (fallbackPage !== commentsPage) {
                setCommentsPage(fallbackPage);
                updatePageParam(COMMENTS_PAGE_PARAM, fallbackPage);
            }
        }
    }, [
        commentsQuery.isLoading,
        commentsQuery.isFetching,
        commentsPageData.content.length,
        commentsPageData.totalElements,
        commentsPageData.totalPages,
        commentsPage,
        updatePageParam,
    ]);

    useEffect(() => {
        if (!actionMenuOpen && openCommentMenuId === null && openTipMenuId === null) {
            return;
        }
        const handlePointerDown = (event: MouseEvent) => {
            const target = event.target as HTMLElement;
            if (target.closest("[data-journey-menu]")) {
                return;
            }
            setActionMenuOpen(false);
            setOpenCommentMenuId(null);
            setOpenTipMenuId(null);
        };
        const handleKeyDown = (event: KeyboardEvent) => {
            if (event.key === "Escape") {
                setActionMenuOpen(false);
                setOpenCommentMenuId(null);
                setOpenTipMenuId(null);
            }
        };
        document.addEventListener("mousedown", handlePointerDown);
        document.addEventListener("keydown", handleKeyDown);
        return () => {
            document.removeEventListener("mousedown", handlePointerDown);
            document.removeEventListener("keydown", handleKeyDown);
        };
    }, [actionMenuOpen, openCommentMenuId, openTipMenuId]);

    const parsePageFromLink = useCallback((page: number | string) => {
        if (typeof page === "string") {
            const url = new URL(page, window.location.origin);
            return Number(url.searchParams.get("page") ?? "1");
        }
        return page;
    }, []);

    const handleCreatedEventsPageChange = useCallback(
        (page: number | string) => {
            const parsedPage = parsePageFromLink(page);
            setCreatedEventsPage(parsedPage);
            updatePageParam(CREATED_EVENTS_PAGE_PARAM, parsedPage);
        },
        [parsePageFromLink, updatePageParam]
    );

    const handleAttendingEventsPageChange = useCallback(
        (page: number | string) => {
            const parsedPage = parsePageFromLink(page);
            setAttendingEventsPage(parsedPage);
            updatePageParam(ATTENDING_EVENTS_PAGE_PARAM, parsedPage);
        },
        [parsePageFromLink, updatePageParam]
    );

    const handleInterestsPageChange = useCallback(
        (page: number | string) => {
            const parsedPage = parsePageFromLink(page);
            setInterestsPage(parsedPage);
            updatePageParam(INTERESTS_PAGE_PARAM, parsedPage);
        },
        [parsePageFromLink, updatePageParam]
    );

    const handleEventsSubtabChange = useCallback(
        (tab: "created" | "attending") => {
            setEventsSubtab(tab);
            updateEventsTabParam(tab);
        },
        [updateEventsTabParam]
    );

    const handleTipsPageChange = useCallback(
        (page: number | string) => {
            const parsedPage = parsePageFromLink(page);
            setTipsPage(parsedPage);
            updatePageParam(TIPS_PAGE_PARAM, parsedPage);
        },
        [parsePageFromLink, updatePageParam]
    );

    const handleCommentsPageChange = useCallback(
        (page: number | string) => {
            const parsedPage = parsePageFromLink(page);
            setCommentsPage(parsedPage);
            updatePageParam(COMMENTS_PAGE_PARAM, parsedPage);
        },
        [parsePageFromLink, updatePageParam]
    );

    if (!hasValidId) {
        return <NotFoundPage />;
    }

    if (isLoading) {
        return <PageStatus className="journey-detail-page" message={t("admin.dashboard.loading", { defaultValue: "Cargando..." })} />;
    }

    if (isNotFound) {
        return (
            <div className="journey-detail-page">
                <div className="layout-container">
                    <div className="main-content">
                        <div className="content-container">
                            <div className="empty-state">
                                <p className="empty-message">{t("journey.not.found.title")}</p>
                                <p className="empty-message">{t("journey.not.found.message")}</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        );
    }

    if (isError) {
        return <PageStatus className="journey-detail-page" variant="error" message={t("admin.dashboard.error", { defaultValue: "Error cargando datos." })} />;
    }
    if (!data) {
        return <PageStatus className="journey-detail-page" variant="error" message={t("admin.dashboard.error", { defaultValue: "Error cargando datos." })} />;
    }

    const handleBack = () => {
        const previous = popFromNavigationStack();
        if (previous && previous !== `${location.pathname}${location.search}`) {
            navigate(previous);
            return;
        }
        if (window.history.length > 1) {
            navigate(-1);
            return;
        }
        navigate("/journeys");
    };

    const currentJourneyLocation = `${location.pathname}${location.search}`;

    const handleCreateTip = () => {
        if (!id) {
            return;
        }
        pushToNavigationStack(currentJourneyLocation);
        const nextParams = new URLSearchParams();
        if (tipsPage > 1) {
            nextParams.set(TIPS_PAGE_PARAM, String(tipsPage));
        }
        navigate(`/journeys/${id}/tips/create${nextParams.toString() ? `?${nextParams.toString()}` : ""}`);
    };

    const handleReportComment = (responseId: number) => {
        pushToNavigationStack(currentJourneyLocation);
        setOpenCommentMenuId(null);
        navigate(`/reports/journey-responses/${responseId}/create`);
    };

    const handleDeleteComment = (responseId: number) => {
        if (!id) {
            return;
        }
        pushToNavigationStack(currentJourneyLocation);
        setOpenCommentMenuId(null);
        const nextParams = new URLSearchParams({ journeyId: id });
        if (commentsPage > 1) {
            nextParams.set(COMMENTS_PAGE_PARAM, String(commentsPage));
        }
        navigate(`/journeys/reply/${responseId}/delete?${nextParams.toString()}`);
    };

    const handleReplySubmit = (event: FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        gate.runOrPrompt(async () => {
            if (!id) {
                setReplyError(t("admin.dashboard.error", { defaultValue: "Error cargando datos." }));
                return;
            }
            if (!replyMessage.trim()) {
                setReplyError(t("NotNull.replyJourneyForm.message", { defaultValue: "Please enter a message." }));
                return;
            }
            try {
                setReplySubmitting(true);
                setReplyError(null);
                setReplySuccess(null);
                await createJourneyResponse(Number(id), { message: replyMessage.trim() });
                setReplyMessage("");
                setReplySuccess(t("replyJourney.success"));
                const refreshedCommentsPage = await getJourneyResponses(
                    Number(id),
                    { page: 1, size: commentsPageSize }
                );
                const targetCommentsPage = Math.max(1, refreshedCommentsPage.totalPages || 1);
                setCommentsPage(targetCommentsPage);
                updatePageParam(COMMENTS_PAGE_PARAM, targetCommentsPage);
                await Promise.all([
                    queryClient.invalidateQueries({
                        predicate: (query) =>
                            query.queryKey[0] === "journeyComments" &&
                            String(query.queryKey[1]) === String(id),
                    }),
                    queryClient.invalidateQueries({ queryKey: ["journeyDetail", id] }),
                ]);
                refetch();
            } catch (err) {
                console.error("Failed to submit journey response", err);
                setReplyError(apiErrorMessage(err, t("admin.dashboard.error", { defaultValue: "Error cargando datos." })));
            } finally {
                setReplySubmitting(false);
            }
        });
    };

    return (
        <>
        <div className="journey-detail-page">
            <div className="layout-container">
                <div className="main-content">
                    <div className="content-container">
                        <div className="back-navigation">
                            <button
                                type="button"
                                className="back-link"
                                onClick={handleBack}
                            >
                                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="icon">
                                    <path d="M19 12H5"></path>
                                    <path d="M12 19l-7-7 7-7"></path>
                                </svg>
                                <span>{isOwner ? t("journey.detail.back.to.profile") : t("journey.detail.back.to.list")}</span>
                            </button>
                        </div>

                        <div className="content-card journey-detail-card">
                            {isFetching && (
                                <p className="section__helper">
                                    {t("admin.dashboard.loading", { defaultValue: "Actualizando..." })}
                                </p>
                            )}
                            <div className="journey-actions">
                                <div style={{ position: "relative", display: "inline-block" }} data-journey-menu>
                                    <button
                                        type="button"
                                        className="btn-action btn-menu"
                                        id="actionMenuButton"
                                        onClick={() => setActionMenuOpen((prev) => !prev)}
                                        style={{
                                            background: "none",
                                            border: "1px solid #e0e0e0",
                                            borderRadius: "6px",
                                            padding: "8px",
                                            cursor: "pointer",
                                        }}
                                        aria-haspopup="menu"
                                        aria-expanded={actionMenuOpen}
                                    >
                                        <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="btn-icon">
                                            <circle cx="12" cy="12" r="1"></circle>
                                            <circle cx="12" cy="5" r="1"></circle>
                                            <circle cx="12" cy="19" r="1"></circle>
                                        </svg>
                                    </button>

                                    {actionMenuOpen && (
                                        <div
                                            id="actionDropdown"
                                            role="menu"
                                            style={{
                                                position: "absolute",
                                                right: 0,
                                                top: "100%",
                                                backgroundColor: "white",
                                                minWidth: "180px",
                                                boxShadow: "0px 8px 16px 0px rgba(0,0,0,0.2)",
                                                borderRadius: "8px",
                                                zIndex: 1000,
                                                border: "1px solid #e0e0e0",
                                                padding: "8px 0",
                                            }}
                                        >
                                            {isOwner && (
                                                <Link
                                                    to={`/journeys/${id}/update`}
                                                    style={{
                                                        color: "#333",
                                                        padding: "12px 16px",
                                                        textDecoration: "none",
                                                        display: "flex",
                                                        alignItems: "center",
                                                        gap: "12px",
                                                    }}
                                                >
                                                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                                        <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                                                        <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
                                                    </svg>
                                                    <span>{t("journey.edit")}</span>
                                                </Link>
                                            )}
                                            {(isOwner || admin) && (
                                                <Link
                                                    to={`/journeys/${id}/delete`}
                                                    className="action-menu__item is-danger"
                                                    style={{
                                                        gap: "12px",
                                                    }}
                                                >
                                                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                                        <path d="M3 6h18"></path>
                                                        <path d="M19 6v14a2 2 0 01-2 2H7a2 2 0 01-2-2V6m3 0V4a2 2 0 012-2h4a2 2 0 012 2v2"></path>
                                                        <line x1="10" y1="11" x2="10" y2="17"></line>
                                                        <line x1="14" y1="11" x2="14" y2="17"></line>
                                                    </svg>
                                                    <span>{t("journey.delete")}</span>
                                                </Link>
                                            )}
                                            {!isOwner && (
                                                <Link
                                                    to={`/reports/journeys/${id}/create`}
                                                    className="action-menu__item is-danger"
                                                    onClick={() => pushToNavigationStack(`${location.pathname}${location.search}`)}
                                                    style={{
                                                        gap: "12px",
                                                    }}
                                                >
                                                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                                        <path d="M12 9v4"></path>
                                                        <path d="M12 17h.01"></path>
                                                        <circle cx="12" cy="12" r="10"></circle>
                                                    </svg>
                                                    <span>{t("journey.report")}</span>
                                                </Link>
                                            )}
                                        </div>
                                    )}
                                </div>
                            </div>

                            <div className="journey-detail-header">
                                <div className="journey-user-info">
                                    <div className="user-details">
                                        <h1 className="journey-title">
                                            {t("journey.detail.section.title", {
                                                values: {
                                                    0: creatorReady ? (data.user?.firstname ?? "—") : "…",
                                                    1: creatorReady ? (data.user?.lastname ?? "") : "",
                                                },
                                            })}
                                        </h1>
                                        <div className="journey-meta">
                                            <div className="journey-destination">
                                                <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="icon">
                                                    <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0118 0z"></path>
                                                    <circle cx="12" cy="10" r="3"></circle>
                                                </svg>
                                                <span className="destination-text">
                                                    {destinationReady
                                                        ? `${data.destinationUniversity?.city ?? "—"} - ${data.destinationUniversity?.name ?? "—"}`
                                                        : destinationLoading
                                                          ? "…"
                                                          : "—"}
                                                </span>
                                            </div>
                                            <div className="journey-dates">
                                                <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="icon">
                                                    <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                                                    <line x1="16" y1="2" x2="16" y2="6"></line>
                                                    <line x1="8" y1="2" x2="8" y2="6"></line>
                                                    <line x1="3" y1="10" x2="21" y2="10"></line>
                                                </svg>
                                                <span className="date-range">
                                                    {formatDate(data.startDate, locale)} → {formatDate(data.endDate, locale)}
                                                </span>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                {creatorReady && data.user ? (
                                    <CreatorCard creator={data.user} isJourneyCreator showName={false} />
                                ) : creatorError ? (
                                    <p className="section__helper">{t("journey.host.error", { defaultValue: "We couldn't load the host." })}</p>
                                ) : creatorLoading ? (
                                    <PageStatus compact message={t("admin.dashboard.loading", { defaultValue: "Cargando..." })} />
                                ) : null}

                                <div className="section-content">
                                    <div className="journey-description-card">
                                        <p className="journey-description-text">{data.description}</p>
                                    </div>
                                </div>
                            </div>

                            <section className="content-section">
                                <div className="section-header">
                                    <h2 className="section-title">
                                        <svg xmlns="http://www.w3.org/2000/svg" width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="icon">
                                            <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"></path>
                                        </svg>
                                        {t("journey.detail.interests")}
                                    </h2>
                                </div>
                                <div className="section-content">
                                    {interestsQuery.isLoading ? (
                                        <PageStatus compact message={t("admin.dashboard.loading", { defaultValue: "Cargando..." })} />
                                    ) : interestsPageData.content.length === 0 ? (
                                        <div className="empty-state">
                                            <div className="empty-icon">
                                                <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1" strokeLinecap="round" strokeLinejoin="round" className="empty-icon-img">
                                                    <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"></path>
                                                </svg>
                                            </div>
                                            <p className="empty-message">{t("journey.detail.no.interests")}</p>
                                        </div>
                                    ) : (
                                        <>
                                            <div className="interests-container">
                                                {interestsPageData.content.map((interest) => (
                                                    <div key={interest.id} className="interest-tag">
                                                        {interest.name}
                                                    </div>
                                                ))}
                                            </div>
                                            <Pagination
                                                totalPages={interestsPageData.totalPages}
                                                currentPage={interestsPageData.currentPage}
                                                pageSize={interestsPageData.pageSize}
                                                nextPage={interestsPageData.next}
                                                prevPage={interestsPageData.prev}
                                                firstPage={interestsPageData.first}
                                                lastPage={interestsPageData.last}
                                                onPageChange={handleInterestsPageChange}
                                                previousLabel={t("pagination.prev")}
                                                nextLabel={t("pagination.next")}
                                            />
                                        </>
                                    )}
                                </div>
                            </section>

                            <section className="content-section">
                                <div className="section-header">
                                    <h2 className="section-title">
                                        <svg xmlns="http://www.w3.org/2000/svg" width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="icon">
                                            <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                                            <line x1="16" y1="2" x2="16" y2="6"></line>
                                            <line x1="8" y1="2" x2="8" y2="6"></line>
                                            <line x1="3" y1="10" x2="21" y2="10"></line>
                                        </svg>
                                        {t("journey.detail.events")}
                                        <span className="count">({activeEventsPage.totalPages})</span>
                                    </h2>
                                    <button
                                        type="button"
                                        className="toggle-comments-btn"
                                        aria-label="Toggle events"
                                        onClick={() => setEventsOpen((prev) => !prev)}
                                    >
                                        <span id="events-collapse-icon" style={{ display: eventsOpen ? "inline" : "none" }}>
                                            <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="icon">
                                                <polyline points="18 15 12 9 6 15"></polyline>
                                            </svg>
                                        </span>
                                        <span id="events-expand-icon" style={{ display: eventsOpen ? "none" : "inline" }}>
                                            <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="icon">
                                                <polyline points="6 9 12 15 18 9"></polyline>
                                            </svg>
                                        </span>
                                    </button>
                                </div>

                                {eventsOpen && (
                                    <div id="events-list" className="section-content events-list">
                                                <div className="events-filter-subtabs">
                                            <button
                                                type="button"
                                                className={`events-subtab ${eventsSubtab === "created" ? "active" : ""}`}
                                                onClick={() => handleEventsSubtabChange("created")}
                                            >
                                                {t("journey.events.created", { defaultValue: "Created" })}
                                            </button>
                                            <button
                                                type="button"
                                                className={`events-subtab ${eventsSubtab === "attending" ? "active" : ""}`}
                                                onClick={() => handleEventsSubtabChange("attending")}
                                            >
                                                {t("journey.events.attending", { defaultValue: "Attending" })}
                                            </button>
                                        </div>
                                        {eventsLoading ? (
                                            <PageStatus compact message={t("admin.dashboard.loading", { defaultValue: "Cargando..." })} />
                                        ) : activeEventsPage.content.length === 0 ? (
                                            <div className="empty-state">
                                                <div className="empty-icon">
                                                    <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1" strokeLinecap="round" strokeLinejoin="round" className="empty-icon-img">
                                                        <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                                                        <line x1="16" y1="2" x2="16" y2="6"></line>
                                                        <line x1="8" y1="2" x2="8" y2="6"></line>
                                                        <line x1="3" y1="10" x2="21" y2="10"></line>
                                                    </svg>
                                                </div>
                                                <p className="empty-message">
                                                    {eventsSubtab === "created"
                                                        ? t("journey.events.no.created", { defaultValue: "No events created during this journey" })
                                                        : t("journey.events.no.attending", { defaultValue: "No events attended during this journey" })}
                                                </p>
                                            </div>
                                        ) : (
                                            <>
                                                <div className="journey-events-container">
                                                    {activeEventsPage.content.map((event) => (
                                                        <Link key={event.id} to={`/events/${event.id}`} className="journey-event-card-link">
                                                            <div className="journey-event-card">
                                                                <div className="journey-event-left">
                                                                    {event.flyerImageUrl ? (
                                                                        <img src={event.flyerImageUrl} alt="Event flyer" className="journey-event-image" />
                                                                    ) : (
                                                                        <div className="journey-event-image-placeholder">
                                                                            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round">
                                                                                <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                                                                                <line x1="16" y1="2" x2="16" y2="6"></line>
                                                                                <line x1="8" y1="2" x2="8" y2="6"></line>
                                                                                <line x1="3" y1="10" x2="21" y2="10"></line>
                                                                            </svg>
                                                                        </div>
                                                                    )}
                                                                </div>
                                                                <div className="journey-event-content">
                                                                    <h3 className="journey-event-title">{event.title}</h3>
                                                                    <div className="journey-event-meta">
                                                                        <div className="journey-event-meta-item">
                                                                            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                                                                <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0118 0z"></path>
                                                                                <circle cx="12" cy="10" r="3"></circle>
                                                                            </svg>
                                                                            <span>{event.city}</span>
                                                                        </div>
                                                                        <div className="journey-event-meta-item">
                                                                            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                                                                <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                                                                                <line x1="16" y1="2" x2="16" y2="6"></line>
                                                                                <line x1="8" y1="2" x2="8" y2="6"></line>
                                                                                <line x1="3" y1="10" x2="21" y2="10"></line>
                                                                            </svg>
                                                                            <span>{formatDate(event.date, locale)}</span>
                                                                        </div>
                                                                        {event.time && (
                                                                            <div className="journey-event-meta-item">
                                                                                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                                                                    <circle cx="12" cy="12" r="10"></circle>
                                                                                    <polyline points="12 6 12 12 16 14"></polyline>
                                                                                </svg>
                                                                                <span>{event.time}</span>
                                                                            </div>
                                                                        )}
                                                                    </div>
                                                                    <p className="journey-event-description">{event.description}</p>
                                                                </div>
                                                                <div className="journey-event-arrow">
                                                                    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                                                        <polyline points="9 18 15 12 9 6"></polyline>
                                                                    </svg>
                                                                </div>
                                                            </div>
                                                        </Link>
                                                    ))}
                                                </div>
                                                <Pagination
                                                    totalPages={activeEventsPage.totalPages}
                                                    currentPage={activeEventsPage.currentPage}
                                                    pageSize={activeEventsPage.pageSize}
                                                    nextPage={activeEventsPage.next}
                                                    prevPage={activeEventsPage.prev}
                                                    firstPage={activeEventsPage.first}
                                                    lastPage={activeEventsPage.last}
                                                    onPageChange={eventsSubtab === "created" ? handleCreatedEventsPageChange : handleAttendingEventsPageChange}
                                                    previousLabel={t("pagination.prev")}
                                                    nextLabel={t("pagination.next")}
                                                />
                                            </>
                                        )}
                                    </div>
                                )}
                            </section>

                            <section className="content-section">
                                <div className="section-header">
                                    <h2 className="section-title">
                                        <svg xmlns="http://www.w3.org/2000/svg" width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="icon">
                                            <path d="M9 11H5a2 2 0 0 0-2 2v3c0 1.1.9 2 2 2h4l3 3V8l-3 3z"></path>
                                            <path d="M22 4H12a2 2 0 0 0-2 2v4a2 2 0 0 0 2 2h9l1 1V6a2 2 0 0 0-2-2z"></path>
                                        </svg>
                                        {t("journey.detail.tips")}
                                        <span className="count">({tipsPageData.totalElements})</span>
                                    </h2>
                                </div>
                                <div className="section-content">
                                    {tipsQuery.isLoading ? (
                                        <PageStatus compact message={t("admin.dashboard.loading", { defaultValue: "Cargando..." })} />
                                    ) : tipsPageData.content.length === 0 ? (
                                        <div className="empty-state">
                                            <div className="empty-icon">
                                                <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1" strokeLinecap="round" strokeLinejoin="round" className="empty-icon-img">
                                                    <path d="M9 11H5a2 2 0 0 0-2 2v3c0 1.1.9 2 2 2h4l3 3V8l-3 3z"></path>
                                                    <path d="M22 4H12a2 2 0 0 0-2 2v4a2 2 0 0 0 2 2h9l1 1V6a2 2 0 0 0-2-2z"></path>
                                                </svg>
                                            </div>
                                            <p className="empty-message">{t("journey.detail.no.tips")}</p>
                                            {isOwner && <p className="empty-message">{t("journey.detail.add.first.tip")}</p>}
                                        </div>
                                    ) : (
                                        <>
                                            <div className="tips-container">
                                                {tipsPageData.content.map((tip) => (
                                                    <div key={tip.id} className="tip-card">
                                                        <div className="tip-header">
                                                            <div className="tip-meta">
                                                                <h3 className="tip-title">{tip.title}</h3>
                                                                <p className="tip-date">{formatDate(tip.dateTime, locale)}</p>
                                                            </div>
                                                            {isOwner && (
                                                                <div style={{ position: "relative", display: "inline-block" }} data-journey-menu>
                                                                    <button
                                                                        type="button"
                                                                        className="btn-action"
                                                                        aria-label={t("tip.edit")}
                                                                        onClick={() => setOpenTipMenuId((prev) => (prev === tip.id ? null : tip.id))}
                                                                        style={{
                                                                            background: "none",
                                                                            border: "1px solid #e0e0e0",
                                                                            borderRadius: "4px",
                                                                            padding: "6px",
                                                                            cursor: "pointer",
                                                                        }}
                                                                    >
                                                                        <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                                                            <circle cx="12" cy="12" r="1"></circle>
                                                                            <circle cx="12" cy="5" r="1"></circle>
                                                                            <circle cx="12" cy="19" r="1"></circle>
                                                                        </svg>
                                                                    </button>
                                                                    {openTipMenuId === tip.id && (
                                                                        <div
                                                                            style={{
                                                                                position: "absolute",
                                                                                right: 0,
                                                                                top: "100%",
                                                                                backgroundColor: "white",
                                                                                minWidth: "160px",
                                                                                boxShadow: "0px 8px 16px 0px rgba(0,0,0,0.2)",
                                                                                borderRadius: "6px",
                                                                                zIndex: 1000,
                                                                                border: "1px solid #e0e0e0",
                                                                                padding: "6px 0",
                                                                            }}
                                                                        >
                                                                            <Link
                                                                                to={`/journeys/tips/${tip.id}/update?journeyId=${id}&tipsPage=${tipsPage}`}
                                                                                style={{
                                                                                    color: "#333",
                                                                                    padding: "10px 14px",
                                                                                    textDecoration: "none",
                                                                                    display: "flex",
                                                                                    alignItems: "center",
                                                                                    gap: "10px",
                                                                                    fontSize: "13px",
                                                                                }}
                                                                            >
                                                                                <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                                                                    <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                                                                                    <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
                                                                                </svg>
                                                                                <span>{t("tip.edit")}</span>
                                                                            </Link>
                                                                            <Link
                                                                                to={`/journeys/tips/${tip.id}/delete?journeyId=${id}&tipsPage=${tipsPage}`}
                                                                                className="action-menu__item is-danger"
                                                                                style={{
                                                                                    gap: "10px",
                                                                                }}
                                                                            >
                                                                                <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                                                                    <path d="M3 6h18"></path>
                                                                                    <path d="M19 6v14a2 2 0 01-2 2H7a2 2 0 01-2-2V6m3 0V4a2 2 0 012-2h4a2 2 0 012 2v2"></path>
                                                                                </svg>
                                                                                <span>{t("tip.delete")}</span>
                                                                            </Link>
                                                                        </div>
                                                                    )}
                                                                </div>
                                                            )}
                                                        </div>
                                                        <div className="tip-content">
                                                            <p className="tip-message">{tip.content}</p>
                                                        </div>
                                                    </div>
                                                ))}
                                            </div>
                                            <Pagination
                                                totalPages={tipsPageData.totalPages}
                                                currentPage={tipsPageData.currentPage}
                                                pageSize={tipsPageData.pageSize}
                                                nextPage={tipsPageData.next}
                                                prevPage={tipsPageData.prev}
                                                firstPage={tipsPageData.first}
                                                lastPage={tipsPageData.last}
                                                onPageChange={handleTipsPageChange}
                                                previousLabel={t("pagination.prev")}
                                                nextLabel={t("pagination.next")}
                                            />
                                        </>
                                    )}

                                    {isOwner && (
                                        <div className="add-tip-button-container">
                                            <button type="button" className="btn btn-primary" onClick={handleCreateTip}>
                                                <svg xmlns="http://www.w3.org/2000/svg" width="18" className="btn-icon" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                                    <path d="M12 5v14"></path>
                                                    <path d="M5 12h14"></path>
                                                </svg>
                                                <span>{t("journey.tip.add")}</span>
                                            </button>
                                        </div>
                                    )}
                                </div>
                            </section>

                            <section className="content-section">
                                <div className="section-header">
                                    <h2 className="section-title">
                                        <svg xmlns="http://www.w3.org/2000/svg" width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="icon">
                                            <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path>
                                        </svg>
                                        {t("journey.detail.responses")}
                                        <span className="count">({commentsPageData.totalElements})</span>
                                    </h2>
                                    <button
                                        type="button"
                                        className="toggle-comments-btn"
                                        aria-label="Toggle comments"
                                        onClick={() => setCommentsOpen((prev) => !prev)}
                                    >
                                        <span id="collapse-icon" style={{ display: commentsOpen ? "inline" : "none" }}>
                                            <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="icon">
                                                <polyline points="18 15 12 9 6 15"></polyline>
                                            </svg>
                                        </span>
                                        <span id="expand-icon" style={{ display: commentsOpen ? "none" : "inline" }}>
                                            <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="icon">
                                                <polyline points="6 9 12 15 18 9"></polyline>
                                            </svg>
                                        </span>
                                    </button>
                                </div>

                                {commentsOpen && (
                                    <div id="comments-list" className="section-content responses-list">
                                        {commentsQuery.isLoading ? (
                                            <PageStatus compact message={t("admin.dashboard.loading", { defaultValue: "Cargando..." })} />
                                        ) : commentsPageData.content.length === 0 ? (
                                            <div className="empty-state">
                                                <div className="empty-icon">
                                                    <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1" strokeLinecap="round" strokeLinejoin="round" className="empty-icon-img">
                                                        <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path>
                                                    </svg>
                                                </div>
                                                <p className="empty-message">{t("journey.detail.no.responses")}</p>
                                            </div>
                                        ) : (
                                            commentsPageData.content.map((response) => (
                                                <div key={response.id} className="chat-message">
                                                    <div className="response-header">
                                                        <div className="response-user">
                                                            <div className="response-avatar">
                                                                <div className="avatar-placeholder">
                                                                    <AvatarFallbackIcon size={18} />
                                                                </div>
                                                            </div>
                                                            <div className="response-user-info">
                                                                <h3 className="response-username">{response.user.username}</h3>
                                                                <p className="response-date">{formatDateTime(response.dateTime, locale)}</p>
                                                            </div>
                                                        </div>
                                                        <div style={{ position: "relative", display: "inline-block" }} data-journey-menu>
                                                            <button
                                                                type="button"
                                                                className="btn-action"
                                                                aria-label="Comment actions"
                                                                onClick={() =>
                                                                    setOpenCommentMenuId((prev) => (prev === response.id ? null : response.id))
                                                                }
                                                                style={{
                                                                    background: "none",
                                                                    border: "1px solid #e0e0e0",
                                                                    borderRadius: "4px",
                                                                    padding: "6px",
                                                                    cursor: "pointer",
                                                                }}
                                                            >
                                                                <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                                                    <circle cx="12" cy="12" r="1"></circle>
                                                                    <circle cx="12" cy="5" r="1"></circle>
                                                                    <circle cx="12" cy="19" r="1"></circle>
                                                                </svg>
                                                            </button>
                                                            {openCommentMenuId === response.id && (
                                                                <div
                                                                    style={{
                                                                        position: "absolute",
                                                                        right: 0,
                                                                        top: "100%",
                                                                        backgroundColor: "white",
                                                                        minWidth: "160px",
                                                                        boxShadow: "0px 8px 16px 0px rgba(0,0,0,0.2)",
                                                                        borderRadius: "6px",
                                                                        zIndex: 1000,
                                                                        border: "1px solid #e0e0e0",
                                                                        padding: "6px 0",
                                                                    }}
                                                                >
                                                                    {
                                                                        <button
                                                                            type="button"
                                                                            onClick={() => handleReportComment(response.id)}
                                                                            className="action-menu__item is-danger"
                                                                            style={{
                                                                                gap: "10px",
                                                                            }}
                                                                        >
                                                                            <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                                                                <path d="M12 9v4"></path>
                                                                                <path d="M12 17h.01"></path>
                                                                                <circle cx="12" cy="12" r="10"></circle>
                                                                            </svg>
                                                                            <span>{t("comment.report")}</span>
                                                                        </button>
                                                                    }
                                                                    {(admin || response.user.username === currentUsername) && (
                                                                        <button
                                                                            type="button"
                                                                            onClick={() => handleDeleteComment(response.id)}
                                                                            className="action-menu__item is-danger"
                                                                            style={{
                                                                                gap: "10px",
                                                                            }}
                                                                        >
                                                                            <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                                                                <path d="M3 6h18"></path>
                                                                                <path d="M19 6v14a2 2 0 01-2 2H7a2 2 0 01-2-2V6m3 0V4a2 2 0 012-2h4a2 2 0 012 2v2"></path>
                                                                            </svg>
                                                                            <span>{t("comment.delete")}</span>
                                                                        </button>
                                                                    )}
                                                                </div>
                                                            )}
                                                        </div>
                                                    </div>
                                                    <div className="response-body">
                                                        <p className="response-message">{response.message}</p>
                                                    </div>
                                                </div>
                                            ))
                                        )}
                                        <Pagination
                                            totalPages={commentsPageData.totalPages}
                                            currentPage={commentsPageData.currentPage}
                                            pageSize={commentsPageData.pageSize}
                                            nextPage={commentsPageData.next}
                                            prevPage={commentsPageData.prev}
                                            firstPage={commentsPageData.first}
                                            lastPage={commentsPageData.last}
                                            onPageChange={handleCommentsPageChange}
                                            previousLabel={t("pagination.prev")}
                                            nextLabel={t("pagination.next")}
                                        />
                                    </div>
                                )}

                                <div className="section-content">
                                    <div className="reply-form-container">
                                        <h3 className="reply-title">
                                            <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="icon">
                                                <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                                                <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
                                            </svg>
                                            {t("reply.message")}
                                        </h3>
                                        <form
                                            className="reply-form"
                                            onSubmit={handleReplySubmit}
                                        >
                                            <div className="form-group">
                                                <label className="form-label" htmlFor="journey-reply">
                                                    {t("reply.message")}
                                                </label>
                                                <textarea
                                                    id="journey-reply"
                                                    className="form-textarea"
                                                    placeholder={t("reply.message.hint")}
                                                    value={replyMessage}
                                                    onChange={(event) => {
                                                        setReplyMessage(event.target.value);
                                                        if (replyError) {
                                                            setReplyError(null);
                                                        }
                                                        if (replySuccess) {
                                                            setReplySuccess(null);
                                                        }
                                                    }}
                                                />
                                            </div>
                                            {replySuccess && <p className="form-field__text">{replySuccess}</p>}
                                            {replyError && <p className="error-message">{replyError}</p>}
                                            <div className="form-actions">
                                                <button type="submit" className="btn btn-primary" disabled={replySubmitting}>
                                                    {t("reply.submit")}
                                                </button>
                                            </div>
                                        </form>
                                    </div>
                                </div>
                            </section>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <LoginRequiredModal
            open={gate.open}
            onClose={gate.close}
            nextPath={`${location.pathname}${location.search}`}
        />
        </>
    );
}
