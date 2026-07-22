import { useNavigate } from "react-router-dom";
import Button from "@/components/ui/Button";
import { useI18n } from "@/lib/i18n";
import { INTERNAL_PATH_FALLBACK, sanitizeInternalPath } from "@/lib/utils/internalPath";

interface LoginRequiredModalProps {
    open: boolean;
    onClose: () => void;
    nextPath?: string;
}

export default function LoginRequiredModal({ open, onClose, nextPath }: LoginRequiredModalProps) {
    const nav = useNavigate();
    const { t } = useI18n();
    if (!open) return null;

    const safeNextPath = sanitizeInternalPath(nextPath, INTERNAL_PATH_FALLBACK) ?? INTERNAL_PATH_FALLBACK;
    const next = safeNextPath ? `?next=${encodeURIComponent(safeNextPath)}` : "";

    return (
        <div className="modal-overlay" role="presentation" onClick={onClose}>
            <div className="modal-card" role="dialog" aria-modal="true" onClick={(e) => e.stopPropagation()}>
                <div className="modal-icon" aria-hidden="true">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth={2}>
                        <circle cx="12" cy="12" r="10" />
                        <path d="M12 8v4" />
                        <path d="M12 16h.01" />
                    </svg>
                </div>
                <h3>{t("auth.required.title")}</h3>
                <p>{t("auth.required.message")}</p>
                <div className="modal-actions">
                    <Button variant="danger" onClick={onClose}>
                        {t("common.cancel")}
                    </Button>
                    <Button variant="primary" onClick={() => nav(`/login${next}`)}>
                        {t("auth.login")}
                    </Button>
                </div>
            </div>
        </div>
    );
}
