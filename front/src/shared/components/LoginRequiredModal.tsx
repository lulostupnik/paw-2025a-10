import { useNavigate } from "react-router-dom";

export default function LoginRequiredModal({
                                               open,
                                               onClose,
                                               nextPath,
                                           }: {
    open: boolean;
    onClose: () => void;
    nextPath?: string;
}) {
    const nav = useNavigate();
    if (!open) return null;

    const next = nextPath ? `?next=${encodeURIComponent(nextPath)}` : "";

    return (
        <div
            onClick={onClose}
            style={{
                position: "fixed",
                inset: 0,
                background: "rgba(0,0,0,.35)",
                display: "grid",
                placeItems: "center",
                padding: 16,
            }}
        >
            <div
                onClick={(e) => e.stopPropagation()}
                style={{ width: 440, maxWidth: "100%", background: "#fff", borderRadius: 14, padding: 18 }}
            >
                <h3 style={{ marginTop: 0 }}>Necesitás una cuenta</h3>
                <p>Para realizar esta acción tenés que iniciar sesión o registrarte.</p>

                <div style={{ display: "flex", gap: 10, justifyContent: "flex-end", marginTop: 16 }}>
                    <button onClick={onClose} style={{ padding: "10px 12px" }}>
                        Cancelar
                    </button>
                    <button onClick={() => nav(`/login${next}`)} style={{ padding: "10px 12px" }}>
                        Iniciar sesión
                    </button>
                    <button onClick={() => nav(`/register${next}`)} style={{ padding: "10px 12px" }}>
                        Registrarse
                    </button>
                </div>
            </div>
        </div>
    );
}
