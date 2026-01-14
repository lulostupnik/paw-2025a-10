import { useState } from "react";
import { Link, useParams } from "react-router-dom";
import { useI18n } from "@/lib/i18n";
import { isAdmin } from "@/lib/auth/auth";
import ForbiddenPage from "@/pages/errors/ForbiddenPage";
import { useAdminInterestDetailData } from "@/hooks/useAdminDetailData";
import type { AdminDetailScenario } from "@/mocks/adminDetail.mock";

const SCENARIO: AdminDetailScenario = "normal";

export default function InterestDetailPage() {
    const { t } = useI18n();
    const { id } = useParams();
    const { data: interest, isLoading, isError } = useAdminInterestDetailData({ scenario: SCENARIO, id });
    const [modalOpen, setModalOpen] = useState(false);

    if (!isAdmin()) {
        return <ForbiddenPage />;
    }

    if (isLoading) {
        return <div className="entity-detail-page">{t("admin.dashboard.loading", { defaultValue: "Cargando..." })}</div>;
    }

    if (isError) {
        return <div className="entity-detail-page">{t("admin.dashboard.error", { defaultValue: "Error cargando datos." })}</div>;
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
                                <Link to="/admin?tab=interests" className="btn-text">
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
                            <button type="button" className="close-modal" aria-label="Close" onClick={() => setModalOpen(false)}>
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
                                onClick={() => {
                                    // TODO: call delete interest endpoint.
                                    setModalOpen(false);
                                }}
                            >
                                {t("interest.delete.confirm")}
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}
