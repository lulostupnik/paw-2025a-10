import { Link, useLocation } from "react-router-dom";
import type { ProfileDetail } from "@/types/profile";
import { useI18n } from "@/lib/i18n";
import { classNames } from "@/lib/utils/classNames";

interface ProfileTabsProps {
    profile: ProfileDetail;
    activeTab: "info" | "interests" | "events";
}

const parseIdFromUrl = (url?: string | null) => {
    if (!url) {
        return null;
    }
    const match = url.match(/\/(\d+)(?:\/)?$/);
    return match ? Number(match[1]) : null;
};

export default function ProfileTabs({ profile, activeTab }: ProfileTabsProps) {
    const { t } = useI18n();
    const location = useLocation();
    const returnPath = (location.state as { from?: string } | null)?.from;
    const linkState = returnPath ? { from: returnPath } : undefined;
    const journeyId = parseIdFromUrl(profile.links?.journeyUrl);
    const hasJourney = Boolean(journeyId);

    return (
        <div className="profile-tabs">
            <Link
                to={`/profiles/${profile.id}/info`}
                state={linkState}
                className={classNames("profile-tab", activeTab === "info" && "active")}
            >
                <svg xmlns="http://www.w3.org/2000/svg" className="tab-icon" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                    <circle cx="12" cy="12" r="10"></circle>
                    <line x1="12" y1="16" x2="12" y2="12"></line>
                    <line x1="12" y1="8" x2="12.01" y2="8"></line>
                </svg>
                <span className="tab-text">{t("profile.tab.info")}</span>
            </Link>
            <Link
                to={`/profiles/${profile.id}/interests`}
                state={linkState}
                className={classNames("profile-tab", activeTab === "interests" && "active")}
            >
                <svg xmlns="http://www.w3.org/2000/svg" className="tab-icon" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                    <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"></path>
                </svg>
                <span className="tab-text">{t("profile.tab.interests")}</span>
            </Link>

            <Link
                to={`/profiles/${profile.id}/events`}
                state={linkState}
                className={classNames("profile-tab", activeTab === "events" && "active")}
            >
                <svg xmlns="http://www.w3.org/2000/svg" className="tab-icon" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                    <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                    <line x1="16" y1="2" x2="16" y2="6"></line>
                    <line x1="8" y1="2" x2="8" y2="6"></line>
                    <line x1="3" y1="10" x2="21" y2="10"></line>
                </svg>
                <span className="tab-text">{t("profile.tab.events")}</span>
            </Link>

            {!profile.isMine && hasJourney && (
                <Link
                    to={`/journeys/${journeyId}`}
                    state={linkState}
                    className="btn-primary profile-tabs-right"
                >
                    <svg xmlns="http://www.w3.org/2000/svg" className="tab-icon" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                        <path d="M9 20l-5.447-2.724A1 1 0 013 16.382V5.618a1 1 0 011.447-.894L9 7m0 13l6-3m-6 3V7m6 10l4.553 2.276A1 1 0 0021 18.382V7.618a1 1 0 00-.553-.894L15 4m0 13V4m0 0L9 7"></path>
                    </svg>
                    <span className="tab-text">{t("profile.tab.journeys")}</span>
                </Link>
            )}
            {profile.isMine && !hasJourney && (
                <Link to="/journeys/create" className="btn-primary profile-tabs-right">
                    <svg xmlns="http://www.w3.org/2000/svg" className="button-icon" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                        <path d="M9 20l-5.447-2.724A1 1 0 013 16.382V5.618a1 1 0 011.447-.894L9 7m0 13l6-3m-6 3V7m6 10l4.553 2.276A1 1 0 0021 18.382V7.618a1 1 0 00-.553-.894L15 4m0 13V4m0 0L9 7"></path>
                    </svg>
                    <span>{t("profile.create.journey")}</span>
                </Link>
            )}
            {profile.isMine && hasJourney && (
                <Link to={`/journeys/${journeyId}`} className="btn-primary profile-tabs-right">
                    <svg xmlns="http://www.w3.org/2000/svg" className="button-icon" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                        <path d="M9 20l-5.447-2.724A1 1 0 013 16.382V5.618a1 1 0 011.447-.894L9 7m0 13l6-3m-6 3V7m6 10l4.553 2.276A1 1 0 0021 18.382V7.618a1 1 0 00-.553-.894L15 4m0 13V4m0 0L9 7"></path>
                    </svg>
                    <span>{t("journey.view.my")}</span>
                </Link>
            )}
        </div>
    );
}
