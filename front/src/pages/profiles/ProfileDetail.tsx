import { useEffect, useMemo } from "react";
import { useLocation, useNavigate, useParams, useSearchParams } from "react-router-dom";
import { useI18n } from "@/lib/i18n";
import { useProfileDetail } from "@/hooks/profiles/useProfileDetail";
import { useProfileEvents } from "@/hooks/profiles/useProfileEvents";
import ProfileHeader from "@/components/profiles/ProfileHeader";
import ProfileTabs from "@/components/profiles/ProfileTabs";
import ProfileInfoTab from "@/components/profiles/ProfileInfoTab";
import ProfileInterestsTab from "@/components/profiles/ProfileInterestsTab";
import ProfileEventsTab from "@/components/profiles/ProfileEventsTab";
import type { ProfileInterest } from "@/types/profile";

const paginate = <T,>(items: T[], page: number, pageSize: number) => {
    const totalItems = items.length;
    const totalPages = Math.max(1, Math.ceil(totalItems / pageSize));
    const safePage = Math.min(Math.max(page, 1), totalPages);
    const start = (safePage - 1) * pageSize;
    return {
        content: items.slice(start, start + pageSize),
        totalPages,
        currentPage: safePage,
        pageSize,
        totalItems,
    };
};

export default function ProfileDetail() {
    const { t } = useI18n();
    const navigate = useNavigate();
    const location = useLocation();
    const { profileId = "me", tab } = useParams();
    const [searchParams, setSearchParams] = useSearchParams();
    const { data: profile, isLoading, isError } = useProfileDetail(profileId);

    const activeTab = tab === "interests" || tab === "events" ? tab : "info";
    const rawEventsTab = searchParams.get("eventsTab");
    const eventsTab = rawEventsTab === "attending" || rawEventsTab === "finished" ? rawEventsTab : "created";
    const createdPage = Number(searchParams.get("page")) || 1;
    const attendingPage = Number(searchParams.get("attendingPage")) || 1;
    const finishedPage = Number(searchParams.get("finishedPage")) || 1;
    const pageSize = Number(searchParams.get("size")) || 6;

    const { created, attending, finished } = useProfileEvents(profileId, {
        createdPage,
        attendingPage,
        finishedPage,
        size: pageSize,
    });

    const interestsPage = Number(searchParams.get("page")) || 1;
    const interestsPageSize = 4;
    const pagedInterests = useMemo(() => paginate<ProfileInterest>(profile?.interests ?? [], interestsPage, interestsPageSize), [interestsPage, interestsPageSize, profile?.interests]);

    const updateSearch = (updates: Record<string, string>) => {
        const next = new URLSearchParams(searchParams);
        Object.entries(updates).forEach(([key, value]) => {
            if (!value) {
                next.delete(key);
            } else {
                next.set(key, value);
            }
        });
        setSearchParams(next, { replace: true });
    };

    const returnPathKey = `profile:return:${profileId}`;
    const fromState = (location.state as { from?: string } | null)?.from;
    const returnPath = fromState ?? sessionStorage.getItem(returnPathKey);

    useEffect(() => {
        if (fromState) {
            sessionStorage.setItem(returnPathKey, fromState);
        }
    }, [fromState, returnPathKey]);

    const handleBack = () => {
        if (returnPath) {
            navigate(returnPath, { replace: true });
        } else {
            navigate("/events", { replace: true });
        }
    };

    if (isLoading) {
        return (
            <div className="profile-page">
                <div className="layout-container">
                    <div className="main-content">
                        <div className="content-container">
                            <div className="error-container">
                                <div className="status-icon status-icon--loading" aria-hidden="true">
                                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                                        <path d="M21 12a9 9 0 1 1-2.64-6.36" />
                                        <path d="M21 3v6h-6" />
                                    </svg>
                                </div>
                                <p>{t("admin.dashboard.loading", { defaultValue: "Cargando..." })}</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        );
    }

    if (isError || !profile) {
        return (
            <div className="profile-page">
                <div className="layout-container">
                    <div className="main-content">
                        <div className="content-container">
                            <div className="error-container">
                                <div className="status-icon status-icon--error" aria-hidden="true">
                                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                                        <circle cx="12" cy="12" r="10" />
                                        <line x1="12" y1="8" x2="12" y2="12" />
                                        <line x1="12" y1="16" x2="12.01" y2="16" />
                                    </svg>
                                </div>
                                <p>{isError ? t("admin.dashboard.error", { defaultValue: "Error cargando datos." }) : t("profile.not.found")}</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        );
    }

    return (
        <div className="profile-page">
            <div className="layout-container">
                <div className="main-content">
                    <div className="content-container">
                        {!profile.isMine && (
                            <div className="back-button-container">
                                <button type="button" className="back-link" onClick={handleBack}>
                                    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                        <path d="M19 12H5"></path>
                                        <path d="M12 19l-7-7 7-7"></path>
                                    </svg>
                                    <span>{t("event.detail.back.to.list")}</span>
                                </button>
                            </div>
                        )}
                        <div className="header-container">
                            <h2 className="page-title">{t("profile.page.title")}</h2>
                        </div>
                        <ProfileHeader profile={profile} />

                        <div className="profile-tabs-container">
                            <ProfileTabs profile={profile} activeTab={activeTab} />

                            <div className="profile-content">
                                {activeTab === "info" && <ProfileInfoTab profile={profile} />}
                                {activeTab === "interests" && (
                                    <ProfileInterestsTab
                                        interests={pagedInterests.content}
                                        isMine={profile.isMine}
                                        page={pagedInterests.currentPage}
                                        totalPages={pagedInterests.totalPages}
                                        pageSize={pagedInterests.pageSize}
                                        onPageChange={(page) => updateSearch({ page: String(page) })}
                                    />
                                )}
                                {activeTab === "events" && (
                                    <ProfileEventsTab
                                        isMine={profile.isMine}
                                        activeTab={eventsTab}
                                        created={created}
                                        attending={attending}
                                        finished={finished}
                                        onTabChange={(nextTab) => updateSearch({ eventsTab: nextTab })}
                                        onCreatedPageChange={(page) => updateSearch({ page: String(page) })}
                                        onAttendingPageChange={(page) => updateSearch({ attendingPage: String(page) })}
                                        onFinishedPageChange={(page) => updateSearch({ finishedPage: String(page) })}
                                    />
                                )}
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}
