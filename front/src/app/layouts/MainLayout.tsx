import { Outlet } from "react-router-dom";
import TopBar from "@/components/TopBar";

export default function MainLayout() {
    return (
        <div style={{ minHeight: "100vh", display: "flex", flexDirection: "column" }}>
            <TopBar />
            <main style={{ flex: 1 }}>
                <Outlet />
            </main>
        </div>
    );
}
