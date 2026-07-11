import { Link } from "react-router-dom";
import { useI18n } from "@/lib/i18n";
import Pagination from "@/components/listing/Pagination";
import PageStatus from "@/components/ui/PageStatus";
import EventCard from "@/components/cards/EventCard";
import type { PageResult } from "@/types/pagination";
import type { ProfileEvent } from "@/types/event";
import { classNames } from "@/lib/utils/classNames";

interface ProfileEventsTabProps {
    isMine: boolean;
    activeTab: "created" | "attending" | "finished";
    created: PageResult<ProfileEvent>;
    attending: PageResult<ProfileEvent>;
    finished: PageResult<ProfileEvent>;
    isLoading?: boolean;
    onTabChange: (tab: "created" | "attending" | "finished") => void;
    onCreatedPageChange: (page: number | string) => void;
    onAttendingPageChange: (page: number | string) => void;
    onFinishedPageChange: (page: number | string) => void;
}

export default function ProfileEventsTab({
    isMine,
    activeTab,
    created,
    attending,
    finished,
    isLoading = false,
    onTabChange,
    onCreatedPageChange,
    onAttendingPageChange,
    onFinishedPageChange,
}: ProfileEventsTabProps) {
    const { t } = useI18n();

    return (
        <div className="profile-section active" id="events-section">
            <div className="profile-card">
                <h2 className="section-title">{t("nav.events")}</h2>
                {isMine && (
                    <div className="section-actions">
                        <Link to="/events/create" className="btn-primary">
                            <svg xmlns="http://www.w3.org/2000/svg" className="btn-icon" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                <line x1="12" y1="5" x2="12" y2="19"></line>
                                <line x1="5" y1="12" x2="19" y2="12"></line>
                            </svg>
                            {t("event.create.button")}
                        </Link>
                    </div>
                )}

                <div className="events-filter-tabs">
                    <button
                        type="button"
                        className={classNames("events-tab", activeTab === "created" && "active")}
                        data-events-tab="created"
                        onClick={() => onTabChange("created")}
                    >
                        {t("profile.events.created")}
                    </button>
                    <button
                        type="button"
                        className={classNames("events-tab", activeTab === "attending" && "active")}
                        data-events-tab="attending"
                        onClick={() => onTabChange("attending")}
                    >
                        {t("profile.events.attending")}
                    </button>
                    <button
                        type="button"
                        className={classNames("events-tab", activeTab === "finished" && "active")}
                        data-events-tab="finished"
                        onClick={() => onTabChange("finished")}
                    >
                        {t("profile.events.finished")}
                    </button>
                </div>

                <div className={classNames("events-tab-content", activeTab === "created" && "active")} id="created-events">
                    <div className="cards-grid">
                        {isLoading ? (
                            <PageStatus compact message={t("admin.dashboard.loading", { defaultValue: "Cargando..." })} />
                        ) : created.content.length === 0 ? (
                            <div className="empty-state">
                                <div className="empty-icon">
                                    <svg xmlns="http://www.w3.org/2000/svg" className="empty-svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                                    </svg>
                                </div>
                                <p className="empty-message">{t("profile.no.created.events")}</p>
                                {isMine && (
                                    <Link to="/events/create" className="empty-action-btn">
                                        {t("event.create.button")}
                                    </Link>
                                )}
                            </div>
                        ) : (
                            created.content.map((event) => (
                                <EventCard key={event.id} event={event} />
                            ))
                        )}
                    </div>
                    <Pagination
                        totalPages={created.totalPages}
                        currentPage={created.currentPage}
                        pageSize={created.pageSize}
                        onPageChange={onCreatedPageChange}
                        previousLabel={t("pagination.prev")}
                        nextLabel={t("pagination.next")}
                        firstPage={created.first}
                        lastPage={created.last}
                        nextPage={created.next}
                        prevPage={created.prev}
                    />
                </div>

                <div className={classNames("events-tab-content", activeTab === "attending" && "active")} id="attending-events">
                    <div className="cards-grid">
                        {isLoading ? (
                            <PageStatus compact message={t("admin.dashboard.loading", { defaultValue: "Cargando..." })} />
                        ) : attending.content.length === 0 ? (
                            <div className="empty-state">
                                <div className="empty-icon">
                                    <svg xmlns="http://www.w3.org/2000/svg" className="empty-svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                                    </svg>
                                </div>
                                <p className="empty-message">{t("profile.no.attending.events")}</p>
                                <Link to="/events" className="empty-action-btn">
                                    {t("profile.explore.events")}
                                </Link>
                            </div>
                        ) : (
                            attending.content.map((event) => (
                                <EventCard key={event.id} event={event} />
                            ))
                        )}
                    </div>
                    <Pagination
                        totalPages={attending.totalPages}
                        currentPage={attending.currentPage}
                        pageSize={attending.pageSize}
                        onPageChange={onAttendingPageChange}
                        previousLabel={t("pagination.prev")}
                        nextLabel={t("pagination.next")}
                        firstPage={attending.first}
                        lastPage={attending.last}
                        nextPage={attending.next}
                        prevPage={attending.prev}
                    />
                </div>

                <div className={classNames("events-tab-content", activeTab === "finished" && "active")} id="finished-events">
                    <div className="cards-grid">
                        {isLoading ? (
                            <PageStatus compact message={t("admin.dashboard.loading", { defaultValue: "Cargando..." })} />
                        ) : finished.content.length === 0 ? (
                            <div className="empty-state">
                                <div className="empty-icon">
                                    <svg xmlns="http://www.w3.org/2000/svg" className="empty-svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                                    </svg>
                                </div>
                                <p className="empty-message">{t("profile.no.finished.events")}</p>
                                <Link to="/events" className="empty-action-btn">
                                    {t("profile.explore.events")}
                                </Link>
                            </div>
                        ) : (
                            finished.content.map((event) => (
                                <EventCard key={event.id} event={event} />
                            ))
                        )}
                    </div>
                    <Pagination
                        totalPages={finished.totalPages}
                        currentPage={finished.currentPage}
                        pageSize={finished.pageSize}
                        onPageChange={onFinishedPageChange}
                        previousLabel={t("pagination.prev")}
                        nextLabel={t("pagination.next")}
                        firstPage={finished.first}
                        lastPage={finished.last}
                        nextPage={finished.next}
                        prevPage={finished.prev}
                        />
                </div>
            </div>
        </div>
    );
}
