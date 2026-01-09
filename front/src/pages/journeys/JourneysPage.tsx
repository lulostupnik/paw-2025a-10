import { useNavigate } from "react-router-dom";
import { useAuthGate } from "@/hooks/useAuthGate";
import LoginRequiredModal from "@/components/LoginRequiredModal";

export default function JourneysListPage() {
    const nav = useNavigate();
    const gate = useAuthGate();

    return (
        <div style={{ padding: 20 }}>
            <h1>Viajes</h1>

            {/* Example of gated private action */}
            <button onClick={() => gate.runOrPrompt(() => nav("/journeys/create"))}>
                Crear viaje (privado)
            </button>

            <LoginRequiredModal open={gate.open} onClose={gate.close} nextPath="/journeys/create" />
        </div>
    );
}
