import { apiErrorMessage } from "@/lib/api/client";
import { useState } from "react";
import { useQueryClient } from "@tanstack/react-query";
import { Link, useParams } from "react-router-dom";
import { useI18n } from "@/lib/i18n";
import { isAdmin } from "@/lib/auth/auth";
import ForbiddenPage from "@/pages/errors/ForbiddenPage";
import { useAdminUserDetailData } from "@/hooks/useAdminDetailData";
import { updateUserBlocked } from "@/lib/api/users";
import PageStatus from "@/components/ui/PageStatus";
import AvatarFallbackIcon from "@/components/ui/AvatarFallbackIcon";

export default function UserDetailPage() {
    const { t } = useI18n();
    const queryClient = useQueryClient();
    const { id } = useParams();
    const { data: user, isLoading, isError } = useAdminUserDetailData({ id });
    const [modalOpen, setModalOpen] = useState(false);
    const [actionError, setActionError] = useState<string | null>(null);
    const [actionSubmitting, setActionSubmitting] = useState(false);

    if (!isAdmin()) {
        return <ForbiddenPage />;
    }

    if (isLoading) {
        return <PageStatus className="entity-detail-page" message={t("admin.dashboard.loading", { defaultValue: "Cargando..." })} />;
    }

    if (isError) {
        return <PageStatus className="entity-detail-page" variant="error" message={t("admin.dashboard.error", { defaultValue: "Error cargando datos." })} />;
    }

    if (!user) {
        return <PageStatus className="entity-detail-page" variant="error" message={t("admin.dashboard.error", { defaultValue: "Error cargando datos." })} />;
    }

    return (
        <div className="entity-detail-page user-detail-page">
            <div className="container">
                <div className="detail-container">
                    <div className="featured-journey-card">
                        <div className="journey-card-content">
                            <div className="journey-card-header">
                                <h1 className="journey-card-title">
                                    {user.firstname} {user.lastname}
                                </h1>
                                <p className="journey-card-subtitle">{user.username}</p>
                            </div>

                            <div className="detail-content">
                                <h2 className="section-title-landing">{t("user.detail.information")}</h2>

                                <div className="features-grid">
                                    <div className="feature-card">
                                        <h3 className="feature-title">{t("user.detail.email")}</h3>
                                        <p className="feature-description">{user.email}</p>
                                    </div>

                                    <div className="feature-card">
                                        <h3 className="feature-title">{t("user.detail.firstname")}</h3>
                                        <p className="feature-description">{user.firstname}</p>
                                    </div>

                                    <div className="feature-card">
                                        <h3 className="feature-title">{t("user.detail.lastname")}</h3>
                                        <p className="feature-description">{user.lastname}</p>
                                    </div>

                                    <div className="feature-card">
                                        <h3 className="feature-title">{t("user.detail.university")}</h3>
                                        <p className="feature-description">{user.university?.name ?? ""}</p>
                                    </div>

                                    <div className="feature-card">
                                        <h3 className="feature-title">{t("user.detail.career")}</h3>
                                        <p className="feature-description">{user.career?.name ?? ""}</p>
                                    </div>

                                    <div className="feature-card">
                                        <h3 className="feature-title">{t("user.detail.language")}</h3>
                                        <p className="feature-description">{user.locale}</p>
                                    </div>

                                    <div className="feature-card">
                                        <h3 className="feature-title">{t("user.detail.profilePicture")}</h3>
                                        <div className="profile-picture-container">
                                            {user.profilePictureUrl ? (
                                                <img
                                                    src={user.profilePictureUrl}
                                                    alt={t("user.detail.profilePicture.alt")}
                                                    className="profile-picture"
                                                />
                                            ) : (
                                                <div className="profile-picture-placeholder" aria-hidden="true">
                                                    <AvatarFallbackIcon size={48} />
                                                </div>
                                            )}
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div className="detail-actions">
                                <Link to="/admin/users" className="btn-text">
                                    {t("users.back")}
                                </Link>
                                <button
                                    type="button"
                                    className={`cta-button ${user.blocked ? "primary" : "delete-button"}`}
                                    onClick={() => setModalOpen(true)}
                                >
                                    {user.blocked ? t("user.detail.unblock") : t("user.detail.block")}
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            {modalOpen && (
                <div
                    className="modal"
                    role="dialog"
                    aria-modal="true"
                    onClick={(event) => {
                        if (event.target === event.currentTarget) {
                            setModalOpen(false);
                        }
                    }}
                >
                    <div className="modal-content">
                        <div className="modal-header">
                            <h2>
                                {user.blocked ? t("user.unblock.confirm.title") : t("user.block.confirm.title")}
                            </h2>
                            <button type="button" className="close-modal" aria-label="Close" onClick={() => setModalOpen(false)}>
                                &times;
                            </button>
                        </div>
                        <div className="modal-body">
                            <p>
                                {user.blocked
                                    ? t("user.unblock.confirm.message", { values: { 0: user.firstname } })
                                    : t("user.block.confirm.message", { values: { 0: user.firstname } })}
                            </p>
                            {!user.blocked && <p className="warning-text">{t("user.block.confirm.warning")}</p>}
                        </div>
                        <div className="modal-footer">
                            <button type="button" className="cta-button secondary" onClick={() => setModalOpen(false)}>
                                {t("user.block.cancel")}
                            </button>
                            <button
                                type="button"
                                className={`cta-button ${user.blocked ? "primary" : "delete-button"}`}
                                disabled={actionSubmitting}
                                onClick={async () => {
                                    if (!id) {
                                        setActionError(t("admin.dashboard.error", { defaultValue: "Error cargando datos." }));
                                        return;
                                    }
                                    setActionSubmitting(true);
                                    setActionError(null);
                                    try {
                                        await updateUserBlocked(Number(id), !user.blocked);
                                        queryClient.invalidateQueries({ queryKey: ["adminUserDetail", id] });
                                        setModalOpen(false);
                                    } catch (error) {
                                        console.error("Failed to update user status", error);
                                        setActionError(apiErrorMessage(error, t("admin.dashboard.error", { defaultValue: "Error cargando datos." })));
                                        setActionSubmitting(false);
                                    }
                                }}
                            >
                                {user.blocked ? t("user.unblock.confirm") : t("user.block.confirm")}
                            </button>
                        </div>
                        {actionError && <p className="error-message">{actionError}</p>}
                    </div>
                </div>
            )}
        </div>
    );
}
