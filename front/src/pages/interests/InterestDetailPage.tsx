import { apiErrorMessage } from "@/lib/api/client";
import { useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useI18n } from "@/lib/i18n";
import { isAdmin } from "@/lib/auth/auth";
import ForbiddenPage from "@/pages/errors/ForbiddenPage";
import { useAdminInterestDetailData } from "@/hooks/useAdminDetailData";
import { deleteInterest } from "@/lib/api/interests";
import PageStatus from "@/components/ui/PageStatus";
import { useToast } from "@/components/ui/ToastProvider";
import { invalidateAdminEntityQueries } from "@/lib/api/queryInvalidation";

export default function InterestDetailPage() {
    const { t } = useI18n();
    const navigate = useNavigate();
    const { showToast } = useToast();
    const { id } = useParams();
    const { data: interest, isLoading, isError } = useAdminInterestDetailData({ id });
    const [modalOpen, setModalOpen] = useState(false);
    const [actionError, setActionError] = useState<string | null>(null);
    const queryClient = useQueryClient();

    const deleteInterestMutation = useMutation({
        mutationFn: (interestId: string) => deleteInterest(interestId),
        onSuccess: async (_data, interestId) => {
            await invalidateAdminEntityQueries(queryClient, "interest", interestId);
            showToast(t("admin.toast.deleted"), { variant: "success" });
            navigate("/admin/interests");
        },
        onError: (error) => {
            console.error("Failed to delete interest", error);
            setActionError(apiErrorMessage(error, t("admin.dashboard.error", { defaultValue: "Error cargando datos." })));
        },
    });

    if (!isAdmin()) {
        return <ForbiddenPage />;
    }

    if (isLoading) {
        return <PageStatus className="entity-detail-page" message={t("admin.dashboard.loading", { defaultValue: "Cargando..." })} />;
    }

    if (isError) {
        return <PageStatus className="entity-detail-page" variant="error" message={t("admin.dashboard.error", { defaultValue: "Error cargando datos." })} />;
    }

    if (!interest) {
        return <PageStatus className="entity-detail-page" variant="error" message={t("admin.dashboard.error", { defaultValue: "Error cargando datos." })} />;
    }

    return (
        <div className="entity-detail-page interest-detail-page">
            <div className="container">
                <div className="detail-container">
                    <div className="featured-journey-card">
                        <div className="journey-card-content">
                            <div className="journey-card-header">
                                <h1 className="journey-card-title">{interest.name}</h1>
                            </div>

                            <div className="detail-content">
                                <h2 className="section-title-landing">{t("interest.detail.information")}</h2>

                                <div className="features-grid">
                                    <div className="feature-card">
                                        <h3 className="feature-title">{t("interest.detail.name")}</h3>
                                        <p className="feature-description">{interest.name}</p>
                                    </div>
                                </div>
                            </div>

                            <div className="detail-actions">
                                <Link to="/admin/interests" className="btn-text">
                                    {t("back")}
                                </Link>
                                <div className="hero-cta">
                                    <Link to={`/interests/${id}/edit`} className="cta-button primary">
                                        {t("interest.detail.edit")}
                                    </Link>
                                    <button type="button" className="cta-button delete-button" onClick={() => setModalOpen(true)}>
                                        {t("interests.detail.delete")}
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
                            <h2>{t("interest.delete.confirm.title")}</h2>
                            <button type="button" className="close-modal" aria-label={t("common.close")} onClick={() => setModalOpen(false)}>
                                &times;
                            </button>
                        </div>
                        <div className="modal-body">
                            <p>{t("interest.delete.confirm.message")}</p>
                            <p className="warning-text">{t("interest.delete.confirm.warning")}</p>
                        </div>
                        <div className="modal-footer">
                            <button type="button" className="cta-button secondary" onClick={() => setModalOpen(false)}>
                                {t("interest.delete.cancel")}
                            </button>
                            <button
                                type="button"
                                className="cta-button delete-button"
                                disabled={deleteInterestMutation.isPending}
                                onClick={() => {
                                    if (!id) {
                                        setActionError(t("admin.dashboard.error", { defaultValue: "Error cargando datos." }));
                                        return;
                                    }
                                    setActionError(null);
                                    deleteInterestMutation.mutate(id);
                                }}
                            >
                                {t("interest.delete.confirm")}
                            </button>
                        </div>
                        {actionError && <p className="error-message">{actionError}</p>}
                    </div>
                </div>
            )}
        </div>
    );
}
