import { useEffect, useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import { isAxiosError } from "axios";
import Button from "@/components/ui/Button";
import StatusCard from "@/components/ui/StatusCard";
import { verifyEmailToken } from "@/lib/api/auth";
import { useI18n } from "@/lib/i18n";

type VerificationStatus = "loading" | "success" | "expired" | "invalid" | "blocked" | "already" | "error";

interface VerificationState {
    status: VerificationStatus;
}

const mapVerificationError = (error: unknown): VerificationStatus => {
    if (!isAxiosError(error)) {
        return "error";
    }
    const status = error.response?.status;
    const message = String(error.response?.data?.message ?? "").toLowerCase();

    if (status === 410 || message.includes("expir")) {
        return "expired";
    }
    if (status === 403) {
        return "blocked";
    }
    if (status === 409 && message.includes("validat")) {
        return "already";
    }
    if (status && (status < 500 ? status >= 400 : false)) {
        return status === 400 || status === 404 ? "invalid" : "error";
    }
    return "error";
};

export default function EmailVerificationPage() {
    const { t } = useI18n();
    const navigate = useNavigate();
    const [searchParams] = useSearchParams();
    const token = searchParams.get("token");
    const userId = searchParams.get("userId");
    const email = searchParams.get("email") ?? searchParams.get("amp;email") ?? "";
    const [state, setState] = useState<VerificationState>(() =>
        token && userId ? { status: "loading" } : { status: "invalid" }
    );

    useEffect(() => {
        if (!token || !userId) {
            setState({ status: "invalid" });
            return;
        }

        let cancelled = false;
        setState({ status: "loading" });

        verifyEmailToken({ userId, email, token })
            .then(() => {
                if (!cancelled) {
                    setState({ status: "success" });
                }
            })
            .catch((error) => {
                if (!cancelled) {
                    setState({ status: mapVerificationError(error) });
                }
            });

        return () => {
            cancelled = true;
        };
    }, [token, userId, email]);

    const renderStatusCard = () => {
        switch (state.status) {
            case "loading":
                return (
                    <StatusCard
                        variant="info"
                        title={t("verification.loading.title")}
                        description={t("verification.loading.description")}
                    />
                );
            case "success":
                return (
                    <StatusCard
                        variant="success"
                        title={t("verification.success.title")}
                        description={t("verification.success.description")}
                        actions={
                            <>
                                <Button onClick={() => navigate("/login")}>
                                    {t("verification.actions.login")}
                                </Button>
                                <Button variant="ghost" onClick={() => navigate("/explore")}>
                                    {t("verification.actions.explore")}
                                </Button>
                            </>
                        }
                    >
                        <ul className="status-card__list">
                            <li>{t("validated.feature1.description")}</li>
                            <li>{t("validated.feature2.description")}</li>
                            <li>{t("validated.feature3.description")}</li>
                        </ul>
                    </StatusCard>
                );
            case "expired":
                return (
                    <StatusCard
                        variant="warning"
                        title={t("expiredtoken.title")}
                        description={t("expiredtoken.explanation")}
                        actions={
                            <Button onClick={() => navigate("/forgot-password")}>
                                {t("forgotpassword.submit")}
                            </Button>
                        }
                    >
                        <p>{t("expiredtoken.instructions")}</p>
                    </StatusCard>
                );
            case "invalid":
                return (
                    <StatusCard
                        variant="error"
                        title={t("invalidtoken.title")}
                        description={t("invalidtoken.explanation")}
                        actions={
                            <Button onClick={() => navigate("/login")}>
                                {t("verification.actions.login")}
                            </Button>
                        }
                    >
                        <p>{t("invalidtoken.instructions")}</p>
                    </StatusCard>
                );
            case "blocked":
                return (
                    <StatusCard
                        variant="error"
                        title={t("blocked.title")}
                        description={t("blocked.explanation")}
                        actions={
                            <Button variant="ghost" onClick={() => navigate("/")}>
                                {t("blocked.back.to.home")}
                            </Button>
                        }
                    >
                        <p>{t("blocked.instructions")}</p>
                    </StatusCard>
                );
            case "already":
                return (
                    <StatusCard
                        variant="info"
                        title={t("validated.title")}
                        description={t("validated.subtitle")}
                        actions={
                            <>
                                <Button onClick={() => navigate("/login")}>
                                    {t("verification.actions.login")}
                                </Button>
                                <Button variant="ghost" onClick={() => navigate("/explore")}>
                                    {t("verification.actions.explore")}
                                </Button>
                            </>
                        }
                    >
                        <p>{t("validated.help.text")}</p>
                    </StatusCard>
                );
            case "error":
            default:
                return (
                    <StatusCard
                        variant="error"
                        title={t("verification.error.title")}
                        description={t("verification.error.description")}
                        actions={
                            <Button variant="ghost" onClick={() => navigate("/login")}>
                                {t("verification.actions.login")}
                            </Button>
                        }
                    />
                );
        }
    };

    return (
        <div className="page-shell auth-page verification-page">
            <header className="register-header">
                <p className="eyebrow">{t("email.validation.title")}</p>
                <h1>{t("email.validation.heading")}</h1>
                <p className="register-header__lead">{t("email.validation.explanation")}</p>
            </header>

            <div className="verification-grid">
                {renderStatusCard()}
                <div className="card verification-help">
                    <h3>{t("verification.pending.title")}</h3>
                    <p>{t("email.validation.features")}</p>
                    <ul>
                        <li>{t("validated.feature1.description")}</li>
                        <li>{t("validated.feature2.description")}</li>
                        <li>{t("validated.feature3.description")}</li>
                    </ul>
                </div>
            </div>
        </div>
    );
}
