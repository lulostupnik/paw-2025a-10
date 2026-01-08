import { Link } from "react-router-dom";

export default function NotFoundPage() {
    return (
        <div style={{ padding: 20 }}>
            <h1>404</h1>
            <Link to="/">Go home</Link>
        </div>
    );
}
