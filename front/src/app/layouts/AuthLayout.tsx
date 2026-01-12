import { Outlet } from "react-router-dom";

export default function AuthLayout() {
    return (
        <div style={{ minHeight: "100vh", display: "grid", placeItems: "center", padding: 16 }}>
            <div style={{ width: 420, maxWidth: "100%" }}>
                <Outlet />
            </div>
        </div>
    );
}
