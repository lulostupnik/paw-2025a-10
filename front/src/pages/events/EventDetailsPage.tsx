import { useMemo, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import { useI18n } from "@/lib/i18n";
import { isAdmin } from "@/lib/auth/auth";
import CreatorCard from "@/components/detail/CreatorCard";
import AdminPagination from "@/components/admin-dashboard/AdminPagination";
import { useEventDetailData } from "@/hooks/useEventDetailData";
import type { EventDetailScenario } from "@/mocks/eventDetail.mock";

const SCENARIO: EventDetailScenario = "normal";

const paginate = <T,>(items: T[], page: number, pageSize: number) => {
    const totalItems = items.length;
    const totalPages = Math.ceil(totalItems / pageSize) || 1;
    const safePage = Math.min(Math.max(page, 1), totalPages);
    const start = (safePage - 1) * pageSize;
    return {
        content: items.slice(start, start + pageSize),
        totalPages,
        currentPage: safePage,
        pageSize,
    };
};

const formatDate = (value: string, locale: string) => {
    const date = new Date(value);
    if (Number.isNaN(date.getTime())) {
        return value;
    }
    return new Intl.DateTimeFormat(locale, { year: "numeric", month: "long", day: "numeric" }).format(date);
};

const formatDateTime = (value: string, locale: string) => {
    const date = new Date(value);
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

export default function EventDetailPage() {
    const { t, locale } = useI18n();
    const navigate = useNavigate();
    const { id } = useParams();
    const { data, isLoading, isError } = useEventDetailData({ scenario: SCENARIO });
    const [actionMenuOpen, setActionMenuOpen] = useState(false);
    const [openCommentMenuId, setOpenCommentMenuId] = useState<number | null>(null);
    const [activeTab, setActiveTab] = useState<"details" | "chat" | "rating">("details");
    const [attendeesPage, setAttendeesPage] = useState(1);
    const [commentsPage, setCommentsPage] = useState(1);
    const [ratingValue, setRatingValue] = useState<number>(data.averageRating ?? 0);

    const isOwner = false;
    const admin = isAdmin();
    const isAttending = true;
    const isFull = data.attendeesLimit ? data.attendeesCount >= data.attendeesLimit : false;

    const pagedAttendees = useMemo(() => paginate(data.attendees, attendeesPage, 6), [data.attendees, attendeesPage]);
    const pagedComments = useMemo(() => paginate(data.comments, commentsPage, 4), [data.comments, commentsPage]);

    if (isLoading) {
        return <div className="event-detail-page">{t("admin.dashboard.loading", { defaultValue: "Cargando..." })}</div>;
    }

    if (isError) {
        return <div className="event-detail-page">{t("admin.dashboard.error", { defaultValue: "Error cargando datos." })}</div>;
    }

    return (
        <div className="event-detail-page">
            <div className="layout-container">
                <div className="main-content">
                    <div className="content-container">
                        <div className="back-button-container">
                            <button
                                type="button"
                                className="back-link"
                                onClick={() => {
                                    if (window.history.length > 1) {
                                        navigate(-1);
                                    } else {
                                        navigate("/events");
                                    }
                                }}
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
                                        <div style={{ position: "relative", display: "inline-block" }}>
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
                                                    {!isOwner && data.isFuture && !isFull && (
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
                                                                // TODO: attend/cancel event.
                                                            }}
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

                                        {!isOwner && data.isFuture && !isFull && isAttending && (
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

                                <CreatorCard creator={data.user} />

                                <div className="event-flyer">
                                    {data.flyerImageUrl ? (
                                        <img src={data.flyerImageUrl} alt="Event Flyer" className="flyer-image" />
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
                                        onClick={() => setActiveTab("details")}
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
                                        onClick={() => setActiveTab("chat")}
                                    >
                                        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                            <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path>
                                        </svg>
                                        <span>{t("event.chat")}</span>
                                        <span className="count">({data.comments.length})</span>
                                    </button>
                                    {!data.isFuture && (
                                        <button
                                            type="button"
                                            id="rating-tab"
                                            className={`tab-btn ${activeTab === "rating" ? "active" : ""}`}
                                            data-tab="rating"
                                            onClick={() => setActiveTab("rating")}
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
                                                                <span className="stat-value">6</span>
                                                            </div>
                                                            <div className="stat-item">
                                                                <span className="stat-label">{t("event.stats.attendedEvents")}</span>
                                                                <span className="stat-value">12</span>
                                                            </div>
                                                            <div className="stat-item">
                                                                <span className="stat-label">{t("event.stats.topCountry")}</span>
                                                                <span className="stat-value">Argentina (18)</span>
                                                            </div>
                                                            <div className="stat-item">
                                                                <span className="stat-label">{t("event.stats.totalParticipants")}</span>
                                                                {data.attendeesLimit ? (
                                                                    <span className="stat-value">
                                                                        ({data.attendeesCount} / {data.attendeesLimit})
                                                                    </span>
                                                                ) : (
                                                                    <span className="stat-value">
                                                                        ({data.attendeesCount} / {t("event.noAttendeesLimit")})
                                                                    </span>
                                                                )}
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>

                                                <div id="attendees-list" className="attendees-grid">
                                                    {pagedAttendees.content.length === 0 ? (
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
                                                        pagedAttendees.content.map((attendee) => (
                                                            <div key={attendee.id} className="attendee-card">
                                                                <div className="attendee-avatar">
                                                                    {attendee.profilePictureUrl ? (
                                                                        <img src={attendee.profilePictureUrl} alt="Profile" className="detail-avatar-img" />
                                                                    ) : (
                                                                        <div className="avatar-placeholder">
                                                                            {attendee.firstname[0]}
                                                                            {attendee.lastname[0]}
                                                                        </div>
                                                                    )}
                                                                </div>
                                                                <div className="attendee-info">
                                                                    <h3 className="attendee-name">
                                                                        {attendee.firstname} {attendee.lastname}
                                                                    </h3>
                                                                    <p className="attendee-email">{attendee.email}</p>
                                                                </div>
                                                            </div>
                                                        ))
                                                    )}
                                                </div>

                                                <AdminPagination
                                                    totalPages={pagedAttendees.totalPages}
                                                    currentPage={pagedAttendees.currentPage}
                                                    pageSize={pagedAttendees.pageSize}
                                                    onPageChange={setAttendeesPage}
                                                    previousLabel={t("pagination.prev")}
                                                    nextLabel={t("pagination.next")}
                                                />
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
                                                        <span className="count">({data.comments.length})</span>
                                                    </h2>
                                                </div>

                                                <div id="chat-list" className="chat-list">
                                                    {pagedComments.content.length === 0 ? (
                                                        <div className="empty-state">
                                                            <div className="empty-icon">
                                                                <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1" strokeLinecap="round" strokeLinejoin="round">
                                                                    <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path>
                                                                </svg>
                                                            </div>
                                                            <p className="empty-message">{t("event.no.responses")}</p>
                                                        </div>
                                                    ) : (
                                                        pagedComments.content.map((response) => (
                                                            <div key={response.id} className="chat-message">
                                                                <div className="message-header">
                                                                    <div className="message-user">
                                                                        <div className="message-avatar">
                                                                            <div className="avatar-placeholder">{response.user.username.slice(0, 1)}</div>
                                                                        </div>
                                                                        <div className="message-user-info">
                                                                            <h3 className="message-username">{response.user.username}</h3>
                                                                            <p className="message-date">{formatDateTime(response.dateTime, locale)}</p>
                                                                        </div>
                                                                    </div>
                                                                    <div style={{ position: "relative", display: "inline-block" }}>
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
                                                                                <Link
                                                                                    to={`/reports/event-responses/${response.id}/create`}
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
                                                                                        <path d="M12 9v4"></path>
                                                                                        <path d="M12 17h.01"></path>
                                                                                        <circle cx="12" cy="12" r="10"></circle>
                                                                                    </svg>
                                                                                    <span>{t("comment.report")}</span>
                                                                                </Link>
                                                                                {admin && (
                                                                                    <Link
                                                                                        to={`/events/reply/${response.id}/delete`}
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

                                                    <AdminPagination
                                                        totalPages={pagedComments.totalPages}
                                                        currentPage={pagedComments.currentPage}
                                                        pageSize={pagedComments.pageSize}
                                                        onPageChange={setCommentsPage}
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
                                                    <form
                                                        className="reply-form"
                                                        onSubmit={(event) => {
                                                            event.preventDefault();
                                                            // TODO: submit event response.
                                                        }}
                                                    >
                                                        <div className="form-group">
                                                            <label className="form-label" htmlFor="event-reply">
                                                                {t("reply.message")}
                                                            </label>
                                                            <textarea
                                                                id="event-reply"
                                                                className="form-textarea"
                                                                placeholder={t("reply.message.hint")}
                                                            />
                                                        </div>
                                                        <div className="form-actions">
                                                            <button type="submit" className="btn btn-primary">
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
                                                        {data.averageRating && (
                                                            <span className="count">({data.ratings.length} {t("event.rating.reviews")})</span>
                                                        )}
                                                    </h2>
                                                </div>

                                                <div className="rating-content">
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

                                                    <div className="user-rating-form">
                                                        <h3 className="rating-form-title">
                                                            <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                                                <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon>
                                                            </svg>
                                                            {t("event.rating.add")}
                                                        </h3>
                                                        <form
                                                            className="rating-form"
                                                            onSubmit={(event) => {
                                                                event.preventDefault();
                                                                // TODO: submit rating for event.
                                                            }}
                                                        >
                                                            <div className="rating-input-container">
                                                                <label className="rating-label">{t("event.rating.yourRating")}</label>
                                                                <div className="star-rating-input">
                                                                    {Array.from({ length: 5 }, (_, index) => {
                                                                        const value = index + 1;
                                                                        return (
                                                                            <label key={value} className="star-label">
                                                                                <input
                                                                                    type="radio"
                                                                                    name="rating"
                                                                                    value={value}
                                                                                    checked={ratingValue === value}
                                                                                    onChange={() => setRatingValue(value)}
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
                                                            <div className="form-actions">
                                                                <button type="submit" className="btn btn-primary">
                                                                    {t("event.rating.submit")}
                                                                </button>
                                                            </div>
                                                        </form>
                                                    </div>
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
    );
}
