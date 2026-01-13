import { useNavigate } from "react-router-dom";
import { useI18n } from "@/lib/i18n";
import type { AdminReport } from "@/mocks/adminDashboard.mock";
import type { PagedResult } from "@/hooks/useAdminDashboardData";
import AdminPagination from "./AdminPagination";
import AdminTabHeader from "./AdminTabHeader";
import ClickableRow from "./ClickableRow";

interface ReportsTabProps {
    data: PagedResult<AdminReport>;
    searchValue: string;
    onSearchChange: (value: string) => void;
    onSearchSubmit: () => void;
    onPageChange: (page: number) => void;
    isLoading: boolean;
    isError: boolean;
    searchIconSrc: string;
}

const getReasonLabel = (reason: string, t: (key: string, options?: { defaultValue?: string }) => string) => {
    const reasonKey = {
        SPAM: "report.reason.spam",
        HARRASMENT: "report.reason.harassment",
        INAPPROPRIATE_CONTENT: "report.reason.inappropriate",
        MISINFORMATION: "report.reason.misinformation",
        HATE_SPEECH: "report.reason.hate_speech",
        VIOLENCE: "report.reason.violence",
        OTHER: "report.reason.other",
    }[reason];

    return reasonKey ? t(reasonKey) : reason;
};

const getStatusConfig = (status: string, t: (key: string, options?: { defaultValue?: string }) => string) => {
    switch (status) {
        case "PENDING":
            return { label: t("report.status.pending"), className: "status-pending", progress: 25, color: "#f59e0b" };
        case "UNDER_REVIEW":
            return {
                label: t("report.status.under_review"),
                className: "status-under-review",
                progress: 60,
                color: "#3b82f6",
            };
        case "RESOLVED":
            return { label: t("report.status.resolved"), className: "status-resolved", progress: 100, color: "#10b981" };
        case "DISMISSED":
            return { label: t("report.status.dismissed"), className: "status-dismissed", progress: 100, color: "#6b7280" };
        default:
            return { label: status, className: undefined, progress: 0, color: "#e5e7eb" };
    }
};

export default function ReportsTab({
    data,
    searchValue,
    onSearchChange,
    onSearchSubmit,
    onPageChange,
    isLoading,
    isError,
    searchIconSrc,
}: ReportsTabProps) {
    const navigate = useNavigate();
    const { t } = useI18n();
    const isEmpty = !isLoading && !isError && data.content.length === 0;
    const loadingLabel = t("admin.dashboard.loading", { defaultValue: "Cargando datos..." });
    const errorLabel = t("admin.dashboard.error", { defaultValue: "No se pudieron cargar los datos." });

    return (
        <div className="tab-content active" id="reports-tab">
            <AdminTabHeader
                title={t("admin.manage.reports")}
                searchPlaceholder={t("admin.search.reports")}
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
                            <th>{t("admin.column.reportedUser")}</th>
                            <th>{t("admin.column.reportingUser")}</th>
                            <th>{t("admin.column.description")}</th>
                            <th>{t("admin.column.reason")}</th>
                            <th>{t("admin.column.status")}</th>
                        </tr>
                    </thead>
                    <tbody>
                        {data.content.map((report) => {
                            const status = getStatusConfig(report.status, t);
                            return (
                                <ClickableRow key={report.id} onClick={() => navigate(`/reports/${report.id}`)}>
                                    <td>{report.reportedUser.username}</td>
                                    <td>{report.reportingUser.username}</td>
                                    <td>{report.description}</td>
                                    <td>{getReasonLabel(report.reason, t)}</td>
                                    <td>
                                        <div className="report-status">
                                            <span className={`status-badge ${status.className ?? ""}`.trim()}>
                                                {status.label}
                                            </span>
                                            <div className="status-progress">
                                                <div
                                                    className="status-progress-fill"
                                                    style={{ width: `${status.progress}%`, backgroundColor: status.color }}
                                                />
                                            </div>
                                        </div>
                                    </td>
                                </ClickableRow>
                            );
                        })}
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
