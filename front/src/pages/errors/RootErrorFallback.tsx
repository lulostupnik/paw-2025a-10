import { useI18n } from "@/lib/i18n";

export default function RootErrorFallback() {
    const { t } = useI18n();

    return (
        <div className="page-shell error-page">
            <div className="error-card">
                <span className="error-code">{t("error.500.code")}</span>
                <h1 className="error-title">{t("error.500.title")}</h1>
                <p className="error-message">{t("error.500.message")}</p>
                <div className="error-actions">
                    <a href="/" className="primary-action">
                        {t("error.action.home")}
                    </a>
                </div>
            </div>
        </div>
    );
}
