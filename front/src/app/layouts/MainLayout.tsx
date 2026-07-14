import { Outlet, useLocation } from "react-router-dom";
import TopBar from "@/components/TopBar";

export default function MainLayout() {
    const location = useLocation();

    return (
        <div style={{ minHeight: "100vh", display: "flex", flexDirection: "column" }}>
            <TopBar />
            <main key={`${location.pathname}${location.search}`} style={{ flex: 1 }}>
                <Outlet />
            </main>
        </div>
    );
}
