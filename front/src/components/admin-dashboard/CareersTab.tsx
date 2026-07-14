import { Link, useNavigate } from "react-router-dom";
import { useI18n } from "@/lib/i18n";
import type { AdminCareer } from "@/types/admin";
import Pagination from "../listing/Pagination";
import AdminTabHeader from "./AdminTabHeader";
import ClickableRow from "./ClickableRow";
import type { PageResult } from "@/types/pagination";
import PageStatus from "@/components/ui/PageStatus";

interface CareersTabProps {
    data: PageResult<AdminCareer>;
    searchValue: string;
    onSearchChange: (value: string) => void;
    onSearchSubmit: (value: string) => void;
    onPageChange: (page: number | string) => void;
    isLoading: boolean;
    isError: boolean;
    plusIconSrc: string;
}

export default function CareersTab({
    data,
    searchValue,
    onSearchChange,
    onSearchSubmit,
    onPageChange,
    isLoading,
    isError,
    plusIconSrc,
}: CareersTabProps) {
    const navigate = useNavigate();
    const { t } = useI18n();
    const isEmpty = !isLoading && !isError && data.content.length === 0;
    const loadingLabel = t("admin.dashboard.loading", { defaultValue: "Cargando datos..." });
    const errorLabel = t("admin.dashboard.error", { defaultValue: "No se pudieron cargar los datos." });

    return (
        <div className="tab-content active" id="careers-tab">
            <AdminTabHeader
                title={t("admin.manage.careers")}
                searchPlaceholder={t("admin.search.careers")}
                searchValue={searchValue}
                searchButtonLabel={t("admin.search.button")}
                onSearchChange={onSearchChange}
                onSearchSubmit={onSearchSubmit}
                actions={
                    <Link to="/careers/create" className="btn btn-primary btn-with-icon">
                        <img src={plusIconSrc} alt={t("careers.create.button")} className="btn-icon" />
                        {t("careers.create.button")}
                    </Link>
                }
            />
            <div className="table-container">
                <table className="data-table">
                    <thead>
                        <tr>
                            <th>{t("admin.column.name")}</th>
                        </tr>
                    </thead>
                    <tbody>
                        {data.content.map((career) => (
                            <ClickableRow
                                key={career.id}
                                onClick={() => {
                                    navigate(`/careers/${career.id}`);
                                }}
                            >
                                <td>{career.name}</td>
                            </ClickableRow>
                        ))}
                    </tbody>
                </table>

                {isLoading && <PageStatus compact message={loadingLabel} />}
                {isError && <PageStatus compact variant="error" message={errorLabel} />}
                {isEmpty && <div className="no-results">{t("admin.no.results")}</div>}

                <Pagination
                    totalPages={data.totalPages}
                    currentPage={data.currentPage}
                    pageSize={data.pageSize}
                    onPageChange={onPageChange}
                    previousLabel={t("pagination.prev")}
                    nextLabel={t("pagination.next")}
                    firstPage={data.first}
                    lastPage={data.last}
                    nextPage={data.next}
                    prevPage={data.prev}
                />
            </div>
        </div>
    );
}
