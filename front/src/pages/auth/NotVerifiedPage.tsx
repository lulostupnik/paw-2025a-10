import { Link, useLocation } from "react-router-dom";
import StatusCard from "@/components/ui/StatusCard";
import { useI18n } from "@/lib/i18n";
import { SUPPORT_EMAIL } from "@/lib/utils/support";

export default function NotVerifiedPage() {
    const { t } = useI18n();
    const location = useLocation();
    const email = (location.state as { email?: string } | null)?.email ?? "";
    const subject = encodeURIComponent(t("notvalid.contact.us"));
    const supportHref = `mailto:${SUPPORT_EMAIL}?subject=${subject}`;

    return (
        <div className="page-shell auth-page">
            <header className="register-header">
                <p className="eyebrow">{t("notvalid.access.denied")}</p>
                <h1>{t("notvalid.account")}</h1>
                <p className="register-header__lead">{t("notvalid.subtitle")}</p>
            </header>

            <StatusCard
                variant="error"
                title={t("notvalid.title")}
                description={t("notvalid.default.reason")}
                actions={
                    <>
                        <a className="btn btn--outline" href={supportHref}>
                            {t("notvalid.contact.us")}
                        </a>
                        <Link to="/" className="btn btn--ghost">
                            {t("notvalid.back.to.home")}
                        </Link>
                    </>
                }
            >
                <p>{t("notvalid.what.happened")}</p>
                <p>{t("notvalid.explanation")}</p>
                <p>{t("notvalid.what.to.do")}</p>
                <p>{t("notvalid.instructions")}</p>
                {/* The login attempt that landed the user here already (re)sent the verification email. */}
                {email && (
                    <p className="status-card__highlight">{t("notvalid.resend.sent")}</p>
                )}
            </StatusCard>
        </div>
    );
}
