import { apiErrorMessage } from "@/lib/api/client";
import { useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import { useQueryClient } from "@tanstack/react-query";
import { useI18n } from "@/lib/i18n";
import { isAdmin } from "@/lib/auth/auth";
import ForbiddenPage from "@/pages/errors/ForbiddenPage";
import { useAdminUniversityDetailData } from "@/hooks/useAdminDetailData";
import { deleteUniversity } from "@/lib/api/universities";
import PageStatus from "@/components/ui/PageStatus";
import { useToast } from "@/components/ui/ToastProvider";
import { invalidateAdminEntityQueries } from "@/lib/api/queryInvalidation";

export default function UniversityDetailPage() {
    const { t } = useI18n();
    const navigate = useNavigate();
    const queryClient = useQueryClient();
    const { showToast } = useToast();
    const { id } = useParams();
    const { data: university, isLoading, isError } = useAdminUniversityDetailData({ id });
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

    if (!university) {
        return <PageStatus className="entity-detail-page" variant="error" message={t("admin.dashboard.error", { defaultValue: "Error cargando datos." })} />;
    }

    return (
        <div className="entity-detail-page university-detail-page">
            <div className="container">
                <div className="detail-container">
                    <div className="featured-journey-card">
                        <div className="journey-card-content">
                            <div className="journey-card-header">
                                <h1 className="journey-card-title">{university.name}</h1>
                                <p className="journey-card-subtitle">{university.abbreviation}</p>
                            </div>

                            <div className="detail-content">
                                <h2 className="section-title-landing">{t("university.detail.information")}</h2>

                                <div className="features-grid">
                                    <div className="feature-card">
                                        <h3 className="feature-title">{t("university.detail.name")}</h3>
                                        <p className="feature-description">{university.name}</p>
                                    </div>

                                    <div className="feature-card">
                                        <h3 className="feature-title">{t("university.detail.abbreviation")}</h3>
                                        <p className="feature-description">{university.abbreviation}</p>
                                    </div>

                                    <div className="feature-card">
                                        <h3 className="feature-title">{t("university.detail.city")}</h3>
                                        <p className="feature-description">{university.city.name}</p>
                                    </div>
                                </div>
                            </div>

                            <div className="detail-actions">
                                <Link to="/admin/universities" className="btn-text">
                                    {t("university.back")}
                                </Link>
                                <div className="hero-cta">
                                    <Link to={`/universities/${id}/edit`} className="cta-button primary">
                                        {t("university.detail.edit")}
                                    </Link>
                                    <button type="button" className="cta-button delete-button" onClick={() => setModalOpen(true)}>
                                        {t("university.detail.delete")}
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
                            <h2>{t("university.delete.confirm.title")}</h2>
                            <button type="button" className="close-modal" aria-label={t("common.close")} onClick={() => setModalOpen(false)}>
                                &times;
                            </button>
                        </div>
                        <div className="modal-body">
                            <p>{t("university.delete.confirm.message")}</p>
                            <p className="warning-text">{t("university.delete.confirm.warning")}</p>
                        </div>
                        <div className="modal-footer">
                            <button type="button" className="cta-button secondary" onClick={() => setModalOpen(false)}>
                                {t("university.delete.cancel")}
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
                                        await deleteUniversity(id);
                                        await invalidateAdminEntityQueries(queryClient, "university", id);
                                        showToast(t("admin.toast.deleted"), { variant: "success" });
                                        navigate("/admin/universities");
                                    } catch (error) {
                                        console.error("Failed to delete university", error);
                                        setActionError(apiErrorMessage(error, t("admin.dashboard.error", { defaultValue: "Error cargando datos." })));
                                        setActionSubmitting(false);
                                    }
                                }}
                            >
                                {t("university.delete.confirm")}
                            </button>
                        </div>
                        {actionError && <p className="error-message">{actionError}</p>}
                    </div>
                </div>
            )}
        </div>
    );
}
