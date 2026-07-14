import { useCallback, useEffect } from "react";
import { useLocation, useNavigate, useParams, useSearchParams } from "react-router-dom";
import { useI18n } from "@/lib/i18n";
import { useProfileDetail } from "@/hooks/profiles/useProfileDetail";
import { useProfileEvents } from "@/hooks/profiles/useProfileEvents";
import ProfileHeader from "@/components/profiles/ProfileHeader";
import ProfileTabs from "@/components/profiles/ProfileTabs";
import ProfileInfoTab from "@/components/profiles/ProfileInfoTab";
import ProfileInterestsTab from "@/components/profiles/ProfileInterestsTab";
import ProfileEventsTab from "@/components/profiles/ProfileEventsTab";
import { useProfileInterests } from "@/hooks/profiles/useProfileInterests";
import { emptyPage } from "@/types/pagination";
import { sanitizeInternalPath } from "@/lib/utils/internalPath";
import PageStatus from "@/components/ui/PageStatus";

export default function ProfileDetail() {
    const { t } = useI18n();
    const navigate = useNavigate();
    const location = useLocation();
    const { profileId = "me", tab } = useParams();
    const [searchParams, setSearchParams] = useSearchParams();
    const { data: profile, isLoading, isError, infoLoading } = useProfileDetail({ profileId });

    const activeTab = tab === "interests" || tab === "events" ? tab : "info";
    const rawEventsTab = searchParams.get("eventsTab");
    const eventsTab = rawEventsTab === "attending" || rawEventsTab === "finished" ? rawEventsTab : "created";
    const page = Number(searchParams.get("page")) || 1;
    const pageSize = Number(searchParams.get("size")) || 6;

    // Sólo se ve un sub-listado a la vez, así que el `page` de la URL pagina el
    // sub-tab activo; los otros dos vuelven a su primera página.
    const profileEvents = useProfileEvents(profileId, {
        createdPage: eventsTab === "created" ? page : 1,
        attendingPage: eventsTab === "attending" ? page : 1,
        finishedPage: eventsTab === "finished" ? page : 1,
        size: pageSize,
        enabled: activeTab === "events",
    });

    const pagedInterests = useProfileInterests({
        profileId,
        page,
        size: pageSize,
        enabled: activeTab === "interests",
    });

    const updateSearch = useCallback((updates: Record<string, string>) => {
        const next = new URLSearchParams(searchParams);
        Object.entries(updates).forEach(([key, value]) => {
            if (!value) {
                next.delete(key);
            } else {
                next.set(key, value);
            }
        });
        setSearchParams(next);
    }, [searchParams, setSearchParams]);

    const returnPathKey = `profile:return:${profileId}`;
    const fromState = sanitizeInternalPath((location.state as { from?: string } | null)?.from);
    const returnPath = fromState ?? sanitizeInternalPath(sessionStorage.getItem(returnPathKey));

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

    const handlePageChange = useCallback(
        (page: number | string) => {
            if (typeof page === "string") {
                const pageNumber = new URL(page).searchParams.get('page') ?? '1';
                setSearchParams((prev) => {
                    const next = new URLSearchParams(prev);
                    next.set('page', pageNumber);
                    return next;
                });
            } else {
                setSearchParams((prev) => {
                    const next = new URLSearchParams(prev);
                    next.set("page", page.toString());
                    return next;
                });
            }
        },
        [setSearchParams]
    );

    if (isLoading) {
        return <PageStatus className="profile-page" message={t("admin.dashboard.loading", { defaultValue: "Cargando..." })} />;
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
                                {activeTab === "info" && <ProfileInfoTab profile={profile} infoLoading={infoLoading} />}
                                {activeTab === "interests" && (
                                    <ProfileInterestsTab
                                        isMine={profile.isMine}
                                        page={pagedInterests.data ?? emptyPage()}
                                        isLoading={pagedInterests.isLoading}
                                        onPageChange={handlePageChange}
                                    />
                                )}
                                {activeTab === "events" && (
                                    <ProfileEventsTab
                                        isMine={profile.isMine}
                                        activeTab={eventsTab}
                                        isLoading={profileEvents.isLoading}
                                        created={profileEvents.created ?? emptyPage()}
                                        attending={profileEvents.attending ?? emptyPage()}
                                        finished={profileEvents.finished ?? emptyPage()}
                                        onTabChange={(nextTab) => updateSearch({
                                            eventsTab: nextTab === "created" ? "" : nextTab,
                                            page: "",
                                        })}
                                        onPageChange={handlePageChange}
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
