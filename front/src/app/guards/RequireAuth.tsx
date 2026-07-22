import { Navigate, Outlet, useLocation } from "react-router-dom";
import { isAdmin, isLoggedIn } from "@/lib/auth/auth";

interface RequireAuthProps {
    requireAdmin?: boolean;
}

export default function RequireAuth({ requireAdmin }: RequireAuthProps) {
    const loc = useLocation();
    if (!isLoggedIn()) {
        return <Navigate to={`/login?next=${encodeURIComponent(loc.pathname)}`} replace />;
    }
    if (requireAdmin && !isAdmin()) {
        return <Navigate to="/error/403" replace />;
    }
    return <Outlet />;
}
