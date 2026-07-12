import { Link, Navigate, useLocation } from "react-router-dom";
import StatusCard from "@/components/ui/StatusCard";
import { useI18n } from "@/lib/i18n";

export default function BlockedAccountPage() {
    const { t } = useI18n();
    const location = useLocation();
    const state = location.state as { blockedEmail?: string } | null;
    const email = typeof state?.blockedEmail === "string" ? state.blockedEmail : "";
    if (!email) {
        return <Navigate to="/login" replace />;
    }

    return (
        <div className="page-shell auth-page blocked-account-page">
            <header className="register-header">
                <p className="eyebrow">{t("blocked.access.denied")}</p>
                <h1>{t("blocked.account")}</h1>
                <p className="register-header__lead">{t("blocked.subtitle")}</p>
            </header>

            <StatusCard
                variant="error"
                title={t("blocked.title")}
                description={t("blocked.explanation")}
                actions={
                    <Link to="/" className="btn btn--ghost">
                        {t("blocked.back.to.home")}
                    </Link>
                }
            >
                <p>{t("blocked.instructions")}</p>
            </StatusCard>
        </div>
    );
}
