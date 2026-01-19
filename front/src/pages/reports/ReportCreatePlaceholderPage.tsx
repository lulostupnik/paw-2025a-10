import { useNavigate, useParams } from "react-router-dom";
import Button from "@/components/ui/Button";
import { useI18n } from "@/lib/i18n";
import { popFromNavigationStack } from "@/lib/utils/navigationStack";

export default function ReportCreatePlaceholderPage() {
    const { t } = useI18n();
    const navigate = useNavigate();
    const { id } = useParams();

    const handleBack = () => {
        const previous = popFromNavigationStack();
        if (previous) {
            navigate(previous);
            return;
        }
        navigate(-1);
    };

    return (
        <div className="page-shell">
            <div className="card">
                <h1>{t("report.create.placeholder.title", { defaultValue: "Reportar contenido" })}</h1>
                <p>
                    {t("report.create.placeholder.message", {
                        defaultValue: "Esta pantalla todavia no esta migrada. Usa el flujo legacy para reportar.",
                    })}
                </p>
                {id && <p className="text-muted">{t("report.create.placeholder.target", { defaultValue: `Contenido: ${id}` })}</p>}
                <Button type="button" variant="secondary" onClick={handleBack}>
                    {t("journey.edit.back", { defaultValue: "Back" })}
                </Button>
            </div>
        </div>
    );
}
