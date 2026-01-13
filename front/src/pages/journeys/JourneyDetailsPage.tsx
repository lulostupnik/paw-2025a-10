import { useMemo, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import { useI18n } from "@/lib/i18n";
import { isAdmin } from "@/lib/auth/auth";
import CreatorCard from "@/components/detail/CreatorCard";
import AdminPagination from "@/components/admin-dashboard/AdminPagination";
import { useJourneyDetailData } from "@/hooks/useJourneyDetailData";
import type { JourneyDetailScenario } from "@/mocks/journeyDetail.mock";

const SCENARIO: JourneyDetailScenario = "normal";

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

export default function JourneyDetailPage() {
    const { t, locale } = useI18n();
    const navigate = useNavigate();
    const { id } = useParams();
    const { data, isLoading, isError } = useJourneyDetailData({ scenario: SCENARIO });
    const [actionMenuOpen, setActionMenuOpen] = useState(false);
    const [commentsOpen, setCommentsOpen] = useState(true);
    const [eventsOpen, setEventsOpen] = useState(true);
    const [openCommentMenuId, setOpenCommentMenuId] = useState<number | null>(null);
    const [interestsPage, setInterestsPage] = useState(1);
    const [eventsPage, setEventsPage] = useState(1);
    const [commentsPage, setCommentsPage] = useState(1);

    const isOwner = false;
    const admin = isAdmin();

    const pagedInterests = useMemo(() => paginate(data.interests, interestsPage, 8), [data.interests, interestsPage]);
    const pagedEvents = useMemo(() => paginate(data.events, eventsPage, 6), [data.events, eventsPage]);
    const pagedComments = useMemo(() => paginate(data.comments, commentsPage, 4), [data.comments, commentsPage]);

    if (isLoading) {
        return <div className="journey-detail-page">{t("admin.dashboard.loading", { defaultValue: "Cargando..." })}</div>;
    }

    if (isError) {
        return <div className="journey-detail-page">{t("admin.dashboard.error", { defaultValue: "Error cargando datos." })}</div>;
    }

    return (
        <div className="journey-detail-page">
            <div className="layout-container">
                <div className="main-content">
                    <div className="content-container">
                        <div className="back-navigation">
                            <button
                                type="button"
                                className="back-link"
                                onClick={() => {
                                    if (window.history.length > 1) {
                                        navigate(-1);
                                    } else {
                                        navigate("/journeys");
                                    }
                                }}
                            >
                                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="icon">
                                    <path d="M19 12H5"></path>
                                    <path d="M12 19l-7-7 7-7"></path>
                                </svg>
                                <span>{isOwner ? t("journey.detail.back.to.profile") : t("journey.detail.back.to.list")}</span>
                            </button>
                        </div>

                        <div className="content-card journey-detail-card">
                            <div className="journey-actions">
                                <div style={{ position: "relative", display: "inline-block" }}>
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
                                                    <span>{t("journey.delete")}</span>
                                                </Link>
                                            )}
                                            {!isOwner && (
                                                <Link
                                                    to={`/reports/journeys/${id}/create`}
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
                                                values: { 0: data.user.firstname, 1: data.user.lastname },
                                            })}
                                        </h1>
                                        <div className="journey-meta">
                                            <div className="journey-destination">
                                                <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="icon">
                                                    <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0118 0z"></path>
                                                    <circle cx="12" cy="10" r="3"></circle>
                                                </svg>
                                                <span className="destination-text">
                                                    {data.destinationUniversity.city} - {data.destinationUniversity.name}
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

                                <CreatorCard creator={data.user} isJourneyCreator showName={false} />

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
                                    {pagedInterests.content.length === 0 ? (
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
                                                {pagedInterests.content.map((interest) => (
                                                    <div key={interest} className="interest-tag">
                                                        {interest}
                                                    </div>
                                                ))}
                                            </div>
                                            <AdminPagination
                                                totalPages={pagedInterests.totalPages}
                                                currentPage={pagedInterests.currentPage}
                                                pageSize={pagedInterests.pageSize}
                                                onPageChange={setInterestsPage}
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
                                        <span className="count">({pagedEvents.totalPages})</span>
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
                                        {pagedEvents.content.length === 0 ? (
                                            <div className="empty-state">
                                                <div className="empty-icon">
                                                    <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1" strokeLinecap="round" strokeLinejoin="round" className="empty-icon-img">
                                                        <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                                                        <line x1="16" y1="2" x2="16" y2="6"></line>
                                                        <line x1="8" y1="2" x2="8" y2="6"></line>
                                                        <line x1="3" y1="10" x2="21" y2="10"></line>
                                                    </svg>
                                                </div>
                                                <p className="empty-message">{t("journey.detail.no.events")}</p>
                                            </div>
                                        ) : (
                                            <>
                                                <div className="journey-events-container">
                                                    {pagedEvents.content.map((event) => (
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
                                                <AdminPagination
                                                    totalPages={pagedEvents.totalPages}
                                                    currentPage={pagedEvents.currentPage}
                                                    pageSize={pagedEvents.pageSize}
                                                    onPageChange={setEventsPage}
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
                                            <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path>
                                        </svg>
                                        {t("journey.detail.responses")}
                                        <span className="count">({data.comments.length})</span>
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
                                        {pagedComments.content.length === 0 ? (
                                            <div className="empty-state">
                                                <div className="empty-icon">
                                                    <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1" strokeLinecap="round" strokeLinejoin="round" className="empty-icon-img">
                                                        <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path>
                                                    </svg>
                                                </div>
                                                <p className="empty-message">{t("journey.detail.no.responses")}</p>
                                            </div>
                                        ) : (
                                            pagedComments.content.map((response) => (
                                                <div key={response.id} className="chat-message">
                                                    <div className="response-header">
                                                        <div className="response-user">
                                                            <div className="response-avatar">
                                                                <div className="avatar-placeholder">{response.user.username.slice(0, 1)}</div>
                                                            </div>
                                                            <div className="response-user-info">
                                                                <h3 className="response-username">{response.user.username}</h3>
                                                                <p className="response-date">{formatDateTime(response.dateTime, locale)}</p>
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
                                                                    {(!isOwner || admin) && (
                                                                        <Link
                                                                            to={`/reports/journey-responses/${response.id}/create`}
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
                                                                    )}
                                                                    {admin && (
                                                                        <Link
                                                                            to={`/journeys/reply/${response.id}/delete`}
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
                                                    <div className="response-body">
                                                        <p className="response-message">{response.message}</p>
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
                                            onSubmit={(event) => {
                                                event.preventDefault();
                                                // TODO: submit journey response.
                                            }}
                                        >
                                            <div className="form-group">
                                                <label className="form-label" htmlFor="journey-reply">
                                                    {t("reply.message")}
                                                </label>
                                                <textarea
                                                    id="journey-reply"
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
                            </section>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}
