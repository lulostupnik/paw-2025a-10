import { apiErrorMessage } from "@/lib/api/client";
import { useCallback, useEffect, useMemo, useState, type FormEvent } from "react";
import { Link, useLocation, useNavigate, useParams, useSearchParams } from "react-router-dom";
import { keepPreviousData, useQuery, useQueryClient } from "@tanstack/react-query";
import { useI18n } from "@/lib/i18n";
import { getUserId, getUsername, isAdmin } from "@/lib/auth/auth";
import CreatorCard from "@/components/detail/CreatorCard";
import Pagination from "@/components/listing/Pagination";
import LoginRequiredModal from "@/components/LoginRequiredModal";
import NotFoundPage from "@/pages/errors/NotFoundPage";
import PageStatus from "@/components/ui/PageStatus";
import { useEventDetailData } from "@/hooks/useEventDetailData";
import { popFromNavigationStack, pushToNavigationStack } from "@/lib/utils/navigationStack";
import { attendEvent, createEventRating, createEventResponse, deleteEventRating, getEventAttendance, getEventStatistics, listEventAttendees, listEventResponses, unattendEvent, updateEventRating } from "@/lib/api/events";
import { useAuthGate } from "@/hooks/useAuthGate";
import { emptyPage, mapPageList, type PageResult } from "@/types/pagination";
import { getUserByUrl } from "@/lib/api/journeys";
import type { EventAttendee, EventComment } from "@/types/event";
import { parseApiDate } from "@/lib/utils/date";
import AvatarFallbackIcon from "@/components/ui/AvatarFallbackIcon";
import type { QueryClient } from "@tanstack/react-query";

const TAB_PARAM = "tab";
const ATTENDEES_PAGE_PARAM = "attendeesPage";
const COMMENTS_PAGE_PARAM = "commentsPage";

const parsePageParam = (value: string | null) => {
    const raw = Number(value ?? "1");
    return Number.isFinite(raw) && raw > 0 ? raw : 1;
};

const parseTabParam = (value: string | null): "details" | "chat" | "rating" => {
    if (value === "chat" || value === "rating") {
        return value;
    }
    return "details";
};

const parsePageFromLink = (page: number | string) => {
    if (typeof page === "string") {
        const url = new URL(
            page,
            typeof window !== "undefined" ? window.location.origin : "http://localhost"
        );
        return Number(url.searchParams.get("page") ?? "1");
    }
    return page;
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

const renderStars = (rating: number) => {
    const stars = Array.from({ length: 5 }, (_, index) => {
        const starValue = index + 1;
        if (rating >= starValue) {
            return (
                <svg key={starValue} className="star filled" xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
                    <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon>
                </svg>
            );
        }
        if (rating >= starValue - 0.5) {
            return (
                <svg key={starValue} className="star half-filled" xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <defs>
                        <linearGradient id={`half-fill-${starValue}`}>
                            <stop offset="50%" stopColor="currentColor" stopOpacity="1" />
                            <stop offset="50%" stopColor="transparent" stopOpacity="0" />
                        </linearGradient>
                    </defs>
                    <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2" fill={`url(#half-fill-${starValue})`} />
                </svg>
            );
        }
        return (
            <svg key={starValue} className="star empty" xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon>
            </svg>
        );
    });

    return <div className="rating-stars-display">{stars}</div>;
};

interface AttendanceOverride {
    eventId: number;
    attending: boolean;
    attendeesCount: number;
}

interface EventScopedMessage {
    eventId: number;
    message: string;
}

interface RatingDraft {
    key: string;
    value: number;
}

const scopedMessage = (state: EventScopedMessage | null, eventId?: number) =>
    state && state.eventId === eventId ? state.message : null;

interface EventResponseApi {
    id: number;
    message: string;
    dateTime: string;
    links?: {
        authorUrl?: string | null;
    } | null;
}

const mapEventResponsesPage = async (page: PageResult<EventResponseApi>, signal?: AbortSignal, queryClient?: QueryClient): Promise<PageResult<EventComment>> => {
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

export default function EventDetailPage() {
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
        isFetching,
        creatorLoading,
        creatorError,
        creatorReady,
        ratingsLoading,
        ratingsError,
    } = useEventDetailData({ eventId: hasValidId ? id : undefined });
    const [actionMenuOpen, setActionMenuOpen] = useState(false);
    const [openCommentMenuId, setOpenCommentMenuId] = useState<number | null>(null);
    const requestedTab = parseTabParam(searchParams.get(TAB_PARAM));
    const activeTab = data?.isFuture && requestedTab === "rating" ? "details" : requestedTab;
    const attendeesPage = parsePageParam(searchParams.get(ATTENDEES_PAGE_PARAM));
    const commentsPage = parsePageParam(searchParams.get(COMMENTS_PAGE_PARAM));
    const [attendanceOverride, setAttendanceOverride] = useState<AttendanceOverride | null>(null);
    const [attendSubmitting, setAttendSubmitting] = useState(false);
    const [attendErrorState, setAttendErrorState] = useState<EventScopedMessage | null>(null);
    const [ratingDraft, setRatingDraft] = useState<RatingDraft | null>(null);
    const [hoverRating, setHoverRating] = useState<number | null>(null);
    const [ratingSubmitting, setRatingSubmitting] = useState(false);
    const [ratingErrorState, setRatingErrorState] = useState<EventScopedMessage | null>(null);
    const [replyMessage, setReplyMessage] = useState("");
    const [replyError, setReplyError] = useState<string | null>(null);
    const [replySubmitting, setReplySubmitting] = useState(false);
    const [replySuccess, setReplySuccess] = useState<string | null>(null);

    const userId = getUserId();
    const username = getUsername();
    const isOwner = data?.user?.id === userId;
    const admin = isAdmin();
    const canViewAttendees = isOwner;
    const attendanceQuery = useQuery({
        queryKey: ["eventAttendance", id, userId],
        queryFn: async ({ signal }) => {
            if (!id || !userId) {
                return false;
            }
            try {
                await getEventAttendance(Number(id), userId, signal);
                return true;
            } catch (error) {
                const status = (error as { response?: { status?: number } } | undefined)?.response?.status;
                if (status === 404) {
                    return false;
                }
                throw error;
            }
        },
        enabled: hasValidId && Boolean(userId),
        placeholderData: keepPreviousData,
    });
    const isAttendingFromData = attendanceQuery.data ?? false;
    // Optimistic attendance and in-progress errors only apply to the event they were produced for,
    // so they are discarded as soon as a different event is loaded.
    const activeAttendance = attendanceOverride?.eventId === data?.id ? attendanceOverride : null;
    const isAttending = activeAttendance?.attending ?? isAttendingFromData;
    const attendeesCount = activeAttendance?.attendeesCount ?? (data?.attendeesCount ?? 0);
    const attendError = scopedMessage(attendErrorState, data?.id);
    const ratingError = scopedMessage(ratingErrorState, data?.id);
    const isFull = data?.attendeesLimit ? attendeesCount >= data.attendeesLimit : false;
    const existingRating = useMemo(
        () => data?.ratings?.find((rating) => rating.user.username === username) ?? null,
        [data?.ratings, username]
    );
    // The picked rating is a draft over the stored one; it is dropped whenever the event or the
    // stored rating changes.
    const ratingBaseline = existingRating?.rating ?? 0;
    const ratingKey = `${data?.id ?? ""}:${ratingBaseline}`;
    const ratingValue = ratingDraft?.key === ratingKey ? ratingDraft.value : ratingBaseline;

    const attendeesPageSize = 6;
    const commentsPageSize = 4;
    const attendeesQuery = useQuery<PageResult<EventAttendee>>({
        queryKey: ["eventAttendees", id, attendeesPage],
        queryFn: async ({ signal }) => {
            if (!id) {
                return emptyPage<EventAttendee>();
            }
            return listEventAttendees(Number(id), { page: attendeesPage, size: attendeesPageSize }, signal, queryClient);
        },
        enabled: hasValidId && canViewAttendees,
        placeholderData: keepPreviousData,
    });
    const commentsQuery = useQuery<PageResult<EventComment>>({
        queryKey: ["eventComments", id, commentsPage],
        queryFn: async ({ signal }) => {
            if (!id) {
                return emptyPage<EventComment>();
            }
            const page = await listEventResponses(Number(id), { page: commentsPage, size: commentsPageSize }, signal);
            return mapEventResponsesPage(page, signal, queryClient);
        },
        enabled: hasValidId,
        placeholderData: keepPreviousData,
    });
    const statsQuery = useQuery({
        queryKey: ["eventStatistics", id],
        queryFn: async ({ signal }) => {
            if (!id) {
                throw new Error("missing-event-id");
            }
            return getEventStatistics(Number(id), signal);
        },
        enabled: hasValidId,
        placeholderData: keepPreviousData,
    });
    const attendeesPageData = canViewAttendees ? (attendeesQuery.data ?? emptyPage<EventAttendee>()) : emptyPage<EventAttendee>();
    const commentsPageData = commentsQuery.data ?? emptyPage<EventComment>();
    const stats = statsQuery.data ?? null;
    const toSafeNumber = (value: unknown, fallback = 0) =>
        typeof value === "number" && Number.isFinite(value) ? value : fallback;
    const statsParticipantsCount = stats ? toSafeNumber(stats.totalParticipants, attendeesCount) : attendeesCount;
    const statsMaxParticipants = stats ? toSafeNumber(stats.maxParticipants, data?.attendeesLimit ?? 0) : (data?.attendeesLimit ?? 0);
    const topCountry = typeof stats?.topCountry === "string" ? stats.topCountry.trim() : "";
    const topCountryCount = stats ? toSafeNumber(stats.topCountryCount) : 0;
    const topCountryLabel = topCountry ? `${topCountry} (${topCountryCount})` : "—";
    const createdEventsLabel = stats ? String(toSafeNumber(stats.eventsCreatedByOrganizer)) : "—";
    const attendedEventsLabel = stats ? String(toSafeNumber(stats.eventsOrganizerAttends)) : "—";

    const updateEventSearchParams = useCallback(
        (updater: (params: URLSearchParams) => void) => {
            const nextParams = new URLSearchParams(searchParams);
            updater(nextParams);
            setSearchParams(nextParams);
        },
        [searchParams, setSearchParams]
    );

    const updateTabParam = useCallback(
        (tab: "details" | "chat" | "rating") => {
            updateEventSearchParams((params) => {
                if (tab === "details") {
                    params.delete(TAB_PARAM);
                    return;
                }
                params.set(TAB_PARAM, tab);
            });
        },
        [updateEventSearchParams]
    );

    const updatePageParam = useCallback(
        (paramName: string, page: number) => {
            updateEventSearchParams((params) => {
                if (page > 1) {
                    params.set(paramName, String(page));
                } else {
                    params.delete(paramName);
                }
            });
        },
        [updateEventSearchParams]
    );

    const refreshEventViews = () => {
        if (!id) {
            return;
        }
        queryClient.invalidateQueries({ queryKey: ["eventDetail", id] });
        queryClient.invalidateQueries({ queryKey: ["eventAttendees", id] });
        queryClient.invalidateQueries({ queryKey: ["eventStatistics", id] });
        queryClient.invalidateQueries({ queryKey: ["eventAttendance", id, userId] });
    };

    const resolveAttendanceError = (error: unknown, action: "attend" | "unattend") => {
        const response = (error as { response?: { status?: number; data?: { message?: string } } } | undefined)?.response;
        const status = response?.status;
        const rawMessage = response?.data?.message ?? "";
        const message = rawMessage.toLowerCase();

        if (message) {
            if (message.includes("already attending")) {
                return t("event.attend.error.already");
            }
            if (message.includes("is full")) {
                return t("event.attend.error.full");
            }
            if (message.includes("not in the future")) {
                return t("event.attend.error.notFuture");
            }
            if (message.includes("attendance") && message.includes("not found")) {
                return t("event.unattend.error.notAttending");
            }
        }

        if (status === 401) {
            return action === "attend" ? t("event.attend.error.unauthorized") : t("event.unattend.error.unauthorized");
        }
        if (status === 403) {
            return action === "attend" ? t("event.attend.error.forbidden") : t("event.unattend.error.forbidden");
        }
        if (status === 404 && action === "unattend") {
            return t("event.unattend.error.notAttending");
        }
        if (status === 409 && action === "attend") {
            return t("event.attend.error.conflict");
        }

        return action === "attend" ? t("event.attend.error.generic") : t("event.unattend.error.generic");
    };

    const handleAttendeesPageChange = useCallback(
        (page: number | string) => {
            updatePageParam(ATTENDEES_PAGE_PARAM, parsePageFromLink(page));
        },
        [updatePageParam]
    );

    const handleCommentsPageChange = useCallback(
        (page: number | string) => {
            updatePageParam(COMMENTS_PAGE_PARAM, parsePageFromLink(page));
        },
        [updatePageParam]
    );

    useEffect(() => {
        if (commentsQuery.isLoading || commentsQuery.isFetching) {
            return;
        }
        if (commentsPageData.content.length === 0 && commentsPageData.totalElements > 0 && commentsPage > 1) {
            const fallbackPage = Math.max(1, Math.min(commentsPage - 1, commentsPageData.totalPages || commentsPage - 1));
            if (fallbackPage !== commentsPage) {
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

    // A future event has no rating tab, so drop the parameter from the URL if it points there.
    useEffect(() => {
        if (data?.isFuture && requestedTab === "rating") {
            updateTabParam("details");
        }
    }, [data?.isFuture, requestedTab, updateTabParam]);

    useEffect(() => {
        if (!actionMenuOpen && openCommentMenuId === null) {
            return;
        }
        const handlePointerDown = (event: MouseEvent) => {
            const target = event.target as HTMLElement;
            if (target.closest("[data-event-menu]")) {
                return;
            }
            setActionMenuOpen(false);
            setOpenCommentMenuId(null);
        };
        const handleKeyDown = (event: KeyboardEvent) => {
            if (event.key === "Escape") {
                setActionMenuOpen(false);
                setOpenCommentMenuId(null);
            }
        };
        document.addEventListener("mousedown", handlePointerDown);
        document.addEventListener("keydown", handleKeyDown);
        return () => {
            document.removeEventListener("mousedown", handlePointerDown);
            document.removeEventListener("keydown", handleKeyDown);
        };
    }, [actionMenuOpen, openCommentMenuId]);

    if (!hasValidId) {
        return <NotFoundPage />;
    }

    if (isLoading) {
        return <PageStatus className="event-detail-page" message={t("admin.dashboard.loading", { defaultValue: "Cargando..." })} />;
    }

    if (isError || !data) {
        return <PageStatus className="event-detail-page" variant="error" message={t("admin.dashboard.error", { defaultValue: "Error cargando datos." })} />;
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
        navigate("/events");
    };

    const handleAttend = async () => {
        gate.runOrPrompt(async () => {
            if (!id || attendSubmitting) {
                return;
            }
            setAttendSubmitting(true);
            setAttendErrorState(null);
            try {
                await attendEvent(Number(id));
                setAttendanceOverride((prev) => ({
                    eventId: data.id,
                    attending: true,
                    attendeesCount: (prev?.eventId === data.id ? prev.attendeesCount : data.attendeesCount) + 1,
                }));
                refreshEventViews();
            } catch (error) {
                console.error("Failed to attend event", error);
                setAttendErrorState({ eventId: data.id, message: resolveAttendanceError(error, "attend") });
            } finally {
                setAttendSubmitting(false);
            }
        });
    };

    const handleUnattend = async () => {
        gate.runOrPrompt(async () => {
            if (!id || attendSubmitting) {
                return;
            }
            setAttendSubmitting(true);
            setAttendErrorState(null);
            try {
                if (!userId) {
                    throw new Error("missing-user-id");
                }
                await unattendEvent(Number(id), userId);
                setAttendanceOverride((prev) => ({
                    eventId: data.id,
                    attending: false,
                    attendeesCount: Math.max(0, (prev?.eventId === data.id ? prev.attendeesCount : data.attendeesCount) - 1),
                }));
                refreshEventViews();
            } catch (error) {
                console.error("Failed to unattend event", error);
                setAttendErrorState({ eventId: data.id, message: resolveAttendanceError(error, "unattend") });
            } finally {
                setAttendSubmitting(false);
            }
        });
    };

    const handleRatingSubmit = (event: FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        gate.runOrPrompt(async () => {
            if (!id) {
                setRatingErrorState({ eventId: data.id, message: t("admin.dashboard.error", { defaultValue: "Error cargando datos." }) });
                return;
            }
            if (!ratingValue) {
                setRatingErrorState({ eventId: data.id, message: t("event.rating.placeholder", { defaultValue: "Select a rating." }) });
                return;
            }
            setRatingSubmitting(true);
            setRatingErrorState(null);
            try {
                if (existingRating?.id) {
                    await updateEventRating(Number(id), existingRating.id, { rating: ratingValue });
                } else {
                    await createEventRating(Number(id), { rating: ratingValue });
                }
                queryClient.invalidateQueries({ queryKey: ["eventDetail", id] });
                queryClient.invalidateQueries({ queryKey: ["eventRatings", id] });
            } catch (error) {
                console.error("Failed to submit rating", error);
                setRatingErrorState({
                    eventId: data.id,
                    message: apiErrorMessage(error, t("admin.dashboard.error", { defaultValue: "Error cargando datos." })),
                });
            } finally {
                setRatingSubmitting(false);
            }
        });
    };

    const handleRatingDelete = () => {
        gate.runOrPrompt(async () => {
            if (!id || !existingRating?.id) {
                return;
            }
            setRatingSubmitting(true);
            setRatingErrorState(null);
            try {
                await deleteEventRating(Number(id), existingRating.id);
                setRatingDraft({ key: ratingKey, value: 0 });
                setHoverRating(null);
                await queryClient.invalidateQueries({ queryKey: ["eventDetail", id] });
                await queryClient.invalidateQueries({ queryKey: ["eventRatings", id] });
            } catch (error) {
                console.error("Failed to delete rating", error);
                setRatingErrorState({
                    eventId: data.id,
                    message: apiErrorMessage(error, t("event.rating.delete.error", { defaultValue: "We couldn't remove your rating." })),
                });
            } finally {
                setRatingSubmitting(false);
            }
        });
    };

    const handleReplySubmit = (event: FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        gate.runOrPrompt(async () => {
            if (!id) {
                setReplyError(t("admin.dashboard.error", { defaultValue: "Error cargando datos." }));
                return;
            }
            if (!replyMessage.trim()) {
                setReplyError(t("NotNull.replyEventForm.message", { defaultValue: "Please enter a message." }));
                return;
            }
            try {
                setReplySubmitting(true);
                setReplyError(null);
                setReplySuccess(null);
                await createEventResponse(Number(id), { message: replyMessage.trim() });
                setReplyMessage("");
                setReplySuccess(t("replyEvent.success", { defaultValue: "Message successfully sent!" }));
                const refreshedCommentsPage = await listEventResponses(
                    Number(id),
                    { page: 1, size: commentsPageSize }
                );
                const targetCommentsPage = Math.max(1, refreshedCommentsPage.totalPages || 1);
                updatePageParam(COMMENTS_PAGE_PARAM, targetCommentsPage);
                await Promise.all([
                    queryClient.invalidateQueries({
                        predicate: (query) =>
                            query.queryKey[0] === "eventComments" &&
                            String(query.queryKey[1]) === String(id),
                    }),
                    queryClient.invalidateQueries({ queryKey: ["eventDetail", id] }),
                ]);
            } catch (error) {
                console.error("Failed to submit event response", error);
                setReplyError(apiErrorMessage(error, t("admin.dashboard.error", { defaultValue: "Error cargando datos." })));
            } finally {
                setReplySubmitting(false);
            }
        });
    };

return (
    <>
    <div className="event-detail-page">
        <div className="layout-container">
                <div className="main-content">
                    <div className="content-container">
                        <div className="back-button-container">
                            <button
                                type="button"
                                className="back-link"
                                onClick={handleBack}
                            >
                                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                    <path d="M19 12H5"></path>
                                    <path d="M12 19l-7-7 7-7"></path>
                                </svg>
                                <span>{isOwner ? t("event.detail.back.to.profile") : t("event.detail.back.to.list")}</span>
                            </button>
                        </div>

                        <div className="event-detail-container">
                            <div className="event-top-section">
                                <div className="event-header">
                                    <h1 className="event-title">{data.title}</h1>

                                    <div className="action-controls">
                                <div style={{ position: "relative", display: "inline-block" }} data-event-menu>
                                            <button
                                                type="button"
                                                className="btn-action btn-menu"
                                                id="eventActionMenuButton"
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
                                                    id="eventActionDropdown"
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
                                                    {!isOwner && data.isFuture && (!isFull || isAttending) && (
                                                        <button
                                                            type="button"
                                                            style={{
                                                                width: "100%",
                                                                background: "none",
                                                                border: "none",
                                                                color: "#333",
                                                                padding: "12px 16px",
                                                                textDecoration: "none",
                                                                display: "flex",
                                                                alignItems: "center",
                                                                gap: "12px",
                                                                cursor: "pointer",
                                                                fontSize: "14px",
                                                            }}
                                                            onClick={() => {
                                                                setActionMenuOpen(false);
                                                                if (isAttending) {
                                                                    handleUnattend();
                                                                } else {
                                                                    handleAttend();
                                                                }
                                                            }}
                                                            disabled={attendSubmitting}
                                                        >
                                                            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                                                {isAttending ? (
                                                                    <>
                                                                        <line x1="18" y1="6" x2="6" y2="18"></line>
                                                                        <line x1="6" y1="6" x2="18" y2="18"></line>
                                                                    </>
                                                                ) : (
                                                                    <>
                                                                        <path d="M21 14V6a2 2 0 0 0-2-2H5a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h8"></path>
                                                                        <line x1="16" y1="2" x2="16" y2="6"></line>
                                                                        <line x1="8" y1="2" x2="8" y2="6"></line>
                                                                        <line x1="3" y1="10" x2="21" y2="10"></line>
                                                                        <line x1="19" y1="15" x2="19" y2="21"></line>
                                                                        <line x1="16" y1="18" x2="22" y2="18"></line>
                                                                    </>
                                                                )}
                                                            </svg>
                                                            <span>{isAttending ? t("event.cancel.attendance", { defaultValue: "Cancel Attendance" }) : t("event.attend")}</span>
                                                        </button>
                                                    )}
                                                    {isOwner && (
                                                        <Link
                                                            to={`/events/${id}/update`}
                                                            onClick={() => setActionMenuOpen(false)}
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
                                                            <span>{t("event.edit")}</span>
                                                        </Link>
                                                    )}
                                                    {!isOwner && (
                                                        <Link
                                                            to={`/reports/events/${id}/create`}
                                                            className="action-menu__item is-danger"
                                                            onClick={() => {
                                                                setActionMenuOpen(false);
                                                                pushToNavigationStack(`${location.pathname}${location.search}`);
                                                            }}
                                                            style={{
                                                                gap: "12px",
                                                            }}
                                                        >
                                                            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                                                <path d="M12 9v4"></path>
                                                                <path d="M12 17h.01"></path>
                                                                <circle cx="12" cy="12" r="10"></circle>
                                                            </svg>
                                                            <span>{t("event.report")}</span>
                                                        </Link>
                                                    )}
                                                    {(isOwner || admin) && (
                                                        <Link
                                                            to={`/events/${id}/delete`}
                                                            className="action-menu__item is-danger"
                                                            onClick={() => setActionMenuOpen(false)}
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
                                                            <span>{t("event.delete")}</span>
                                                        </Link>
                                                    )}
                                                </div>
                                            )}
                                        </div>

                                        {!isOwner && data.isFuture && isAttending && (
                                            <div className="attendance-status">
                                                <div className="attending-detail-badge">
                                                    <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                                        <polyline points="20 6 9 17 4 12"></polyline>
                                                    </svg>
                                                    <span>{t("event.attending")}</span>
                                                </div>
                                            </div>
                                        )}

                                        {!isOwner && data.isFuture && isFull && !isAttending && (
                                            <div className="event-full">
                                                <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                                    <circle cx="12" cy="12" r="10"></circle>
                                                    <line x1="12" y1="8" x2="12" y2="12"></line>
                                                    <line x1="12" y1="16" x2="12.01" y2="16"></line>
                                                </svg>
                                                <span>{t("event.full")}</span>
                                            </div>
                                        )}
                                    </div>
                                </div>

                                {isFetching && (
                                    <p className="section__helper">{t("admin.dashboard.loading", { defaultValue: "Actualizando..." })}</p>
                                )}
                                {attendError && <p className="section__helper">{attendError}</p>}

                                <div className="event-meta">
                                    <div className="meta-item">
                                        <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                            <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0118 0z"></path>
                                            <circle cx="12" cy="10" r="3"></circle>
                                        </svg>
                                        <span>{data.city.name}</span>
                                    </div>
                                    <div className="meta-item">
                                        <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                            <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                                            <line x1="16" y1="2" x2="16" y2="6"></line>
                                            <line x1="8" y1="2" x2="8" y2="6"></line>
                                            <line x1="3" y1="10" x2="21" y2="10"></line>
                                        </svg>
                                        <span>{formatDate(data.date, locale)}</span>
                                    </div>
                                    <div className="meta-item">
                                        <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                            <circle cx="12" cy="12" r="10"></circle>
                                            <polyline points="12 6 12 12 16 14"></polyline>
                                        </svg>
                                        <span>{data.time ?? t("event.allDayEvent")}</span>
                                    </div>
                                    {data.address && (
                                        <div className="meta-item">
                                            <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                                <polygon points="1 6 1 22 8 18 16 22 23 18 23 2 16 6 8 2 1 6"></polygon>
                                                <line x1="8" y1="2" x2="8" y2="18"></line>
                                                <line x1="16" y1="6" x2="16" y2="22"></line>
                                            </svg>
                                            <span>{data.address}</span>
                                        </div>
                                    )}
                                </div>

                                {creatorReady ? (
                                    <CreatorCard creator={data.user} />
                                ) : creatorError ? (
                                    <p className="section__helper">{t("event.creator.error", { defaultValue: "We couldn't load the organizer." })}</p>
                                ) : creatorLoading ? (
                                    <PageStatus compact message={t("admin.dashboard.loading", { defaultValue: "Cargando..." })} />
                                ) : null}

                                <div className="event-flyer">
                                    {data.flyerImageUrl ? (
                                        <img src={data.flyerImageUrl} alt={t("event.flyer.alt")} className="flyer-image" />
                                    ) : (
                                        <div className="flyer-placeholder">
                                            <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1" strokeLinecap="round" strokeLinejoin="round">
                                                <rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect>
                                                <circle cx="8.5" cy="8.5" r="1.5"></circle>
                                                <polyline points="21 15 16 10 5 21"></polyline>
                                            </svg>
                                            <p>{t("event.no.flyer")}</p>
                                        </div>
                                    )}
                                </div>

                                <div className="event-description">
                                    <h3 className="description-title">
                                        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                            <circle cx="12" cy="12" r="10"></circle>
                                            <line x1="12" y1="16" x2="12" y2="12"></line>
                                            <line x1="12" y1="8" x2="12.01" y2="8"></line>
                                        </svg>
                                        {t("event.description")}
                                    </h3>
                                    <div className="description-content">
                                        <p>{data.description}</p>
                                    </div>
                                </div>
                            </div>

                            <div className="tabs-container">
                                <div className="tabs-header">
                                    <button
                                        type="button"
                                        id="details-tab"
                                        className={`tab-btn ${activeTab === "details" ? "active" : ""}`}
                                        data-tab="details"
                                        onClick={() => updateTabParam("details")}
                                    >
                                        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                            <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
                                            <circle cx="9" cy="7" r="4"></circle>
                                            <path d="M23 21v-2a4 4 0 0 0-3-3.87"></path>
                                            <path d="M16 3.13a4 4 0 0 1 0 7.75"></path>
                                        </svg>
                                        <span>{t("event.details")}</span>
                                    </button>
                                    <button
                                        type="button"
                                        id="chat-tab"
                                        className={`tab-btn ${activeTab === "chat" ? "active" : ""}`}
                                        data-tab="chat"
                                        onClick={() => updateTabParam("chat")}
                                    >
                                        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                            <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path>
                                        </svg>
                                        <span>{t("event.chat")}</span>
                                        <span className="count">({commentsPageData.totalElements})</span>
                                    </button>
                                    {!data.isFuture && (
                                        <button
                                            type="button"
                                            id="rating-tab"
                                            className={`tab-btn ${activeTab === "rating" ? "active" : ""}`}
                                            data-tab="rating"
                                            onClick={() => updateTabParam("rating")}
                                        >
                                            <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                                <path d="M12 17l-5.5 3.5L8 14l-4-3h5L12 4l3 7h5l-4 3 1.5 6.5z"></path>
                                            </svg>
                                            <span>{t("event.rating")}</span>
                                        </button>
                                    )}
                                </div>

                                <div className="tabs-content">
                                    {activeTab === "details" && (
                                        <div id="details-content" className="tab-content" style={{ display: "block" }}>
                                            <div className="content-section">
                                                <div className="section-header">
                                                    <div className="attendees-header">
                                                        <h2 className="section-title">
                                                            <svg xmlns="http://www.w3.org/2000/svg" width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                                                <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
                                                                <circle cx="9" cy="7" r="4"></circle>
                                                                <path d="M23 21v-2a4 4 0 0 0-3-3.87"></path>
                                                                <path d="M16 3.13a4 4 0 0 1 0 7.75"></path>
                                                            </svg>
                                                            {t("event.data")}
                                                        </h2>
                                                    </div>
                                                </div>

                                                <div className="data-statistics-container">
                                                    <div className="data-section">
                                                        <div className="statistics-list">
                                                            <div className="stat-item">
                                                                <span className="stat-label">{t("event.stats.createdEvents")}</span>
                                                                <span className="stat-value">{createdEventsLabel}</span>
                                                            </div>
                                                            <div className="stat-item">
                                                                <span className="stat-label">{t("event.stats.attendedEvents")}</span>
                                                                <span className="stat-value">{attendedEventsLabel}</span>
                                                            </div>
                                                            <div className="stat-item">
                                                                <span className="stat-label">{t("event.stats.topCountry")}</span>
                                                                <span className="stat-value">{topCountryLabel}</span>
                                                            </div>
                                                            <div className="stat-item">
                                                                <span className="stat-label">{t("event.stats.totalParticipants")}</span>
                                                                {statsMaxParticipants > 0 ? (
                                                                    <span className="stat-value">
                                                                        ({statsParticipantsCount} / {statsMaxParticipants})
                                                                    </span>
                                                                ) : (
                                                                    <span className="stat-value">
                                                                        ({statsParticipantsCount} / {t("event.noAttendeesLimit")})
                                                                    </span>
                                                                )}
                                                            </div>
                                                        </div>
                                                        {statsQuery.isLoading && (
                                                            <PageStatus compact message={t("event.stats.loading", { defaultValue: "Loading statistics..." })} />
                                                        )}
                                                        {statsQuery.isError && (
                                                            <p className="section__helper">{t("event.stats.error", { defaultValue: "We couldn't load statistics." })}</p>
                                                        )}
                                                    </div>
                                                </div>

                                                {canViewAttendees && (
                                                    <>
                                                        <div id="attendees-list" className="attendees-grid">
                                                            {attendeesQuery.isLoading ? (
                                                                <PageStatus compact message={t("admin.dashboard.loading", { defaultValue: "Cargando..." })} />
                                                            ) : attendeesQuery.isError ? (
                                                                <p className="section__helper">{t("admin.dashboard.error", { defaultValue: "Error cargando datos." })}</p>
                                                            ) : attendeesPageData.content.length === 0 ? (
                                                                <div className="empty-state">
                                                                    <div className="empty-icon">
                                                                        <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1" strokeLinecap="round" strokeLinejoin="round">
                                                                            <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
                                                                            <circle cx="9" cy="7" r="4"></circle>
                                                                            <path d="M23 21v-2a4 4 0 0 0-3-3.87"></path>
                                                                            <path d="M16 3.13a4 4 0 0 1 0 7.75"></path>
                                                                        </svg>
                                                                    </div>
                                                                    <p className="empty-message">{t("event.no.attendees")}</p>
                                                                </div>
                                                            ) : (
                                                                attendeesPageData.content.map((attendee) => (
                                                                    <div key={attendee.id} className="attendee-card">
                                                                        <div className="attendee-avatar">
                                                                        {attendee.profilePictureUrl ? (
                                                                            <img src={attendee.profilePictureUrl} alt={t("profile.picture.alt")} className="detail-avatar-img" />
                                                                        ) : (
                                                                            <div className="avatar-placeholder">
                                                                                <AvatarFallbackIcon size={18} />
                                                                            </div>
                                                                        )}
                                                                        </div>
                                                                        <div className="attendee-info">
                                                                            <h3 className="attendee-name">
                                                                                {attendee.firstname} {attendee.lastname}
                                                                            </h3>
                                                                        </div>
                                                                    </div>
                                                                ))
                                                            )}
                                                        </div>

                                                        <Pagination
                                                            totalPages={attendeesPageData.totalPages}
                                                            currentPage={attendeesPageData.currentPage}
                                                            pageSize={attendeesPageData.pageSize}
                                                            nextPage={attendeesPageData.next}
                                                            prevPage={attendeesPageData.prev}
                                                            firstPage={attendeesPageData.first}
                                                            lastPage={attendeesPageData.last}
                                                            onPageChange={handleAttendeesPageChange}
                                                            previousLabel={t("pagination.prev")}
                                                            nextLabel={t("pagination.next")}
                                                        />
                                                    </>
                                                )}
                                            </div>
                                        </div>
                                    )}

                                    {activeTab === "chat" && (
                                        <div id="chat-content" className="tab-content" style={{ display: "block" }}>
                                            <div className="content-section">
                                                <div className="section-header">
                                                    <h2 className="section-title">
                                                        <svg xmlns="http://www.w3.org/2000/svg" width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                                            <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path>
                                                        </svg>
                                                        {t("event.responses")}
                                                        <span className="count">({commentsPageData.totalElements})</span>
                                                    </h2>
                                                </div>

                                                <div id="chat-list" className="chat-list">
                                                    {commentsQuery.isLoading ? (
                                                        <PageStatus compact message={t("admin.dashboard.loading", { defaultValue: "Cargando..." })} />
                                                    ) : commentsPageData.content.length === 0 ? (
                                                        <div className="empty-state">
                                                            <div className="empty-icon">
                                                                <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1" strokeLinecap="round" strokeLinejoin="round">
                                                                    <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path>
                                                                </svg>
                                                            </div>
                                                            <p className="empty-message">{t("event.no.responses")}</p>
                                                        </div>
                                                    ) : (
                                                        commentsPageData.content.map((response) => (
                                                            <div key={response.id} className="chat-message">
                                                                <div className="message-header">
                                                                    <div className="message-user">
                                                            <div className="message-avatar">
                                                                <div className="avatar-placeholder">
                                                                    <AvatarFallbackIcon size={18} />
                                                                </div>
                                                            </div>
                                                                        <div className="message-user-info">
                                                                            <h3 className="message-username">{response.user.username}</h3>
                                                                            <p className="message-date">{formatDateTime(response.dateTime, locale)}</p>
                                                                        </div>
                                                                    </div>
                                                        <div style={{ position: "relative", display: "inline-block" }} data-event-menu>
                                                                        <button
                                                                            type="button"
                                                                            className="btn-action"
                                                                            aria-label={t("comment.actions")}
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
                                                                                    <Link
                                                                                        to={`/reports/event-responses/${response.id}/create`}
                                                                                        className="action-menu__item is-danger"
                                                                                        onClick={() => pushToNavigationStack(`${location.pathname}${location.search}`)}
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
                                                                                </Link>
                                                                                    {(admin || response.user.username === username) && (
                                                                                        <Link
                                                                                            to={`/events/reply/${response.id}/delete?eventId=${id}`}
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
                                                                                    </Link>
                                                                                )}
                                                                            </div>
                                                                        )}
                                                                    </div>
                                                                </div>
                                                                <div className="message-content">
                                                                    <p className="message-text">{response.message}</p>
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

                                                <div className="reply-container">
                                                    <h3 className="reply-title">
                                                        <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                                            <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                                                            <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
                                                        </svg>
                                                        {t("reply.message")}
                                                    </h3>
                                                    <form className="reply-form" onSubmit={handleReplySubmit}>
                                                        <div className="form-group">
                                                            <label className="form-label" htmlFor="event-reply">
                                                                {t("reply.message")}
                                                            </label>
                                                            <textarea
                                                                id="event-reply"
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
                                        </div>
                                    )}

                                    {activeTab === "rating" && !data.isFuture && (
                                        <div id="rating-content" className="tab-content" style={{ display: "block" }}>
                                            <div className="content-section">
                                                <div className="section-header">
                                                    <h2 className="section-title">
                                                        <svg xmlns="http://www.w3.org/2000/svg" width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                                            <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon>
                                                        </svg>
                                                        {t("event.rating")}
                                                        {data.averageRating && !ratingsLoading && (
                                                            <span className="count">({data.ratingCount} {t("event.rating.reviews")})</span>
                                                        )}
                                                    </h2>
                                                </div>

                                                <div className="rating-content">
                                                    {ratingsLoading && (
                                                        <PageStatus compact message={t("admin.dashboard.loading", { defaultValue: "Cargando..." })} />
                                                    )}
                                                    {ratingsError && (
                                                        <p className="section__helper">{t("event.rating.load.error", { defaultValue: "We couldn't load ratings." })}</p>
                                                    )}
                                                    {data.averageRating ? (
                                                        <div className="rating-summary">
                                                            <div className="average-rating">
                                                                <span className="rating-number">{data.averageRating}</span>
                                                                {renderStars(data.averageRating)}
                                                                <span className="rating-text">{t("event.rating.outOf", { values: { 0: 5 } })}</span>
                                                            </div>
                                                        </div>
                                                    ) : (
                                                        <div className="rating-summary empty-state">
                                                            <div className="empty-icon">
                                                                <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1" strokeLinecap="round" strokeLinejoin="round">
                                                                    <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon>
                                                                </svg>
                                                            </div>
                                                            <p className="empty-message">{t("event.rating.noRatings")}</p>
                                                        </div>
                                                    )}

                                                    {isAttending && (
                                                        <div className="user-rating-form">
                                                            <h3 className="rating-form-title">
                                                                <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                                                    <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon>
                                                                </svg>
                                                                {existingRating ? t("event.rating.update") : t("event.rating.add")}
                                                            </h3>
                                                            <form className="rating-form" onSubmit={handleRatingSubmit}>
                                                                <div className="rating-input-container">
                                                                    <label className="rating-label">{t("event.rating.yourRating")}</label>
                                                                    <div
                                                                        className="star-rating-input"
                                                                        onMouseLeave={() => setHoverRating(null)}
                                                                    >
                                                                        {Array.from({ length: 5 }, (_, index) => {
                                                                            const value = index + 1;
                                                                            const isActive = (hoverRating ?? ratingValue) >= value;
                                                                            return (
                                                                                <label
                                                                                    key={value}
                                                                                    className={`star-label${isActive ? " active" : ""}`}
                                                                                    onMouseEnter={() => setHoverRating(value)}
                                                                                >
                                                                                    <input
                                                                                        type="radio"
                                                                                        name="rating"
                                                                                        value={value}
                                                                                        checked={ratingValue === value}
                                                                                        onChange={() => setRatingDraft({ key: ratingKey, value })}
                                                                                        onFocus={() => setHoverRating(value)}
                                                                                        onBlur={() => setHoverRating(null)}
                                                                                    />
                                                                                    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="currentColor" stroke="currentColor" strokeWidth="2">
                                                                                        <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon>
                                                                                    </svg>
                                                                                </label>
                                                                            );
                                                                        })}
                                                                    </div>
                                                                    <div className="rating-value-display">
                                                                        <span id="current-rating-value">{ratingValue}</span>
                                                                        <span className="rating-max">/ 5</span>
                                                                    </div>
                                                                </div>
                                                                {ratingError && (
                                                                    <p className="form-field__text form-field__text--error">{ratingError}</p>
                                                                )}
                                                                <div className="form-actions">
                                                                    <button type="submit" className="btn btn-primary" disabled={ratingSubmitting}>
                                                                        {existingRating ? t("event.rating.update.submit") : t("event.rating.submit")}
                                                                    </button>
                                                                    {existingRating && (
                                                                        <button
                                                                            type="button"
                                                                            className="btn-secondary"
                                                                            onClick={handleRatingDelete}
                                                                            disabled={ratingSubmitting}
                                                                        >
                                                                            {t("event.rating.delete", { defaultValue: "Remove Rating" })}
                                                                        </button>
                                                                    )}
                                                                </div>
                                                            </form>
                                                        </div>
                                                    )}
                                                </div>
                                            </div>
                                        </div>
                                    )}
                                </div>
                            </div>
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
