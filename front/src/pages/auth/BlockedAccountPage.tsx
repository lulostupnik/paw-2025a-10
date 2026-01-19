import { useMemo } from "react";
import { Link, Navigate, useLocation } from "react-router-dom";
import StatusCard from "@/components/ui/StatusCard";
import { useI18n } from "@/lib/i18n";
import { SUPPORT_EMAIL } from "@/lib/utils/support";

export default function BlockedAccountPage() {
    const { t } = useI18n();
    const location = useLocation();
    const state = location.state as { blockedEmail?: string } | null;
    const email = typeof state?.blockedEmail === "string" ? state.blockedEmail : "";
    if (!email) {
        return <Navigate to="/login" replace />;
    }
    const supportHref = useMemo(() => {
        const subject = encodeURIComponent(t("blocked.contact.us"));
        const body = encodeURIComponent(`${t("blocked.default.reason")}\n${t("blocked.reference")} ${email}`);
        return `mailto:${SUPPORT_EMAIL}?subject=${subject}&body=${body}`;
    }, [email, t]);

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
                    <>
                        <a className="btn btn--primary" href={supportHref}>
                            {t("blocked.contact.us")}
                        </a>
                        <Link to="/" className="btn btn--ghost">
                            {t("blocked.back.to.home")}
                        </Link>
                    </>
                }
            >
                <p>{t("blocked.instructions")}</p>
                <p className="status-card__reference">
                    {t("blocked.reference")} <code>{email}</code>
                </p>
            </StatusCard>
        </div>
    );
}
