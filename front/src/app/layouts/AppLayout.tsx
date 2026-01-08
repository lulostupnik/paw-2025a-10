import { Outlet } from "react-router-dom";
import Navbar from "../../shared/components/Navbar";
import Sidebar from "../../shared/components/Sidebar";

export default function AppLayout() {
    return (
        <div style={{ minHeight: "100vh", display: "flex", flexDirection: "column" }}>
            <Navbar />
            <div style={{ flex: 1, display: "flex" }}>
                <Sidebar />
                <main style={{ flex: 1, padding: 16 }}>
                    <Outlet />
                </main>
            </div>
        </div>
    );
}
