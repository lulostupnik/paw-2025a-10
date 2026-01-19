import { useMemo } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import Button from "@/components/ui/Button";
import StatusCard from "@/components/ui/StatusCard";
import { useI18n } from "@/lib/i18n";
import { SUPPORT_EMAIL } from "@/lib/utils/support";

type ConfirmationStatus = "success" | "expired" | "invalid" | "blocked" | "error";

const mapStatusParam = (value: string | null): ConfirmationStatus => {
    if (!value) {
        return "success";
    }
    switch (value.toLowerCase()) {
        case "expired":
            return "expired";
        case "invalid":
            return "invalid";
        case "blocked":
            return "blocked";
        case "error":
            return "error";
        default:
            return "success";
    }
};

const referenceForStatus = (status: ConfirmationStatus) => {
    if (status === "success") {
        return undefined;
    }
    return Math.random().toString(36).slice(2, 10).toUpperCase();
};

export default function PasswordResetConfirmationPage() {
    const { t } = useI18n();
    const navigate = useNavigate();
    const [params] = useSearchParams();
    const status = mapStatusParam(params.get("status"));
    const reference = useMemo(() => referenceForStatus(status), [status]);

    const renderStatusCard = () => {
        switch (status) {
            case "success":
                return (
                    <StatusCard
                        variant="success"
                        title={t("password.confirmation.title")}
                        description={t("password.confirmation.description")}
                        actions={
                            <>
                                <Button onClick={() => navigate("/login")}>
                                    {t("password.confirmation.actions.login")}
                                </Button>
                                <Button variant="ghost" onClick={() => navigate("/")}>
                                    {t("password.confirmation.actions.home")}
                                </Button>
                            </>
                        }
                    >
                        <p>{t("password.confirmation.security")}</p>
                    </StatusCard>
                );
            case "expired":
                return (
                    <StatusCard
                        variant="warning"
                        title={t("expiredtoken.title")}
                        description={t("expiredtoken.explanation")}
                        actions={
                            <>
                                <Button onClick={() => navigate("/forgot-password")}>
                                    {t("forgotpassword.submit")}
                                </Button>
                                <a className="btn btn--ghost" href={`mailto:${SUPPORT_EMAIL}`}>
                                    {t("expiredtoken.contact.us")}
                                </a>
                            </>
                        }
                    >
                        <p>{t("expiredtoken.instructions")}</p>
                        {reference && (
                            <p className="status-card__reference">
                                {t("expiredtoken.reference")} <code>{reference}</code>
                            </p>
                        )}
                    </StatusCard>
                );
            case "invalid":
                return (
                    <StatusCard
                        variant="error"
                        title={t("invalidtoken.title")}
                        description={t("invalidtoken.explanation")}
                        actions={
                            <>
                                <Button onClick={() => navigate("/forgot-password")}>
                                    {t("forgotpassword.submit")}
                                </Button>
                                <a className="btn btn--ghost" href={`mailto:${SUPPORT_EMAIL}`}>
                                    {t("invalidtoken.contact.us")}
                                </a>
                            </>
                        }
                    >
                        <p>{t("password.confirmation.error.description")}</p>
                        {reference && (
                            <p className="status-card__reference">
                                {t("invalidtoken.reference")} <code>{reference}</code>
                            </p>
                        )}
                    </StatusCard>
                );
            case "blocked":
                return (
                    <StatusCard
                        variant="error"
                        title={t("blocked.title")}
                        description={t("blocked.explanation")}
                        actions={
                            <>
                                <a className="btn btn--primary" href={`mailto:${SUPPORT_EMAIL}`}>
                                    {t("blocked.contact.us")}
                                </a>
                                <Button variant="ghost" onClick={() => navigate("/")}>
                                    {t("blocked.back.to.home")}
                                </Button>
                            </>
                        }
                    >
                        <p>{t("password.confirmation.error.description")}</p>
                        {reference && (
                            <p className="status-card__reference">
                                {t("blocked.reference")} <code>{reference}</code>
                            </p>
                        )}
                    </StatusCard>
                );
            case "error":
            default:
                return (
                    <StatusCard
                        variant="error"
                        title={t("password.confirmation.error.title")}
                        description={t("password.confirmation.help")}
                        actions={
                            <>
                                <a className="btn btn--primary" href={`mailto:${SUPPORT_EMAIL}`}>
                                    {t("password.confirmation.actions.support")}
                                </a>
                                <Button variant="ghost" onClick={() => navigate("/login")}>
                                    {t("password.confirmation.actions.login")}
                                </Button>
                            </>
                        }
                    />
                );
        }
    };

    return (
        <div className="page-shell auth-page password-confirmation-page">
            <header className="register-header">
                <p className="eyebrow">{t("reset.password.success.title")}</p>
                <h1>{t("password.confirmation.title")}</h1>
                <p className="register-header__lead">{t("password.confirmation.subtitle")}</p>
            </header>

            <div className="password-confirmation-grid">
                {renderStatusCard()}
                <div className="card password-confirmation-tips">
                    <h3>{t("password.confirmation.tips.title")}</h3>
                    <ul>
                        <li>{t("password.confirmation.tips.1")}</li>
                        <li>{t("password.confirmation.tips.2")}</li>
                        <li>{t("password.confirmation.tips.3")}</li>
                    </ul>
                    <a className="btn btn--outline" href={`mailto:${SUPPORT_EMAIL}`}>
                        {t("password.confirmation.actions.support")}
                    </a>
                </div>
            </div>
        </div>
    );
}
