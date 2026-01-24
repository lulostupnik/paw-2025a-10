import { Link, useNavigate } from "react-router-dom";
import { useI18n } from "@/lib/i18n";
import type { AdminCity, PagedResult } from "@/types/admin";
import AdminPagination from "./AdminPagination";
import AdminTabHeader from "./AdminTabHeader";
import ClickableRow from "./ClickableRow";

interface CitiesTabProps {
    data: PagedResult<AdminCity>;
    searchValue: string;
    onSearchChange: (value: string) => void;
    onSearchSubmit: (value: string) => void;
    onPageChange: (page: number) => void;
    isLoading: boolean;
    isError: boolean;
    plusIconSrc: string;
}

export default function CitiesTab({
    data,
    searchValue,
    onSearchChange,
    onSearchSubmit,
    onPageChange,
    isLoading,
    isError,
    plusIconSrc,
}: CitiesTabProps) {
    const navigate = useNavigate();
    const { t } = useI18n();
    const isEmpty = !isLoading && !isError && data.content.length === 0;
    const loadingLabel = t("admin.dashboard.loading", { defaultValue: "Cargando datos..." });
    const errorLabel = t("admin.dashboard.error", { defaultValue: "No se pudieron cargar los datos." });

    return (
        <div className="tab-content active" id="cities-tab">
            <AdminTabHeader
                title={t("admin.manage.cities")}
                searchPlaceholder={t("admin.search.cities")}
                searchValue={searchValue}
                searchButtonLabel={t("admin.search.button")}
                onSearchChange={onSearchChange}
                onSearchSubmit={onSearchSubmit}
                actions={
                    <Link to="/cities/create" className="btn btn-primary btn-with-icon">
                        <img src={plusIconSrc} alt={t("cities.create.button")} className="btn-icon" />
                        {t("cities.create.button")}
                    </Link>
                }
            />
            <div className="table-container">
                <table className="data-table">
                    <thead>
                        <tr>
                            <th>{t("admin.column.name")}</th>
                            <th>{t("admin.column.country")}</th>
                        </tr>
                    </thead>
                    <tbody>
                        {data.content.map((city) => (
                            <ClickableRow
                                key={city.id}
                                onClick={() => {
                                    // TODO: wire to the real city detail route.
                                    navigate(`/cities/${city.id}`);
                                }}
                            >
                                <td>{city.name}</td>
                                <td>{city.country}</td>
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
