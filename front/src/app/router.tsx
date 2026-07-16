import { lazy, Suspense, type ReactNode } from "react";
import { createBrowserRouter } from "react-router-dom";
import MainLayout from "@app/layouts/MainLayout";
import RequireAuth from "@app/guards/RequireAuth";
import HomeRoute from "@app/guards/HomeRoute";
import PageStatus from "@/components/ui/PageStatus";

import NotFoundPage from "@/pages/errors/NotFoundPage";
import ForbiddenPage from "@/pages/errors/ForbiddenPage";
import ServerErrorPage from "@/pages/errors/ServerErrorPage";
import BadRequestPage from "@/pages/errors/BadRequestPage";
import MethodNotAllowedPage from "@/pages/errors/MethodNotAllowedPage";
import UnsupportedMediaPage from "@/pages/errors/UnsupportedMediaPage";

import JourneysListPage from "@/pages/journeys/JourneysPage";
import EventsListPage from "@/pages/events/EventsPage";
const ExplorePage = lazy(() => import("@/pages/explore/ExplorePage"));
const JourneyDetailPage = lazy(() => import("@/pages/journeys/JourneyDetailsPage"));
const JourneyCreatePage = lazy(() => import("@/pages/journeys/JourneyCreatePage"));
const JourneyEditPage = lazy(() => import("@/pages/journeys/JourneyEditPage"));
const JourneyDeletePage = lazy(() => import("@/pages/journeys/JourneyDeletePage"));
const JourneyReplyDeletePage = lazy(() => import("@/pages/journeys/JourneyReplyDeletePage"));
const JourneyTipCreatePage = lazy(() => import("@/pages/journeys/JourneyTipCreatePage"));
const JourneyTipEditPage = lazy(() => import("@/pages/journeys/JourneyTipEditPage"));
const JourneyTipDeletePage = lazy(() => import("@/pages/journeys/JourneyTipDeletePage"));
const EventDetailPage = lazy(() => import("@/pages/events/EventDetailsPage"));
const EventCreatePage = lazy(() => import("@/pages/events/EventCreatePage"));
const EventEditPage = lazy(() => import("@/pages/events/EventEditPage"));
const EventDeletePage = lazy(() => import("@/pages/events/EventDeletePage"));
const EventReplyDeletePage = lazy(() => import("@/pages/events/EventReplyDeletePage"));
const ProfileDetail = lazy(() => import("@/pages/profiles/ProfileDetail"));
const ProfileForm = lazy(() => import("@/pages/profiles/ProfileForm"));
const ProfilePictureForm = lazy(() => import("@/pages/profiles/ProfilePictureForm"));
const ProfilePasswordForm = lazy(() => import("@/pages/profiles/ProfilePasswordForm"));
const ProfileInterestsEdit = lazy(() => import("@/pages/profiles/ProfileInterestsEdit"));
const ProfileRedirect = lazy(() => import("@/pages/profiles/ProfileRedirect"));
const AdminPage = lazy(() => import("@/pages/admin/AdminPage"));
const ReportDetailPage = lazy(() => import("@/pages/admin/ReportDetailPage"));
const ReportCreatePage = lazy(() => import("@/pages/reports/ReportCreatePage"));
const UserDetailPage = lazy(() => import("@/pages/users/UserDetailPage"));
const UniversityDetailPage = lazy(() => import("@/pages/universities/UniversityDetailPage"));
const UniversityCreatePage = lazy(() => import("@/pages/universities/UniversityCreatePage"));
const UniversityEditPage = lazy(() => import("@/pages/universities/UniversityEditPage"));
const InterestDetailPage = lazy(() => import("@/pages/interests/InterestDetailPage"));
const InterestCreatePage = lazy(() => import("@/pages/interests/InterestCreatePage"));
const InterestEditPage = lazy(() => import("@/pages/interests/InterestEditPage"));
const CityDetailPage = lazy(() => import("@/pages/cities/CityDetailPage"));
const CityCreatePage = lazy(() => import("@/pages/cities/CityCreatePage"));
const CityEditPage = lazy(() => import("@/pages/cities/CityEditPage"));
const CareerDetailPage = lazy(() => import("@/pages/careers/CareerDetailPage"));
const CareerCreatePage = lazy(() => import("@/pages/careers/CareerCreatePage"));
const CareerEditPage = lazy(() => import("@/pages/careers/CareerEditPage"));
const LoginPage = lazy(() => import("@/pages/auth/LoginPage"));
const RegisterPage = lazy(() => import("@/pages/auth/RegisterPage"));
const ForgotPasswordPage = lazy(() => import("@/pages/auth/ForgotPasswordPage"));
const EmailVerificationPage = lazy(() => import("@/pages/auth/EmailVerificationPage"));
const PasswordResetPage = lazy(() => import("@/pages/auth/PasswordResetPage"));
const PasswordResetConfirmationPage = lazy(() => import("@/pages/auth/PasswordResetConfirmationPage"));

const withSuspense = (element: ReactNode) => (
    <Suspense fallback={<PageStatus className="route-loading" message="Loading..." />}>
        {element}
    </Suspense>
);

export const router = createBrowserRouter(
    [
    {
        path: "/",
        element: <MainLayout />,
        errorElement: <ServerErrorPage />,
        children: [
            { index: true, element: <HomeRoute /> },

            { path: "journeys", element: <JourneysListPage /> },
            { path: "journeys/:id", element: withSuspense(<JourneyDetailPage />) },

            { path: "events", element: <EventsListPage /> },
            { path: "events/:id", element: withSuspense(<EventDetailPage />) },

            { path: "login", element: withSuspense(<LoginPage />) },
            { path: "register", element: withSuspense(<RegisterPage />) },
            { path: "forgot-password", element: withSuspense(<ForgotPasswordPage />) },
            { path: "validate", element: withSuspense(<EmailVerificationPage />) },
            { path: "reset-password", element: withSuspense(<PasswordResetPage />) },
            { path: "password/reset/confirmation", element: withSuspense(<PasswordResetConfirmationPage />) },
            { path: "error/400", element: <BadRequestPage /> },
            { path: "error/403", element: <ForbiddenPage /> },
            { path: "error/405", element: <MethodNotAllowedPage /> },
            { path: "error/415", element: <UnsupportedMediaPage /> },
            { path: "error/500", element: <ServerErrorPage /> },

            {
                element: <RequireAuth />,
                children: [
                    { path: "explore", element: withSuspense(<ExplorePage />) },
                    { path: "journeys/create", element: withSuspense(<JourneyCreatePage />) },
                    { path: "journeys/:id/update", element: withSuspense(<JourneyEditPage />) },
                    { path: "journeys/:id/delete", element: withSuspense(<JourneyDeletePage />) },
                    { path: "journeys/reply/:responseId/delete", element: withSuspense(<JourneyReplyDeletePage />) },
                    { path: "journeys/:journeyId/tips/create", element: withSuspense(<JourneyTipCreatePage />) },
                    { path: "journeys/tips/:tipId/update", element: withSuspense(<JourneyTipEditPage />) },
                    { path: "journeys/tips/:tipId/delete", element: withSuspense(<JourneyTipDeletePage />) },
                    { path: "events/create", element: withSuspense(<EventCreatePage />) },
                    { path: "events/:id/update", element: withSuspense(<EventEditPage />) },
                    { path: "events/:id/delete", element: withSuspense(<EventDeletePage />) },
                    { path: "events/reply/:responseId/delete", element: withSuspense(<EventReplyDeletePage />) },
                    { path: "profiles/:profileId", element: withSuspense(<ProfileRedirect />) },
                    { path: "profiles/:profileId/:tab", element: withSuspense(<ProfileDetail />) },
                    { path: "profiles/me/edit", element: withSuspense(<ProfileForm />) },
                    { path: "profiles/me/edit-picture", element: withSuspense(<ProfilePictureForm />) },
                    { path: "profiles/me/change-password", element: withSuspense(<ProfilePasswordForm />) },
                    { path: "profiles/me/interests/edit", element: withSuspense(<ProfileInterestsEdit />) },
                    { path: "reports/journeys/:id/create", element: withSuspense(<ReportCreatePage reportType="JOURNEY" />) },
                    { path: "reports/events/:id/create", element: withSuspense(<ReportCreatePage reportType="EVENT" />) },
                    { path: "reports/journey-responses/:id/create", element: withSuspense(<ReportCreatePage reportType="JOURNEY_RESPONSE" />) },
                    { path: "reports/event-responses/:id/create", element: withSuspense(<ReportCreatePage reportType="EVENT_RESPONSE" />) },
                    { path: "universities/:id", element: withSuspense(<UniversityDetailPage />) },
                    { path: "interests/:id", element: withSuspense(<InterestDetailPage />) },
                    { path: "cities/:id", element: withSuspense(<CityDetailPage />) },
                    { path: "careers/:id", element: withSuspense(<CareerDetailPage />) },
                ],
            },

            {
                element: <RequireAuth requireAdmin />,
                children: [
                    { path: "admin", element: withSuspense(<AdminPage />) },
                    { path: "admin/:tab", element: withSuspense(<AdminPage />) },
                    { path: "reports/:id", element: withSuspense(<ReportDetailPage />) },
                    { path: "users/:id", element: withSuspense(<UserDetailPage />) },
                    { path: "universities/create", element: withSuspense(<UniversityCreatePage />) },
                    { path: "universities/:id/edit", element: withSuspense(<UniversityEditPage />) },
                    { path: "interests/create", element: withSuspense(<InterestCreatePage />) },
                    { path: "interests/:id/edit", element: withSuspense(<InterestEditPage />) },
                    { path: "cities/create", element: withSuspense(<CityCreatePage />) },
                    { path: "cities/:id/edit", element: withSuspense(<CityEditPage />) },
                    { path: "careers/create", element: withSuspense(<CareerCreatePage />) },
                    { path: "careers/:id/edit", element: withSuspense(<CareerEditPage />) },
                ],
            },

            { path: "*", element: <NotFoundPage /> },
        ],
    },
    ],
    { basename: import.meta.env.BASE_URL },
);
