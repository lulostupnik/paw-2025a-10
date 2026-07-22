import { Link, useLocation } from "react-router-dom";
import { useQueryClient } from "@tanstack/react-query";
import { useI18n } from "@/lib/i18n";
import { pushToNavigationStack } from "@/lib/utils/navigationStack";
import { prefetchProfileDetail } from "@/lib/utils/prefetchDetail";
import AvatarFallbackIcon from "@/components/ui/AvatarFallbackIcon";

interface CreatorCardProps {
    creator: {
        id: number;
        firstname: string;
        lastname: string;
        username: string;
        profilePictureUrl?: string | null;
        university?: { name: string } | null;
        career?: { name: string } | null;
    };
    isJourneyCreator?: boolean;
    showName?: boolean;
}

export default function CreatorCard({ creator, isJourneyCreator = false, showName = true }: CreatorCardProps) {
    const { t } = useI18n();
    const location = useLocation();
    const queryClient = useQueryClient();
    const handlePrefetch = () => prefetchProfileDetail(queryClient, creator.id);

    return (
        <Link
            to={`/profiles/${creator.id}/info`}
            state={{ from: `${location.pathname}${location.search}` }}
            className="profile-card-link"
            onMouseEnter={handlePrefetch}
            onFocus={handlePrefetch}
            onClick={() => pushToNavigationStack(`${location.pathname}${location.search}`)}
        >
            <div className="event-creator">
                <h3 className="creator-title">
                    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                        <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                        <circle cx="12" cy="7" r="4"></circle>
                    </svg>
                    {isJourneyCreator ? t("journey.host") : t("event.creator")}
                </h3>

                <div className="creator-profile">
                    <div className="creator-avatar-container">
                        {creator.profilePictureUrl ? (
                            <div className="creator-image-wrapper">
                                <img
                                    src={creator.profilePictureUrl}
                                    alt={t("event.creator.profile.image", { defaultValue: "Creator Profile" })}
                                    className="creator-profile-img"
                                />
                            </div>
                        ) : (
                            <div className="creator-avatar-placeholder">
                                <AvatarFallbackIcon size={28} />
                            </div>
                        )}
                    </div>
                    <div className="creator-info">
                        {showName && <h4 className="creator-name">{creator.firstname} {creator.lastname}</h4>}
                        <p className="creator-username">@{creator.username}</p>
                        <div className="creator-details">
                            {creator.university?.name && (
                                <div className="creator-detail">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                        <path d="M2 3h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z"></path>
                                        <path d="M22 3h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z"></path>
                                    </svg>
                                    <span>{creator.university.name}</span>
                                </div>
                            )}
                            {creator.career?.name && (
                                <div className="creator-detail">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                        <rect x="2" y="7" width="20" height="14" rx="2" ry="2"></rect>
                                        <path d="M16 21V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v16"></path>
                                    </svg>
                                    <span>{creator.career.name}</span>
                                </div>
                            )}
                        </div>
                    </div>
                </div>
            </div>
        </Link>
    );
}
