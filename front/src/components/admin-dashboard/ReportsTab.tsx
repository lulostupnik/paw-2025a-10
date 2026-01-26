import { useNavigate } from "react-router-dom";
import { useI18n } from "@/lib/i18n";
import type { ReportListItem } from "@/lib/api/reports";
import Pagination from "../listing/Pagination";
import AdminTabHeader from "./AdminTabHeader";
import ClickableRow from "./ClickableRow";
import type { PageResult } from "@/types/pagination";

interface ReportsTabProps {
    data: PageResult<ReportListItem>;
    searchValue: string;
    onSearchChange: (value: string) => void;
    onSearchSubmit: (value: string) => void;
    onPageChange: (page: number) => void;
    isLoading: boolean;
    isError: boolean;
}

const getReasonLabel = (reason: string, t: (key: string, options?: { defaultValue?: string }) => string) => {
    const reasonKey = {
        SPAM: "report.reason.spam",
        HARASSMENT: "report.reason.harassment",
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

const getTypeLabel = (type: ReportListItem["contentType"], t: (key: string, options?: { defaultValue?: string }) => string) => {
    switch (type) {
        case "journey":
            return t("report.type.journey");
        case "event":
            return t("report.type.event");
        case "journeyResponse":
            return t("report.type.journey.comment");
        case "eventResponse":
            return t("report.type.event.comment");
        default:
            return t("report.detail.type", { defaultValue: "Reporte" });
    }
};

const getTypeClass = (type: ReportListItem["contentType"]) => {
    switch (type) {
        case "journey":
            return "report-type-journey";
        case "event":
            return "report-type-event";
        case "journeyResponse":
        case "eventResponse":
            return "report-type-comment";
        default:
            return "report-type-default";
    }
};

const formatDate = (value: string | null | undefined, locale: string) => {
    if (!value) {
        return "—";
    }
    const date = new Date(value);
    if (Number.isNaN(date.getTime())) {
        return value;
    }
    return new Intl.DateTimeFormat(locale, { dateStyle: "medium" }).format(date);
};

const normalizeDescription = (value: string) =>
    value
        .replace(/(Evento|Viaje)([A-Za-zÁÉÍÓÚÑáéíóúñ])/g, "$1 $2")
        .replace(/\s+/g, " ")
        .trim();

export default function ReportsTab({
    data,
    searchValue,
    onSearchChange,
    onSearchSubmit,
    onPageChange,
    isLoading,
    isError,
}: ReportsTabProps) {
    const navigate = useNavigate();
    const { t, locale } = useI18n();
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
                            <th>{t("admin.column.date")}</th>
                        </tr>
                    </thead>
                    <tbody>
                        {data.content.map((report: ReportListItem) => {
                            const status = getStatusConfig(report.status, t);
                            const typeLabel = getTypeLabel(report.contentType, t);
                            const descriptionText = report.description?.trim()
                                ? normalizeDescription(report.description)
                                : t("report.no.additional.details");
                            return (
                                <ClickableRow key={report.id} onClick={() => navigate(`/reports/${report.id}`)}>
                                    <td>
                                        <div className="report-user">
                                            <span>@{report.reportedUser.username}</span>
                                            {report.reportedUser.blocked && (
                                                <span className="report-user__badge">{t("user.status.blocked")}</span>
                                            )}
                                        </div>
                                    </td>
                                    <td>
                                        <div className="report-user">
                                            <span>@{report.reportingUser.username}</span>
                                        </div>
                                    </td>
                                    <td>
                                        <div className="report-meta">
                                            <span className={`report-type-badge ${getTypeClass(report.contentType)}`}>
                                                {typeLabel}
                                            </span>
                                            <span className="report-description">
                                                {descriptionText}
                                            </span>
                                        </div>
                                    </td>
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
                                    <td>
                                        <span className="report-date">{formatDate(report.createdAt, locale)}</span>
                                    </td>
                                </ClickableRow>
                            );
                        })}
                    </tbody>
                </table>

                {isLoading && <div className="no-results">{loadingLabel}</div>}
                {isError && <div className="no-results">{errorLabel}</div>}
                {isEmpty && <div className="no-results">{t("admin.no.results")}</div>}

                <Pagination
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
