import { useNavigate } from "react-router-dom";
import { useI18n } from "@/lib/i18n";
import type { AdminEvent } from "@/mocks/adminDashboard.mock";
import type { PagedResult } from "@/hooks/useAdminDashboardData";
import AdminPagination from "./AdminPagination";
import AdminTabHeader from "./AdminTabHeader";
import ClickableRow from "./ClickableRow";

interface EventsTabProps {
    data: PagedResult<AdminEvent>;
    searchValue: string;
    onSearchChange: (value: string) => void;
    onSearchSubmit: () => void;
    onPageChange: (page: number) => void;
    isLoading: boolean;
    isError: boolean;
    searchIconSrc: string;
}

const getProgressPercent = (count: number, limit?: number | null) => {
    if (!limit || limit <= 0) {
        return 0;
    }

    return Math.min(100, Math.round((count / limit) * 100));
};

export default function EventsTab({
    data,
    searchValue,
    onSearchChange,
    onSearchSubmit,
    onPageChange,
    isLoading,
    isError,
    searchIconSrc,
}: EventsTabProps) {
    const navigate = useNavigate();
    const { t } = useI18n();
    const isEmpty = !isLoading && !isError && data.content.length === 0;
    const loadingLabel = t("admin.dashboard.loading", { defaultValue: "Cargando datos..." });
    const errorLabel = t("admin.dashboard.error", { defaultValue: "No se pudieron cargar los datos." });

    return (
        <div className="tab-content active" id="events-tab">
            <AdminTabHeader
                title={t("admin.manage.events")}
                searchPlaceholder={t("admin.search.events")}
                searchValue={searchValue}
                searchButtonLabel={t("admin.search.button")}
                onSearchChange={onSearchChange}
                onSearchSubmit={onSearchSubmit}
                searchIconSrc={searchIconSrc}
            />
            <div className="table-container">
                <table className="data-table">
                    <thead>
                        <tr>
                            <th>{t("admin.column.title")}</th>
                            <th>{t("admin.column.organizer")}</th>
                            <th>{t("admin.column.location")}</th>
                            <th>{t("admin.column.date")}</th>
                            <th>{t("admin.column.attendees")}</th>
                        </tr>
                    </thead>
                    <tbody>
                        {data.content.map((event) => (
                            <ClickableRow key={event.id} onClick={() => navigate(`/events/${event.id}`)}>
                                <td>{event.title}</td>
                                <td>{event.user.username}</td>
                                <td>{event.city}</td>
                                <td>{event.date}</td>
                                <td>
                                    {event.attendeesLimit && event.attendeesLimit > 0 ? (
                                        <div className="attendee-progress">
                                            <span className="attendee-count">
                                                {event.attendeesCount}/{event.attendeesLimit}
                                            </span>
                                            <div className="progress-bar">
                                                <div
                                                    className="progress-fill"
                                                    style={{ width: `${getProgressPercent(event.attendeesCount, event.attendeesLimit)}%` }}
                                                />
                                            </div>
                                        </div>
                                    ) : (
                                        <span className="unlimited-attendees">
                                            {t("admin.unlimited.attendees")}
                                        </span>
                                    )}
                                </td>
                            </ClickableRow>
                        ))}
                    </tbody>
                </table>

                {isLoading && <div className="no-results">{loadingLabel}</div>}
                {isError && <div className="no-results">{errorLabel}</div>}
                {isEmpty && <div className="no-results">{t("admin.no.results")}</div>}

                <AdminPagination
                    totalPages={data.totalPages}
                    currentPage={data.currentPage}
                    pageSize={data.pageSize}
                    onPageChange={onPageChange}
                    previousLabel={t("pagination.prev")}
                    nextLabel={t("pagination.next")}
                />
            </div>
        </div>
    );
}
