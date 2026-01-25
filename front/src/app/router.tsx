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
import JourneyEditPage from "@/pages/journeys/JourneyEditPage";
import JourneyDeletePage from "@/pages/journeys/JourneyDeletePage";
import JourneyReplyDeletePage from "@/pages/journeys/JourneyReplyDeletePage";
import JourneyTipCreatePage from "@/pages/journeys/JourneyTipCreatePage";
import JourneyTipEditPage from "@/pages/journeys/JourneyTipEditPage";
import JourneyTipDeletePage from "@/pages/journeys/JourneyTipDeletePage";

import EventsListPage from "@/pages/events/EventsPage";
import EventDetailPage from "@/pages/events/EventDetailsPage";
import EventCreatePage from "@/pages/events/EventCreatePage";
import EventEditPage from "@/pages/events/EventEditPage";
import EventDeletePage from "@/pages/events/EventDeletePage";
import EventReplyDeletePage from "@/pages/events/EventReplyDeletePage";

import ProfileDetail from "@/pages/profiles/ProfileDetail";
import ProfileForm from "@/pages/profiles/ProfileForm";
import ProfilePictureForm from "@/pages/profiles/ProfilePictureForm";
import ProfilePasswordForm from "@/pages/profiles/ProfilePasswordForm";
import ProfileInterestsEdit from "@/pages/profiles/ProfileInterestsEdit";
import ProfileRedirect from "@/pages/profiles/ProfileRedirect";
import AdminPage from "@/pages/admin/AdminPage";
import ReportDetailPage from "@/pages/admin/ReportDetailPage";
import ReportCreatePage from "@/pages/reports/ReportCreatePage";
import UserDetailPage from "@/pages/users/UserDetailPage";
import UniversityDetailPage from "@/pages/universities/UniversityDetailPage";
import UniversityCreatePage from "@/pages/universities/UniversityCreatePage";
import UniversityEditPage from "@/pages/universities/UniversityEditPage";
import InterestDetailPage from "@/pages/interests/InterestDetailPage";
import InterestCreatePage from "@/pages/interests/InterestCreatePage";
import InterestEditPage from "@/pages/interests/InterestEditPage";
import CityDetailPage from "@/pages/cities/CityDetailPage";
import CityCreatePage from "@/pages/cities/CityCreatePage";
import CityEditPage from "@/pages/cities/CityEditPage";
import CareerDetailPage from "@/pages/careers/CareerDetailPage";
import CareerCreatePage from "@/pages/careers/CareerCreatePage";
import CareerEditPage from "@/pages/careers/CareerEditPage";

import LoginPage from "@/pages/auth/LoginPage";
import RegisterPage from "@/pages/auth/RegisterPage";
import ForgotPasswordPage from "@/pages/auth/ForgotPasswordPage";
import EmailVerificationPage from "@/pages/auth/EmailVerificationPage";
import PasswordResetPage from "@/pages/auth/PasswordResetPage";
import PasswordResetConfirmationPage from "@/pages/auth/PasswordResetConfirmationPage";
import BlockedAccountPage from "@/pages/auth/BlockedAccountPage";

export const router = createBrowserRouter([
    {
        path: "/",
        element: <MainLayout />,
        children: [
            // PUBLIC
            { index: true, element: <LandingPage /> },

            { path: "journeys", element: <JourneysListPage /> },
            { path: "journeys/:id", element: <JourneyDetailPage /> },

            { path: "events", element: <EventsListPage /> },
            { path: "events/:id", element: <EventDetailPage /> },

            { path: "login", element: <LoginPage /> },
            { path: "register", element: <RegisterPage /> },
            { path: "forgot-password", element: <ForgotPasswordPage /> },
            { path: "validate", element: <EmailVerificationPage /> },
            { path: "reset-password", element: <PasswordResetPage /> },
            { path: "password/reset/confirmation", element: <PasswordResetConfirmationPage /> },
            { path: "blocked", element: <BlockedAccountPage /> },
            { path: "error/400", element: <BadRequestPage /> },
            { path: "error/403", element: <ForbiddenPage /> },
            { path: "error/405", element: <MethodNotAllowedPage /> },
            { path: "error/415", element: <UnsupportedMediaPage /> },
            { path: "error/500", element: <ServerErrorPage /> },

            // PRIVATE PAGES (same layout, guarded)
            {
                element: <RequireAuth />,
                children: [
                    { path: "explore", element: <ExplorePage /> },
                    { path: "journeys/create", element: <JourneyCreatePage /> },
                    { path: "journeys/:id/update", element: <JourneyEditPage /> },
                    { path: "journeys/:id/delete", element: <JourneyDeletePage /> },
                    { path: "journeys/reply/:responseId/delete", element: <JourneyReplyDeletePage /> },
                    { path: "journeys/:journeyId/tips/create", element: <JourneyTipCreatePage /> },
                    { path: "journeys/tips/:tipId/update", element: <JourneyTipEditPage /> },
                    { path: "journeys/tips/:tipId/delete", element: <JourneyTipDeletePage /> },
                    { path: "events/create", element: <EventCreatePage /> },
                    { path: "events/:id/update", element: <EventEditPage /> },
                    { path: "events/:id/delete", element: <EventDeletePage /> },
                    { path: "events/reply/:responseId/delete", element: <EventReplyDeletePage /> },
                    { path: "profiles/:profileId", element: <ProfileRedirect /> },
                    { path: "profiles/:profileId/:tab", element: <ProfileDetail /> },
                    { path: "profiles/me/edit", element: <ProfileForm /> },
                    { path: "profiles/me/edit-picture", element: <ProfilePictureForm /> },
                    { path: "profiles/me/change-password", element: <ProfilePasswordForm /> },
                    { path: "profiles/me/interests/edit", element: <ProfileInterestsEdit /> },
                    { path: "admin", element: <AdminPage /> },
                    { path: "admin/:tab", element: <AdminPage /> },
                    { path: "reports/:id", element: <ReportDetailPage /> },
                    { path: "reports/journeys/:id/create", element: <ReportCreatePage reportType="JOURNEY" /> },
                    { path: "reports/events/:id/create", element: <ReportCreatePage reportType="EVENT" /> },
                    { path: "reports/journey-responses/:id/create", element: <ReportCreatePage reportType="JOURNEY_RESPONSE" /> },
                    { path: "reports/event-responses/:id/create", element: <ReportCreatePage reportType="EVENT_RESPONSE" /> },
                    { path: "users/:id", element: <UserDetailPage /> },
                    { path: "universities/create", element: <UniversityCreatePage /> },
                    { path: "universities/:id/edit", element: <UniversityEditPage /> },
                    { path: "universities/:id", element: <UniversityDetailPage /> },
                    { path: "interests/create", element: <InterestCreatePage /> },
                    { path: "interests/:id/edit", element: <InterestEditPage /> },
                    { path: "interests/:id", element: <InterestDetailPage /> },
                    { path: "cities/create", element: <CityCreatePage /> },
                    { path: "cities/:id/edit", element: <CityEditPage /> },
                    { path: "cities/:id", element: <CityDetailPage /> },
                    { path: "careers/create", element: <CareerCreatePage /> },
                    { path: "careers/:id/edit", element: <CareerEditPage /> },
                    { path: "careers/:id", element: <CareerDetailPage /> },
                ],
            },

            { path: "*", element: <NotFoundPage /> },
        ],
    },
]);
