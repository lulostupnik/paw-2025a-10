import { useNavigate } from "react-router-dom";
import { useI18n } from "@/lib/i18n";
import type { AdminJourney } from "@/mocks/adminDashboard.mock";
import type { PagedResult } from "@/hooks/useAdminDashboardData";
import AdminPagination from "./AdminPagination";
import AdminTabHeader from "./AdminTabHeader";
import ClickableRow from "./ClickableRow";

interface JourneysTabProps {
    data: PagedResult<AdminJourney>;
    searchValue: string;
    onSearchChange: (value: string) => void;
    onSearchSubmit: (value: string) => void;
    onPageChange: (page: number) => void;
    isLoading: boolean;
    isError: boolean;
}

export default function JourneysTab({
    data,
    searchValue,
    onSearchChange,
    onSearchSubmit,
    onPageChange,
    isLoading,
    isError,
}: JourneysTabProps) {
    const navigate = useNavigate();
    const { t } = useI18n();
    const isEmpty = !isLoading && !isError && data.content.length === 0;
    const loadingLabel = t("admin.dashboard.loading", { defaultValue: "Cargando datos..." });
    const errorLabel = t("admin.dashboard.error", { defaultValue: "No se pudieron cargar los datos." });

    return (
        <div className="tab-content active" id="journeys-tab">
            <AdminTabHeader
                title={t("admin.manage.journeys")}
                searchPlaceholder={t("admin.search.journeys")}
                searchValue={searchValue}
                searchButtonLabel={t("admin.search.button")}
                onSearchChange={onSearchChange}
                onSearchSubmit={onSearchSubmit}
            />
            <div className="table-container">
                <table className="data-table">
                    <thead>
                        <tr>
                            <th>{t("admin.column.user")}</th>
                            <th>{t("admin.column.destination")}</th>
                            <th>{t("admin.column.university")}</th>
                            <th>{t("admin.column.start.date")}</th>
                            <th>{t("admin.column.end.date")}</th>
                        </tr>
                    </thead>
                    <tbody>
                        {data.content.map((journey) => (
                            <ClickableRow
                                key={journey.id}
                                onClick={() => navigate(`/journeys/${journey.id}`)}
                            >
                                <td>{journey.user.username}</td>
                                <td>{journey.destinationUniversity.city}</td>
                                <td>{journey.destinationUniversity.name}</td>
                                <td>{journey.startDate}</td>
                                <td>{journey.endDate}</td>
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
