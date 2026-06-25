import { useState } from "react";
import { Link, useLocation } from "react-router-dom";
import StatusCard from "@/components/ui/StatusCard";
import Button from "@/components/ui/Button";
import { resendVerificationEmail } from "@/lib/api/auth";
import { useI18n } from "@/lib/i18n";
import { SUPPORT_EMAIL } from "@/lib/utils/support";

type ResendStatus = "idle" | "sending" | "sent" | "error";

export default function NotVerifiedPage() {
    const { t } = useI18n();
    const location = useLocation();
    const email = (location.state as { email?: string } | null)?.email ?? "";
    const [resendStatus, setResendStatus] = useState<ResendStatus>("idle");
    const subject = encodeURIComponent(t("notvalid.contact.us"));
    const supportHref = `mailto:${SUPPORT_EMAIL}?subject=${subject}`;

    const handleResend = async () => {
        if (!email || resendStatus === "sending" || resendStatus === "sent") {
            return;
        }
        setResendStatus("sending");
        try {
            await resendVerificationEmail(email);
            setResendStatus("sent");
        } catch {
            setResendStatus("error");
        }
    };

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
                        {email && (
                            <Button
                                variant="primary"
                                onClick={handleResend}
                                disabled={resendStatus === "sending" || resendStatus === "sent"}
                            >
                                {resendStatus === "sending"
                                    ? t("notvalid.resend.sending")
                                    : t("notvalid.resend.cta")}
                            </Button>
                        )}
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
                {resendStatus === "sent" && (
                    <p className="status-card__highlight">{t("notvalid.resend.sent")}</p>
                )}
                {resendStatus === "error" && (
                    <p className="status-card__highlight">{t("notvalid.resend.error")}</p>
                )}
            </StatusCard>
        </div>
    );
}
