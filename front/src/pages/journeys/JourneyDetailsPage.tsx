import { useParams } from "react-router-dom";

export default function JourneyDetailPage() {
    const { id } = useParams();
    return (
        <div style={{ padding: 20 }}>
            <h1>Detalle viaje</h1>
            <p>ID: {id}</p>
        </div>
    );
}
