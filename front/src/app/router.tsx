import { createBrowserRouter } from "react-router-dom";
import MainLayout from "./layouts/MainLayout";
import RequireAuth from "./guards/RequireAuth";

import NotFoundPage from "./pages/NotFoundPage";
import ForbiddenPage from "./pages/ForbiddenPage";
import ServerErrorPage from "./pages/ServerErrorPage";
import BadRequestPage from "./pages/BadRequestPage";
import MethodNotAllowedPage from "./pages/MethodNotAllowedPage";
import UnsupportedMediaPage from "./pages/UnsupportedMediaPage";

import LandingPage from "../features/landing/pages/LandingPage";
import ExplorePage from "../features/explore/pages/ExplorePage";

import JourneysListPage from "../features/journeys/pages/JourneysPage.tsx";
import JourneyDetailPage from "../features/journeys/pages/JourneysDetailsPage.tsx";
import JourneyCreatePage from "../features/journeys/pages/JourneysCreatePage.tsx";

import EventsListPage from "../features/events/pages/EventsPage.tsx";
import EventDetailPage from "../features/events/pages/EventsDetailsPage.tsx";

import ProfilePage from "../features/profile/pages/ProfilePage";
import AdminPage from "../features/auth/pages/AdminPage";

import LoginPage from "../features/auth/pages/LoginPage";
import RegisterPage from "../features/auth/pages/RegisterPage";

export const router = createBrowserRouter([
    {
        path: "/",
        element: <MainLayout />,
        children: [
            // PUBLIC
            { index: true, element: <LandingPage /> },
            { path: "explore", element: <ExplorePage /> },

            { path: "journeys", element: <JourneysListPage /> },
            { path: "journeys/:id", element: <JourneyDetailPage /> },

            { path: "events", element: <EventsListPage /> },
            { path: "events/:id", element: <EventDetailPage /> },

            { path: "login", element: <LoginPage /> },
            { path: "register", element: <RegisterPage /> },
            { path: "error/400", element: <BadRequestPage /> },
            { path: "error/403", element: <ForbiddenPage /> },
            { path: "error/405", element: <MethodNotAllowedPage /> },
            { path: "error/415", element: <UnsupportedMediaPage /> },
            { path: "error/500", element: <ServerErrorPage /> },

            // PRIVATE PAGES (same layout, guarded)
            {
                element: <RequireAuth />,
                children: [
                    { path: "journeys/create", element: <JourneyCreatePage /> },
                    { path: "profile", element: <ProfilePage /> },
                    { path: "admin", element: <AdminPage /> },
                ],
            },

            { path: "*", element: <NotFoundPage /> },
        ],
    },
]);
