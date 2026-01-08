import { createBrowserRouter } from "react-router-dom";
import AppLayout from "./layouts/AppLayout";
import AuthLayout from "./layouts/AuthLayout";

import HomePage from "./pages/HomePage";
import JourneysPage from "./pages/JourneysPage";
import EventsPage from "./pages/EventsPage";
import ProfilePage from "./pages/ProfilePage";
import AdminPage from "./pages/AdminPage";
import LoginPage from "./pages/LoginPage";
import NotFoundPage from "./pages/NotFoundPage";

export const router = createBrowserRouter([
    // auth area
    {
        element: <AuthLayout />,
        children: [{ path: "/login", element: <LoginPage /> }],
    },

    // app area
    {
        path: "/",
        element: <AppLayout />,
        children: [
            { index: true, element: <HomePage /> },
            { path: "journeys", element: <JourneysPage /> },
            { path: "events", element: <EventsPage /> },
            { path: "profile", element: <ProfilePage /> },
            { path: "admin", element: <AdminPage /> },
        ],
    },

    { path: "*", element: <NotFoundPage /> },
]);
