import { useState, type MouseEvent } from "react";
import { useNavigate } from "react-router-dom";
import { classNames } from "@/lib/utils/classNames";
import { useI18n } from "@/lib/i18n";
import type { AdminUser } from "@/types/admin";
import { updateUserBlocked } from "@/lib/api/users";
import Pagination from "../listing/Pagination";
import AdminTabHeader from "./AdminTabHeader";
import ClickableRow from "./ClickableRow";
import type { PageResult } from "@/types/pagination";

interface UsersTabProps {
    data: PageResult<AdminUser>;
    searchValue: string;
    onSearchChange: (value: string) => void;
    onSearchSubmit: (value: string) => void;
    onPageChange: (page: number) => void;
    isLoading: boolean;
    isError: boolean;
    blockIconSrc: string;
    unblockIconSrc: string;
    onRefresh: () => void;
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
}: UsersTabProps) {
    const navigate = useNavigate();
    const { t } = useI18n();
    const [modalState, setModalState] = useState<ModalState | null>(null);
    const [actionError, setActionError] = useState<string | null>(null);
    const [actionSubmitting, setActionSubmitting] = useState(false);
    const isEmpty = !isLoading && !isError && data.content.length === 0;
    const loadingLabel = t("admin.dashboard.loading", { defaultValue: "Cargando datos..." });
    const errorLabel = t("admin.dashboard.error", { defaultValue: "No se pudieron cargar los datos." });

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
                                    // TODO: wire to the real user detail route.
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
                            <button type="button" className="close-modal" aria-label="Close" onClick={closeModal}>
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
                                        setActionError(t("admin.dashboard.error", { defaultValue: "No se pudieron cargar los datos." }));
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
        </div>
    );
}
