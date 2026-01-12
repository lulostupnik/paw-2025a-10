import { Navigate, Outlet, useLocation } from "react-router-dom";
import { isLoggedIn } from "@/lib/auth/auth";

export default function RequireAuth() {
    const loc = useLocation();
    if (!isLoggedIn()) {
        return <Navigate to={`/login?next=${encodeURIComponent(loc.pathname)}`} replace />;
    }
    return <Outlet />;
}
