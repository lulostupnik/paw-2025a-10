import type { ProfileDetail } from "@/types/profile";
import { useI18n } from "@/lib/i18n";

interface ProfileInfoTabProps {
    profile: ProfileDetail;
}

const renderStars = (rating: number) =>
    Array.from({ length: 5 }, (_, index) => {
        const starValue = index + 1;
        if (rating >= starValue) {
            return (
                <svg key={starValue} className="star filled" xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
                    <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon>
                </svg>
            );
        }
        if (rating >= starValue - 0.5) {
            return (
                <svg key={starValue} className="star half-filled" xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <defs>
                        <linearGradient id={`profile-half-${starValue}`}>
                            <stop offset="50%" stopColor="currentColor" stopOpacity="1" />
                            <stop offset="50%" stopColor="transparent" stopOpacity="0" />
                        </linearGradient>
                    </defs>
                    <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2" fill={`url(#profile-half-${starValue})`} />
                </svg>
            );
        }
        return (
            <svg key={starValue} className="star empty" xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon>
            </svg>
        );
    });

export default function ProfileInfoTab({ profile }: ProfileInfoTabProps) {
    const { t } = useI18n();
    const createdRating = profile.ratingStats.averageCreatedEventsRating ?? 0;
    const attendedRating = profile.ratingStats.averageAttendedEventsRating ?? 0;

    return (
        <div className="profile-section active" id="info-section">
            <div className="profile-card">
                <h2 className="section-title">{t("profile.personal.info")}</h2>

                <div className="info-list">
                    {profile.isMine && profile.email && (
                        <div className="info-item">
                            <h3 className="info-label">{t("profile.email")}</h3>
                            <p className="info-value">{profile.email}</p>
                        </div>
                    )}
                    <div className="info-item">
                        <h3 className="info-label">{t("profile.firstname")}</h3>
                        <p className="info-value">{profile.firstname}</p>
                    </div>

                    <div className="info-item">
                        <h3 className="info-label">{t("profile.lastname")}</h3>
                        <p className="info-value">{profile.lastname}</p>
                    </div>

                    {profile.university?.name && (
                        <div className="info-item">
                            <h3 className="info-label">{t("profile.home.university")}</h3>
                            <p className="info-value">{profile.university.name}</p>
                        </div>
                    )}

                    {profile.career?.name && (
                        <div className="info-item">
                            <h3 className="info-label">{t("profile.career")}</h3>
                            <p className="info-value">{profile.career.name}</p>
                        </div>
                    )}
                </div>
            </div>

            <div className="profile-card">
                <h2 className="section-title">
                    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                        <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon>
                    </svg>
                    {t("profile.rating.statistics")}
                </h2>

                <div className="rating-statistics-container">
                    <div className="rating-stat-item">
                        <div className="rating-stat-header">
                            <h3 className="rating-stat-label">
                                <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                    <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                                    <line x1="16" y1="2" x2="16" y2="6"></line>
                                    <line x1="8" y1="2" x2="8" y2="6"></line>
                                    <line x1="3" y1="10" x2="21" y2="10"></line>
                                </svg>
                                {t("profile.rating.events.created")}
                            </h3>
                        </div>

                        {createdRating > 0 ? (
                            <div className="rating-display">
                                <div className="rating-stars">{renderStars(createdRating)}</div>
                                <div className="rating-details">
                                    <span className="rating-number">{createdRating.toFixed(1)}</span>
                                </div>
                            </div>
                        ) : (
                            <div className="rating-empty-state">
                                <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                    <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon>
                                </svg>
                                <span className="empty-text">{t("profile.rating.no.created.events")}</span>
                            </div>
                        )}
                    </div>

                    <div className="rating-stat-item">
                        <div className="rating-stat-header">
                            <h3 className="rating-stat-label">
                                <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                    <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"></path>
                                    <circle cx="12" cy="10" r="3"></circle>
                                </svg>
                                {t("profile.rating.events.attended")}
                            </h3>
                        </div>

                        {attendedRating > 0 ? (
                            <div className="rating-display">
                                <div className="rating-stars">{renderStars(attendedRating)}</div>
                                <div className="rating-details">
                                    <span className="rating-number">{attendedRating.toFixed(1)}</span>
                                </div>
                            </div>
                        ) : (
                            <div className="rating-empty-state">
                                <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                    <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon>
                                </svg>
                                <span className="empty-text">{t("profile.rating.no.attended.events")}</span>
                            </div>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
}
