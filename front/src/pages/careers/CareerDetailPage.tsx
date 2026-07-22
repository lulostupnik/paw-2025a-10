import { apiErrorMessage } from "@/lib/api/client";
import { useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import { useQueryClient } from "@tanstack/react-query";
import { useI18n } from "@/lib/i18n";
import { isAdmin } from "@/lib/auth/auth";
import ForbiddenPage from "@/pages/errors/ForbiddenPage";
import { useAdminCareerDetailData } from "@/hooks/useAdminDetailData";
import { deleteCareer } from "@/lib/api/careers";
import PageStatus from "@/components/ui/PageStatus";
import { useToast } from "@/components/ui/ToastProvider";
import { invalidateAdminEntityQueries } from "@/lib/api/queryInvalidation";

export default function CareerDetailPage() {
    const { t } = useI18n();
    const navigate = useNavigate();
    const queryClient = useQueryClient();
    const { showToast } = useToast();
    const { id } = useParams();
    const { data: career, isLoading, isError } = useAdminCareerDetailData({ id });
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

    if (!career) {
        return <PageStatus className="entity-detail-page" variant="error" message={t("admin.dashboard.error", { defaultValue: "Error cargando datos." })} />;
    }

    return (
        <div className="entity-detail-page career-detail-page">
            <div className="container">
                <div className="detail-container">
                    <div className="featured-journey-card">
                        <div className="journey-card-content">
                            <div className="journey-card-header">
                                <h1 className="journey-card-title">{career.name}</h1>
                            </div>

                            <div className="detail-content">
                                <h2 className="section-title-landing">{t("career.detail.information")}</h2>

                                <div className="features-grid">
                                    <div className="feature-card">
                                        <h3 className="feature-title">{t("career.detail.name")}</h3>
                                        <p className="feature-description">{career.name}</p>
                                    </div>
                                </div>
                            </div>

                            <div className="detail-actions">
                                <Link to="/admin/careers" className="btn-text">
                                    {t("back")}
                                </Link>
                                <div className="hero-cta">
                                    <Link to={`/careers/${id}/edit`} className="cta-button primary">
                                        {t("career.detail.edit")}
                                    </Link>
                                    <button type="button" className="cta-button delete-button" onClick={() => setModalOpen(true)}>
                                        {t("career.detail.delete")}
                                    </button>
                                </div>
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
                            <h2>{t("career.delete.confirm.title")}</h2>
                            <button type="button" className="close-modal" aria-label={t("common.close")} onClick={() => setModalOpen(false)}>
                                &times;
                            </button>
                        </div>
                        <div className="modal-body">
                            <p>{t("career.delete.confirm.message")}</p>
                            <p className="warning-text">{t("career.delete.confirm.warning")}</p>
                        </div>
                        <div className="modal-footer">
                            <button type="button" className="cta-button secondary" onClick={() => setModalOpen(false)}>
                                {t("career.delete.cancel")}
                            </button>
                            <button
                                type="button"
                                className="cta-button delete-button"
                                disabled={actionSubmitting}
                                onClick={async () => {
                                    if (!id) {
                                        setActionError(t("admin.dashboard.error", { defaultValue: "Error cargando datos." }));
                                        return;
                                    }
                                    setActionSubmitting(true);
                                    setActionError(null);
                                    try {
                                        await deleteCareer(id);
                                        await invalidateAdminEntityQueries(queryClient, "career", id);
                                        showToast(t("admin.toast.deleted"), { variant: "success" });
                                        navigate("/admin/careers");
                                    } catch (error) {
                                        console.error("Failed to delete career", error);
                                        setActionError(apiErrorMessage(error, t("admin.dashboard.error", { defaultValue: "Error cargando datos." })));
                                        setActionSubmitting(false);
                                    }
                                }}
                            >
                                {t("career.delete.confirm")}
                            </button>
                        </div>
                        {actionError && <p className="error-message">{actionError}</p>}
                    </div>
                </div>
            )}
        </div>
    );
}
