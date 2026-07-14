import { apiErrorMessage } from "@/lib/api/client";
import { useEffect, useId, useState, type MouseEvent } from "react";
import { useQuery } from "@tanstack/react-query";
import { useNavigate } from "react-router-dom";
import { classNames } from "@/lib/utils/classNames";
import { useI18n } from "@/lib/i18n";
import { searchCareers, searchInterests, searchUniversities, type CatalogOption, type CatalogSearchFn } from "@/lib/api/catalog";
import { EMPTY_ADMIN_USER_FILTERS, type AdminUser, type AdminUserFilters } from "@/types/admin";
import { updateUserBlocked } from "@/lib/api/users";
import Pagination from "../listing/Pagination";
import AdminTabHeader from "./AdminTabHeader";
import ClickableRow from "./ClickableRow";
import PageStatus from "@/components/ui/PageStatus";
import type { PageResult } from "@/types/pagination";

interface UsersTabProps {
    data: PageResult<AdminUser>;
    searchValue: string;
    onSearchChange: (value: string) => void;
    onSearchSubmit: (value: string) => void;
    onPageChange: (page: number | string) => void;
    isLoading: boolean;
    isError: boolean;
    blockIconSrc: string;
    unblockIconSrc: string;
    onRefresh: () => void;
    filters: AdminUserFilters;
    onApplyFilters: (filters: AdminUserFilters) => void;
    onResetFilters: () => void;
}

type UserAction = "block" | "unblock";

interface ModalState {
    user: AdminUser;
    action: UserAction;
}

export default function UsersTab({
    data,
    searchValue,
    onSearchChange,
    onSearchSubmit,
    onPageChange,
    isLoading,
    isError,
    blockIconSrc,
    unblockIconSrc,
    onRefresh,
    filters,
    onApplyFilters,
    onResetFilters,
}: UsersTabProps) {
    const navigate = useNavigate();
    const { t } = useI18n();
    const [modalState, setModalState] = useState<ModalState | null>(null);
    const [filtersOpen, setFiltersOpen] = useState(false);
    const [actionError, setActionError] = useState<string | null>(null);
    const [actionSubmitting, setActionSubmitting] = useState(false);
    const isEmpty = !isLoading && !isError && data.content.length === 0;
    const loadingLabel = t("admin.dashboard.loading", { defaultValue: "Cargando datos..." });
    const errorLabel = t("admin.dashboard.error", { defaultValue: "No se pudieron cargar los datos." });
    const hasActiveFilters =
        filters.blocked !== null ||
        Boolean(filters.universityId) ||
        Boolean(filters.careerId) ||
        Boolean(filters.interestId);

    const closeModal = () => {
        setModalState(null);
        setActionError(null);
        setActionSubmitting(false);
    };

    const handleActionClick = (event: MouseEvent<HTMLButtonElement>, user: AdminUser, action: UserAction) => {
        event.stopPropagation();
        setModalState({ user, action });
    };

    return (
        <div className="tab-content active" id="users-tab">
            <AdminTabHeader
                title={t("admin.manage.users")}
                searchPlaceholder={t("admin.search.users")}
                searchValue={searchValue}
                searchButtonLabel={t("admin.search.button")}
                onSearchChange={onSearchChange}
                onSearchSubmit={onSearchSubmit}
                actions={
                    <button
                        type="button"
                        className="btn-secondary"
                        onClick={() => setFiltersOpen(true)}
                    >
                        {hasActiveFilters
                            ? t("admin.users.filters.active", { defaultValue: "Filters applied" })
                            : t("listing.filters")}
                    </button>
                }
            />
            <div className="table-container">
                <table className="data-table">
                    <thead>
                        <tr>
                            <th>{t("admin.column.name")}</th>
                            <th>{t("admin.column.email")}</th>
                            <th>{t("admin.column.university")}</th>
                            <th>{t("admin.column.actions")}</th>
                        </tr>
                    </thead>
                    <tbody>
                        {data.content.map((user) => (
                            <ClickableRow
                                key={user.id}
                                onClick={() => {
                                    navigate(`/users/${user.id}`);
                                }}
                            >
                                <td>{user.firstname}</td>
                                <td>{user.email}</td>
                                <td>{user.university}</td>
                                <td>
                                    {!user.blocked ? (
                                        <button
                                            type="button"
                                            className="btn-danger btn-with-icon block-user-btn"
                                            onClick={(event) => handleActionClick(event, user, "block")}
                                            aria-label={t("user.block")}
                                        >
                                            <img className="btn-icon" alt={t("user.block")} src={blockIconSrc} />
                                        </button>
                                    ) : (
                                        <button
                                            type="button"
                                            className="btn-primary btn-with-icon block-user-btn"
                                            onClick={(event) => handleActionClick(event, user, "unblock")}
                                            aria-label={t("user.unblock")}
                                        >
                                            <img className="btn-icon" alt={t("user.unblock")} src={unblockIconSrc} />
                                        </button>
                                    )}
                                </td>
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
                    nextPage={data.next}
                    lastPage={data.last}
                    prevPage={data.prev}
                    firstPage={data.first}
                />
            </div>

            {modalState && (
                <div
                    className="modal"
                    role="dialog"
                    aria-modal="true"
                    onClick={(event) => {
                        if (event.target === event.currentTarget) {
                            closeModal();
                        }
                    }}
                >
                    <div className="modal-content">
                        <div className="modal-header">
                            <h2>
                                {modalState.action === "block"
                                    ? t("user.block.confirm.title")
                                    : t("user.unblock.confirm.title")}
                            </h2>
                            <button type="button" className="close-modal" aria-label={t("common.close")} onClick={closeModal}>
                                &times;
                            </button>
                        </div>
                        <div className="modal-body">
                            <p>
                                {modalState.action === "block"
                                    ? t("user.block.confirm.message", { values: { 0: modalState.user.firstname } })
                                    : t("user.unblock.confirm.message", { values: { 0: modalState.user.firstname } })}
                            </p>
                            {modalState.action === "block" && (
                                <p className="warning-text">{t("user.block.confirm.warning")}</p>
                            )}
                        </div>
                        <div className="modal-footer">
                            <button type="button" className="cta-button secondary" onClick={closeModal}>
                                {t("user.block.cancel")}
                            </button>
                            <button
                                type="button"
                                className={classNames(
                                    "cta-button",
                                    modalState.action === "block" ? "delete-button" : "primary"
                                )}
                                disabled={actionSubmitting}
                                onClick={async () => {
                                    setActionError(null);
                                    setActionSubmitting(true);
                                    try {
                                        await updateUserBlocked(modalState.user.id, modalState.action === "block");
                                        onRefresh();
                                        closeModal();
                                    } catch (error) {
                                        console.error("Failed to update user status", error);
                                        setActionError(apiErrorMessage(error, t("admin.dashboard.error", { defaultValue: "No se pudieron cargar los datos." })));
                                        setActionSubmitting(false);
                                    }
                                }}
                            >
                                {modalState.action === "block" ? t("user.block.confirm") : t("user.unblock.confirm")}
                            </button>
                        </div>
                        {actionError && <p className="error-message">{actionError}</p>}
                    </div>
                </div>
            )}

            {filtersOpen && (
                <UsersFiltersModal
                    filters={filters}
                    onApply={(nextFilters) => {
                        onApplyFilters(nextFilters);
                        setFiltersOpen(false);
                    }}
                    onReset={() => {
                        onResetFilters();
                        setFiltersOpen(false);
                    }}
                    onClose={() => setFiltersOpen(false)}
                />
            )}
        </div>
    );
}

interface UsersFiltersModalProps {
    filters: AdminUserFilters;
    onApply: (filters: AdminUserFilters) => void;
    onReset: () => void;
    onClose: () => void;
}

// El modal se monta recién al abrirse, así el borrador de filtros arranca desde
// los filtros vigentes sin sincronizarlo con un efecto.
function UsersFiltersModal({ filters, onApply, onReset, onClose }: UsersFiltersModalProps) {
    const { t } = useI18n();
    const [draftFilters, setDraftFilters] = useState<AdminUserFilters>(filters);

    return (
        <div
            className="modal"
            role="dialog"
            aria-modal="true"
            onClick={(event) => {
                if (event.target === event.currentTarget) {
                    onClose();
                }
            }}
        >
            <div className="modal-content">
                <div className="modal-header">
                    <h2>{t("admin.users.filters.title", { defaultValue: "Filter users" })}</h2>
                    <button type="button" className="close-modal" aria-label={t("common.close")} onClick={onClose}>
                        &times;
                    </button>
                </div>
                <div className="modal-body">
                    <BlockedSelectField
                        label={t("admin.users.filters.blocked", { defaultValue: "Blocked status" })}
                        value={draftFilters.blocked}
                        onChange={(value) => setDraftFilters((prev) => ({ ...prev, blocked: value }))}
                    />
                    <AdminCatalogSelectField
                        label={t("admin.users.filters.university", { defaultValue: "University" })}
                        placeholder={t("admin.users.filters.university.placeholder", { defaultValue: "Search for a university..." })}
                        value={draftFilters.universityId && draftFilters.universityName ? { id: draftFilters.universityId, name: draftFilters.universityName } : null}
                        onChange={(option) =>
                            setDraftFilters((prev) => ({
                                ...prev,
                                universityId: option?.id ?? null,
                                universityName: option?.name ?? "",
                            }))
                        }
                        fetcher={searchUniversities}
                        catalogKey="admin-users-university"
                    />
                    <AdminCatalogSelectField
                        label={t("admin.users.filters.career", { defaultValue: "Career" })}
                        placeholder={t("admin.users.filters.career.placeholder", { defaultValue: "Search for a career..." })}
                        value={draftFilters.careerId && draftFilters.careerName ? { id: draftFilters.careerId, name: draftFilters.careerName } : null}
                        onChange={(option) =>
                            setDraftFilters((prev) => ({
                                ...prev,
                                careerId: option?.id ?? null,
                                careerName: option?.name ?? "",
                            }))
                        }
                        fetcher={searchCareers}
                        catalogKey="admin-users-career"
                    />
                    <AdminCatalogSelectField
                        label={t("admin.users.filters.interest", { defaultValue: "Interest" })}
                        placeholder={t("admin.users.filters.interest.placeholder", { defaultValue: "Search for an interest..." })}
                        value={draftFilters.interestId && draftFilters.interestName ? { id: draftFilters.interestId, name: draftFilters.interestName } : null}
                        onChange={(option) =>
                            setDraftFilters((prev) => ({
                                ...prev,
                                interestId: option?.id ?? null,
                                interestName: option?.name ?? "",
                            }))
                        }
                        fetcher={searchInterests}
                        catalogKey="admin-users-interest"
                    />
                </div>
                <div className="modal-footer">
                    <button
                        type="button"
                        className="cta-button secondary"
                        onClick={() => {
                            setDraftFilters(EMPTY_ADMIN_USER_FILTERS);
                            onReset();
                        }}
                    >
                        {t("listing.filters.reset", { defaultValue: "Reset" })}
                    </button>
                    <button type="button" className="cta-button primary" onClick={() => onApply(draftFilters)}>
                        {t("listing.filters.apply", { defaultValue: "Apply filters" })}
                    </button>
                </div>
            </div>
        </div>
    );
}

const useDebouncedValue = (value: string, delay = 250) => {
    const [debounced, setDebounced] = useState(value);

    useEffect(() => {
        const handle = window.setTimeout(() => setDebounced(value), delay);
        return () => window.clearTimeout(handle);
    }, [value, delay]);

    return debounced;
};

interface AdminCatalogSelectFieldProps {
    label: string;
    placeholder: string;
    value: CatalogOption | null;
    onChange: (option: CatalogOption | null) => void;
    fetcher: CatalogSearchFn;
    catalogKey: string;
}

function AdminCatalogSelectField({
    label,
    placeholder,
    value,
    onChange,
    fetcher,
    catalogKey,
}: AdminCatalogSelectFieldProps) {
    const { t } = useI18n();
    const inputId = useId();
    const [query, setQuery] = useState(value?.name ?? "");
    const [open, setOpen] = useState(false);
    const debouncedQuery = useDebouncedValue(query);
    // El texto tipeado es un borrador de la opción elegida: si la selección
    // cambia desde afuera, el borrador vuelve a reflejarla.
    const [lastValue, setLastValue] = useState(value);
    if (lastValue !== value) {
        setLastValue(value);
        setQuery(value?.name ?? "");
    }

    const { data: options = [], isLoading } = useQuery<CatalogOption[]>({
        queryKey: ["admin", "users", "catalog", catalogKey, debouncedQuery],
        queryFn: ({ signal }) => fetcher(debouncedQuery, signal),
        enabled: open,
        staleTime: 60_000,
    });

    return (
        <div className={classNames("form-field", "autocomplete-field")}>
            <label className="input-label" htmlFor={inputId}>
                {label}
            </label>
            {value ? (
                <div className="selected-option">
                    <div>
                        <p className="selected-option__value">{value.name}</p>
                    </div>
                    <button
                        type="button"
                        className="selected-option__action"
                        onClick={() => {
                            onChange(null);
                            setQuery("");
                            setOpen(true);
                        }}
                    >
                        {t("register.autocomplete.change")}
                    </button>
                </div>
            ) : (
                <>
                    <div className="input-with-addon">
                        <input
                            id={inputId}
                            className="input-control"
                            value={query}
                            placeholder={placeholder}
                            onFocus={() => setOpen(true)}
                            onChange={(event) => {
                                setQuery(event.target.value);
                                if (!open) {
                                    setOpen(true);
                                }
                            }}
                            autoComplete="off"
                            spellCheck="false"
                        />
                        <button
                            type="button"
                            className="input-addon"
                            onClick={() => setOpen((prev) => !prev)}
                            aria-label={t("listing.filters.autocomplete.toggle")}
                        >
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth={2}>
                                <path strokeLinecap="round" strokeLinejoin="round" d="M19 9l-7 7-7-7" />
                            </svg>
                        </button>
                    </div>
                    <div className={classNames("autocomplete-panel", open && "is-open")} role="listbox" aria-label={label}>
                        {isLoading && <p className="autocomplete-status">{t("listing.filters.autocomplete.loading")}</p>}
                        {!isLoading && options.length === 0 && (
                            <p className="autocomplete-status">{t("listing.filters.autocomplete.empty")}</p>
                        )}
                        {!isLoading &&
                            options.map((option) => (
                                <button
                                    type="button"
                                    key={option.id}
                                    className="autocomplete-option"
                                    onMouseDown={(event) => event.preventDefault()}
                                    onClick={() => {
                                        onChange(option);
                                        setOpen(false);
                                    }}
                                >
                                    {option.name}
                                </button>
                            ))}
                    </div>
                </>
            )}
        </div>
    );
}

interface BlockedSelectFieldProps {
    label: string;
    value: boolean | null;
    onChange: (value: boolean | null) => void;
}

function BlockedSelectField({ label, value, onChange }: BlockedSelectFieldProps) {
    const { t } = useI18n();
    const inputId = useId();
    return (
        <div className="form-field">
            <label className="input-label" htmlFor={inputId}>
                {label}
            </label>
            <select
                id={inputId}
                className="input-control"
                value={value === null ? "all" : value ? "blocked" : "active"}
                onChange={(event) => {
                    if (event.target.value === "blocked") {
                        onChange(true);
                    } else if (event.target.value === "active") {
                        onChange(false);
                    } else {
                        onChange(null);
                    }
                }}
            >
                <option value="all">{t("admin.users.filter.all")}</option>
                <option value="active">{t("admin.users.filter.active")}</option>
                <option value="blocked">{t("admin.users.filter.blocked")}</option>
            </select>
        </div>
    );
}
