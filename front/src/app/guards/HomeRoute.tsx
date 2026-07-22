import { Navigate } from "react-router-dom";
import LandingPage from "@/pages/landing/LandingPage";
import { isLoggedIn } from "@/lib/auth/auth";

export default function HomeRoute() {
    return isLoggedIn() ? <Navigate to="/explore" replace /> : <LandingPage />;
}
