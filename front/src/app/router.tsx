import { createBrowserRouter } from "react-router-dom";
import MainLayout from "@app/layouts/MainLayout";
import RequireAuth from "@app/guards/RequireAuth";

import NotFoundPage from "@/pages/errors/NotFoundPage";
import ForbiddenPage from "@/pages/errors/ForbiddenPage";
import ServerErrorPage from "@/pages/errors/ServerErrorPage";
import BadRequestPage from "@/pages/errors/BadRequestPage";
import MethodNotAllowedPage from "@/pages/errors/MethodNotAllowedPage";
import UnsupportedMediaPage from "@/pages/errors/UnsupportedMediaPage";

import LandingPage from "@/pages/landing/LandingPage";
import ExplorePage from "@/pages/explore/ExplorePage";

import JourneysListPage from "@/pages/journeys/JourneysPage";
import JourneyDetailPage from "@/pages/journeys/JourneyDetailsPage";
import JourneyCreatePage from "@/pages/journeys/JourneyCreatePage";

import EventsListPage from "@/pages/events/EventsPage";
import EventDetailPage from "@/pages/events/EventDetailsPage";
import EventCreatePage from "@/pages/events/EventCreatePage";

import ProfilePage from "@/pages/profile/ProfilePage";
import AdminPage from "@/pages/admin/AdminPage";

import LoginPage from "@/pages/auth/LoginPage";
import RegisterPage from "@/pages/auth/RegisterPage";

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
                    { path: "events/create", element: <EventCreatePage /> },
                    { path: "profile", element: <ProfilePage /> },
                    { path: "admin", element: <AdminPage /> },
                ],
            },

            { path: "*", element: <NotFoundPage /> },
        ],
    },
]);
