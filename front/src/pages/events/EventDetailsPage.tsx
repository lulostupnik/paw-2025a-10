import { useParams } from "react-router-dom";

export default function EventDetailPage() {
    const { id } = useParams();
    return (
        <div style={{ padding: 20 }}>
            <h1>Detalle evento</h1>
            <p>ID: {id}</p>
        </div>
    );
}
